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

package uk.co.deloittedigital.dropwizard.flyway.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import org.flywaydb.core.Flyway;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

@Data
public class FlywayConfiguration {

    public boolean cleanOnStartup = false;
    public boolean validateOnStartup = true;
    public boolean migrateOnStartup = true;
    @NotNull public String[] locations;
    public boolean validateOnHealthcheck = false;
    public long validationHealthcheckTimeoutMillis = TimeUnit.SECONDS.toMillis(1);
    public String[] schemas;

    private Flyway flyway;

    public Flyway getFlywayInstance() {
        if (flyway == null) {
            flyway = new Flyway();
            flyway.setLocations(locations);
            flyway.setSchemas(schemas);
        }
        return flyway;
    }

    @VisibleForTesting @JsonIgnore
    public void setFlywayInstance(Flyway flyway) {
        this.flyway = flyway;
    }
}
