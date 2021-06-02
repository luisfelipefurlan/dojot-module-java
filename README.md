# dojot-module-java
Common library to be used within dojot modules. Now with notes of fresh brewed coffee.


**Attention**: As of version v2.0, this library no longer has integration with the old "auth" service, but with the "keycloak" service.
Be aware that the environment variables `KEYCLOAK_USERNAME` and `KEYCLOAK_PASSWORD` will probably have to be passed to services that use this library. This user should be able to get the existing realm lists from keycloak.
