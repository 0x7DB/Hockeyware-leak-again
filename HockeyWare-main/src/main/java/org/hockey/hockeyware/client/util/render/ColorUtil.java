package org.hockey.hockeyware.client.util.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.hockey.hockeyware.client.features.module.modules.Client.Preferences;

import java.awt.*;

public class ColorUtil {

    public static ChatFormatting nameColorFromSetting;
    public static ChatFormatting nameBracketFromSetting;
    int r;
    int g;
    int b;
    int a;


    public ColorUtil(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }

    public static Color releasedDynamicRainbow(int delay, float saturation, float brightness) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0);
        return Color.getHSBColor((float) (rainbowState % 360.0 / 360.0), saturation / 255.0f, brightness / 255.0f);
    }

    public static ChatFormatting getDimentionColor(int dimention) {
        switch (dimention) {
            case -1:
                return ChatFormatting.DARK_RED;
            case 0:
                return ChatFormatting.DARK_GREEN;
            case 1:
                return ChatFormatting.DARK_PURPLE;
            default:
                return ChatFormatting.GRAY;
        }
    }

    public static int toHex(int r, int g, int b) {
        return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static float[] toRGBA(int hex) {
        // r, g, b, a
        return new float[]{(hex >> 16 & 0xff) / 255.0f, (hex >> 8 & 0xff) / 255.0f, (hex & 0xff) / 255.0f, (hex >> 24 & 0xff) / 255.0f};
    }

    public static int getHealthColor(final Entity entity) {
        final int scale = (int) Math.round(255.0 - ((EntityLivingBase) entity).getHealth() * 255.0 / ((EntityLivingBase) entity).getMaxHealth());
        final int damageColor = 255 - scale << 8 | scale << 16;
        return 0xFF000000 | damageColor;
    }

    public static int getHealthColor(final float health) {
        final int scale = (int) Math.round(255.0 - health * 255.0 / Minecraft.getMinecraft().player.getMaxHealth());
        final int damageColor = 255 - scale << 8 | scale << 16;
        return 0xFF000000 | damageColor;
    }

    public static ChatFormatting getNameColorFromSetting(Preferences.NameColor setting) {
        if (setting.equals(Preferences.NameColor.DarkRed)) {
            nameColorFromSetting = ChatFormatting.DARK_RED;
        }
        if (setting.equals(Preferences.NameColor.Red)) {
            nameColorFromSetting = ChatFormatting.RED;
        }
        if (setting.equals(Preferences.NameColor.Gold)) {
            nameColorFromSetting = ChatFormatting.GOLD;
        }
        if (setting.equals(Preferences.NameColor.Yellow)) {
            nameColorFromSetting = ChatFormatting.YELLOW;
        }
        if (setting.equals(Preferences.NameColor.DarkGreen)) {
            nameColorFromSetting = ChatFormatting.DARK_GREEN;
        }
        if (setting.equals(Preferences.NameColor.Green)) {
            nameColorFromSetting = ChatFormatting.GREEN;
        }
        if (setting.equals(Preferences.NameColor.Aqua)) {
            nameColorFromSetting = ChatFormatting.AQUA;
        }
        if (setting.equals(Preferences.NameColor.DarkAqua)) {
            nameColorFromSetting = ChatFormatting.DARK_AQUA;
        }
        if (setting.equals(Preferences.NameColor.DarkBlue)) {
            nameColorFromSetting = ChatFormatting.DARK_BLUE;
        }
        if (setting.equals(Preferences.NameColor.Blue)) {
            nameColorFromSetting = ChatFormatting.BLUE;
        }
        if (setting.equals(Preferences.NameColor.LightPurple)) {
            nameColorFromSetting = ChatFormatting.LIGHT_PURPLE;
        }
        if (setting.equals(Preferences.NameColor.DarkPurple)) {
            nameColorFromSetting = ChatFormatting.DARK_PURPLE;
        }
        if (setting.equals(Preferences.NameColor.White)) {
            nameColorFromSetting = ChatFormatting.WHITE;
        }
        if (setting.equals(Preferences.NameColor.Gray)) {
            nameColorFromSetting = ChatFormatting.GRAY;
        }
        if (setting.equals(Preferences.NameColor.DarkGray)) {
            nameColorFromSetting = ChatFormatting.DARK_GRAY;
        }
        if (setting.equals(Preferences.NameColor.Black)) {
            nameColorFromSetting = ChatFormatting.BLACK;
        }
        return nameColorFromSetting;
    }

    public static ChatFormatting getBracketColorFromSetting(Preferences.BracketColor setting) {
        if (setting.equals(Preferences.BracketColor.DarkRed)) {
            nameBracketFromSetting = ChatFormatting.DARK_RED;
        }
        if (setting.equals(Preferences.BracketColor.Red)) {
            nameBracketFromSetting = ChatFormatting.RED;
        }
        if (setting.equals(Preferences.BracketColor.Gold)) {
            nameBracketFromSetting = ChatFormatting.GOLD;
        }
        if (setting.equals(Preferences.BracketColor.Yellow)) {
            nameBracketFromSetting = ChatFormatting.YELLOW;
        }
        if (setting.equals(Preferences.BracketColor.DarkGreen)) {
            nameBracketFromSetting = ChatFormatting.DARK_GREEN;
        }
        if (setting.equals(Preferences.BracketColor.Green)) {
            nameBracketFromSetting = ChatFormatting.GREEN;
        }
        if (setting.equals(Preferences.BracketColor.Aqua)) {
            nameBracketFromSetting = ChatFormatting.AQUA;
        }
        if (setting.equals(Preferences.BracketColor.DarkAqua)) {
            nameBracketFromSetting = ChatFormatting.DARK_AQUA;
        }
        if (setting.equals(Preferences.BracketColor.DarkBlue)) {
            nameBracketFromSetting = ChatFormatting.DARK_BLUE;
        }
        if (setting.equals(Preferences.BracketColor.Blue)) {
            nameBracketFromSetting = ChatFormatting.BLUE;
        }
        if (setting.equals(Preferences.BracketColor.LightPurple)) {
            nameBracketFromSetting = ChatFormatting.LIGHT_PURPLE;
        }
        if (setting.equals(Preferences.BracketColor.DarkPurple)) {
            nameBracketFromSetting = ChatFormatting.DARK_PURPLE;
        }
        if (setting.equals(Preferences.BracketColor.White)) {
            nameBracketFromSetting = ChatFormatting.WHITE;
        }
        if (setting.equals(Preferences.BracketColor.Gray)) {
            nameBracketFromSetting = ChatFormatting.GRAY;
        }
        if (setting.equals(Preferences.BracketColor.DarkGray)) {
            nameBracketFromSetting = ChatFormatting.DARK_GRAY;
        }
        if (setting.equals(Preferences.BracketColor.Black)) {
            nameBracketFromSetting = ChatFormatting.BLACK;
        }
        return nameBracketFromSetting;
    }
}
