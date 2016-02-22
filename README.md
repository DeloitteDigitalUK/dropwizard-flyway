# Dropwizard Flyway bundle

[![Circle CI](https://circleci.com/gh/DeloitteDigitalUK/dropwizard-flyway.svg?style=svg)](https://circleci.com/gh/DeloitteDigitalUK/dropwizard-flyway)

## Overview

This Dropwizard bundle adds support for [Flyway](https://flywaydb.org/) database migrations. This can be used to ensure
that necessary database migrations have been applied before a service comes into operation.

## Usage

In your service's `initialize` method, add the bundle:

    bootstrap.addBundle(new FlywayBundle());

Your service's configuration class should implement
`uk.co.deloittedigital.dropwizard.flyway.config.FlywayConfigurationProvider`. Flyway migrations should be on the classpath
(recommended - included within your app's JAR) or resolvable on the filesystem, with paths specified in the configuration
file.

### Configuration options

This bundle currently supports the following basic options, which run in this order if configured:

* cleanOnStartup (default false): applies Flyway's schema clean command to the connected DB - this may be destructive
* migrateOnStartup (default true): applies Flyway migrations to the connected DB
* validateOnStartup (default true): applies Flyway's validate command to the connected DB, to check that it matches the service's expected schema patch state

### Healthcheck

When the configuration property `validateOnHealthcheck` is set to true, a Flyway schema validation healthcheck will be 
added to the service. If Flyway cannot validate the current schema, this will cause healthchecks to fail.

This is aimed at situations where it is useful to bring services online before their corresponding schemas have been 
applied, and where failing healthchecks are used to keep services offline (e.g. when monitored by load balancers).

Once necessary migrations have been applied, the healthcheck will switch to reporting a healthy state.

### Migration task

The bundle also adds a task to allow schema migration to be triggered externally through the admin port, e.g.:

    curl -X POST http://server:8081/tasks/flyway-migrate

## License

See [LICENSE](LICENSE.txt).

## Copyright

Copyright (c) 2015, 2016 Deloitte MCS Ltd.

See [AUTHORS](AUTHORS.txt) for contributors.