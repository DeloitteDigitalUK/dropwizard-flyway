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
import io.dropwizard.servlets.tasks.Task;
import org.flywaydb.core.Flyway;

import java.io.PrintWriter;

/**
 * Admin task to perform Flyway migrations. Usage example:
 *
 * curl -X POST http://server:8081/tasks/flyway-migrate
 */
public class FlywayMigrateTask extends Task {

    private final Flyway flyway;

    public FlywayMigrateTask(Flyway flyway) {
        super("flyway-migrate");
        this.flyway = flyway;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        this.flyway.migrate();
    }
}
