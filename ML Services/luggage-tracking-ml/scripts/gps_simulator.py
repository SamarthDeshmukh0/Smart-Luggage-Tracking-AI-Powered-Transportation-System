"""
GPS Tracking Simulator
Simulates real-time GPS tracking and sends data to Spring Boot backend
Usage: python scripts/gps_simulator.py <tracking_id> <source_coords> <dest_coords>
Example: python scripts/gps_simulator.py TRK123456 "19.0760,72.8777" "18.5204,73.8567"
"""

import requests
import time
import math
import sys
from datetime import datetime

class GPSSimulator:
    """Simulates GPS tracking for luggage"""
    
    def __init__(self, tracking_id, start_coords, end_coords, spring_boot_url="http://localhost:8080"):
        self.tracking_id = tracking_id
        self.start_lat, self.start_lon = start_coords
        self.end_lat, self.end_lon = end_coords
        self.spring_boot_url = spring_boot_url
        self.api_endpoint = f"{spring_boot_url}/api/tracking/update"
        
        # Simulation parameters
        self.update_interval = 0.5  # seconds between updates
        self.total_duration = 15  # total journey time in seconds
        self.total_points = int(self.total_duration / self.update_interval)
        
        print("=" * 60)
        print("GPS SIMULATOR INITIALIZED")
        print("=" * 60)
        print(f"Tracking ID: {self.tracking_id}")
        print(f"Start: ({self.start_lat:.4f}, {self.start_lon:.4f})")
        print(f"End: ({self.end_lat:.4f}, {self.end_lon:.4f})")
        print(f"Spring Boot API: {self.api_endpoint}")
        print(f"Total journey time: {self.total_duration}s")
        print(f"Update interval: {self.update_interval}s")
        print(f"Total GPS points: {self.total_points}")
        print("=" * 60)
    
    def calculate_distance(self, lat1, lon1, lat2, lon2):
        """Calculate distance between two GPS points (Haversine formula)"""
        R = 6371.0  # Earth radius in km
        
        lat1_rad = math.radians(lat1)
        lat2_rad = math.radians(lat2)
        dLat = math.radians(lat2 - lat1)
        dLon = math.radians(lon2 - lon1)
        
        a = math.sin(dLat/2) ** 2 + \
            math.cos(lat1_rad) * math.cos(lat2_rad) * math.sin(dLon/2) ** 2
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        
        return R * c
    
    def generate_route_points(self):
        """Generate interpolated GPS points along the route"""
        points = []
        for i in range(self.total_points + 1):
            ratio = i / self.total_points
            
            # Linear interpolation
            lat = self.start_lat + (self.end_lat - self.start_lat) * ratio
            lon = self.start_lon + (self.end_lon - self.start_lon) * ratio
            
            # Add small random variation to simulate realistic GPS
            lat += (math.sin(i * 0.1) * 0.0001)  # ~10m variation
            lon += (math.cos(i * 0.1) * 0.0001)
            
            points.append((lat, lon))
        
        return points
    
    def calculate_speed(self, prev_point, curr_point, time_delta):
        """Calculate speed between two points"""
        if prev_point is None:
            return 0.0
        
        distance_km = self.calculate_distance(
            prev_point[0], prev_point[1],
            curr_point[0], curr_point[1]
        )
        
        time_hours = time_delta / 3600.0
        if time_hours == 0:
            return 0.0
        
        speed = distance_km / time_hours
        
        # Add realistic variation
        speed += (math.sin(time.time()) * 5)  # ±5 km/h variation
        return max(0, speed)
    
    def send_gps_data(self, latitude, longitude, speed):
        """Send GPS data to Spring Boot backend"""
        payload = {
            "tracking_id": self.tracking_id,
            "latitude": latitude,
            "longitude": longitude,
            "speed": speed,
            "timestamp": datetime.utcnow().isoformat() + "Z"
        }
        
        try:
            response = requests.post(
                self.api_endpoint,
                json=payload,
                headers={"Content-Type": "application/json"},
                timeout=5
            )
            
            if response.status_code == 200:
                return True, response.json()
            else:
                return False, f"HTTP {response.status_code}"
        except requests.exceptions.RequestException as e:
            return False, str(e)
    
    def simulate(self):
        """Run the GPS simulation"""
        print("\n🚀 Starting GPS simulation...\n")
        
        route_points = self.generate_route_points()
        total_distance = self.calculate_distance(
            self.start_lat, self.start_lon,
            self.end_lat, self.end_lon
        )
        
        print(f"Total route distance: {total_distance:.2f} km")
        print(f"Average speed: {(total_distance / (self.total_duration / 3600)):.2f} km/h\n")
        
        prev_point = None
        success_count = 0
        failure_count = 0
        
        for i, (lat, lon) in enumerate(route_points):
            # Calculate remaining distance
            remaining_distance = self.calculate_distance(
                lat, lon, self.end_lat, self.end_lon
            )
            
            # Calculate speed
            speed = self.calculate_speed(prev_point, (lat, lon), self.update_interval)
            
            # Send to backend
            success, response = self.send_gps_data(lat, lon, speed)
            
            # Progress
            progress = (i / self.total_points) * 100
            status = "✅" if success else "❌"
            
            print(f"{status} [{i+1}/{self.total_points+1}] "
                  f"Progress: {progress:5.1f}% | "
                  f"Pos: ({lat:.4f}, {lon:.4f}) | "
                  f"Speed: {speed:5.1f} km/h | "
                  f"Remaining: {remaining_distance:6.2f} km")
            
            if success:
                success_count += 1
                # Check for anomalies in response
                if isinstance(response, dict) and response.get('anomalyDetected'):
                    print(f"   ⚠️  ANOMALY: {response.get('anomalyMessage', 'Unknown')}")
            else:
                failure_count += 1
                print(f"   ⚠️  Error: {response}")
            
            prev_point = (lat, lon)
            
            # Wait before next update
            if i < len(route_points) - 1:
                time.sleep(self.update_interval)
        
        # Summary
        print("\n" + "=" * 60)
        print("SIMULATION COMPLETED")
        print("=" * 60)
        print(f"Total points sent: {self.total_points + 1}")
        print(f"Successful: {success_count}")
        print(f"Failed: {failure_count}")
        print(f"Success rate: {(success_count / (self.total_points + 1) * 100):.1f}%")
        print(f"Total distance: {total_distance:.2f} km")
        print(f"Journey time: {self.total_duration} seconds")
        print("=" * 60)

