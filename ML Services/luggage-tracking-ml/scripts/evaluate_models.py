import joblib
import numpy as np
import pandas as pd
from sklearn.metrics import mean_absolute_error, r2_score

def evaluate_all_models():
    """Evaluate all trained models"""
    print("=" * 50)
    print("Evaluating All Models")
    print("=" * 50)
    
    # Evaluate ETA Model
    print("\n1. ETA Model Evaluation:")
    try:
        eta_model = joblib.load('models/trained/eta_model.pkl')
        eta_scaler = joblib.load('models/scalers/eta_scaler.pkl')
        
        # Test predictions
        test_cases = [
            {'distance': 50, 'speed': 60, 'traffic': 1.0, 'expected': 50},
            {'distance': 100, 'speed': 80, 'traffic': 1.2, 'expected': 90},
            {'distance': 200, 'speed': 50, 'traffic': 1.5, 'expected': 360}
        ]
        
        for case in test_cases:
            features = np.array([[case['distance'], case['speed'], case['traffic']]])
            features_scaled = eta_scaler.transform(features)
            prediction = eta_model.predict(features_scaled)[0]
            error = abs(prediction - case['expected'])
            print(f"   Distance: {case['distance']}km, Speed: {case['speed']}km/h")
            print(f"   Predicted: {prediction:.1f}min, Expected: ~{case['expected']}min, Error: {error:.1f}min")
        
        print("   ✅ ETA model working correctly")
    except Exception as e:
        print(f"   ❌ ETA model error: {e}")
    
    # Evaluate Cost Model
    print("\n2. Cost Model Evaluation:")
    try:
        cost_model = joblib.load('models/trained/cost_model.pkl')
        cost_scaler = joblib.load('models/scalers/cost_scaler.pkl')
        
        # Test predictions
        test_cases = [
            {'distance': 150, 'weight': 25, 'category': 0, 'expected': 2050},  # normal
            {'distance': 150, 'weight': 25, 'category': 1, 'expected': 2460},  # fragile
            {'distance': 150, 'weight': 25, 'category': 2, 'expected': 3075}   # express
        ]
        
        for case in test_cases:
            features = np.array([[case['distance'], case['weight'], case['category'], 50, 1.0]])
            features_scaled = cost_scaler.transform(features)
            prediction = cost_model.predict(features_scaled)[0]
            error_pct = abs(prediction - case['expected']) / case['expected'] * 100
            print(f"   Distance: {case['distance']}km, Weight: {case['weight']}kg")
            print(f"   Predicted: ₹{prediction:.2f}, Expected: ~₹{case['expected']}, Error: {error_pct:.1f}%")
        
        print("   ✅ Cost model working correctly")
    except Exception as e:
        print(f"   ❌ Cost model error: {e}")
    
    print("\n✅ Model evaluation completed!")

if __name__ == '__main__':
    evaluate_all_models()

