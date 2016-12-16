# Thaw project


### How to run vertx server

You must run Main method on RestServer class, with this JVM option

```java
--add-exports java.base/sun.nio.ch=ALL-UNNAMED
--add-exports java.base/sun.net.dns=ALL-UNNAMED
```

### Features

We have 3 requests actually <br>
* GET request for list all users <br>
* GET request for list all channels <br>
* PUT request for add new channel

### Bugs

If you using web browser or any script, you __must__ accept our certificate

This __method__ allow all useful requests for rest server
```java
private void allowRequest(Router router);
```

### Catch Exception for request method
We create Consumer which throws SQLException to catch them
