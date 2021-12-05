# ezyfox-boot <img src="https://github.com/youngmonkeys/ezyfox-boot/blob/master/logo.png" width="64" />

boot project for ezyfox

# How to use?

1. Add dependency to your `pom.xml`:

```xml
<dependency>
	<groupId>com.tvd12</groupId>
	<artifactId>ezyfox-boot-autoconfigure</artifactId>
	<version>1.0.2</version>
</dependency>
```

2. Create and edit configuration file:

You can create `src/main/resources/[FILE_NAME].properties` file:

```properties
# for mongodb
database.mongo.uri=mongodb://root:123456@127.0.0.1:27017/[DATABASE_NAME]
database.mongo.database=[DATABASE_NAME]
database.mongo.collection.naming.case=UNDERSCORE
database.mongo.collection.naming.ignored_suffix=Entity

# for jpa
datasource.jdbcUrl=[your jdbc url, i.e jdbc:h2:mem:testdb]
datasource.driverClassName=[your driver class name, i.e org.h2.Driver]
datasource.username=[your db username]
datasource.password=[your db password]

# for redis
redis.uri=[your uri]
```

Or `src/main/resources/[FILE_NAME].yaml` file like this:

```yaml
# for mongodb
database:
    mongo:
    uri: mongodb://root:123456@127.0.0.1:27017/[DATABASE_NAME]
    database: [DATABASE_NAME]
    collection:
         naming.case: UNDERSCORE
         naming.ignored_suffix: Entity

# for jpa
datasource:
    jdbcUrl: [your jdbc url, i.e jdbc:h2:mem:testdb]
    driverClassName: [your driver class name, i.e org.h2.Driver]
    username: [your db username]
    password: [your db password]
    
# for redis
redis:
  uri: [your uri]
```

**NOTE:** You can add above fields directly to `application.properties` or `application.yaml` file and skip Step 3. By default, `ezyfox-boot` automatically loads `application.properties` and `application.yaml`, so we don't need to specify the `@EzyPropertiesSources`

3. Add `@EzyPropertiesSources({"[FILE_NAME].properties"})` before the main class:

```java
@EzyPropertiesSources({"[FILE_NAME].properties"})  
public class ServerStartup {  
	...
    public static void main(String[] args) throws Exception {  
    }
}
```
