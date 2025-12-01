from flask import Flask, request, jsonify
from anthropic import Anthropic
import os
from dotenv import load_dotenv
import logging

# load environment variables
load_dotenv()

# Configure Logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__) # __name__ tells flask where to find template/static file -> app hangles HTTP request

# Initialize Anthropic Client
api_key = os.getenv("ANTHROPIC_API_KEY")

if not api_key:
    logger.error("ANTHROPIC_API_KEY not found in environment variable")
    raise ValueError("ANTHROPIC_API_KEY environment variable is required")

client = Anthropic(api_key=api_key)
logger.info("Anthropic client initialized successfully")


def format_prompt(trip_data):
    """
    Format a prompt for AI summary generation from trip data.
    
    Args:
        trip_data: Dictionary containing trip information
        
    Returns:
        Formatted prompt string
    """
    participants = ", ".join(trip_data.get('users', []))
    duration = trip_data.get('duration', {})
    date_range = f"{duration.get('start', '')} to {duration.get('end', '')}"
    
    events = trip_data.get('events', [])
    event_count = len(events)
    
    if not events:
        events_text = "No specific events planned yet"
        event_count_text = ""
    else:
        events_list = []
        for index, event in enumerate(events, start=1):
            event_duration = event.get('duration', {})
            time_range = f"{event_duration.get('start', '')} - {event_duration.get('end', '')}"
            
            location = event.get('location')
            location_text = ""
            if location:
                lat = location.get('latitude', '')
                lon = location.get('longitude', '')
                location_text = f" at ({lat}, {lon})"
            
            description = event.get('description', '')
            description_text = f" - {description}" if description else ""
            
            events_list.append(
                f"- Day {index}: {event.get('title', '')} ({time_range}){location_text}{description_text}"
            )
        events_text = "\n".join(events_list)
        event_count_text = f"YOU MUST INCLUDE ALL {event_count} EVENTS - NO EXCEPTIONS"
    
    prompt = f"""Generate a concise summary of this trip for a travel app. Include ALL events and activities that happened during the trip.

CRITICAL REQUIREMENTS FOR TRAVEL APP SUMMARY:
- Write ONE complete summary (not multiple summaries)
- MANDATORY: Include EVERY SINGLE event listed below - this includes ALL types: tours, meals, travel/transportation, markets, activities, etc.
- Do NOT skip any event, even if it seems minor (like "Drive to X", "Lunch at Y", "Market visit", etc.) - ALL events are important for a travel app
- Do NOT add a conclusion paragraph at the end - end with the last event/activity
- VARIATION: Each time you generate a summary, vary your approach - use different opening phrases, vary sentence structure, use different descriptive words, and present events from slightly different perspectives while keeping all facts accurate
- Start directly with the first event/activity - do NOT begin with a preview or conclusion statement, but vary how you introduce the first event
- Use daytime references only (morning, afternoon, evening, night) - do not include specific times like "9 AM" or "1-hour drive"
- Keep descriptions brief and to the point - avoid excessive detail
- Write in a narrative style that flows naturally from one event to the next in chronological order
- Mention key locations and activities for each event
- Ensure the summary ends naturally after describing the last event - do not add a conclusion paragraph
- IMPORTANT: Travel/transportation events (like "Drive to X", "Train to Y") are valid events and MUST be included
- IMPORTANT: All activities matter - meals, markets, walks, tours, everything listed must be mentioned

Trip Details:
Title: {trip_data.get('name', '')}
Dates: {date_range}
Owner: {trip_data.get('owner', '')}
Participants: {participants}

Events and Activities ({event_count_text}):
{events_text}

Generate a concise travel app summary that covers ALL {event_count} events listed above in strict chronological order. 
Start with the first event, not a preview. End with the last event - do NOT add a conclusion paragraph.
Before finishing, verify you have mentioned all {event_count} events - count them to ensure none are missing."""
    
    return prompt


@app.route('/api/generate-summary', methods=['POST'])
def generate_summary():
    """
    Generate AI summary for a trip.
    
    Expected request body:
    {
        "trip": {
            "id": "trip_123",
            "name": "Summer Getaway",
            "owner": "John",
            "users": ["John", "Jane"],
            "duration": {
                "start": "2025-07-01T09:00",
                "end": "2025-07-10T17:00"
            },
            "events": [...]
        },
        "model": "claude-3-haiku-20240307",  # optional
        "max_tokens": 350,                    # optional
        "temperature": 0.9                    # optional (higher = more variation)
    }
    
    Returns:
    {
        "summary": "Generated summary text...",
        "model": "claude-3-haiku-20240307"
    }
    """
    try:
        # Get request data
        data = request.json
        
        if not data:
            return jsonify({"error": "Request body is required"}), 400
        
        trip_data = data.get('trip')
        model = data.get('model', 'claude-3-haiku-20240307')
        max_tokens = data.get('max_tokens', 350)
        temperature = data.get('temperature', 0.9)
        
        # Validate trip data
        if not trip_data:
            return jsonify({"error": "Trip data is required"}), 400
        
        logger.info(f"Formatting prompt for trip: {trip_data.get('id', 'unknown')}")
        
        # Format prompt from trip data
        prompt = format_prompt(trip_data)
        
        logger.info(f"Generating summary with model: {model}, max_tokens: {max_tokens}")
        
        # Call Claude API
        message = client.messages.create(
            model=model,
            max_tokens=max_tokens,
            temperature=temperature,
            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ]
        )
        
        # Extract summary from response
        summary = message.content[0].text.strip()
        
        logger.info(f"Successfully generated summary (length: {len(summary)} characters)")
        
        return jsonify({
            "summary": summary,
            "model": model
        }), 200
        
    except Exception as e:
        logger.error(f"Error generating summary: {str(e)}", exc_info=True)
        return jsonify({"error": str(e)}), 500


@app.route('/health', methods=['GET'])
def health():
    """
    Health check endpoint.
    
    Returns:
    {
        "status": "healthy"
    }
    """
    return jsonify({"status": "healthy"}), 200


if __name__ == '__main__':
    port = int(os.getenv('PORT', 5001))  # Use port 5001 by default, or PORT env variable
    logger.info(f"Starting AI service on http://0.0.0.0:{port}")
    app.run(host='0.0.0.0', port=port, debug=True)
