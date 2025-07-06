# YouTube Notes Extractor

A Python tool that extracts transcripts from YouTube videos and generates structured, readable notes.

## Features

- Extract transcripts from YouTube videos
- Generate structured notes with key topics
- Support for multiple output formats (Markdown, JSON)
- Automatic content organization into readable sections
- Keyword extraction for topic identification
- Estimated reading time calculation

## Installation

1. Install Python dependencies:
```bash
pip install -r requirements.txt
```

2. Make the script executable:
```bash
chmod +x youtube_notes_extractor.py
```

## Usage

### Basic Usage

Extract notes from a YouTube video:
```bash
python youtube_notes_extractor.py "https://www.youtube.com/watch?v=VIDEO_ID"
```

### Advanced Options

**Specify output file:**
```bash
python youtube_notes_extractor.py "https://www.youtube.com/watch?v=VIDEO_ID" -o my_notes.md
```

**Output as JSON:**
```bash
python youtube_notes_extractor.py "https://www.youtube.com/watch?v=VIDEO_ID" -f json
```

**Specify preferred languages:**
```bash
python youtube_notes_extractor.py "https://www.youtube.com/watch?v=VIDEO_ID" -l en es fr
```

## Output Format

The tool generates structured notes with the following sections:

1. **Video Information** - Title, author, extraction date
2. **Key Topics** - Most frequently mentioned topics
3. **Content Summary** - Organized into readable paragraphs
4. **Full Transcript** - Complete original transcript

## Examples

### Example 1: Educational Video
```bash
python youtube_notes_extractor.py "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
```

### Example 2: Custom Output
```bash
python youtube_notes_extractor.py "https://www.youtube.com/watch?v=dQw4w9WgXcQ" -o lecture_notes.md
```

## Requirements

- Python 3.6+
- Internet connection
- Videos must have available transcripts (auto-generated or manual)

## Troubleshooting

**"Could not extract transcript"** - The video may not have transcripts available or may be private/restricted.

**"Could not extract video ID"** - Check that the URL is a valid YouTube URL.

**Missing dependencies** - Run `pip install -r requirements.txt` to install required packages.

## Limitations

- Only works with videos that have transcripts available
- Transcript quality depends on YouTube's auto-generation or manual captions
- Does not work with private or restricted videos
- Language detection is basic and may not be perfect

## Tips

- For best results, use videos with manual captions rather than auto-generated ones
- Educational content typically produces the most structured notes
- Long videos may take a few moments to process
- Check the key topics section to quickly understand the video's main themes