from app.models.eta_predictor import ETAPredictor
from app.models.cost_predictor import CostPredictor
from app.models.anomaly_detector import AnomalyDetector
from app.utils.logger import logger

class PredictionService:
    """Service layer for all predictions"""
    
    def __init__(self):
        self.eta_predictor = ETAPredictor()
        self.cost_predictor = CostPredictor()
        self.anomaly_detector = AnomalyDetector()
        logger.info("PredictionService initialized")
    
    def predict_eta(self, distance_remaining, avg_speed, traffic_factor):
        """Predict ETA"""
        try:
            eta_minutes = self.eta_predictor.predict(
                distance_remaining, 
                avg_speed, 
                traffic_factor
            )
            
            # Format result
            hours = int(eta_minutes // 60)
            minutes = int(eta_minutes % 60)
            
            if hours > 0:
                formatted_eta = f"{hours}h {minutes}m"
            else:
                formatted_eta = f"{minutes}m"
            
            return {
                'predicted_eta_minutes': round(eta_minutes, 2),
                'formatted_eta': formatted_eta,
                'hours': hours,
                'minutes': minutes
            }
        except Exception as e:
            logger.error(f"ETA prediction service error: {e}")
            raise
    
    def predict_cost(self, distance_km, weight_kg, luggage_type, 
                     speed_pattern=50.0, traffic_factor=1.0):
        """Predict transportation cost"""
        try:
            predicted_cost = self.cost_predictor.predict(
                distance_km,
                weight_kg,
                luggage_type,
                speed_pattern,
                traffic_factor
            )
            
            return {
                'predicted_cost': round(predicted_cost, 2),
                'currency': 'INR',
                'breakdown': {
                    'distance_km': distance_km,
                    'weight_kg': weight_kg,
                    'category': luggage_type,
                    'traffic_factor': traffic_factor
                }
            }
        except Exception as e:
            logger.error(f"Cost prediction service error: {e}")
            raise
    
    def detect_anomaly(self, speed, latitude=None, longitude=None,
                       expected_direction=None, current_direction=None):
        """Detect anomalies"""
        try:
            result = self.anomaly_detector.detect(
                speed,
                latitude,
                longitude,
                expected_direction,
                current_direction
            )
            
            return result
        except Exception as e:
            logger.error(f"Anomaly detection service error: {e}")
            raise

# Create singleton instance
prediction_service = PredictionService()

