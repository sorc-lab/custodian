# custodian
Command line interface for scheduling and tracking cleaning tasks.

# Install
```
# install both client and server modules and run tests
$ mvn clean install -U

# run client jar (version may be different)
$ java -jar .\client\target\custodian-client-0.0.1-SNAPSHOT.jar

# run server jar
$ cd server
$ mvn spring-boot:run
```

# Features/TODO List
- Add remove and edit cleaning tasks.
- Tasks can be added/removed/edited by id or label. Labels must be unique.
- Tasks can be marked as completed, causing the timer to reset.
- Tasks completed, or within a duration window are marked green.
- Tasks that have expired their duration are set to red, and the timer is not reset.
- Tasks that are within a fraction of days until expiry are set to yellow or orange.
- Ability to list tasks and see their colors, labels, and descriptions
- Ability to view an individual task to see its id, label, description and set duration timer.
- Ability to edit any of the fields, except the id.
- CLI home screen will list notifications/alerts. Soon to expire tasks and expired tasks.
- Can also list notifications with its own command to see same thing as printed in home screen alerts.
