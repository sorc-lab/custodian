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

# API Notes
```
/* --- CREATE TASK ----------------------------------------------------
1. get task timer duration via getTimerDurationDays * 24hrs in
seconds. Might consider putting this in a util? Unit test this calc.
2. SET expiration of the task
IF Task.getCreatedAt is not null, ie it got set
store expiration as date created in SECONDS
ELSE
set creation time to NOW in seconds
3. Create the new task object
- set label via input label
- set the description via input
- set createdAt if not already set. again if not set already, set to NOW in seconds
- set the timer duration based on input DTO
- set status hardcoded to NEW for all new created items
4. save the Task via DAL repo interface
5. save the task to filesystem
- TODO: Fill this out. a lot goes on in here.
-> Update all task status. routine for when any new taks is created, it updates ALL other tasks.
* get current time
* get ALL tasks in DATABASE (important)
* loops tasks
-> get expiration date for task in list
-> check IF current time is equal to expiration date OR curr time is after exp date
-> IF expired, sets status to EXPIRED and saves the task to repo with new status.
-> After updating ALL tasks status to expired if needed, proceeds on to process.
-> Again, it finds ALL tasks. Note, this happens twice now for all task creation. lookup twice.
-> Takes all tasks and converts their Java objects to one large json string.
-> Uses file util to make file path to json file.
-> Uses file util to write the json file to disk.

// NOTES: Weird things found so far:
//  - Sets expiration based on if createdAt already set? How would it already be set?
//  - Again newTask sets the createdAt and checks if already has createdAt value. How?
//  - Create task method has hacks, multi-db reads and syncing mechanisms in place. Many side effects.
*/
```
