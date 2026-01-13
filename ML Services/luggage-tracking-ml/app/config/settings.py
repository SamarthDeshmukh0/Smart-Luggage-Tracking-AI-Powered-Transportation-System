import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    """Application configuration"""
    
    # Flask
    FLASK_ENV = os.getenv('FLASK_ENV', 'development')
    DEBUG = FLASK_ENV == 'development'
    PORT = int(os.getenv('PORT', 5000))
    HOST = '0.0.0.0'
    
    # Model paths
    MODEL_PATH = os.getenv('MODEL_PATH', 'models/trained')
    SCALER_PATH = 'models/scalers'
    
    # Logging
    LOG_LEVEL = os.getenv('LOG_LEVEL', 'INFO')
    
    # External services
    SPRING_BOOT_URL = os.getenv('SPRING_BOOT_URL', 'http://localhost:8080')
    
    # Model configuration
    ETA_MODEL_NAME = 'eta_model.pkl'
    COST_MODEL_NAME = 'cost_model.pkl'
    ETA_SCALER_NAME = 'eta_scaler.pkl'
    COST_SCALER_NAME = 'cost_scaler.pkl'
    
    # Prediction parameters
    DEFAULT_TRAFFIC_FACTOR = 1.0
    MIN_SPEED = 5.0  # km/h - below this is considered not moving
    MAX_SPEED = 120.0  # km/h - above this is considered abnormal
    DEVIATION_THRESHOLD = 45.0  # degrees
    
    # Pricing constants
    BASE_FARE = 50.0
    PRICE_PER_KM = 10.0
    PRICE_PER_KG = 20.0
    
    CATEGORY_MULTIPLIERS = {
        'normal': 1.0,
        'fragile': 1.2,
        'express': 1.5
    }
    
    @staticmethod
    def get_model_path(model_name):
        return os.path.join(Config.MODEL_PATH, model_name)
    
    @staticmethod
    def get_scaler_path(scaler_name):
        return os.path.join(Config.SCALER_PATH, scaler_name)

