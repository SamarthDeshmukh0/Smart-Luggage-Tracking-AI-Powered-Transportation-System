import joblib
import numpy as np
from app.config.settings import Config
from app.utils.logger import logger
from app.utils.encoders import CategoryEncoder
import os

class CostPredictor:
    """Cost Prediction Model"""
    
    def __init__(self):
        self.model = None
        self.scaler = None
        self.load_model()
    
    def load_model(self):
        """Load trained model and scaler"""
        try:
            model_path = Config.get_model_path(Config.COST_MODEL_NAME)
            scaler_path = Config.get_scaler_path(Config.COST_SCALER_NAME)
            
            if os.path.exists(model_path):
                self.model = joblib.load(model_path)
                logger.info("Cost model loaded successfully")
            else:
                logger.warning("Cost model not found, using fallback calculation")
            
            if os.path.exists(scaler_path):
                self.scaler = joblib.load(scaler_path)
                logger.info("Cost scaler loaded successfully")
        except Exception as e:
            logger.error(f"Error loading cost model: {e}")
    
    def predict(self, distance_km, weight_kg, luggage_type, speed_pattern=50.0, traffic_factor=1.0):
        """Predict transportation cost"""
        try:
            if self.model is not None and self.scaler is not None:
                # Encode category
                category_encoded = CategoryEncoder.encode_luggage_type(luggage_type)
                
                # Prepare features
                features = np.array([[
                    distance_km,
                    weight_kg,
                    category_encoded,
                    speed_pattern,
                    traffic_factor
                ]])
                
                # Scale features
                features_scaled = self.scaler.transform(features)
                
                # Predict
                predicted_cost = self.model.predict(features_scaled)[0]
                
                return max(0, predicted_cost)  # Ensure non-negative
            else:
                # Fallback calculation
                return self._fallback_calculation(distance_km, weight_kg, luggage_type, traffic_factor)
        except Exception as e:
            logger.error(f"Cost prediction error: {e}")
            return self._fallback_calculation(distance_km, weight_kg, luggage_type, traffic_factor)
    
    def _fallback_calculation(self, distance_km, weight_kg, luggage_type, traffic_factor):
        """Fallback cost calculation when model is not available"""
        base_fare = Config.BASE_FARE
        distance_charge = distance_km * Config.PRICE_PER_KM
        weight_charge = weight_kg * Config.PRICE_PER_KG
        
        # Get category multiplier
        category_multiplier = Config.CATEGORY_MULTIPLIERS.get(luggage_type.lower(), 1.0)
        
        # Calculate total
        total_cost = (base_fare + distance_charge + weight_charge) * category_multiplier * traffic_factor
        
        return max(0, total_cost)

