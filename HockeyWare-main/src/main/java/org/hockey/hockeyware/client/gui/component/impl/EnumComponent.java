package org.hockey.hockeyware.client.gui.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.render.RenderUtil;

public class EnumComponent<E extends Enum<E>> extends Component {
    private final Setting<E> enumSetting;

    public EnumComponent(Setting<E> enumSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(enumSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.enumSetting = enumSetting;
    }


    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel() + ": " + ChatFormatting.GRAY + getEnumSetting().getValue().name(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), 0xFFFFFFFF);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel() + ": " + ChatFormatting.GRAY + getEnumSetting().getValue().name(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered) {
            if (mouseButton == 0)
                enumSetting.setValue((E) Setting.increase(enumSetting.getValue()));
            else if (mouseButton == 1)
                enumSetting.setValue((E) Setting.decrease(enumSetting.getValue()));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public Setting<E> getEnumSetting() {
        return enumSetting;
    }

}
