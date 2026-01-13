from flask import Blueprint, request, jsonify
from app.services.prediction_service import prediction_service
from app.utils.validators import validate_eta_request, ValidationError
from app.utils.logger import logger

eta_bp = Blueprint('eta', __name__)

@eta_bp.route('/eta', methods=['POST'])
def predict_eta():
    """
    Predict ETA (Estimated Time of Arrival)
    
    Request Body:
    {
        "distance_remaining": 50.5,
        "avg_speed": 60.0,
        "traffic_factor": 1.2
    }
    
    Response:
    {
        "predicted_eta_minutes": 52.3,
        "formatted_eta": "52m",
        "hours": 0,
        "minutes": 52
    }
    """
    try:
        # Get request data
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        # Validate request
        validate_eta_request(data)
        
        # Extract parameters
        distance_remaining = float(data.get('distance_remaining'))
        avg_speed = float(data.get('avg_speed'))
        traffic_factor = float(data.get('traffic_factor', 1.0))
        
        # Log request
        logger.info(f"ETA prediction request - Distance: {distance_remaining}km, Speed: {avg_speed}km/h")
        
        # Predict
        result = prediction_service.predict_eta(
            distance_remaining,
            avg_speed,
            traffic_factor
        )
        
        # Log result
        logger.info(f"ETA predicted: {result['predicted_eta_minutes']} minutes")
        
        return jsonify(result), 200
        
    except ValidationError as e:
        logger.warning(f"Validation error: {str(e)}")
        return jsonify({'error': str(e)}), 400
    except Exception as e:
        logger.error(f"ETA prediction error: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

