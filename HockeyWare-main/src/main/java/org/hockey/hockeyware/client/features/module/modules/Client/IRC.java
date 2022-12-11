package org.hockey.hockeyware.client.features.module.modules.Client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.IRCMessage;
import org.hockey.hockeyware.loader.License;
import org.hockey.hockeyware.loader.WebClient;

import java.util.HashMap;

public class IRC extends Module
{
    private static IRC instance;
    private static boolean preventDisable = true;

    private final HashMap< String, ChatFormatting > colorCodes = new HashMap<>( 3 );
    private final Setting< String > prefixSet = new Setting<>( "Prefix", "@" );

    public IRC()
    {
        super( "IRC", "HockeyWare Cross Server Chat", Category.Client );
        colorCodes.put( "Normal", ChatFormatting.DARK_AQUA );
        colorCodes.put( "Beta", ChatFormatting.GOLD );
        colorCodes.put( "Developer", ChatFormatting.DARK_RED );
        instance = this;
    }

    public void onMessage( String message )
    {
        if ( !isOn() )
            return;

        JsonObject object;
        try
        {
            object = new JsonParser().parse( message ).getAsJsonObject();
        } catch ( JsonSyntaxException e )
        {

            err();
            return;
        }

        if ( object.has( "timeout" ) )
        {
            IRCMessage.sendErrorMessage( "Please Wait " + object.get( "timeout" ).getAsInt() + " Seconds Before Sending Another Message" );
            return;
        }

        if ( !( object.has( "message" ) && object.has( "author" ) && object.has( "accountType" ) ) )
        {
            err();
            return;
        }

        final String color = colorCodes.get( object.get( "accountType" ).getAsString() ).toString();
        IRCMessage.sendMessage(
                String.format( "%s%s%s: %s", color,
                        object.get( "author" ).getAsString(), ChatFormatting.GRAY, object.get( "message" ).getAsString() )
        );
    }

    @SubscribeEvent
    public void onChat( ClientChatEvent event )
    {
        String msg = event.getOriginalMessage();
        System.out.println( msg );
        if ( msg.startsWith( getPrefix() ) )
        {
            event.setCanceled( true );
            msg = msg.substring( getPrefix().length() );
            int sub = 0;
            while ( msg.startsWith( " " ) )
            {
                sub++;
                msg = msg.substring( sub );
            }
            if ( StringUtils.isBlank( msg ) || msg.isEmpty() )
                return;
            JsonObject object = new JsonObject();
            object.addProperty( "author", License.getInstance().getName() );
            object.addProperty( "accountType", License.getInstance().getAccountType() );
            object.addProperty( "message", msg );
            WebClient.getInstance().send( "irc [" + object + "]" );
        }
    }

    private void err()
    {
        IRCMessage.sendErrorMessage( "Failed to read IRC message properly.. turning" );
        toggle( true );
    }

    public static IRC getInstance()
    {
        return instance;
    }

    public String getPrefix()
    {
        return prefixSet.getValue();
    }

    @Override
    public String getDisplayInfo()
    {
        return super.getDisplayInfo();
    }

    @Override
    public void toggle( boolean silent )
    {
        if ( isOn() && preventDisable )
            return;
        super.toggle( silent );
    }
}
