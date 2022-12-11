package org.hockey.hockeyware.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.hockey.hockeyware.client.features.module.modules.Client.IRC;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class WebClient extends WebSocketClient
{

    private static WebClient instance;

    public WebClient() throws Exception
    {
        super( new URI( "ws://207.148.21.100:1226" ) );
        //super( new URI( "ws://127.0.0.1:1226" ) );
        if (instance != null)
            throw new RuntimeException();
        instance = this;
    }

    @Override
    public void onOpen( ServerHandshake handshakedata )
    {

    }

    @Override
    @SuppressWarnings( "deprecation" )
    public void onMessage( String message )
    {
        if ( message.startsWith( "ver" ) )
        {
            if ( message.equals( "ver false" ) )
                ErrorHandle.popup( "Your Loader Is Outdated! Download The New One From Panel.HockeyWare.org", true );
            else
                new Thread( LoaderGui::new ).start();
        }

        if ( message.startsWith( "authtoken" ) )
        {
            LoaderCoreMod.thread.resume();
            Cache.cache( Loader.getLastLogin(), Loader.getLastPassword() );
            JsonObject object = new JsonParser().parse( message.replace( "authtoken ", "" ) ).getAsJsonObject();
            new License( Loader.getLastLogin(), object.get( "accountType" ).getAsString() );
            send( "receivejar " + object.get( "token").getAsString() );
        }

        if ( message.startsWith( "autherr" ) )
        {
            ErrorHandle.popup(
                    message.replace( "autherr ", "" ), false );
            Cache.remove();
            if ( !LoaderGui.instance.frame.isVisible() )
            {
                LogManager.getLogger( "HockeyWare" ).error( "Failed To Login To HockeyWare With Saved Auth Data " + message.replace( "autherr ", "" ) );
                LoaderGui.instance.frame.setVisible( true );
            }
            LoaderGui.instance.button.setEnabled( true );
            LoaderGui.instance.passText.setText( "" );
        }

        if (message.startsWith( "irc" ))
        {
            String json = message.replace( "irc ", "" );
            if ( IRC.getInstance() != null )
            {
                IRC.getInstance().onMessage( json );
            }
        }

        if ( message.startsWith( "receiveerr" ) )
        {
            throw new RuntimeException( "that should not happen" );
        }
    }

    @Override
    public void onMessage( ByteBuffer bytes )
    {
        try
        {
            LoaderGui.instance.frame.setVisible( false );
            Loader.load( bytes.array() );
        } catch ( Exception e )
        {
            e.printStackTrace();
            Loader.unsafeCrash();
        }
    }

    @Override
    public void onClose( int code, String reason, boolean remote )
    {
        ErrorHandle.popup( "Can't Connect To HockeyWare Server", true );
    }

    @Override
    public void onError( Exception ex )
    {
        ex.printStackTrace();
        if ( ex.getMessage().equals( "Connection refused: connect" ) )
        {
            ErrorHandle.popup( "The Server Is Offline", true );
            return;
        }
        ErrorHandle.popup( "There Was An Error" + ex.getMessage(), true );
    }

    public static WebClient getInstance()
    {
        return instance;
    }
}
