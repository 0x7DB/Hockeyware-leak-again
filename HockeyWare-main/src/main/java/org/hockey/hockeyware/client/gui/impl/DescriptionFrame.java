package org.hockey.hockeyware.client.gui.impl;

import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Frame;
import org.hockey.hockeyware.client.util.render.Render2DUtil;

import java.awt.*;
import java.util.List;

public class DescriptionFrame extends Frame {

    private String description;

    public DescriptionFrame(float posX, float posY, float width, float height) {
        super("Description", posX, posY, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (description == null || !ClickGUI.description.getValue()) {
            return;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        final Color clr = ClickGUI.color.getValue();

        Render2DUtil.drawRect(getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), ClickGUI.color.getValue().getRGB());
        Render2DUtil.drawBorderedRect(getPosX(), getPosY(), getPosX() + getWidth(), getPosY() + getHeight(), 0.5f, 0, 0xff000000);
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel(), getPosX() + 3, getPosY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), 0xFFFFFFFF);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getPosX() + 3, getPosY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
        }

        float y = this.getPosY() + 2 + (getHeight() / 2) + (CustomFont.INSTANCE.isOn() ? HockeyWare.INSTANCE.fontManager.getTextHeight() : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
        List<String> strings = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(this.getDescription(), (int) this.getWidth() - 1);

        Render2DUtil.drawRect(getPosX(), getPosY() + getHeight(), getPosX() + getWidth(), getPosY() + getHeight() + 3 + ((CustomFont.INSTANCE.isOn() ? HockeyWare.INSTANCE.fontManager.getTextHeight() : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) + 1) * strings.size(), 0x92000000);

        for (String string : strings) {
            if (CustomFont.INSTANCE.isOn()) {
                HockeyWare.INSTANCE.fontManager.drawStringWithShadow(string, this.getPosX() + 3, y, 0xFFFFFFFF);
                y += HockeyWare.INSTANCE.fontManager.getTextHeight() + 1;
            } else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, this.getPosX() + 3, y, 0xFFFFFFFF);
                y += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1;
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
