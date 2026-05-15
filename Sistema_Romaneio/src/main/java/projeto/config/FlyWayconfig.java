package projeto.config;

import org.flywaydb.core.Flyway;

public class FlyWayconfig {
    public static void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:postgresql://localhost:5432/logidutra",
                        "postgres",
                        "03122007"
                )
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }
}
