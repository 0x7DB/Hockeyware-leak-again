package org.hockey.hockeyware.client.gui.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.setting.Keybind;
import org.hockey.hockeyware.client.util.render.Render2DUtil;
import org.hockey.hockeyware.client.util.render.RenderUtil;
import org.lwjgl.input.Keyboard;

public class KeybindComponent extends Component {
    private final Keybind bindSetting;
    private boolean binding;

    public KeybindComponent(Keybind bindSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(bindSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.bindSetting = bindSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        Render2DUtil.drawBorderedRect(getFinishedX() + 4.5f, getFinishedY() + 1.0f, getFinishedX() + getWidth() - 4.5f, getFinishedY() + getHeight() - 0.5f, 0.5f, hovered ? 0x66333333 : 0, 0xff000000);
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(isBinding() ? "Press a key..." : getBindSetting().getName() + ": " + ChatFormatting.GRAY + Keyboard.getKeyName(getBindSetting().getValue()), getFinishedX() + 6.5f, getFinishedY() + getHeight() - HockeyWare.INSTANCE.fontManager.getTextHeight() - 1f, 0xFFFFFFFF);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(isBinding() ? "Press a key..." : getBindSetting().getName() + ": " + ChatFormatting.GRAY + Keyboard.getKeyName(getBindSetting().getValue()), getFinishedX() + 6.5f, getFinishedY() + getHeight() - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 1f, 0xFFFFFFFF);
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isBinding()) {
            setBinding(false);

            if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK) {
                bindSetting.setValue(Keyboard.KEY_NONE);
                return;
            }

            bindSetting.setValue(keyCode);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + 5, getFinishedY() + 1, getWidth() - 10, getHeight() - 2);
        if (hovered && mouseButton == 0)
            setBinding(!isBinding());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public Keybind getBindSetting() {
        return bindSetting;
    }

    public boolean isBinding() {
        return binding;
    }

    public void setBinding(boolean binding) {
        this.binding = binding;
    }
}
