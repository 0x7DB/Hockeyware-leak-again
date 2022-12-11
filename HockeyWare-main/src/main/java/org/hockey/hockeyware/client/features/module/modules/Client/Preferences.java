package org.hockey.hockeyware.client.features.module.modules.Client;

import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class Preferences extends Module {

    public static Preferences INSTANCE;

    private static boolean preventDisable = true;

    public Preferences() {
        super("Preferences", "Additional Settings For HockeyWare", Category.Client);
        INSTANCE = this;
    }



    public static final Setting<Preferences.BracketColor> bracketColor = new Setting<>("BracketColor", Preferences.BracketColor.Red);
    public static final Setting<Preferences.NameColor> nameColor = new Setting<>("NameColor", Preferences.NameColor.Red);

    //public static final Setting<Boolean> msgFriendsOnAdd = new Setting<>("MsgFriendsOnAdd", true);
    //public static final Setting<Boolean> msgFriendsonRemove = new Setting<>("MsgFriendsOnRemove", false);

    @Override
    public void onEnable() {
        this.toggle(true);
    }

    @Override
    public void onDisable() {
        this.toggle(true);
    }

    public enum BracketColor {
        DarkRed, Red, Gold, Yellow, DarkGreen, Green, Aqua, DarkAqua, DarkBlue, Blue, LightPurple, DarkPurple, White, Gray, DarkGray, Black
    }

    public enum NameColor {
        DarkRed, Red, Gold, Yellow, DarkGreen, Green, Aqua, DarkAqua, DarkBlue, Blue, LightPurple, DarkPurple, White, Gray, DarkGray, Black

    }

    @Override
    public void toggle( boolean silent )
    {
        if ( isOn() && preventDisable )
            return;
        super.toggle( silent );
    }

}