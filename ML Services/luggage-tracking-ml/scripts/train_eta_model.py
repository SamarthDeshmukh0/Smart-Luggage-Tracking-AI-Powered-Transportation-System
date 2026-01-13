import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score
import joblib
import os

def train_eta_model():
    """Train ETA prediction model"""
    print("=" * 50)
    print("Training ETA Prediction Model")
    print("=" * 50)
    
    # Load training data
    print("\n1. Loading training data...")
    df = pd.read_csv('data/training/eta_training_data.csv')
    print(f"   Loaded {len(df)} samples")
    
    # Prepare features and target
    X = df[['distance_remaining', 'avg_speed', 'traffic_factor']]
    y = df['actual_eta_minutes']
    
    # Split data
    print("\n2. Splitting data (80% train, 20% test)...")
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )
    print(f"   Training samples: {len(X_train)}")
    print(f"   Test samples: {len(X_test)}")
    
    # Scale features
    print("\n3. Scaling features...")
    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)
    X_test_scaled = scaler.transform(X_test)
    
    # Train model
    print("\n4. Training Random Forest model...")
    model = RandomForestRegressor(
        n_estimators=100,
        max_depth=10,
        random_state=42,
        n_jobs=-1
    )
    model.fit(X_train_scaled, y_train)
    print("   ✅ Model trained")
    
    # Evaluate
    print("\n5. Evaluating model...")
    y_pred = model.predict(X_test_scaled)
    
    mae = mean_absolute_error(y_test, y_pred)
    rmse = np.sqrt(mean_squared_error(y_test, y_pred))
    r2 = r2_score(y_test, y_pred)
    
    print(f"   MAE:  {mae:.2f} minutes")
    print(f"   RMSE: {rmse:.2f} minutes")
    print(f"   R² Score: {r2:.4f}")
    
    # Feature importance
    print("\n6. Feature importance:")
    for feature, importance in zip(X.columns, model.feature_importances_):
        print(f"   {feature}: {importance:.4f}")
    
    # Save model and scaler
    print("\n7. Saving model and scaler...")
    os.makedirs('models/trained', exist_ok=True)
    os.makedirs('models/scalers', exist_ok=True)
    
    joblib.dump(model, 'models/trained/eta_model.pkl')
    joblib.dump(scaler, 'models/scalers/eta_scaler.pkl')
    print("   ✅ Model saved: models/trained/eta_model.pkl")
    print("   ✅ Scaler saved: models/scalers/eta_scaler.pkl")
    
    print("\n✅ ETA model training completed!")
    return model, scaler

if __name__ == '__main__':
    train_eta_model()
