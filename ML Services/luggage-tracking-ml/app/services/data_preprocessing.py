import numpy as np
from app.utils.logger import logger

class DataPreprocessor:
    """Data preprocessing utilities"""
    
    @staticmethod
    def normalize_speed(speed):
        """Normalize speed to reasonable range"""
        return max(0, min(speed, 200))  # 0-200 km/h
    
    @staticmethod
    def normalize_distance(distance):
        """Normalize distance"""
        return max(0, min(distance, 2000))  # 0-2000 km (max in India)
    
    @staticmethod
    def normalize_weight(weight):
        """Normalize weight"""
        return max(0.1, min(weight, 100))  # 0.1-100 kg
    
    @staticmethod
    def normalize_traffic_factor(traffic_factor):
        """Normalize traffic factor"""
        return max(0.5, min(traffic_factor, 3.0))  # 0.5-3.0
    
    @staticmethod
    def calculate_direction(lat1, lon1, lat2, lon2):
        """Calculate bearing/direction between two GPS points"""
        # Convert to radians
        lat1_rad = np.radians(lat1)
        lat2_rad = np.radians(lat2)
        lon_diff_rad = np.radians(lon2 - lon1)
        
        # Calculate bearing
        x = np.sin(lon_diff_rad) * np.cos(lat2_rad)
        y = np.cos(lat1_rad) * np.sin(lat2_rad) - \
            np.sin(lat1_rad) * np.cos(lat2_rad) * np.cos(lon_diff_rad)
        
        bearing = np.degrees(np.arctan2(x, y))
        
        # Normalize to 0-360
        bearing = (bearing + 360) % 360
        
        return bearing
    
    @staticmethod
    def calculate_distance(lat1, lon1, lat2, lon2):
        """Calculate distance between two GPS points (Haversine formula)"""
        R = 6371.0  # Earth radius in km
        
        lat1_rad = np.radians(lat1)
        lat2_rad = np.radians(lat2)
        dLat = np.radians(lat2 - lat1)
        dLon = np.radians(lon2 - lon1)
        
        a = np.sin(dLat/2) ** 2 + \
            np.cos(lat1_rad) * np.cos(lat2_rad) * np.sin(dLon/2) ** 2
        c = 2 * np.arctan2(np.sqrt(a), np.sqrt(1-a))
        
        distance = R * c
        return distance




