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

package uk.co.deloittedigital.dropwizard.flyway.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.TimeBoundHealthCheck;
import io.dropwizard.util.Duration;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.deloittedigital.dropwizard.flyway.FlywayBundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dropwizard Health Check that will fail if the connected database schema state is not valid with respect to the
 * bundled Flyway migrations.
 */
public class FlywayValidationHealthCheck extends HealthCheck {

    private final TimeBoundHealthCheck timeBoundHealthCheck;
    private final Flyway flyway;
    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayBundle.class);

    public FlywayValidationHealthCheck(Flyway flyway, long timeout) {
        this.flyway = flyway;

        // Thread pool size of 1 so that multiple healthcheck requests don't run concurrently
        ExecutorService validationExecutor = Executors.newFixedThreadPool(1);
        this.timeBoundHealthCheck = new TimeBoundHealthCheck(validationExecutor, Duration.milliseconds(timeout));
    }

    @Override
    protected Result check() throws Exception {
        return timeBoundHealthCheck.check(() -> {
            try {
                LOGGER.debug("Healthcheck triggered Flyway validation");
                flyway.validate();
                LOGGER.debug("Healthcheck Flyway validation returned OK");
                return Result.healthy();
            } catch (FlywayException e) {
                LOGGER.debug("Healthcheck Flyway validation returned an error", e);
                return Result.unhealthy(e.getMessage());
            }
        });
    }
}
