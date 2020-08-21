package org.realityforge.keycloak.domgen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.naming.InitialContext;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;

/**
 * Abstract class from which domgen resolver is generated.
 */
public abstract class AbstractJndiBasedKeycloakConfigResolver
  implements KeycloakConfigResolver
{
  @Nonnull
  private final ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
  @Nullable
  private KeycloakDeployment _deployment;

  @Nonnull
  protected abstract String getRootJndiPath();

  protected abstract void customizeConfiguration( @Nonnull JsonObjectBuilder builder );

  @Override
  public KeycloakDeployment resolve( @Nonnull final OIDCHttpFacade.Request request )
  {
    _lock.readLock().lock();
    try
    {
      if ( null != _deployment )
      {
        return _deployment;
      }
    }
    finally
    {
      _lock.readLock().unlock();
    }
    _lock.writeLock().lock();
    try
    {
      _deployment = buildKeycloakDeployment();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
    return _deployment;
  }

  @Nonnull
  private KeycloakDeployment buildKeycloakDeployment()
  {
    try
    {
      final JsonObjectBuilder builder = Json.createObjectBuilder();
      JndiUtil.buildJsonFromContext( builder, new InitialContext(), getRootJndiPath() );
      customizeConfiguration( builder );

      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Json.createWriter( baos ).write( builder.build() );
      baos.close();
      return KeycloakDeploymentBuilder.build( new ByteArrayInputStream( baos.toByteArray() ) );
    }
    catch ( final Exception e )
    {
      throw new IllegalStateException( "Unable to build deployment configuration", e );
    }
  }
}
