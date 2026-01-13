from flask import Flask
from flask_cors import CORS
from app.config.settings import Config
from app.utils.logger import logger

def create_app():
    """Application factory pattern"""
    app = Flask(__name__)
    
    # Enable CORS
    CORS(app, resources={r"/*": {"origins": "*"}})
    
    # Load configuration
    app.config.from_object(Config)
    
    # Register blueprints
    from app.routes.eta_routes import eta_bp
    from app.routes.cost_routes import cost_bp
    from app.routes.anomaly_routes import anomaly_bp
    
    app.register_blueprint(eta_bp, url_prefix='/predict')
    app.register_blueprint(cost_bp, url_prefix='/predict')
    app.register_blueprint(anomaly_bp, url_prefix='/detect')
    
    # Health check route
    @app.route('/health', methods=['GET'])
    def health_check():
        return {
            'status': 'healthy',
            'version': '1.0.0',
            'models_loaded': True
        }
    
    @app.route('/', methods=['GET'])
    def index():
        return {
            'service': 'Smart Luggage Tracking ML Service',
            'version': '1.0.0',
            'endpoints': {
                'eta_prediction': 'POST /predict/eta',
                'cost_prediction': 'POST /predict/cost',
                'anomaly_detection': 'POST /detect/anomaly',
                'health': 'GET /health'
            }
        }
    
    logger.info("Flask app created successfully")
    return app

