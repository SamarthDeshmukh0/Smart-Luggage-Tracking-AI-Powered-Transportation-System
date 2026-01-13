from flask import Blueprint, request, jsonify
from app.services.prediction_service import prediction_service
from app.utils.validators import validate_anomaly_request, ValidationError
from app.utils.logger import logger

anomaly_bp = Blueprint('anomaly', __name__)

@anomaly_bp.route('/anomaly', methods=['POST'])
def detect_anomaly():
    """
    Detect Anomalies in GPS Tracking Data
    
    Request Body:
    {
        "tracking_id": "TRK123456",
        "speed": 3.2,
        "latitude": 19.0760,
        "longitude": 72.8777,
        "expected_direction": 45.0,
        "current_direction": 120.0
    }
    
    Response:
    {
        "anomaly": true,
        "message": "Luggage not moving",
        "severity": "MEDIUM",
        "anomalies": [
            {
                "type": "NOT_MOVING",
                "message": "Luggage not moving (speed: 3.2 km/h)",
                "severity": "MEDIUM"
            }
        ]
    }
    """
    try:
        # Get request data
        data = request.get_json()
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        # Validate request
        validate_anomaly_request(data)
        
        # Extract parameters
        tracking_id = data.get('tracking_id')
        speed = float(data.get('speed'))
        latitude = data.get('latitude')
        longitude = data.get('longitude')
        expected_direction = data.get('expected_direction')
        current_direction = data.get('current_direction')
        
        # Log request
        logger.info(f"Anomaly detection request - Tracking: {tracking_id}, Speed: {speed}km/h")
        
        # Detect anomalies
        result = prediction_service.detect_anomaly(
            speed,
            latitude,
            longitude,
            expected_direction,
            current_direction
        )
        
        # Log result
        if result['anomaly']:
            logger.warning(f"Anomaly detected for {tracking_id}: {result['message']}")
        else:
            logger.info(f"No anomaly detected for {tracking_id}")
        
        return jsonify(result), 200
        
    except ValidationError as e:
        logger.warning(f"Validation error: {str(e)}")
        return jsonify({'error': str(e)}), 400
    except Exception as e:
        logger.error(f"Anomaly detection error: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@anomaly_bp.route('/anomaly/batch', methods=['POST'])
def detect_anomaly_batch():
    """
    Detect anomalies for multiple GPS points
    
    Request Body:
    {
        "tracking_id": "TRK123456",
        "gps_points": [
            {"speed": 45.2, "latitude": 19.0760, "longitude": 72.8777},
            {"speed": 3.2, "latitude": 19.0780, "longitude": 72.8797},
            {"speed": 125.0, "latitude": 19.0800, "longitude": 72.8817}
        ]
    }
    
    Response:
    {
        "tracking_id": "TRK123456",
        "total_points": 3,
        "anomalies_detected": 2,
        "results": [...]
    }
    """
    try:
        data = request.get_json()
        
        if not data or 'gps_points' not in data:
            return jsonify({'error': 'GPS points required'}), 400
        
        tracking_id = data.get('tracking_id', 'unknown')
        gps_points = data.get('gps_points', [])
        
        results = []
        anomaly_count = 0
        
        for point in gps_points:
            result = prediction_service.detect_anomaly(
                speed=point.get('speed', 0),
                latitude=point.get('latitude'),
                longitude=point.get('longitude')
            )
            
            if result['anomaly']:
                anomaly_count += 1
            
            results.append(result)
        
        return jsonify({
            'tracking_id': tracking_id,
            'total_points': len(gps_points),
            'anomalies_detected': anomaly_count,
            'results': results
        }), 200
        
    except Exception as e:
        logger.error(f"Batch anomaly detection error: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500


