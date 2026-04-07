package org.example.config;

import org.flywaydb.core.Flyway;

public class FlyWayconfig {
    public static void migrate () {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/Roterizador",
                        "postgres",
                        "1234"
                )
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }
}
