# Payment Service (ViGoPay)

This is a project that was developed during the software development course at the OTH Regensburg.

## Testing

1. Install Banking Service library JAR into the root directory<br>
```
mvn install:install-file -Dfile=src/main/resources/banking_source.jar -DgroupId=de.othr.sw -DartifactId=bank -Dversion=0.0.1SNAPSHOT -Dpackaging=jar
```

2. Set environment variables or edit [application-local.properties](./../application-local.properties)

3. Build Jar<br>
```
mvn package
```

4. Run application<br>
```
mvn spring-boot:run
```






