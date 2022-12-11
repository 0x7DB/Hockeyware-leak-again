package org.hockey.hockeyware.client.manager;

import org.hockey.hockeyware.client.gui.font.customFont;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;

import java.awt.*;
import java.util.Locale;

public class FontManager implements Globals {

    public final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ENGLISH);
    public final String fontsList = "22, 2222";
    public String fontName = CustomFont.font.getName();
    public int fontSize = CustomFont.size.getValue();

    private customFont font = new customFont(new Font(fontName, Font.PLAIN, fontSize), true, true);

    public void setFont() {
        this.font = new customFont(new Font(fontName, Font.PLAIN, fontSize), true, true);
    }

    public void reset() {
        this.setFont("Verdana");
        CustomFont.font.setValue(CustomFont.Fonts.Verdana);
        this.setFontSize(18);
        CustomFont.size.setValue(18);
        this.setFont();
    }

    public boolean setFont(String fontName) {
        for (String font : fonts) {
            if (fontName.equalsIgnoreCase(font)) {
                this.fontName = font;
                this.setFont();
                return true;
            }
        }
        return false;
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        this.setFont();
    }

    public void drawStringWithShadow(String string, float x, float y, int colour) {
        this.drawString(string, x, y, colour, true);
    }

    public float drawString(String string, float x, float y, int colour, boolean shadow) {
        if (shadow) {
            return this.font.drawStringWithShadow(string, x, y, colour);
        } else {
            return this.font.drawString(string, x, y, colour);
        }
    }

    public int getTextHeight() {
        return this.font.getStringHeight();
    }

    public int getStringWidth(String string) {
        return this.font.getStringWidth(string);
    }
}

