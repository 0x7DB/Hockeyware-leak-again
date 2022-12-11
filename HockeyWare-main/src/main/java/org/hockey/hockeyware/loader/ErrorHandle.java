package org.hockey.hockeyware.loader;

import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;

public class ErrorHandle
{

    public static void popup( String text, boolean exit )
    {
        JOptionPane.showMessageDialog( null, text, "HockeyWare Loader", JOptionPane.ERROR_MESSAGE );
        if ( exit )
            Loader.unsafeCrash();
    }
}
