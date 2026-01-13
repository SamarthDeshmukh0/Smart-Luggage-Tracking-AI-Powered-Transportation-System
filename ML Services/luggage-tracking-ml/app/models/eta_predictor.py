import joblib
import numpy as np
from app.config.settings import Config
from app.utils.logger import logger
import os

class ETAPredictor:
    """ETA Prediction Model"""
    
    def __init__(self):
        self.model = None
        self.scaler = None
        self.load_model()
    
    def load_model(self):
        """Load trained model and scaler"""
        try:
            model_path = Config.get_model_path(Config.ETA_MODEL_NAME)
            scaler_path = Config.get_scaler_path(Config.ETA_SCALER_NAME)
            
            if os.path.exists(model_path):
                self.model = joblib.load(model_path)
                logger.info("ETA model loaded successfully")
            else:
                logger.warning("ETA model not found, using fallback calculation")
            
            if os.path.exists(scaler_path):
                self.scaler = joblib.load(scaler_path)
                logger.info("ETA scaler loaded successfully")
        except Exception as e:
            logger.error(f"Error loading ETA model: {e}")
    
    def predict(self, distance_remaining, avg_speed, traffic_factor):
        """Predict ETA in minutes"""
        try:
            if self.model is not None and self.scaler is not None:
                # Prepare features
                features = np.array([[distance_remaining, avg_speed, traffic_factor]])
                
                # Scale features
                features_scaled = self.scaler.transform(features)
                
                # Predict
                eta_minutes = self.model.predict(features_scaled)[0]
                
                return max(0, eta_minutes)  # Ensure non-negative
            else:
                # Fallback calculation
                return self._fallback_calculation(distance_remaining, avg_speed, traffic_factor)
        except Exception as e:
            logger.error(f"ETA prediction error: {e}")
            return self._fallback_calculation(distance_remaining, avg_speed, traffic_factor)
    
    def _fallback_calculation(self, distance_remaining, avg_speed, traffic_factor):
        """Fallback ETA calculation when model is not available"""
        # Simple physics: time = distance / speed, adjusted for traffic
        if avg_speed <= 0:
            return 999.0  # Return large number if speed is invalid
        
        eta_hours = (distance_remaining / avg_speed) * traffic_factor
        eta_minutes = eta_hours * 60
        
        return max(0, eta_minutes)

