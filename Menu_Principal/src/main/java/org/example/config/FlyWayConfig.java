package org.example.config;

import org.flywaydb.core.Flyway;

public class FlyWayConfig {
    public static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/postgres",
                        "postgres",
                        "postgres"
                )
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }
}