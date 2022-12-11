package org.hockey.hockeyware.client.gui.component.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.gui.Component;
import org.hockey.hockeyware.client.gui.visibility.Visibilities;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Client.ClickGUI;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.setting.ColorSetting;
import org.hockey.hockeyware.client.setting.Keybind;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.render.Render2DUtil;
import org.hockey.hockeyware.client.util.render.RenderUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ModuleComponent extends Component {

    private final Module module;
    private final ArrayList<Component> components = new ArrayList<>();

    private static final ResourceLocation BLACK_PNG =
            new ResourceLocation("textures/gear.png");

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

    public ModuleComponent(Module module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
        super(module.getName(), posX, posY, offsetX, offsetY, width, height);
        this.module = module;
    }

    @Override
    public void init() {
        getComponents().clear();
        float offY = getHeight();
        this.setDescription(getModule().getDescription());

        if (!getModule().getSettings().isEmpty()) {
            for (Setting<?> setting : getModule().getSettings()) {
                float before = offY;
                if (setting instanceof Keybind) {
                    getComponents().add(new KeybindComponent((Keybind) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                } else if (setting instanceof ColorSetting) {
                    getComponents().add(new ColorComponent((ColorSetting) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                } else if (setting.getValue() instanceof Boolean && !setting.getName().equalsIgnoreCase("enabled")) {
                    getComponents().add(new BooleanComponent((Setting<Boolean>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                } else if (setting.getValue() instanceof String) {
                    getComponents().add(new StringComponent((Setting<String>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                } else if (setting.getValue() instanceof Enum) {
                    getComponents().add(new EnumComponent(setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                } else if (setting.getValue() instanceof Number) {
                    getComponents().add(new NumberComponent((Setting<Number>) setting, getFinishedX(), getFinishedY(), 0, offY, getWidth(), 14));
                    offY += 14;
                }
            }
        }

        getComponents().forEach(Component::init);
    }

    @Override
    public void moved(float posX, float posY) {
        super.moved(posX, posY);
        getComponents().forEach(component -> component.moved(getFinishedX(), getFinishedY()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());

        if (hovered)
            Render2DUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, 0x66333333);
        if (getModule().isToggled(true)) {
            Render2DUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, hovered ? ClickGUI.color.getValue().brighter().getRGB() : ClickGUI.color.getValue().getRGB());
        }
        if(isExtended() || module.isOn() || hovered){

        }else{
            Render2DUtil.drawRect(getFinishedX() + 1, getFinishedY() + 0.5f, getFinishedX() + getWidth() - 1, getFinishedY() + getHeight() - 0.5f, 0x66333333);
        }
        if (CustomFont.INSTANCE.isOn()) {
            HockeyWare.INSTANCE.fontManager.drawStringWithShadow(getLabel(), getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), getModule().isToggled(true) ? 0xFFFFFFFF : 0xFFAAAAAA);
            //if (!getComponents().isEmpty())
                //HockeyWare.INSTANCE.fontManager.drawStringWithShadow(isExtended() ? ClickGUI.close.getValue() : ClickGUI.open.getValue(), getFinishedX() + getWidth() - 4 - HockeyWare.INSTANCE.fontManager.getStringWidth(isExtended() ? ClickGUI.close.getValue() : ClickGUI.open.getValue()), getFinishedY() + getHeight() / 2 - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1), getModule().isToggled(true) ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(getLabel(), getFinishedX() + 4, getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getModule().isToggled(true) ? 0xFFFFFFFF : 0xFFAAAAAA);
            //if (!getComponents().isEmpty())
                //Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(isExtended() ? ClickGUI.close.getValue() : ClickGUI.open.getValue(), getFinishedX() + getWidth() - 4 - Minecraft.getMinecraft().fontRenderer.getStringWidth(isExtended() ? ClickGUI.close.getValue() : ClickGUI.open.getValue()), getFinishedY() + getHeight() / 2 - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1), getModule().isToggled(true) ? 0xFFFFFFFF : 0xFFAAAAAA);
        }

        if (ClickGUI.showBind.getValue() && !String.valueOf(Keyboard.getKeyName(getModule().getKeybind())).equalsIgnoreCase("none")) {
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            String disString = String.valueOf(Keyboard.getKeyName(getModule().getKeybind())).toLowerCase().replace("none", "-");
            disString = String.valueOf(disString.charAt(0)).toUpperCase() + disString.substring(1);
            if (disString.length() > 3) {
                disString = disString.substring(0, 3);
            }
            disString = "[" + disString + "]";
            //float offset = getFinishedX() + getWidth() - (CustomFont.INSTANCE.isOn() ? HockeyWare.INSTANCE.fontManager.getStringWidth(isExtended() ? ClickGUI.close.getValue() : ClickGUI.open.getValue()) : Minecraft.getMinecraft().fontRenderer.getStringWidth(isExtended() ? ClickGUI.close.getValue() : ClickGUI.open.getValue()));
            if (CustomFont.INSTANCE.isOn()) {
                //HockeyWare.INSTANCE.fontManager.drawStringWithShadow(disString, (offset - (HockeyWare.INSTANCE.fontManager.getStringWidth(disString) >> 1)) * 2 - 12, (getFinishedY() + getHeight() / 1.5f - (HockeyWare.INSTANCE.fontManager.getTextHeight() >> 1)) * 2.0f, getModule().isToggled(true) ? 0xFFFFFFFF : 0xFFAAAAAA);
            //} else {
                //Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(disString, (offset - (Minecraft.getMinecraft().fontRenderer.getStringWidth(disString) >> 1)) * 2 - 12, (getFinishedY() + getHeight() / 1.5f - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT >> 1)) * 2.0f, getModule().isToggled(true) ? 0xFFFFFFFF : 0xFFAAAAAA);
            }
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.drawScreen(mouseX, mouseY, partialTicks);
                    }
                }
            }
            if (getModule().isToggled(true)) {
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + 3, getFinishedY() + getHeight() + getComponentsSize(), hovered ? ClickGUI.color.getValue().brighter().getRGB() : ClickGUI.color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + 1.0f, getFinishedY() + getHeight() + getComponentsSize(), getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize() + 2, hovered ? ClickGUI.color.getValue().brighter().getRGB() : ClickGUI.color.getValue().getRGB());
                Render2DUtil.drawRect(getFinishedX() + getWidth() - 3.f, getFinishedY() + getHeight() - 0.5f, getFinishedX() + getWidth() - 1.f, getFinishedY() + getHeight() + getComponentsSize(), hovered ? ClickGUI.color.getValue().brighter().getRGB() : ClickGUI.color.getValue().getRGB());
            }
            //BLACK_PNG
        }
        /*
          Minecraft.getMinecraft().renderEngine.bindTexture(BLACK_PNG);
        drawCompleteImage(this.getFinishedX() - 1.5f + this.getWidth() - 9.5f, this.getFinishedY() - 2.0f - getTextOffset(), 9, 9);
         */
        updatePositions();
    }

    public int getTextOffset() {
        return -4;
    }


    @Override
    public void keyTyped(char character, int keyCode) {
        super.keyTyped(character, keyCode);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.keyTyped(character, keyCode);
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        final boolean hovered = RenderUtil.mouseWithinBounds(mouseX, mouseY, getFinishedX(), getFinishedY(), getWidth(), getHeight());
        if (hovered) {
            switch (mouseButton) {
                case 0:
                    getModule().toggle(false);
                    break;
                case 1:
                    if (!getComponents().isEmpty())
                        setExtended(!isExtended());
                    break;
                default:
                    break;
            }
        }
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        if (isExtended()) {
            for (Component component : getComponents()) {
                if (component instanceof BooleanComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof KeybindComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof NumberComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof EnumComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof ColorComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
                if (component instanceof StringComponent) {
                    if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                        component.mouseReleased(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    private float getComponentsSize() {
        float size = 0;
        for (Component component : getComponents()) {
            if (component instanceof BooleanComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof KeybindComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof NumberComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof EnumComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof ColorComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                    size += component.getHeight();
                }
            }
            if (component instanceof StringComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                    size += component.getHeight();
                }
            }
        }
        return size;
    }

    private void updatePositions() {
        float offsetY = getHeight();
        for (Component component : getComponents()) {
            if (component instanceof BooleanComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((BooleanComponent) component).getBooleanSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof KeybindComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((KeybindComponent) component).getBindSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof NumberComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((NumberComponent) component).getNumberSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof EnumComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((EnumComponent) component).getEnumSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof ColorComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((ColorComponent) component).getColorSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
            if (component instanceof StringComponent) {
                if (Visibilities.VISIBILITY_MANAGER.isVisible(((StringComponent) component).getStringSetting())) {
                    component.setOffsetY(offsetY);
                    component.moved(getPosX(), getPosY());
                    offsetY += component.getHeight();
                }
            }
        }
    }

    public Module getModule() {
        return module;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }
}
