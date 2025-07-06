#!/usr/bin/env python3
"""
YouTube Notes Extractor

This script extracts transcripts from YouTube videos and generates structured notes.
It can also summarize the content and organize it into readable sections.
"""

import argparse
import sys
import re
from typing import List, Dict, Optional
from urllib.parse import urlparse, parse_qs
import json
from datetime import datetime

try:
    from youtube_transcript_api import YouTubeTranscriptApi
    from youtube_transcript_api.formatters import TextFormatter
except ImportError:
    print("Error: youtube-transcript-api not installed. Run: pip install youtube-transcript-api")
    sys.exit(1)

try:
    import requests
except ImportError:
    print("Error: requests not installed. Run: pip install requests")
    sys.exit(1)


class YouTubeNotesExtractor:
    def __init__(self):
        self.formatter = TextFormatter()
    
    def extract_video_id(self, url: str) -> Optional[str]:
        """Extract video ID from YouTube URL."""
        if 'youtu.be' in url:
            return url.split('/')[-1].split('?')[0]
        elif 'youtube.com' in url:
            parsed_url = urlparse(url)
            if 'v' in parse_qs(parsed_url.query):
                return parse_qs(parsed_url.query)['v'][0]
        return None
    
    def get_video_info(self, video_id: str) -> Dict:
        """Get basic video information using YouTube's oEmbed API."""
        try:
            oembed_url = f"https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v={video_id}&format=json"
            response = requests.get(oembed_url)
            if response.status_code == 200:
                return response.json()
        except Exception as e:
            print(f"Warning: Could not fetch video info: {e}")
        return {}
    
    def get_transcript(self, video_id: str, languages: List[str] = ['en']) -> Optional[str]:
        """Extract transcript from YouTube video."""
        try:
            transcript_list = YouTubeTranscriptApi.get_transcript(video_id, languages=languages)
            transcript_text = self.formatter.format_transcript(transcript_list)
            return transcript_text
        except Exception as e:
            print(f"Error extracting transcript: {e}")
            return None
    
    def clean_transcript(self, transcript: str) -> str:
        """Clean and normalize transcript text."""
        # Remove extra whitespace
        transcript = re.sub(r'\s+', ' ', transcript.strip())
        
        # Fix common transcript issues
        transcript = re.sub(r'\[.*?\]', '', transcript)  # Remove [music], [applause], etc.
        transcript = re.sub(r'\(.*?\)', '', transcript)  # Remove (inaudible), etc.
        
        return transcript
    
    def create_structured_notes(self, transcript: str, video_info: Dict) -> Dict:
        """Create structured notes from transcript."""
        # Split transcript into sentences
        sentences = re.split(r'[.!?]+', transcript)
        sentences = [s.strip() for s in sentences if s.strip()]
        
        # Group sentences into paragraphs (every 3-5 sentences)
        paragraphs = []
        current_paragraph = []
        
        for i, sentence in enumerate(sentences):
            current_paragraph.append(sentence)
            if len(current_paragraph) >= 4 or i == len(sentences) - 1:
                paragraphs.append('. '.join(current_paragraph) + '.')
                current_paragraph = []
        
        # Extract potential topics/themes (simple keyword extraction)
        word_freq = {}
        words = transcript.lower().split()
        for word in words:
            if len(word) > 4 and word.isalpha():
                word_freq[word] = word_freq.get(word, 0) + 1
        
        # Get top keywords
        top_keywords = sorted(word_freq.items(), key=lambda x: x[1], reverse=True)[:10]
        
        notes = {
            "video_info": video_info,
            "extraction_date": datetime.now().isoformat(),
            "word_count": len(transcript.split()),
            "estimated_reading_time": f"{len(transcript.split()) // 200} minutes",
            "key_topics": [word for word, freq in top_keywords],
            "structured_content": paragraphs,
            "full_transcript": transcript
        }
        
        return notes
    
    def format_notes_as_markdown(self, notes: Dict) -> str:
        """Format notes as Markdown."""
        video_info = notes.get("video_info", {})
        title = video_info.get("title", "YouTube Video")
        author = video_info.get("author_name", "Unknown")
        
        markdown = f"""# Notes: {title}

**Author:** {author}
**Extracted:** {notes.get('extraction_date', 'Unknown')}
**Word Count:** {notes.get('word_count', 'Unknown')}
**Estimated Reading Time:** {notes.get('estimated_reading_time', 'Unknown')}

## Key Topics
{', '.join(notes.get('key_topics', []))}

## Content Summary

"""
        
        for i, paragraph in enumerate(notes.get('structured_content', []), 1):
            markdown += f"### Section {i}\n{paragraph}\n\n"
        
        markdown += f"""## Full Transcript

{notes.get('full_transcript', 'No transcript available')}

---
*Notes generated by YouTube Notes Extractor*
"""
        
        return markdown
    
    def save_notes(self, notes: Dict, output_file: str, format_type: str = 'markdown'):
        """Save notes to file."""
        if format_type.lower() == 'json':
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(notes, f, indent=2, ensure_ascii=False)
        else:  # markdown
            markdown_content = self.format_notes_as_markdown(notes)
            with open(output_file, 'w', encoding='utf-8') as f:
                f.write(markdown_content)
        
        print(f"Notes saved to: {output_file}")


def main():
    parser = argparse.ArgumentParser(description='Extract notes from YouTube videos')
    parser.add_argument('url', help='YouTube video URL')
    parser.add_argument('-o', '--output', help='Output file name (default: video_title_notes)')
    parser.add_argument('-f', '--format', choices=['markdown', 'json'], default='markdown',
                       help='Output format (default: markdown)')
    parser.add_argument('-l', '--languages', nargs='+', default=['en'],
                       help='Preferred transcript languages (default: en)')
    
    args = parser.parse_args()
    
    extractor = YouTubeNotesExtractor()
    
    # Extract video ID
    video_id = extractor.extract_video_id(args.url)
    if not video_id:
        print("Error: Could not extract video ID from URL")
        sys.exit(1)
    
    print(f"Processing video: {video_id}")
    
    # Get video info
    video_info = extractor.get_video_info(video_id)
    
    # Get transcript
    print("Extracting transcript...")
    transcript = extractor.get_transcript(video_id, args.languages)
    if not transcript:
        print("Error: Could not extract transcript")
        sys.exit(1)
    
    # Clean transcript
    transcript = extractor.clean_transcript(transcript)
    
    # Create structured notes
    print("Creating structured notes...")
    notes = extractor.create_structured_notes(transcript, video_info)
    
    # Determine output file name
    if args.output:
        output_file = args.output
    else:
        title = video_info.get('title', 'youtube_video')
        # Clean title for filename
        clean_title = re.sub(r'[^\w\s-]', '', title)
        clean_title = re.sub(r'[-\s]+', '_', clean_title)
        output_file = f"{clean_title}_notes.{args.format}"
    
    # Save notes
    extractor.save_notes(notes, output_file, args.format)
    
    print("\nNotes extraction completed successfully!")
    print(f"Key topics identified: {', '.join(notes['key_topics'][:5])}")
    print(f"Total word count: {notes['word_count']}")


if __name__ == "__main__":
    main()