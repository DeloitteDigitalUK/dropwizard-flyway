/*
 * Copyright (c) 2016 Deloitte MCS Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.deloittedigital.dropwizard.flyway;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.deloittedigital.dropwizard.flyway.healthcheck.FlywayValidationHealthCheck;
import uk.co.deloittedigital.dropwizard.flyway.tasks.FlywayMigrateTask;
import uk.co.deloittedigital.dropwizard.flyway.config.FlywayConfiguration;
import uk.co.deloittedigital.dropwizard.flyway.config.FlywayConfigurationProvider;

/**
 * Dropwizard bundle to add Flyway migrations support to Dropwizard applications.
 *
 * This bundle provides:
 * * Flyway tasks run on startup, toggled by configuration (clean, migrate, validate)
 * * Flyway validation as part of application healthcheck
 * * Flyway migration as a Dropwizard admin task, triggered on admin HTTP API
 */
public class FlywayBundle implements ConfiguredBundle<FlywayConfigurationProvider> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayBundle.class);

    @Override
    public void run(FlywayConfigurationProvider configurationSource, Environment environment) throws Exception {
        ManagedDataSource dataSource = configurationSource.getManagedDataSource();
        FlywayConfiguration configuration = configurationSource.getFlywayConfiguration();

        Flyway flyway = configuration.getFlywayInstance();
        flyway.setDataSource(dataSource);

        if (configuration.cleanOnStartup) {
            LOGGER.info("Applying Flyway clean to database");
            flyway.clean();
        }

        if (configuration.migrateOnStartup) {
            LOGGER.info("Applying Flyway migrations to database");
            flyway.migrate();
        }

        if (configuration.validateOnStartup) {
            LOGGER.info("Applying Flyway startup validation to database");
            flyway.validate();
        }

        if (configuration.validateOnHealthcheck) {
            LOGGER.info("Adding validation status healthcheck");
            environment.healthChecks().register("database.migrations.validation", new FlywayValidationHealthCheck(flyway, configuration.validationHealthcheckTimeoutMillis));
        }

        environment.admin().addTask(new FlywayMigrateTask(flyway));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }

}
