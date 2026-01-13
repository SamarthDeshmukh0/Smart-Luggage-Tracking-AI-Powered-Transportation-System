import joblib
import numpy as np

def test_eta_model():
    model = joblib.load("models/trained/eta_model.pkl")
    scaler = joblib.load("models/scalers/eta_scaler.pkl")

    sample = np.array([[100, 60, 1.2]])
    scaled = scaler.transform(sample)
    prediction = model.predict(scaled)

    assert prediction[0] > 0
