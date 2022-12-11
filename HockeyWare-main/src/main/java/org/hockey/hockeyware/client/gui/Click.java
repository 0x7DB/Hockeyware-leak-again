package org.hockey.hockeyware.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.gui.component.impl.ColorComponent;
import org.hockey.hockeyware.client.gui.component.impl.KeybindComponent;
import org.hockey.hockeyware.client.gui.component.impl.ModuleComponent;
import org.hockey.hockeyware.client.gui.component.impl.StringComponent;
import org.hockey.hockeyware.client.gui.impl.CategoryFrame;
import org.hockey.hockeyware.client.gui.impl.DescriptionFrame;
import org.hockey.hockeyware.client.util.render.Render2DUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Click extends GuiScreen {
    private static final ResourceLocation BLACK_PNG =
            new ResourceLocation("hockeyware:textures/gui/black.png");


    public static DescriptionFrame descriptionFrame =
            new DescriptionFrame(0, 0, 200, 16);

    private final ArrayList<org.hockey.hockeyware.client.gui.Frame> frames = new ArrayList<>();
    private boolean oldVal = false;
    private boolean attached = false;

    public static Click getGUIINSTANCE() {
        return GUIINSTANCE;
    }

    private static final Click GUIINSTANCE = new Click();

    public void init() {
        if (!attached) {
            ClickGUI.descriptionWidth.addObserver(e -> descriptionFrame.setWidth(e.getValue()));
            attached = true;
        }

        getFrames().clear();
        int x = 2;
        int y = 2;
        for (Category moduleCategory : Category.values()) {
            getFrames().add(new CategoryFrame(moduleCategory, x, y, 110, 16));//x + 12 * 2 > new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth()
            if (114 * 2 + x > new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth()) {
                x = 2;
                y += 60;
            } else x += 114;
        }
        descriptionFrame = new DescriptionFrame(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() - ClickGUI.descriptionWidth.getValue() - 10, new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() / 2f, ClickGUI.descriptionWidth.getValue(), 16);
        getFrames().add(descriptionFrame);
        getFrames().forEach(org.hockey.hockeyware.client.gui.Frame::init);
        oldVal = false;
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        init();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (mc.world == null) {
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.mc.getTextureManager().bindTexture(BLACK_PNG);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(0.0D, this.height, 0.0D).tex(0.0D, (float) this.height / 32.0F + (float) 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, this.height, 0.0D).tex((float) this.width / 32.0F, (float) this.height / 32.0F + (float) 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(this.width, 0.0D, 0.0D).tex((float) this.width / 32.0F, 0).color(64, 64, 64, 255).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0).color(64, 64, 64, 255).endVertex();
            tessellator.draw();
        }

        if (oldVal) {
            init();
            oldVal = false;
        }

        if (ClickGUI.blur.getValue()) {
            final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            Render2DUtil.drawBlurryRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), ClickGUI.blurAmount.getValue(), ClickGUI.blurSize.getValue());
        }

        getFrames().forEach(frame -> frame.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);
        getFrames().forEach(frame -> frame.keyTyped(character, keyCode));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        getFrames().forEach(frame -> frame.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        getFrames().forEach(frame -> frame.mouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        getFrames().forEach(frame -> {
            for (org.hockey.hockeyware.client.gui.Component comp : frame.getComponents()) {
                if (comp instanceof ModuleComponent) {
                    final ModuleComponent moduleComponent = (ModuleComponent) comp;
                    for (org.hockey.hockeyware.client.gui.Component component : moduleComponent.getComponents()) {
                        if (component instanceof KeybindComponent) {
                            final KeybindComponent keybindComponent = (KeybindComponent) component;
                            keybindComponent.setBinding(false);
                        }
                        if (component instanceof StringComponent) {
                            final StringComponent stringComponent = (StringComponent) component;
                            stringComponent.setListening(false);
                        }
                    }
                }
            }
        });
    }

    public void onGuiOpened() {
        getFrames().forEach(frame -> {
            for (org.hockey.hockeyware.client.gui.Component comp : frame.getComponents()) {
                if (comp instanceof ModuleComponent) {
                    final ModuleComponent moduleComponent = (ModuleComponent) comp;
                    for (Component component : moduleComponent.getComponents()) {
                        if (component instanceof ColorComponent) {
                            final ColorComponent colorComponent = (ColorComponent) component;
                            float[] hsb = Color.RGBtoHSB(colorComponent.getColorSetting().getRed(), colorComponent.getColorSetting().getGreen(), colorComponent.getColorSetting().getBlue(), null);
                            colorComponent.setHue(hsb[0]);
                            colorComponent.setSaturation(hsb[1]);
                            colorComponent.setBrightness(hsb[2]);
                            colorComponent.setAlpha(colorComponent.getColorSetting().getAlpha() / 255.f);
                        }
                    }
                }
            }
        });
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }
}