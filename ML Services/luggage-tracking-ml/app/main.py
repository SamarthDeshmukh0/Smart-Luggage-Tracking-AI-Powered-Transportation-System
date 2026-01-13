from app import create_app
from app.config.settings import Config
from app.utils.logger import logger

app = create_app()

if __name__ == '__main__':
    logger.info(f"Starting ML Service on {Config.HOST}:{Config.PORT}")
    logger.info(f"Environment: {Config.FLASK_ENV}")
    app.run(
        host=Config.HOST,
        port=Config.PORT,
        debug=Config.DEBUG
    )

