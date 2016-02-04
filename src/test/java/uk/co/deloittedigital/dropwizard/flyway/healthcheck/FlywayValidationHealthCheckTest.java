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
import com.google.common.util.concurrent.Uninterruptibles;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.rnorth.visibleassertions.VisibleAssertions.assertFalse;
import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;

/**
 *
 */
public class FlywayValidationHealthCheckTest {

    @Mock
    private Flyway mockFlyway;
    private FlywayValidationHealthCheck healthCheck;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        this.healthCheck = new FlywayValidationHealthCheck(mockFlyway, 100);
    }

    @Test
    public void testCheckPassesOkInTime() throws Exception {
        doNothing().when(mockFlyway).validate();

        HealthCheck.Result result = this.healthCheck.check();

        assertTrue("When Flyway validate doesn't throw an exception, the healthcheck result should be healthy", result.isHealthy());
    }

    @Test
    public void testCheckFailsIfExceptionThrow() throws Exception {
        doThrow(FlywayException.class).when(mockFlyway).validate();

        HealthCheck.Result result = this.healthCheck.check();

        assertFalse("When Flyway validate throws an expected exception, the healthcheck result should be unhealthy", result.isHealthy());
    }

    @Test
    public void testCheckFailsIfTooSlow() throws Exception {
        doAnswer(invocation -> {
            Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
            return null;
        }).when(mockFlyway).validate();

        HealthCheck.Result result = this.healthCheck.check();

        assertFalse("When Flyway validate takes longer than the timeout, the healthcheck result should be unhealthy", result.isHealthy());
    }
}