FROM eclipse-temurin:17-jre

WORKDIR /werwolf

# JAR sauber kopieren + umbenennen
COPY target/scala-3.7.3/scalafx-test-assembly-0.1.0-SNAPSHOT.jar app.jar

RUN apt-get update && apt-get install -y \
  libx11-6 \
  libxext6 \
  libxrender1 \
  libxtst6 \
  libxi6 \
  libfreetype6 \
  libfontconfig1 \
  libgl1 \
  libgtk-3-0


# für TUI (tput, clear, Farben)
RUN apt-get update && apt-get install -y ncurses-bin

CMD ["java", "-jar", "app.jar"]
