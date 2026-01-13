CREATE DATABASE luggage_tracking;

use luggage_tracking;

-- Add Payment table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tracking_id VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    created_at DATETIME,
    completed_at DATETIME,
    INDEX idx_tracking_id (tracking_id),
    INDEX idx_user_id (user_id),
    INDEX idx_payment_status (payment_status)
);


-- ============================================
-- DATABASE MIGRATION SCRIPT
-- Add Persistent Tracking Fields to GPS Logs
-- ============================================

-- Add new columns to gps_logs table
ALTER TABLE gps_logs 
ADD COLUMN route_progress_percentage DOUBLE DEFAULT 0.0,
ADD COLUMN is_journey_complete BOOLEAN DEFAULT FALSE,
ADD COLUMN current_route_index INT DEFAULT 0,
ADD COLUMN estimated_total_distance DOUBLE;

-- Create index for faster queries
CREATE INDEX idx_tracking_completion ON gps_logs(tracking_id, is_journey_complete);
CREATE INDEX idx_tracking_progress ON gps_logs(tracking_id, route_progress_percentage);

-- Optional: Add comment to columns
ALTER TABLE gps_logs 
MODIFY COLUMN route_progress_percentage DOUBLE DEFAULT 0.0 COMMENT 'Journey completion percentage (0-100)',
MODIFY COLUMN is_journey_complete BOOLEAN DEFAULT FALSE COMMENT 'Whether journey has reached destination',
MODIFY COLUMN current_route_index INT DEFAULT 0 COMMENT 'Current point index in route array',
MODIFY COLUMN estimated_total_distance DOUBLE COMMENT 'Total estimated journey distance in km';

-- Verify the changes
DESCRIBE gps_logs;

-- Query to check existing data
SELECT 
    tracking_id,
    COUNT(*) as total_logs,
    MAX(is_journey_complete) as is_complete,
    MAX(route_progress_percentage) as max_progress
FROM gps_logs
GROUP BY tracking_id;