CREATE USER "sample-access" WITH ENCRYPTED PASSWORD 'sample@2026.06.27!';
CREATE DATABASE "sample-db" OWNER "sample-access";
GRANT ALL PRIVILEGES ON DATABASE "sample-db" TO "sample-access";

CREATE USER "keycloak-access" WITH ENCRYPTED PASSWORD '!security@access.g&ms';
CREATE DATABASE "keycloak-db" OWNER "keycloak-access";
GRANT ALL PRIVILEGES ON DATABASE "keycloak-db" TO "keycloak-access";
