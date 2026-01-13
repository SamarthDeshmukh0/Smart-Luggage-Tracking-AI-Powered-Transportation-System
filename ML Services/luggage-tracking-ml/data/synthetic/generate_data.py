import pandas as pd
import numpy as np
import os

def generate_eta_training_data(n_samples=1000):
    """Generate synthetic ETA training data"""
    print(f"Generating {n_samples} ETA training samples...")
    
    np.random.seed(42)
    
    data = []
    for _ in range(n_samples):
        distance_remaining = np.random.uniform(1, 500)  # 1-500 km
        avg_speed = np.random.uniform(20, 100)  # 20-100 km/h
        traffic_factor = np.random.uniform(0.5, 2.0)  # 0.5-2.0
        
        # Calculate actual ETA (ground truth)
        # time = distance / speed, adjusted for traffic
        eta_hours = (distance_remaining / avg_speed) * traffic_factor
        eta_minutes = eta_hours * 60
        
        # Add some noise
        noise = np.random.normal(0, 2)  # Mean=0, StdDev=2 minutes
        eta_minutes += noise
        eta_minutes = max(0, eta_minutes)  # Ensure non-negative
        
        data.append({
            'distance_remaining': distance_remaining,
            'avg_speed': avg_speed,
            'traffic_factor': traffic_factor,
            'actual_eta_minutes': eta_minutes
        })
    
    df = pd.DataFrame(data)
    
    # Create directory if not exists
    os.makedirs('data/training', exist_ok=True)
    
    # Save to CSV
    df.to_csv('data/training/eta_training_data.csv', index=False)
    print(f"✅ ETA training data saved: {len(df)} samples")
    print(f"   Average ETA: {df['actual_eta_minutes'].mean():.2f} minutes")
    
    return df

def generate_cost_training_data(n_samples=1000):
    """Generate synthetic cost training data"""
    print(f"Generating {n_samples} cost training samples...")
    
    np.random.seed(42)
    
    # Pricing constants
    BASE_FARE = 50.0
    PRICE_PER_KM = 10.0
    PRICE_PER_KG = 20.0
    CATEGORY_MULTIPLIERS = {'normal': 1.0, 'fragile': 1.2, 'express': 1.5}
    
    data = []
    for _ in range(n_samples):
        distance_km = np.random.uniform(10, 500)  # 10-500 km
        weight_kg = np.random.uniform(1, 50)  # 1-50 kg
        luggage_type = np.random.choice(['normal', 'fragile', 'express'])
        speed_pattern = np.random.uniform(30, 80)  # 30-80 km/h
        traffic_factor = np.random.uniform(0.5, 1.5)  # 0.5-1.5
        
        # Calculate cost (ground truth)
        category_multiplier = CATEGORY_MULTIPLIERS[luggage_type]
        
        distance_charge = distance_km * PRICE_PER_KM
        weight_charge = weight_kg * PRICE_PER_KG
        
        cost = (BASE_FARE + distance_charge + weight_charge) * category_multiplier * traffic_factor
        
        # Add some noise
        noise = np.random.normal(0, 20)  # Mean=0, StdDev=20 rupees
        cost += noise
        cost = max(0, cost)  # Ensure non-negative
        
        # Encode category
        category_encoded = {'normal': 0, 'fragile': 1, 'express': 2}[luggage_type]
        
        data.append({
            'distance_km': distance_km,
            'weight_kg': weight_kg,
            'category_encoded': category_encoded,
            'luggage_type': luggage_type,
            'speed_pattern': speed_pattern,
            'traffic_factor': traffic_factor,
            'cost': cost
        })
    
    df = pd.DataFrame(data)
    
    # Create directory if not exists
    os.makedirs('data/training', exist_ok=True)
    
    # Save to CSV
    df.to_csv('data/training/cost_training_data.csv', index=False)
    print(f"✅ Cost training data saved: {len(df)} samples")
    print(f"   Average cost: ₹{df['cost'].mean():.2f}")
    print(f"   Category distribution:")
    print(df['luggage_type'].value_counts())
    
    return df

if __name__ == '__main__':
    print("=" * 50)
    print("Generating Synthetic Training Data")
    print("=" * 50)
    
    # Generate ETA data
    eta_df = generate_eta_training_data(1000)
    
    # Generate cost data
    cost_df = generate_cost_training_data(1000)
    
    print("\n✅ All training data generated successfully!")

