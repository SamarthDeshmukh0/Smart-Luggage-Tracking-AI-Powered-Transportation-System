import numpy as np
from app.config.settings import Config
from app.utils.logger import logger

class AnomalyDetector:
    """Rule-based Anomaly Detection"""
    
    def __init__(self):
        self.min_speed = Config.MIN_SPEED
        self.max_speed = Config.MAX_SPEED
        self.deviation_threshold = Config.DEVIATION_THRESHOLD
    
    def detect(self, speed, latitude=None, longitude=None, 
               expected_direction=None, current_direction=None,
               last_update_time=None):
        """Detect anomalies in GPS data"""
        anomalies = []
        
        # Check if not moving
        if speed < self.min_speed:
            anomalies.append({
                'type': 'NOT_MOVING',
                'message': f'Luggage not moving (speed: {speed:.1f} km/h)',
                'severity': 'MEDIUM'
            })
        
        # Check for abnormal speed
        if speed > self.max_speed:
            anomalies.append({
                'type': 'ABNORMAL_SPEED',
                'message': f'Abnormal speed detected ({speed:.1f} km/h)',
                'severity': 'HIGH'
            })
        
        # Check for route deviation
        if expected_direction is not None and current_direction is not None:
            direction_diff = abs(expected_direction - current_direction)
            # Normalize to 0-180 range
            direction_diff = min(direction_diff, 360 - direction_diff)
            
            if direction_diff > self.deviation_threshold:
                anomalies.append({
                    'type': 'ROUTE_DEVIATION',
                    'message': f'Route deviation detected ({direction_diff:.1f}° off course)',
                    'severity': 'HIGH'
                })
        
        # Return result
        if anomalies:
            return {
                'anomaly': True,
                'anomalies': anomalies,
                'message': anomalies[0]['message'],  # Primary anomaly
                'severity': anomalies[0]['severity']
            }
        else:
            return {
                'anomaly': False,
                'message': 'Normal operation',
                'severity': 'NONE'
            }

