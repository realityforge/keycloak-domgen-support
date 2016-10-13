package org.realityforge.keycloak.domgen;

import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * Utility class for building json that represents jndi context.
 */
public final class JndiUtil
{
  private JndiUtil()
  {
  }

  /**
   * Build JsonObject that represents context at specified path. Sub-contexts are represented by
   * nested JsonObject instances.
   */
  @Nonnull
  public static JsonObject buildJsonFromContext( @Nonnull final Context context, @Nonnull final String path )
    throws NamingException
  {
    final JsonObjectBuilder object = Json.createObjectBuilder();
    buildJsonFromContext( object, context, path );
    return object.build();
  }

  /**
   * Build JsonObject that represents context at specified path. Sub-contexts are represented by
   * nested JsonObject instances.
   */
  public static void buildJsonFromContext( @Nonnull final JsonObjectBuilder builder,
                                           @Nonnull final Context context,
                                           @Nonnull final String path )
    throws NamingException
  {
    final NamingEnumeration<NameClassPair> enumeration = context.list( path );
    while ( enumeration.hasMore() )
    {
      final NameClassPair pair = enumeration.next();
      final String key = pair.getName();
      final String subpath = path + "/" + key;
      final Object value = context.lookup( subpath );
      if ( null == value )
      {
        builder.addNull( key );
      }
      else if ( String.class.isInstance( value ) )
      {
        builder.add( key, (String) value );
      }
      else if ( Boolean.class.isInstance( value ) )
      {
        builder.add( key, (Boolean) value );
      }
      else if ( Integer.class.isInstance( value ) )
      {
        builder.add( key, (Integer) value );
      }
      else if ( Short.class.isInstance( value ) )
      {
        builder.add( key, (Short) value );
      }
      else if ( Long.class.isInstance( value ) )
      {
        builder.add( key, (Long) value );
      }
      else if ( Float.class.isInstance( value ) )
      {
        builder.add( key, (Float) value );
      }
      else if ( Double.class.isInstance( value ) )
      {
        builder.add( key, (Double) value );
      }
      else if ( Context.class.isInstance( value ) )
      {
        final Context subcontext = (Context) value;
        builder.add( key, buildJsonFromContext( subcontext, subpath ) );
      }
    }
    enumeration.close();
  }
}
