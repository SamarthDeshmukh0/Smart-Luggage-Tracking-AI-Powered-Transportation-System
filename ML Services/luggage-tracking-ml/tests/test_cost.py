import joblib
import numpy as np

def test_cost_model():
    model = joblib.load("models/trained/cost_model.pkl")
    scaler = joblib.load("models/scalers/cost_scaler.pkl")

    sample = np.array([[150, 20, 1, 50, 1.0]])
    scaled = scaler.transform(sample)
    prediction = model.predict(scaled)

    assert prediction[0] > 0
