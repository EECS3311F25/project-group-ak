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

app = Flask(__name__)

# Initialize Anthropic Client
api_key = os.getenv("ANTHROPIC_API_KEY")

if not api_key:
    logger.error("ANTHROPIC_API_KEY not found in environment variable")
    raise ValueError("ANTHROPIC_API_KEY environment variable is required")

client = Anthropic(api_key=api_key)
logger.info("Anthropic client initialized successfully")


@app.route('/api/generate-summary', methods=['POST'])
def generate_summary():
    """
    Generate AI summary for a trip.
    
    Expected request body:
    {
        "prompt": "Generate a summary for...",
        "model": "claude-3-haiku-20240307",  # optional
        "max_tokens": 150,                    # optional
        "temperature": 0.7                    # optional
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
        
        prompt = data.get('prompt')
        model = data.get('model', 'claude-3-haiku-20240307')
        max_tokens = data.get('max_tokens', 150)
        temperature = data.get('temperature', 0.7)
        
        # Validate prompt
        if not prompt:
            return jsonify({"error": "Prompt is required"}), 400
        
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
