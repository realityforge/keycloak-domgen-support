package org.realityforge.keycloak.domgen;

import javax.annotation.Nonnull;
import javax.servlet.ServletRequest;

public interface KeycloakUrlFilter
{
   boolean shouldProtectRequest( @Nonnull ServletRequest request );
}
