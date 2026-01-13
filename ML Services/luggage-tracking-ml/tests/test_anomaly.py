import joblib
import numpy as np

def test_anomaly_model():
    model = joblib.load("models/trained/anomaly_model.joblib")

    sample = np.array([[5, 60, 10, 20]])
    prediction = model.predict(sample)

    assert prediction[0] in [-1, 1]
