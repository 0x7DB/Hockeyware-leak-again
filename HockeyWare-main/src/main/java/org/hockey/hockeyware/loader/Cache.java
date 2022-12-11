package org.hockey.hockeyware.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Cache
{
    private static final File DIR = new File( System.getProperty( "user.home" ) + "/HockeyWare" );
    private static final File AUTH_CACHE = new File( DIR, "HockeyWare.auth" );
    private static AES128 AES = new AES128( "dTrtjACjVaoqHbVVhfTQOrRadnl4guUk2NKhVycotpG4uyJG4f6vIczoCpyuPTrr2iWrm2l9KKMBLf8mVb2yERXvMdbi3OSQxR9X" );

    public static boolean cached()
    {
        return AUTH_CACHE.exists();
    }

    public static Pair< String, String > read()
    {
        try
        {
            final String json = AES.decrypt( Files.readAllBytes( AUTH_CACHE.toPath() ) );
            final JsonObject parsed = new JsonParser().parse( json ).getAsJsonObject();
            return new Pair<>( parsed.get( "username" ).getAsString(), parsed.get( "password" ).getAsString() );
        } catch ( Exception e )
        {
            FileUtils.deleteQuietly( AUTH_CACHE );
            ErrorHandle.popup( "Your Auth Data Is Corrupted, Please Run The Loader Again", true );
        }
        return null;
    }


    public static void cache( String login, String password )
    {
        if ( !DIR.exists() )
            DIR.mkdirs();
        try
        {
            final JsonObject object = new JsonObject();
            object.addProperty( "username", login );
            object.addProperty( "password", password );
            FileUtils.writeByteArrayToFile( AUTH_CACHE, AES.encrypt( object.toString() ) );
        } catch ( Exception e )
        {
            FileUtils.deleteQuietly( AUTH_CACHE );
            ErrorHandle.popup( "There Was An Error Saving Your Auth Data", true );
        }
    }

    public static void remove()
    {
        FileUtils.deleteQuietly( AUTH_CACHE );
        try
        {
            FileUtils.forceDeleteOnExit( AUTH_CACHE );
        } catch ( IOException e )
        {
            // should not happen
        }
    }
}
