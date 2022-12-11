package org.hockey.hockeyware.client.gui.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.math.MathUtil;
import org.hockey.hockeyware.client.util.render.Render2DUtil;
import org.hockey.hockeyware.client.util.render.RenderUtil;

public class NumberComponent extends Component {
    private final Setting<Number> numberSetting;
    private boolean sliding;

    public NumberComponent(Setting<Number> numberSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(numberSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.numberSetting = numberSetting;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (numberSetting.getValue().doubleValue() < numberSetting.getMin().doubleValue()) {
            numberSetting.setValue(numberSetting.getMin());
        }

        if (numberSetting.getValue().doubleValue() > numberSetting.getMax().doubleValue()) {
            numberSetting.setValue(numberSetting.getMax());
        }

        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel() + ": " + ChatFormatting.GRAY + getNumberSetting().getValue(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), 0xFFFFFFFF);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel() + ": " + ChatFormatting.GRAY + getNumberSetting().getValue(), getFinishedX() + 5, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
        }
        float length = MathHelper.floor(((getNumberSetting().getValue()).floatValue() - getNumberSetting().getMin().floatValue()) / (getNumberSetting().getMax().floatValue() - getNumberSetting().getMin().floatValue()) * (getWidth() - 10));
        Render2DUtil.drawBorderedRect(getFinishedX() + 5, getFinishedY() + getHeight() - 2.5f, getFinishedX() + 5 + length, getFinishedY() + getHeight() - 0.5f, 0.5f, hovered ? ClickGUI.color.getValue().brighter().getRGB() : ClickGUI.color.getValue().getRGB(), 0xff000000);
        if (sliding) {
            double val = ((mouseX - (getFinishedX() + 5)) * (getNumberSetting().getMax().doubleValue() - getNumberSetting().getMin().doubleValue()) / (getWidth() - 10) + getNumberSetting().getMin().doubleValue());
            getNumberSetting().setValue(getNumberSetting().numberToValue(MathUtil.round(val, 2)));
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered && mouseButton == 0)
            setSliding(true);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isSliding())
            setSliding(false);
    }

    public Setting<Number> getNumberSetting() {
        return numberSetting;
    }

    public boolean isSliding() {
        return sliding;
    }

    public void setSliding(boolean sliding) {
        this.sliding = sliding;
    }
}
