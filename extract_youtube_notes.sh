#!/bin/bash

# YouTube Notes Extractor Wrapper Script
# This script activates the virtual environment and runs the YouTube notes extractor

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Activate the virtual environment
source "$SCRIPT_DIR/youtube_notes_env/bin/activate"

# Run the YouTube notes extractor with all passed arguments
python "$SCRIPT_DIR/youtube_notes_extractor.py" "$@"