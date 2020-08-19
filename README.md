Miro take home test
---

## Compiling and Testing

```shell script
./mvnw clean package
```

## Running

Run using Maven
```shell script
./mvnw spring-boot:run
```

Run using Docker
```shell script
docker build . --tag=mirotakehome
docker run -it --rm -p 8080:8080 mirotakehome
```
