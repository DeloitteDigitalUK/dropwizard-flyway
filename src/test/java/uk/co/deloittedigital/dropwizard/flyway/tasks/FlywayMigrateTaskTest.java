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

package uk.co.deloittedigital.dropwizard.flyway.tasks;

import com.google.common.collect.ImmutableMultimap;
import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 */
public class FlywayMigrateTaskTest {

    @Mock
    private Flyway mockFlyway;
    private FlywayMigrateTask task;

    @Before
    public void setUp() throws Exception {

        initMocks(this);

        this.task = new FlywayMigrateTask(mockFlyway);
    }

    @Test
    public void testExecute() throws Exception {
        ImmutableMultimap<String, String> emptyMap = ImmutableMultimap.of();
        PrintWriter anyPrintWriter = new PrintWriter(System.out);

        this.task.execute(emptyMap, anyPrintWriter);

        verify(mockFlyway).migrate();
    }
}