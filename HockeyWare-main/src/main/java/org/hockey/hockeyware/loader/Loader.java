package org.hockey.hockeyware.loader;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import sun.misc.URLClassPath;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Loader
{
    public static String version = "1.5";
    private static WebClient client;

    private static String lastLogin;
    private static String lastPassword;

    public static List< String > allowedFileSuffixes = Arrays.asList(
            ".png",//images
            ".glsl",//shaders
            ".shader",//shaders
            ".frag",//shaders
            ".vert",//shaders
            ".jpg",//images
            ".ttf",//fonts
            ".json",//lang files, shaders
            ".csv",//plugin mappings
            ".ScriptEngineFactory",//META_INF service
            ".IBaritoneProvider",//META_INF service
            ".fsh",//shaders
            ".vsh",//shaders
            ".shader",//shaders
            ".lang"//lang files
    );

    public static boolean validResource( String name )
    {
        for ( String suffix : allowedFileSuffixes )
            if ( name.endsWith( suffix ) ) return true;
        return false;
    }

    public static void init()
    {
        try
        {
            client = new WebClient();
            client.connectBlocking();
        } catch ( Exception e )
        {
            e.printStackTrace();
            ErrorHandle.popup( "You Have Lost Connection To The HockeyWare Server", true );
            return;
        }
        client.send( "ver " + version );
    }


    public static void onLogin( String login, String password )
    {
        client.send( String.format( "auth [%s] [%s] [%s]", login, password, getSystemInfo() ) );
        lastLogin = login;
        lastPassword = password;
    }

    public static String getSystemInfo()
    {
        return DigestUtils.sha256Hex( DigestUtils.sha256Hex( System.getenv( "os" )
                + System.getProperty( "os.name" )
                + System.getProperty( "os.arch" )
                + System.getProperty( "user.name" )
                + System.getenv( "SystemRoot" )
                + System.getenv( "HOMEDRIVE" )
                + System.getenv( "PROCESSOR_LEVEL" )
                + System.getenv( "PROCESSOR_REVISION" )
                + System.getenv( "PROCESSOR_IDENTIFIER" )
                + System.getenv( "PROCESSOR_ARCHITECTURE" )
                + System.getenv( "PROCESSOR_ARCHITEW6432" )
                + System.getenv( "NUMBER_OF_PROCESSORS" )
        ) );
    }

    @SuppressWarnings( "unchecked" )
    public static void load( byte[] bytes ) throws Exception
    {
        final Map< String, byte[] > classes = new HashMap<>( 2000 );
        final Map< String, byte[] > resources = new HashMap<>( 2000 );

        ZipInputStream zis = new ZipInputStream( new ByteArrayInputStream( bytes ) );
        ZipEntry entry;
        while ( ( entry = zis.getNextEntry() ) != null )
        {
            if ( entry.getName().endsWith( ".class" ) )
            {
                classes.put( toJavaName( entry.getName() ), IOUtils.toByteArray( zis ) );
                zis.closeEntry();
            } else
            {
                if ( validResource( entry.getName() ) )
                {
                    resources.put( entry.getName(), IOUtils.toByteArray( zis ) );
                    zis.closeEntry();
                }
            }
        }
        zis.close();

        Field f = URLClassLoader.class.getDeclaredField( "ucp" );
        f.setAccessible( true );
        URLClassPath parent = ( URLClassPath ) f.get( Launch.classLoader );
        f.set( Launch.classLoader, new CustomClassPath( parent, resources ) );

        Field cacheF = LaunchClassLoader.class.getDeclaredField( "resourceCache" );
        cacheF.setAccessible( true );
        ConcurrentHashMap< String, byte[] > resourceCache = ( ConcurrentHashMap< String, byte[] > ) cacheF.get( Launch.classLoader );
        resourceCache.putAll( classes );
        cacheF.set( Launch.classLoader, resourceCache );

        Class.forName( "org.hockey.hockeyware.client.mixin.ForgeMixinLoader" )
                .getConstructor().newInstance();

    }

    private static String toJavaName( String entryName )
    {
        return entryName.replace( "/", "." ).replace( ".class", "" );
    }

    public static boolean runningFromIntellij()
    {
        return System.getProperty( "java.class.path" ).contains( "idea_rt.jar" );
    }

    public static void unsafeCrash()
    {
        Unsafe unsafe = null;
        try
        {
            Field f = Unsafe.class.getDeclaredField( "theUnsafe" );
            f.setAccessible( true );
            unsafe = ( Unsafe ) f.get( null );
        } catch ( Exception e )
        {
            FMLCommonHandler.instance().exitJava( -1, true );
            for ( Field f : Minecraft.class.getDeclaredFields() )
            {
                try
                {
                    f.set( null, null );
                } catch ( IllegalAccessException ex )
                {
                    throw new RuntimeException( ex );
                }
            }
        }

        try
        {
            Unsafe.class.getDeclaredMethod( "putAddress", long.class, long.class ).invoke( unsafe, 0L, 0L );
            Unsafe.class.getDeclaredMethod( "freeMemory", long.class ).invoke( unsafe, 0L );
        } catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
        {
            unsafe.putAddress( 0L, 0L );
            unsafe.freeMemory( 0L );
        }
    }

    public static String getLastLogin()
    {
        return lastLogin;
    }

    public static String getLastPassword()
    {
        return lastPassword;
    }
}
