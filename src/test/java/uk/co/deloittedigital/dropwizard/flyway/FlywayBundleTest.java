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

import com.codahale.metrics.health.HealthCheckRegistry;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.junit.Test;
import org.mockito.Mock;
import uk.co.deloittedigital.dropwizard.flyway.config.FlywayConfiguration;
import uk.co.deloittedigital.dropwizard.flyway.config.FlywayConfigurationProvider;
import uk.co.deloittedigital.dropwizard.flyway.healthcheck.FlywayValidationHealthCheck;
import uk.co.deloittedigital.dropwizard.flyway.tasks.FlywayMigrateTask;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FlywayBundleTest {

    @Mock private Flyway mockFlyway;
    @Mock private Environment mockEnvironment;
    @Mock private AdminEnvironment mockAdminEnvironment;
    @Mock private HealthCheckRegistry mockHealthchecks;

    private FlywayConfigurationProvider configurationSource;
    private FlywayConfiguration flywayConfiguration;
    private FlywayBundle flywayBundle;

    @org.junit.Before
    public void setUp() throws Exception {
        initMocks(this);

        when(mockEnvironment.admin()).thenReturn(mockAdminEnvironment);
        when(mockEnvironment.healthChecks()).thenReturn(mockHealthchecks);

        flywayConfiguration = new FlywayConfiguration();
        configurationSource = new DummyConfiguration(flywayConfiguration);
        flywayConfiguration.setFlywayInstance(mockFlyway);

        flywayBundle = new FlywayBundle();
    }

    @Test
    public void testDefaultFlywayBundleConfiguration() throws Exception {
        flywayBundle.run(configurationSource, mockEnvironment);

        verify(mockFlyway, times(0)).clean();
        verify(mockFlyway, times(1)).validate();
        verify(mockFlyway, times(1)).migrate();

        verify(mockAdminEnvironment).addTask(any(FlywayMigrateTask.class));
    }

    @Test
    public void testFlywayBundleWithClean() throws Exception {
        flywayConfiguration.cleanOnStartup = true;

        flywayBundle.run(configurationSource, mockEnvironment);

        verify(mockFlyway, times(1)).clean();
    }

    @Test
    public void testFlywayBundleWithNoValidate() throws Exception {
        flywayConfiguration.validateOnStartup = false;

        flywayBundle.run(configurationSource, mockEnvironment);

        verify(mockFlyway, times(0)).validate();
    }

    @Test
    public void testFlywayBundleWithNoMigrate() throws Exception {
        flywayConfiguration.migrateOnStartup = false;

        flywayBundle.run(configurationSource, mockEnvironment);

        verify(mockFlyway, times(0)).migrate();
    }

    @Test
    public void testFlywayBundleWithValidateHealthcheck() throws Exception {
        flywayConfiguration.validateOnHealthcheck = true;

        flywayBundle.run(configurationSource, mockEnvironment);

        verify(mockHealthchecks).register(anyString(), any(FlywayValidationHealthCheck.class));
    }

    private class DummyConfiguration implements FlywayConfigurationProvider {
        private final FlywayConfiguration flywayConfiguration;

        public DummyConfiguration(FlywayConfiguration flywayConfiguration) {
            this.flywayConfiguration = flywayConfiguration;
        }

        @Override
        public ManagedDataSource getManagedDataSource() {
            return null; // Not required for testing
        }

        @Override
        public FlywayConfiguration getFlywayConfiguration() {
            return flywayConfiguration;
        }
    }
}