# vaadin-gatling-abuse

Simple Vaadin project used for proof-of-concept performance testing with Gatling.

## Architecture (DEV)
```
Spring Boot (localhost:8080)
└── exposes /actuator/prometheus

Docker:
├── Postgres (5432)
├── Postgres Exporter (9187)
├── Prometheus (9090)
└── Grafana (3000)
```

## Prerequisites

- Docker + Docker Compose
- Java 21+
- Maven 3.9+

## Running the application

### 1. Start infrastructure

`docker compose up -d`

This starts:

- Postgres: localhost:5432
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000
- Postgres exporter: http://localhost:9187/metrics

### 2. Start Spring Boot application

`mvn spring-boot:run`

Application runs on:

http://localhost:8080

Metrics endpoint:

http://localhost:8080/actuator/prometheus

## Spring Boot configuration

Required dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

application.properties:

```properties
management.endpoints.web.exposure.include=health,prometheus
management.metrics.tags.application=vaadinstart
```
## Security configuration

Allow Prometheus endpoint without authentication:
```java
http.authorizeHttpRequests(auth -> auth
.requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
.anyRequest().authenticated()
);
```

## Prometheus configuration

Example prometheus.yml:

global:
`scrape_interval: 15s`

scrape_configs:
- job_name: "spring-boot"
  metrics_path: "/actuator/prometheus"
  static_configs:
    - targets: ["host.docker.internal:8080"]

- job_name: "postgres"
  static_configs:
    - targets: ["postgres-exporter:9187"]

Docker Compose must include:

extra_hosts:
- "host.docker.internal:host-gateway"

## Grafana

http://localhost:3000

Default login: admin / admin

### Add Prometheus datasource

http://prometheus:9090

## Dashboard

Import: `spring-boot-actuator-vaadinstart-grafana-v2.json`

Dashboards → New → Import → Upload JSON

## Notes

- `/actuator/prometheus` must be publicly accessible (no login redirect)
- application label is required for dashboards:
  `management.metrics.tags.application=vaadinstart`
- Some metrics (p95 latency, HikariCP histograms) require additional Micrometer configuration
- "No data" in Grafana is often expected in low-traffic DEV environments

## Next steps

- Add nginx + nginx_exporter
- Add node_exporter
- Enable histogram metrics
- Integrate Gatling
