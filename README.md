# Jogging Tracker

## How to build and execute
Execute `mvn spring-boot:run` to start the application. Access its UI client with `http://localhost:8090`.

## Requirements
- User must be able to create an account and log in
- When logged in, user can see, edit and delete his times he entered
- Implement at least two roles with different permission levels (ie: a regular user would only be able to CRUD on his owned records, a user manager would be able to CRUD users, an admin would be able to CRUD on all records and users, etc.)
- Each time entry when entered has a date, distance, and time
- When displayed, each time entry has an average speed
- Filter by dates from-to
- Report on average speed & distance per week
- REST API. Make it possible to perform all user actions via the API, including authentication
- In any case you should be able to explain how a REST API works and demonstrate that by creating functional tests that use the REST Layer directly
- All actions need to be done client side using AJAX, refreshing the page is not acceptable
- Bonus: unit and e2e tests!
- You will not be marked on graphic design, however, do try to keep it as tidy as possible
