package org.hockey.hockeyware.client.gui.component.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.setting.ColorSetting;
import org.hockey.hockeyware.client.setting.SettingResult;
import org.hockey.hockeyware.client.util.client.ClientMessage;
import org.hockey.hockeyware.client.util.math.MathUtil;
import org.hockey.hockeyware.client.util.render.Render2DUtil;
import org.hockey.hockeyware.client.util.render.RenderUtil;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ColorComponent extends Component {
    private final ColorSetting colorSetting;
    private boolean colorExtended, colorSelectorDragging, alphaSelectorDragging, hueSelectorDragging;
    private float hue, saturation, brightness, alpha;
    private boolean slidingSpeed, slidingSaturation, slidingBrightness;

    public ColorComponent(ColorSetting colorSetting, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(colorSetting.getName(), posX, posY, offsetX, offsetY, width, height);
        this.colorSetting = colorSetting;
        float[] hsb = Color.RGBtoHSB(getColorSetting().getValue().getRed(), getColorSetting().getValue().getGreen(), getColorSetting().getValue().getBlue(), null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
        alpha = getColorSetting().getValue().getAlpha() / 255.f;
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + 7 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), 0xFFFFFFFF);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getFinishedX() + 5, getFinishedY() + 7 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
        }
        Render2DUtil.drawBorderedRect(getFinishedX() + getWidth() - 20, getFinishedY() + 4, getFinishedX() + getWidth() - 5, getFinishedY() + 11, 0.5f, getColorSetting().getRGB(), 0xff000000);

        setHeight(isColorExtended() ? (154 + (getColorSetting().isRainbow() ? 13 : 0)) : 14);
        if (isColorExtended()) {
            final float expandedX = getFinishedX() + 1;
            final float expandedY = getFinishedY() + 14;

            final float colorPickerLeft = expandedX + 6;
            final float colorPickerTop = expandedY + 1;
            final float colorPickerRight = colorPickerLeft + (getWidth() - 20);
            final float colorPickerBottom = colorPickerTop + (getHeight() - ((68 + (getColorSetting().isRainbow() ? 56 : 0))));

            final int selectorWhiteOverlayColor = new Color(0xFF, 0xFF, 0xFF, 180).getRGB();

            int colorMouseX = (int) MathUtil.clamp(mouseX, colorPickerLeft, colorPickerRight);
            int colorMouseY = (int) MathUtil.clamp(mouseY, colorPickerTop, colorPickerBottom);

            // Color picker

            Render2DUtil.drawRect(colorPickerLeft - 0.5F, colorPickerTop - 0.5F,
                    colorPickerRight + 0.5F, colorPickerBottom + 0.5F, 0xFF000000);

            drawColorPickerRect(colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);

            float colorSelectorX = saturation * (colorPickerRight - colorPickerLeft);
            float colorSelectorY = (1 - brightness) * (colorPickerBottom - colorPickerTop);

            if (colorSelectorDragging) {
                float wWidth = colorPickerRight - colorPickerLeft;
                float xDif = colorMouseX - colorPickerLeft;
                this.saturation = xDif / wWidth;
                colorSelectorX = xDif;

                float hHeight = colorPickerBottom - colorPickerTop;
                float yDif = colorMouseY - colorPickerTop;
                this.brightness = 1 - (yDif / hHeight);
                colorSelectorY = yDif;

                updateColor(Color.HSBtoRGB(hue, saturation, brightness));
            }

            // Color selector

            final float csLeft = colorPickerLeft + colorSelectorX - 0.5f;
            final float csTop = colorPickerTop + colorSelectorY - 0.5f;
            final float csRight = colorPickerLeft + colorSelectorX + 0.5f;
            final float csBottom = colorPickerTop + colorSelectorY + 0.5f;


            Render2DUtil.drawRect(csLeft - 1, csTop - 1, csLeft, csBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(csRight, csTop - 1, csRight + 1, csBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(csLeft, csTop - 1, csRight, csTop,
                    0xFF000000);

            Render2DUtil.drawRect(csLeft, csBottom, csRight, csBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(csLeft, csTop, csRight, csBottom, selectorWhiteOverlayColor);


            // Hue bar

            final float hueSliderLeft = colorPickerRight + 2;
            final float hueSliderRight = hueSliderLeft + 4;

            int hueMouseY = (int) MathUtil.clamp(mouseY, colorPickerTop, colorPickerBottom);

            final float hueSliderYDif = colorPickerBottom - colorPickerTop;

            float hueSelectorY = (1 - this.hue) * hueSliderYDif;

            if (hueSelectorDragging) {
                float yDif = hueMouseY - colorPickerTop;
                this.hue = 1 - (yDif / hueSliderYDif);
                hueSelectorY = yDif;

                updateColor(Color.HSBtoRGB(hue, saturation, brightness));
            }

            Render2DUtil.drawRect(hueSliderLeft - 0.5F, colorPickerTop - 0.5F, hueSliderRight + 0.5F, colorPickerBottom + 0.5F,
                    0xFF000000);

            final float inc = 0.2F;
            final float times = 1 / inc;
            final float sHeight = colorPickerBottom - colorPickerTop;
            final float size = sHeight / times;
            float sY = colorPickerTop;

            // Draw colored hue bar
            for (int i = 0; i < times; i++) {
                boolean last = i == times - 1;
                Render2DUtil.drawGradientRect(hueSliderLeft, sY, hueSliderRight,
                        sY + size, false,
                        Color.HSBtoRGB(1 - inc * i, 1.0F, 1.0F),
                        Color.HSBtoRGB(1 - inc * (i + 1), 1.0F, 1.0F));
                if (!last)
                    sY += size;
            }

            // Hue Selector

            final float hsTop = colorPickerTop + hueSelectorY - 0.5f;
            final float hsBottom = colorPickerTop + hueSelectorY + 0.5f;

            Render2DUtil.drawRect(hueSliderLeft - 1, hsTop - 1, hueSliderLeft, hsBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(hueSliderRight, hsTop - 1, hueSliderRight + 1, hsBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(hueSliderLeft, hsTop - 1, hueSliderRight, hsTop,
                    0xFF000000);

            Render2DUtil.drawRect(hueSliderLeft, hsBottom, hueSliderRight, hsBottom + 1,
                    0xFF000000);

            Render2DUtil.drawRect(hueSliderLeft, hsTop, hueSliderRight, hsBottom, selectorWhiteOverlayColor);


            // Alpha bar

            final float alphaSliderTop = colorPickerBottom + 2;
            final float alphaSliderBottom = alphaSliderTop + 4;

            int color = Color.HSBtoRGB(hue, saturation, brightness);

            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;

            final float hsHeight = colorPickerRight - colorPickerLeft;

            float alphaSelectorX = alpha * hsHeight;

            if (alphaSelectorDragging) {
                float xDif = colorMouseX - colorPickerLeft;
                this.alpha = xDif / hsHeight;
                alphaSelectorX = xDif;

                updateColor(new Color(r, g, b, (int) (alpha * 255)).getRGB());
            }

            Render2DUtil.drawRect(colorPickerLeft - 0.5F, alphaSliderTop - 0.5F, colorPickerRight + 0.5F, alphaSliderBottom + 0.5F, 0xFF000000);

            Render2DUtil.drawCheckeredBackground(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);

            Render2DUtil.drawGradientRect(colorPickerLeft, alphaSliderTop, colorPickerRight,
                    alphaSliderBottom, true,
                    new Color(r, g, b, 0).getRGB(),
                    new Color(r, g, b, 255).getRGB());

            // Alpha selector

            final float asLeft = colorPickerLeft + alphaSelectorX - 0.5f;
            final float asRight = colorPickerLeft + alphaSelectorX + 0.5f;


            Render2DUtil.drawRect(asLeft - 1,
                    alphaSliderTop,
                    asRight + 1,
                    alphaSliderBottom,
                    0xFF000000);

            Render2DUtil.drawRect(asLeft,
                    alphaSliderTop,
                    asRight,
                    alphaSliderBottom,
                    selectorWhiteOverlayColor);


         /*
            // Buttons

            Render2DUtil.drawGradientRect(colorPickerLeft, alphaSliderBottom + 2, colorPickerLeft + ((getWidth() - 16) / 2), alphaSliderBottom + 14, false, ClickGUI.color.getValue().getRGB(), ClickGUI.color.getValue().darker().darker().getRGB());
            Render2DUtil.drawBorderedRect(colorPickerLeft, alphaSliderBottom + 2, colorPickerLeft + ((getWidth() - 16) / 2), alphaSliderBottom + 14, 0.5f, 0, 0xff000000);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Copy", colorPickerLeft + ((getWidth() - 16) / 2) / 2 - (Minecraft.getMinecraft().fontRenderer.getStringWidth("Copy") >> 1), alphaSliderBottom + 8 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);

            Render2DUtil.drawGradientRect(hueSliderRight - ((getWidth() - 16) / 2), alphaSliderBottom + 2, hueSliderRight, alphaSliderBottom + 14, false, ClickGUI.color.getValue().getRGB(), ClickGUI.color.getValue().darker().darker().getRGB());
            Render2DUtil.drawBorderedRect(hueSliderRight - ((getWidth() - 16) / 2), alphaSliderBottom + 2, hueSliderRight, alphaSliderBottom + 14, 0.5f, 0, 0xff000000);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Paste", hueSliderRight - ((getWidth() - 16) / 4) - (Minecraft.getMinecraft().fontRenderer.getStringWidth("Paste") >> 1), alphaSliderBottom + 8 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);

          */
        }

        if (getColorSetting().isSync() || getColorSetting().isRainbow()) {
            float[] hsb = Color.RGBtoHSB(getColorSetting().getRed(), getColorSetting().getGreen(), getColorSetting().getBlue(), null);
            if (hue != hsb[0] || saturation != hsb[1] || brightness != hsb[2] || alpha != getColorSetting().getAlpha() / 255.f) {
                hue = hsb[0];
                saturation = hsb[1];
                brightness = hsb[2];
                alpha = getColorSetting().getAlpha() / 255.f;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX() + getWidth() - 20, getFinishedY() + 4, 15, 7);
            if (isColorExtended()) {
                final float expandedX = getFinishedX() + 1;
                final float expandedY = getFinishedY() + 14;

                final float colorPickerLeft = expandedX + 6;
                final float colorPickerTop = expandedY + 1;
                final float colorPickerRight = colorPickerLeft + (getWidth() - 20);
                final float colorPickerBottom = colorPickerTop + (getHeight() - ((68 + (getColorSetting().isRainbow() ? 56 : 0))));

                final float alphaSliderTop = colorPickerBottom + 2;
                final float alphaSliderBottom = alphaSliderTop + 4;

                final float hueSliderLeft = colorPickerRight + 2;
                final float hueSliderRight = hueSliderLeft + 4;

                final boolean hoveredSync = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + 16, 12, 12);
                final boolean hoveredRainbow = RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderRight - 12, alphaSliderBottom + 30, 12, 12);
                final float smallWidth = hueSliderRight - colorPickerLeft;
                if (hoveredRainbow) {
                    getColorSetting().setRainbow(!getColorSetting().isRainbow());
                }

                if (!getColorSetting().isSync()) {
                    if (!(getColorSetting().isRainbow() && !getColorSetting().isStaticRainbow())) {
                        if (!hoveredRainbow && !hoveredSync) {
                            if (RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, colorPickerTop - (32 + (getColorSetting().isRainbow() ? 56 : 0)), (getWidth() - 20), (getHeight() - 36)))
                                colorSelectorDragging = true;

                            if (RenderUtil.mouseWithinBounds(mouseX, mouseY, hueSliderLeft, colorPickerTop - (32 + (getColorSetting().isRainbow() ? 56 : 0)), 4, (getHeight() - 36)))
                                hueSelectorDragging = true;
                        }
                    }
                }
                if (!hoveredRainbow && !hoveredSync && RenderUtil.mouseWithinBounds(mouseX, mouseY, colorPickerLeft, alphaSliderTop, (getWidth() - 20), 4))
                    alphaSelectorDragging = true;
            }


            if (hovered)
                setColorExtended(!isColorExtended());
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (colorSelectorDragging)
                colorSelectorDragging = false;
            if (alphaSelectorDragging)
                alphaSelectorDragging = false;
            if (hueSelectorDragging)
                hueSelectorDragging = false;
            if (slidingSpeed)
                slidingSpeed = false;
            if (slidingSaturation)
                slidingSaturation = false;
            if (slidingBrightness)
                slidingBrightness = false;
        }
    }

    public static String get32BitString(int value) {
        StringBuilder r = new StringBuilder(Integer.toHexString(value));

        while (r.length() < 8) {
            r.insert(0, 0);
        }

        return r.toString().toUpperCase();
    }

    private void updateColor(int hex) {
        getColorSetting().setValue(new Color(
                hex >> 16 & 0xFF,
                hex >> 8 & 0xFF,
                hex & 0xFF,
                (int) (alpha * 255)));
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom) {
        final int hueBasedColor = Color.HSBtoRGB(hue, 1.0F, 1.0F);

        Render2DUtil.drawGradientRect(left, top, right, bottom, true, 0xFFFFFFFF, hueBasedColor);

        Render2DUtil.drawGradientRect(left, top, right, bottom, false, 0, 0xFF000000);
    }

    public ColorSetting getColorSetting() {
        return colorSetting;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public boolean isColorExtended() {
        return colorExtended;
    }

    public void setColorExtended(boolean colorExtended) {
        this.colorExtended = colorExtended;
    }

    public void setSlidingSpeed(boolean slidingSpeed) {
        this.slidingSpeed = slidingSpeed;
    }

    public void setSlidingSaturation(boolean slidingSaturation) {
        this.slidingSaturation = slidingSaturation;
    }

    public void setSlidingBrightness(boolean slidingBrightness) {
        this.slidingBrightness = slidingBrightness;
    }

}
