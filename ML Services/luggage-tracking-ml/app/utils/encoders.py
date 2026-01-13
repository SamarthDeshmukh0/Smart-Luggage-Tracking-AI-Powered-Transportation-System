import numpy as np

class CategoryEncoder:
    """Encode categorical variables"""
    
    CATEGORY_MAPPING = {
        'normal': 0,
        'fragile': 1,
        'express': 2
    }
    
    @staticmethod
    def encode_luggage_type(luggage_type):
        """Encode luggage type to numeric"""
        return CategoryEncoder.CATEGORY_MAPPING.get(luggage_type.lower(), 0)
    
    @staticmethod
    def decode_luggage_type(encoded_value):
        """Decode numeric to luggage type"""
        reverse_mapping = {v: k for k, v in CategoryEncoder.CATEGORY_MAPPING.items()}
        return reverse_mapping.get(encoded_value, 'normal')