def parse_coordinates(coord_string):
    """Parse coordinate string 'lat,lon' to tuple"""
    try:
        lat, lon = coord_string.split(',')
        return float(lat.strip()), float(lon.strip())
    except Exception as e:
        raise ValueError(f"Invalid coordinate format: {coord_string}. Use 'lat,lon'")

# Popular Indian city coordinates
INDIAN_CITIES = {
    'mumbai': (19.0760, 72.8777),
    'pune': (18.5204, 73.8567),
    'delhi': (28.7041, 77.1025),
    'bangalore': (12.9716, 77.5946),
    'chennai': (13.0827, 80.2707),
    'kolkata': (22.5726, 88.3639),
    'hyderabad': (17.3850, 78.4867),
    'ahmedabad': (23.0225, 72.5714),
    'jaipur': (26.9124, 75.7873),
    'lucknow': (26.8467, 80.9462),
    'nagpur': (21.1458, 79.0882),
    'indore': (22.7196, 75.8577),
    'bhopal': (23.2599, 77.4126),
    'chandigarh': (30.7333, 76.7794),
    'kochi': (9.9312, 76.2673)
}

def get_coordinates(location):
    """Get coordinates from city name or coordinate string"""
    location_lower = location.lower().strip()
    
    # Check if it's a known city
    if location_lower in INDIAN_CITIES:
        return INDIAN_CITIES[location_lower]
    
    # Try to parse as coordinates
    return parse_coordinates(location)

def main():
    """Main function"""
    # Default values
    tracking_id = "TRK" + str(int(time.time()))
    source = "mumbai"
    destination = "pune"
    spring_boot_url = "http://localhost:8080"
    
    # Parse command line arguments
    if len(sys.argv) >= 2:
        tracking_id = sys.argv[1]
    if len(sys.argv) >= 3:
        source = sys.argv[2]
    if len(sys.argv) >= 4:
        destination = sys.argv[3]
    if len(sys.argv) >= 5:
        spring_boot_url = sys.argv[4]
    
    try:
        # Get coordinates
        start_coords = get_coordinates(source)
        end_coords = get_coordinates(destination)
        
        # Create simulator
        simulator = GPSSimulator(
            tracking_id=tracking_id,
            start_coords=start_coords,
            end_coords=end_coords,
            spring_boot_url=spring_boot_url
        )
        
        # Run simulation
        simulator.simulate()
        
    except ValueError as e:
        print(f"\n❌ Error: {e}")
        print("\nUsage:")
        print(f"  python {sys.argv[0]} <tracking_id> <source> <destination> [spring_boot_url]")
        print("\nExamples:")
        print(f"  python {sys.argv[0]} TRK123 mumbai pune")
        print(f"  python {sys.argv[0]} TRK456 delhi bangalore http://localhost:8080")
        print(f"  python {sys.argv[0]} TRK789 \"19.0760,72.8777\" \"18.5204,73.8567\"")
        print("\nSupported cities:")
        for city in sorted(INDIAN_CITIES.keys()):
            coords = INDIAN_CITIES[city]
            print(f"  - {city.title()}: ({coords[0]:.4f}, {coords[1]:.4f})")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Unexpected error: {e}")
        sys.exit(1)

if __name__ == '__main__':
    main()

