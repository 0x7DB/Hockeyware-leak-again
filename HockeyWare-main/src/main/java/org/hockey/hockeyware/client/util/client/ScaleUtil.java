package org.hockey.hockeyware.client.util.client;


import org.hockey.hockeyware.client.features.Globals;

public class ScaleUtil implements Globals {
    public static float centerTextY(float y, float height) {
        return (y + (height / 2.0f)) - (mc.fontRenderer.FONT_HEIGHT / 2.0f);
    }
}
