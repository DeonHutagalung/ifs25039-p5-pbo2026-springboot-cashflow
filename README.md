# ifs25039-p5-pbo2026-springboot-cashflow

Aplikasi pencatat keuangan (Spring Boot 4.1.2, Vaadin 25.3.4, JDBC PostgreSQL, Clean Architecture).

## Menjalankan di IntelliJ
1. Buka folder proyek (File > Open > pilih `pom.xml`), JDK 25, tunggu Maven import.
2. Buat database: `CREATE DATABASE pbo2026_cashflow;`
3. Sesuaikan `.env` (DB_USERNAME / DB_PASSWORD). Login admin: `ROOT_USERNAME` / `ROOT_PASSWORD` (default admin/admin).
4. Jalankan `App.java` (Run) atau `./mvnw spring-boot:run`, buka http://localhost:8080
5. Uji + coverage: `./mvnw clean verify` ; build JAR: `./mvnw clean package -Pproduction`

## Route
Public: `/login`, `/register`, `/uploads/**`. Protected: `/`, `/profile`. Admin: `/users`, `/user-transactions?userId=ID`.
