# FriendGuard
## User Guide
### Register (From User Side)
User Name: email
Password: any string longer than 6
  All the User's friends, who have registered this app, can receive a notification requesting for FriendGuard, once the user is logged in.

### Set Up a New Session (From User Side)
#### select startTime, endTime, activeCheckInInterval
#### Press StartSessioin Button 
passiveCheckInInterval is hard coded right now

### Confirm Invitation for a new Session (From Friend Side)
#### Once the User start new session, the friends will receive a notification, asking them to confirm being as the FriendGuard in the new session.
#### The Friend can accept and press "Confirm" button. This will pass the session_id generated by the Server to the User

### Get notification for Accept Session (From User Side)
#### Once the user press "Confirm" in this notification, the "session_id" is successfully shared between User and Friends

### Sent Alert (From User Side)
#### By pressing the alert button in Map View, and a notification will be sent to Friends

### Receive Map (From Friend Side)
#### Once Friends receive alert notification from User, they can see where the User sends alert on the Map View by clicking notification.

### Session End (From User Side)
#### If User safely arrives the destination, hit Exit on the Map View and the whole session finishes.



