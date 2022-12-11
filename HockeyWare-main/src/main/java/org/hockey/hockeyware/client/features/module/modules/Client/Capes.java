package org.hockey.hockeyware.client.features.module.modules.Client;

import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;

public class Capes extends Module {

    public static Capes INSTANCE = new Capes();

    private static boolean preventDisable = false;


    public Capes() {
        super("Capes", "Toggles HockeyWare Beta User Capes On/Off", Category.Client);
        INSTANCE = this;
    }

    @Override
    public void toggle( boolean silent )
    {
        if ( isOn() && preventDisable )
            return;
        super.toggle( silent );
    }
}
