class ValidationError(Exception):
    """Custom exception for validation errors"""
    pass


def validate_eta_request(data):
    required_fields = ["distance_km", "avg_speed_kmph"]

    for field in required_fields:
        if field not in data:
            raise ValidationError(f"Missing required field: {field}")

        if not isinstance(data[field], (int, float)):
            raise ValidationError(f"{field} must be a number")

        if data[field] <= 0:
            raise ValidationError(f"{field} must be greater than zero")

    return True


def validate_cost_request(data):
    required_fields = ["distance_km", "weight_kg", "category"]

    for field in required_fields:
        if field not in data:
            raise ValidationError(f"Missing required field: {field}")

    if data["distance_km"] <= 0:
        raise ValidationError("distance_km must be positive")

    if data["weight_kg"] <= 0:
        raise ValidationError("weight_kg must be positive")

    return True


def validate_anomaly_request(data):
    required_fields = [
        "distance_km",
        "avg_speed_kmph",
        "delay_minutes",
        "cost"
    ]

    for field in required_fields:
        if field not in data:
            raise ValidationError(f"Missing required field: {field}")

        if not isinstance(data[field], (int, float)):
            raise ValidationError(f"{field} must be numeric")

    return True
