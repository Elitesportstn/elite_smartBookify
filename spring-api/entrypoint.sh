#!/bin/bash

# Ensure Flutter dependencies are installed
flutter doctor

# Check if a specific command is passed
if [ "$#" -gt 0 ]; then
    exec "$@"
else
    echo "No command specified. Use this container to run Flutter commands or Spring Boot applications."
    bash
fi
