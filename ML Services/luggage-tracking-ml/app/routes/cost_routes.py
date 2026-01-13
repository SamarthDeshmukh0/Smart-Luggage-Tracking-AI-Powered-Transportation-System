from flask import Blueprint, request, jsonify
from app.services.prediction_service import prediction_service
from app.utils.validators import validate_cost_request, ValidationError
from app.utils.logger import logger

cost_bp = Blueprint('cost', __name__)

@cost_bp.route('/cost', methods=['POST'])
def predict_cost():
    """
    Predict Transportation Cost
    
    Request Body:
    {
        "distance_km": 150.0,
        "weight_kg": 25.5,
        "luggage_type": "fragile",
        "speed_pattern": 55.0,
        "traffic_factor": 1.1
    }
    
    Response:
    {
        "predicted_cost": 1450.50,
        "currency": "INR",
        "breakdown": {
            "distance_km": 150.0,
            "weight_kg": 25.5,
            "category": "fragile",
            "traffic_factor": 1.1
        }
    }
    """
    try:
        # Get request data
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        # Validate request
        validate_cost_request(data)
        
        # Extract parameters
        distance_km = float(data.get('distance_km'))
        weight_kg = float(data.get('weight_kg'))
        luggage_type = data.get('luggage_type').lower()
        speed_pattern = float(data.get('speed_pattern', 50.0))
        traffic_factor = float(data.get('traffic_factor', 1.0))
        
        # Log request
        logger.info(f"Cost prediction request - Distance: {distance_km}km, Weight: {weight_kg}kg, Type: {luggage_type}")
        
        # Predict
        result = prediction_service.predict_cost(
            distance_km,
            weight_kg,
            luggage_type,
            speed_pattern,
            traffic_factor
        )
        
        # Log result
        logger.info(f"Cost predicted: ₹{result['predicted_cost']}")
        
        return jsonify(result), 200
        
    except ValidationError as e:
        logger.warning(f"Validation error: {str(e)}")
        return jsonify({'error': str(e)}), 400
    except Exception as e:
        logger.error(f"Cost prediction error: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

