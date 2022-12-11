package org.hockey.hockeyware.client.gui.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.gui.component.impl.*;
import org.hockey.hockeyware.client.gui.visibility.Visibilities;
import org.hockey.hockeyware.client.util.render.Render2DUtil;
import org.hockey.hockeyware.client.util.render.RenderUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryFrame extends org.hockey.hockeyware.client.gui.Frame implements Globals {
    private final Category moduleCategory;

    private static final ResourceLocation PNG =
            new ResourceLocation("textures/arrow.png");

    public static void drawCompleteImage(final float posX, final float posY, final int width, final int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float)height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float)width, (float)height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float)width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public CategoryFrame(Category moduleCategory, float posX, float posY, float width, float height) {
        super(moduleCategory.name(), posX, posY, width, height);
        this.moduleCategory = moduleCategory;
        this.setExtended(true);
    }

    @Override
    public void init() {
        getComponents().clear();
        float offsetY = getHeight() + 1;
        List<Module> moduleList = getHockey().moduleManager.getModules()
                .stream().filter((module) -> module.getCategory().equals(getModuleCategory()))
                .collect(Collectors.toList());

        moduleList.sort(Comparator.comparing(Module::getName));
        for (Module module : moduleList) {
            getComponents().add(new ModuleComponent(module, getPosX(), getPosY(), 0, offsetY, getWidth(), 14));
            offsetY += 14;
        }
        super.init();
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final float scrollMaxHeight = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
        final Color clr = ClickGUI.color.getValue();
        Render2DUtil.drawRect(getPosX() - 1, getPosY(), getPosX() + getWidth() + 1, getPosY() + getHeight(), ClickGUI.color.getValue().getRGB());
        if (CustomFont.INSTANCE.isOn()) {                                                       // x + (width / 2f) - (Safepoint.mc.fontRenderer.getStringWidth(name) / 2f)
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel(), getPosX() + (getWidth() / 2f) - (HockeyWare.INSTANCE.fontManager.getStringWidth(getLabel()) / 2f), getPosY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), 0xFFFFFFFF);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getPosX() + (getWidth() / 2f) - (mc.fontRenderer.getStringWidth(getLabel()) / 2f), getPosY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), 0xFFFFFFFF);
        }
        if (ClickGUI.size.getValue()) {
            String disString = "[" + getComponents().size() + "]";
            if (CustomFont.INSTANCE.isOn()) {
                HockeyWare.INSTANCE.fontManager.drawStringWithShadow(disString, (getPosX() + getWidth() - 3 - HockeyWare.INSTANCE.fontManager.getStringWidth(disString)), (getPosY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1)), 0xFFFFFFFF);
            } else {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(disString, (getPosX() + getWidth() - 3 - Minecraft.getMinecraft().fontRenderer.getStringWidth(disString)), (getPosY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1)), 0xFFFFFFFF);
            }
        }
        if (isExtended()) {
            if (RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY() + getHeight(), getWidth(), (Math.min(getScrollCurrentHeight(), scrollMaxHeight)) + 1) && getScrollCurrentHeight() > scrollMaxHeight) {
                final float scrollSpeed = Math.min(getScrollCurrentHeight(), scrollMaxHeight) / (Minecraft.getDebugFPS() >> 3);
                int wheel = Mouse.getDWheel();
                if (wheel < 0) {
                    if (getScrollY() - scrollSpeed < -(getScrollCurrentHeight() - Math.min(getScrollCurrentHeight(), scrollMaxHeight)))
                        setScrollY((int) -(getScrollCurrentHeight() - Math.min(getScrollCurrentHeight(), scrollMaxHeight)));
                    else setScrollY((int) (getScrollY() - scrollSpeed));
                } else if (wheel > 0) {
                    setScrollY((int) (getScrollY() + scrollSpeed));
                }
            }
            if (getScrollY() > 0) setScrollY(0);
            if (getScrollCurrentHeight() > scrollMaxHeight) {
                if (getScrollY() - 6 < -(getScrollCurrentHeight() - scrollMaxHeight))
                    setScrollY((int) -(getScrollCurrentHeight() - scrollMaxHeight));
            } else if (getScrollY() < 0) setScrollY(0);
            Render2DUtil.drawRect(getPosX(), getPosY() + getHeight(), getPosX() + getWidth(), getPosY() + getHeight() + 1 + (getCurrentHeight()), 0x92000000);
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(getPosX(), getPosY() + getHeight() + 1, getPosX() + getWidth(), getPosY() + getHeight() + scrollMaxHeight + 1);
            getComponents().forEach(component -> component.drawScreen(mouseX, mouseY, partialTicks));
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();
        }
         //   Minecraft.getMinecraft().renderEngine.bindTexture(PNG);
         //   drawCompleteImage(this.getPosX() - 1.5f + this.getWidth() - 13.5f, this.getPosY() - 2.0f - getTextOffset(), 12, 12);
        updatePositions();
    }

    public int getTextOffset() {
        return -4;
    }

    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final float scrollMaxHeight = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - getHeight();
        if (isExtended() && RenderUtil.mouseWithinBounds(mouseX, mouseY, getPosX(), getPosY() + getHeight(), getWidth(), (Math.min(getScrollCurrentHeight(), scrollMaxHeight)) + 1))
            getComponents().forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void updatePositions() {
        float offsetY = getHeight() + 1;
        for (org.hockey.hockeyware.client.gui.Component component : getComponents()) {
            component.setOffsetY(offsetY);
            component.moved(getPosX(), getPosY() + getScrollY());
            if (component instanceof ModuleComponent) {
                if (component.isExtended()) {
                    for (org.hockey.hockeyware.client.gui.Component component1 : ((ModuleComponent) component).getComponents()) {
                        if (component1 instanceof BooleanComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component1).getBooleanSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                        if (component1 instanceof KeybindComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component1).getBindSetting())) {
                                offsetY += component1.getHeight();

                            }
                        }
                        if (component1 instanceof NumberComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component1).getNumberSetting())) {
                                offsetY += component1.getHeight();

                            }
                        }
                        if (component1 instanceof EnumComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component1).getEnumSetting())) {
                                offsetY += component1.getHeight();

                            }
                        }
                        if (component1 instanceof ColorComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component1).getColorSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                        if (component1 instanceof StringComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component1).getStringSetting())) {
                                offsetY += component1.getHeight();
                            }
                        }
                    }
                    offsetY += 3.f;
                }
            }
            offsetY += component.getHeight();
        }
    }

    private float getScrollCurrentHeight() {
        return getCurrentHeight() + getHeight() + 3.f;
    }

    private float getCurrentHeight() {
        float cHeight = 1;
        for (org.hockey.hockeyware.client.gui.Component component : getComponents()) {
            if (component instanceof ModuleComponent) {
                if (component.isExtended()) {
                    for (Component component1 : ((ModuleComponent) component).getComponents()) {
                        if (component1 instanceof BooleanComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component1).getBooleanSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                        if (component1 instanceof KeybindComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component1).getBindSetting())) {
                                cHeight += component1.getHeight();

                            }
                        }
                        if (component1 instanceof NumberComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component1).getNumberSetting())) {
                                cHeight += component1.getHeight();

                            }
                        }
                        if (component1 instanceof EnumComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component1).getEnumSetting())) {
                                cHeight += component1.getHeight();

                            }
                        }
                        if (component1 instanceof ColorComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component1).getColorSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                        if (component1 instanceof StringComponent) {
                            if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component1).getStringSetting())) {
                                cHeight += component1.getHeight();
                            }
                        }
                    }
                    cHeight += 3.f;
                }
            }
            cHeight += component.getHeight();
        }
        return cHeight;
    }


    public Category getModuleCategory() {
        return moduleCategory;
    }
}
