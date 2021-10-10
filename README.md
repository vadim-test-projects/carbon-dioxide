# carbon-dioxide
This is a project to process measurements from carbon-dioxide sensors

Please, find the postman collection in the project root to perform necessary reqeusts.

# Run

To run the project, simply run the main method of the TestApplication class

# DONE

1. Collect sensor measurements endpoint

# TODO

1. Sensor status endpoint
2. Sensor metrics endpoint
3. Listing alerts endpoint

# Future improvements:
1. Currently, number of consecutive critical measurements to go into the Alert states (or back to OK) is 3. 
It would be a better solution to move it to a property file and adapt code accordingly so that it can be dynamically changed.

2. Security

3. Logging

4. Localization of error messages
