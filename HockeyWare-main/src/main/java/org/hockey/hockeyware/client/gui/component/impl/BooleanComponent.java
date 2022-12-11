package org.hockey.hockeyware.client.gui.component.impl;

import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.render.Render2DUtil;
import org.hockey.hockeyware.client.util.render.RenderUtil;

public class BooleanComponent extends Component {
    private final Setting<Boolean> booleanSetting;

    public BooleanComponent(Setting<Boolean> booleanSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(booleanSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.booleanSetting = booleanSetting;
    }


    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 17, getFinishedY() + 1, 12, getHeight() - 2);
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), getBooleanSetting().getValue() ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getBooleanSetting().getValue() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
        Render2DUtil.drawBorderedRect(getFinishedX() + getWidth() - 17, getFinishedY() + 1, getFinishedX() + getWidth() - 5, getFinishedY() + getHeight() - 1, 0.5f, getBooleanSetting().getValue() ? (hovered ? ClickGUI.color.getValue().brighter().getRGB() : ClickGUI.color.getValue().getRGB()) : (hovered ? 0x66333333 : 0), 0xff000000);
        if (getBooleanSetting().getValue())
            Render2DUtil.drawCheckMark(getFinishedX() + getWidth() - 11, getFinishedY() + 1, 10, 0xFFFFFFFF);
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 17, getFinishedY() + 1, 12, getHeight() - 2);
        if (hovered && mouseButton == 0)
            getBooleanSetting().setValue(!getBooleanSetting().getValue());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public Setting<Boolean> getBooleanSetting() {
        return booleanSetting;
    }
}
