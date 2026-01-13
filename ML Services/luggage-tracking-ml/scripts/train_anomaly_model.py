import pandas as pd
from sklearn.ensemble import IsolationForest
import joblib
import os

def train_anomaly_model():
    print("=" * 50)
    print("Training Anomaly Detection Model")
    print("=" * 50)

    # Load data
    df = pd.read_csv("data/training/anomaly_training_data.csv")

    X = df[
        ["avg_speed", "delay_minutes", "route_deviation_km", "stop_duration_min"]
    ]

    # Train Isolation Forest
    model = IsolationForest(
        n_estimators=100,
        contamination=0.3,
        random_state=42
    )

    model.fit(X)

    # Save model
    os.makedirs("models/trained", exist_ok=True)
    joblib.dump(model, "models/trained/anomaly_model.joblib")

    print("✅ Anomaly model trained and saved")

if __name__ == "__main__":
    train_anomaly_model()
