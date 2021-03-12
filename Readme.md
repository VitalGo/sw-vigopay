# Payment Service (ViGoPay)

This is a project that was developed during the software development course at the OTH Regensburg.

## Testing

1. Clone repository<br>
``
git clone git@github.com:VitalGo/OTH-SW-ViGoPay
``

2. Change directory<br>
``
cd OTH-SW-ViGoPay
``

3. Install Banking Service library JAR<br>
```
mvn install:install-file -Dfile=src/main/resources/banking_source.jar -DgroupId=de.othr.sw -DartifactId=bank -Dversion=0.0.1SNAPSHOT -Dpackaging=jar
```

4. Set environment variables or edit [application-local.properties](./../application-local.properties)

5. Build Jar<br>
``
mvn package
``

6. Run application<br>
``
mvn spring-boot:run
``






