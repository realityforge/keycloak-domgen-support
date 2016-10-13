package org.realityforge.keycloak.domgen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nonnull;
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
  private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock();
  private KeycloakDeployment _deployment;

  @Nonnull
  protected abstract String getRootJndiPath();

  @Override
  public KeycloakDeployment resolve( final OIDCHttpFacade.Request request )
  {
    _lock.readLock().lock();
    if ( null != _deployment )
    {
      return _deployment;
    }
    _lock.readLock().unlock();
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
