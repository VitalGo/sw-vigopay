# Payment Service (ViGoPay)

This is a project that was developed during the software development course at the OTH Regensburg.

## Testing

1. Install Banking Service library JAR into the root directory<br>
```
mvn install:install-file -Dfile=src/main/resources/banking_source.jar -DgroupId=de.othr.sw -DartifactId=bank -Dversion=0.0.1SNAPSHOT -Dpackaging=jar
```

2. Edit the [application.properties](src/main/resources/application.properties)
- ``spring.datasource.url=``
- ``spring.datasource.username=``
- ``spring.datasource.password=``
- ``password-salt=``
- ``vigopay-iban=``


3. Run application<br>
```
mvn spring-boot:run
```
