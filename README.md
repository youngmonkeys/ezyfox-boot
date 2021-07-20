

# ezyfox-boot
boot project for ezyfox

# How to use?

1. Add dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.tvd12</groupId>
    <artifactId>ezyfox-boot-autoconfigure</artifactId>
    <version>1.0.0</version>
</dependency>
```

2. Create and edit config file `src/main/resources/[FILE_NAME].properties`:

```properties
database.mongo.uri=mongodb://root:123456@127.0.0.1:27017/[DATABASE_NAME]
database.mongo.database=[DATABASE_NAME]
database.mongo.collection.naming.case=UNDERSCORE
database.mongo.collection.naming.ignored_suffix=Entity
```

**NOTE:** You can add above fields directly to `application.properties` file and skip Step 3. By default, `ezyfox-boot` automatically loads `application.properties`, so we don't need to specify the `@EzyPropertiesSources`

3. Add `@EzyPropertiesSources({"[FILE_NAME].properties"})` before the main class:
```java
@EzyPropertiesSources({"[FILE_NAME].properties"})  
public class ServerStartup {  
	...
    public static void main(String[] args) throws Exception {  
    }
}
```