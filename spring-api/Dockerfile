# ------------------------------------------------------------
# Base image with Flutter + Java + Maven
# ------------------------------------------------------------
FROM ubuntu:20.04

# ------------------------------------------------------------
# Avoid user prompts during package installations
# ------------------------------------------------------------
ENV DEBIAN_FRONTEND=noninteractive
    
# ------------------------------------------------------------
# Install system dependencies for Flutter, Java, Maven, and Android SDK
# ------------------------------------------------------------
RUN apt-get update && \
        apt-get install -y \
            curl \
            git \
            unzip \
            xz-utils \
            zip \
            libglu1-mesa \
            openjdk-17-jdk \
            maven \
            clang \
            cmake \
            ninja-build \
            pkg-config \
            ca-certificates \
            wget \
            gnupg \
        && \
    # Add Google's official GPG key for Chrome
        wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    # Set up the stable repository for Google Chrome
        echo "deb [arch=amd64] https://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
        apt-get update && \
    # Install Google Chrome
        apt-get install -y google-chrome-stable && \
    # Clean up APT when done to reduce image size
        rm -rf /var/lib/apt/lists/*
    
# ------------------------------------------------------------
# Install Flutter
# ------------------------------------------------------------
ARG FLUTTER_VERSION=3.27.1
RUN curl -fSL https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_${FLUTTER_VERSION}-stable.tar.xz -o flutter_linux.tar.xz && \
        tar -xf flutter_linux.tar.xz -C /opt && \
        rm flutter_linux.tar.xz && \
        ln -s /opt/flutter /flutter
    
# Add Flutter to PATH
ENV PATH="/flutter/bin:$PATH"
    
# ------------------------------------------------------------
# Set JAVA_HOME
# ------------------------------------------------------------
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"
    
# ------------------------------------------------------------
# Configure Git to trust the Flutter directory
# ------------------------------------------------------------
RUN git config --global --add safe.directory /opt/flutter
    
# ------------------------------------------------------------
# Switch to stable channel and upgrade Flutter
# ------------------------------------------------------------
RUN flutter channel stable && \
        flutter upgrade
    
# ------------------------------------------------------------
# Install Flutter dependencies and verify installation
# ------------------------------------------------------------
RUN flutter doctor -v
    
# ------------------------------------------------------------
# Install Android SDK Command-line Tools manually
# ------------------------------------------------------------
RUN mkdir -p /opt/android-sdk/cmdline-tools/latest && \
        curl -o commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
        unzip commandlinetools.zip -d /opt/android-sdk/cmdline-tools/latest && \
        rm commandlinetools.zip && \
    # Move cmdline-tools contents to 'latest' directory
        mv /opt/android-sdk/cmdline-tools/latest/cmdline-tools/* /opt/android-sdk/cmdline-tools/latest/ && \
        rm -rf /opt/android-sdk/cmdline-tools/latest/cmdline-tools && \
    # Ensure sdkmanager has execute permissions
        chmod +x /opt/android-sdk/cmdline-tools/latest/bin/*
    
# Set Android SDK environment variables
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH="$PATH:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools"
    
# ------------------------------------------------------------
# Install Android SDK packages and accept licenses
# ------------------------------------------------------------
RUN yes | sdkmanager --licenses && \
        sdkmanager "platforms;android-33" "build-tools;33.0.0" "platform-tools" "emulator" "tools" && \
        sdkmanager --update
    
# ------------------------------------------------------------
# Set Working Directory for the Spring Boot Application
# ------------------------------------------------------------
WORKDIR /app
    
# ------------------------------------------------------------
# Copy Maven project files
# ------------------------------------------------------------
COPY pom.xml .
COPY src ./src
    
# ------------------------------------------------------------
# Build the Spring Boot JAR (skip tests for speed)
# ------------------------------------------------------------
RUN mvn clean package -DskipTests
    
# ------------------------------------------------------------
# Expose the port your Spring Boot app listens on
# ------------------------------------------------------------
EXPOSE 8080
    
# ------------------------------------------------------------
# Run the Spring Boot JAR
# ------------------------------------------------------------
# Adjust 'app.jar' if your artifact has a different name
ENTRYPOINT ["java", "-jar", "/app/target/app.jar"]