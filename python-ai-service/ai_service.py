from flask import Flask, request, jsonify
from anthropic import Anthropic
import os
from dotenv import load_dotenv
import logging

# load environment variables
load_dotenv()

# Initialize Anthropic Client
api_key = os.getenv("ANTHROPIC_API_KEY")

# Configure Logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__) # __name__ tells flask where to find template/static file -> app hangles HTTP request



if not api_key:
    logger.error("ANTHROPIC_API_KEY not found in environment variable")
    raise ValueError("ANTHROPIC_API_KEY environment variable is required")

client = Anthropic(api_key=api_key)
logger.info("Anthropic client initialized successfully")


def format_prompt(trip_data):
    """
    Format a prompt for AI summary generation from trip data.
    
    Args:
        trip_data: Dictionary containing trip information from database
        Expected structure:
        - trip_title (not 'name')
        - trip_duration: {startDate, startTime, endDate, endTime} (not 'duration' with 'start'/'end')
        - events: [{event_title, event_description, event_location, event_duration: {...}}]
    
    Returns:
        Formatted prompt string
    """
    # Handle trip title (database uses 'trip_title', not 'name')
    trip_title = trip_data.get('trip_title') or trip_data.get('name', '')
    
    # Handle duration - database sends Duration object with startDate/startTime/endDate/endTime
    trip_duration = trip_data.get('trip_duration') or trip_data.get('duration', {})
    if isinstance(trip_duration, dict):
        # Database format: {startDate, startTime, endDate, endTime}
        start_date = trip_duration.get('startDate', '')
        start_time = trip_duration.get('startTime', '')
        end_date = trip_duration.get('endDate', '')
        end_time = trip_duration.get('endTime', '')
        
        # Format date range
        if start_date and end_date:
            date_range = f"{start_date} to {end_date}"
        else:
            # Fallback to old format if present
            date_range = f"{trip_duration.get('start', '')} to {trip_duration.get('end', '')}"
    else:
        date_range = ""
    
    # Handle participants (database doesn't have 'users' field, use empty)
    participants = trip_data.get('users', [])
    if not participants:
        participants = []
    participants_text = ", ".join(participants) if participants else "N/A"
    
    # Handle owner (database has 'user_id', not 'owner')
    owner = trip_data.get('owner') or f"User {trip_data.get('user_id', '')}" or "N/A"
    
    events = trip_data.get('events', [])
    event_count = len(events)
    
    if not events:
        events_text = "No specific events planned yet"
        event_count_text = ""
    else:
        # Group events by actual day (using startDate)
        events_by_day = {}
        for event in events:
            # Database uses 'event_title', not 'title'
            event_title = event.get('event_title') or event.get('title', '')
            
            # Handle event duration - database sends Duration object
            event_duration = event.get('event_duration') or event.get('duration', {})
            if isinstance(event_duration, dict):
                # Database format: {startDate, startTime, endDate, endTime}
                start_date = event_duration.get('startDate', '')
                start_time = event_duration.get('startTime', '')
                end_date = event_duration.get('endDate', '')
                end_time = event_duration.get('endTime', '')
                
                # Format time range
                if start_time and end_time:
                    time_range = f"{start_time} - {end_time}"
                elif start_date and end_date:
                    time_range = f"{start_date} - {end_date}"
                else:
                    # Fallback to old format
                    time_range = f"{event_duration.get('start', '')} - {event_duration.get('end', '')}"
            else:
                time_range = ""
                start_date = ""
            
            # Handle location - database uses 'event_location' (String), not 'location' (object)
            location = event.get('event_location') or event.get('location', '')
            location_text = f" at {location}" if location else ""
            
            # Handle description - database uses 'event_description'
            description = event.get('event_description') or event.get('description', '')
            description_text = f" - {description}" if description else ""
            
            # Group by day (use start_date as key, or "Unknown" if no date)
            day_key = start_date if start_date else "Unknown"
            if day_key not in events_by_day:
                events_by_day[day_key] = []
            
            events_by_day[day_key].append({
                'title': event_title,
                'time': time_range,
                'location': location_text,
                'description': description_text
            })
        
        # Format events grouped by day
        events_list = []
        sorted_days = sorted([d for d in events_by_day.keys() if d != "Unknown"]) + (["Unknown"] if "Unknown" in events_by_day else [])
        
        for day_num, day_date in enumerate(sorted_days, start=1):
            day_events = events_by_day[day_date]
            # Show day number only once if multiple events on same day
            if len(day_events) > 1:
                events_list.append(f"Day {day_num} ({day_date if day_date != 'Unknown' else ''}):")
                for event in day_events:
                    events_list.append(f"  - {event['title']} ({event['time']}){event['location']}{event['description']}")
            else:
                # Single event on this day
                event = day_events[0]
                events_list.append(
                    f"- Day {day_num}: {event['title']} ({event['time']}){event['location']}{event['description']}"
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
- CRITICAL: Group events by ACTUAL DAYS - if multiple events are listed for "Day 1", they all happen on the same day. Do NOT treat each event as a separate day. For example, if Day 1 has 3 events, mention all 3 events as happening on Day 1, not as Day 1, Day 2, Day 3.



Trip Details:
Title: {trip_title}
Dates: {date_range}
Owner: {owner}
Participants: {participants_text}

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
        
        trip_id = trip_data.get('trip_title') or trip_data.get('name') or trip_data.get('id', 'unknown')
        logger.info(f"Formatting prompt for trip: {trip_id}")
        
        # Format prompt from trip data
        prompt = format_prompt(trip_data)
        
        # Log the formatted prompt for debugging (first 500 chars)
        logger.debug(f"Formatted prompt preview: {prompt[:500]}...")
        
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
