# claims-service

This app helps us manage claims.


## Setup
The dev-env setup script should automagically download this repo.
Additionally, the script would copy `application-development.yml` to `/src/main/resources`.

## Running
`claims-application` should be detected by intelliJ and added to run configurations. One small tweak needs to be made in order to run properly:
- In `Run/Debug Configurations`, edit `ClaimsAppliation` (under the `Spring Boot` tree node).
- Update `Active Profiles:` to `development` and save.
