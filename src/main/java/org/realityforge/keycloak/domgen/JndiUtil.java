package org.realityforge.keycloak.domgen;

import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

final class JndiUtil
{
  private JndiUtil()
  {
  }

  @Nonnull
  static JsonObject buildJsonFromContext( @Nonnull final Context context, @Nonnull final String path )
    throws NamingException
  {
    final JsonObjectBuilder object = Json.createObjectBuilder();

    final NamingEnumeration<NameClassPair> enumeration = context.list( path );
    while ( enumeration.hasMore() )
    {
      final NameClassPair pair = enumeration.next();
      final String key = pair.getName();
      final String subpath = path + "/" + key;
      final Object value = context.lookup( subpath );
      final String type = pair.getClassName();
      if ( null == type )
      {
        object.addNull( key );
      }
      else if ( String.class.getName().equals( type ) )
      {
        object.add( key, (String) value );
      }
      else if ( Boolean.class.getName().equals( type ) )
      {
        object.add( key, (Boolean) value );
      }
      else if ( Integer.class.getName().equals( type ) )
      {
        object.add( key, (Integer) value );
      }
      else if ( Short.class.getName().equals( type ) )
      {
        object.add( key, (Short) value );
      }
      else if ( Long.class.getName().equals( type ) )
      {
        object.add( key, (Long) value );
      }
      else if ( Float.class.getName().equals( type ) )
      {
        object.add( key, (Float) value );
      }
      else if ( Double.class.getName().equals( type ) )
      {
        object.add( key, (Double) value );
      }
      else if ( Context.class.getName().equals( type ) )
      {
        final Context subcontext = (Context) value;
        object.add( key, buildJsonFromContext( subcontext, subpath ) );
      }
    }
    enumeration.close();

    return object.build();
  }
}
