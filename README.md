# carbon-dioxide
This is a project to process measurements from carbon-dioxide sensors

Please, find the postman collection in the project root to perform necessary reqeusts.

# Run

To run the project, simply run the main method of the TestApplication class

# DONE

Acceptance criteria:
1. The service should be able to receive measurements from each sensor at the rate of 1 per minute
2. If the CO2 level exceeds 2000 ppm the sensor status should be set to WARN
3. If the service receives 3 or more consecutive measurements higher than
2000 the sensor status should be set to ALERT
4. When the sensor reaches to status ALERT an alert should be stored
5. When the sensor reaches to status ALERT it stays in this state until it receives 3
consecutive measurements lower than 2000; then it moves to OK

Endpoints:
1. Collect sensor measurements endpoint

# TODO

Acceptance criteria:
1. The service should provide the following metrics about each sensor: average and maximum CO2 levels for the past 30 days
2. It is possible to list all the alerts for a given sensor

Endpoints:
1. Sensor status endpoint
2. Sensor metrics endpoint
3. Listing alerts endpoint

# Future improvements:
1. Currently, the number of consecutive critical measurements to go into the Alert states (or non-critical ones to go back to OK) is 3.
It would be a better solution to move it to a property file and adapt code accordingly so that it can be dynamically changed.

2. Security

3. Logging

4. Localization of error messages
