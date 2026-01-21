#!/bin/bash

# Skript: build_and_run.sh
# Plattformübergreifend: Linux, Mac, Windows (via WSL oder Git Bash)

# Funktion, um das Betriebssystem zu erkennen
OS="$(uname -s)"

echo "Detected OS: $OS"

# Linux-spezifisch: X11 erlauben
if [ "$OS" = "Linux" ]; then
    echo "Setting X11 permissions for local display..."
    xhost +local: || echo "Warning: xhost failed, continue anyway."
fi

# SBT Build
echo "Running sbt clean assembly..."
sbt clean assembly || { echo "SBT build failed"; exit 1; }

# Docker Build
echo "Building Docker image 'werwolf:tui'..."
docker build -t werwolf:tui . || { echo "Docker build failed"; exit 1; }

# Docker Run
echo "Running Docker container..."
DOCKER_RUN_CMD="docker run -it"

# Linux: DISPLAY-Variable weitergeben und X11 mounten
if [ "$OS" = "Linux" ]; then
    DOCKER_RUN_CMD+=" -e DISPLAY=$DISPLAY -e LIBGL_ALWAYS_SOFTWARE=1 -v /tmp/.X11-unix:/tmp/.X11-unix"
fi

DOCKER_RUN_CMD+=" werwolf:tui"
# Container starten
eval $DOCKER_RUN_CMD
