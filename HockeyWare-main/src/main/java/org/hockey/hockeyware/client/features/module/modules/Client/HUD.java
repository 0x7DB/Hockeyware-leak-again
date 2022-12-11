package org.hockey.hockeyware.client.features.module.modules.Client;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.ColorUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {

    public static final Setting<Boolean> watermark = new Setting<>("Watermark", true);
    public static final Setting<Boolean> arrayList = new Setting<>("ArrayList", true);
    public static final Setting<Boolean> armor = new Setting<>("Armor", true);
    public static final Setting<Boolean> percentage = new Setting<>("ArmorPercentage", false);
    public static final Setting<Boolean> coords = new Setting<>("Coords", true);
    public static final Setting<Boolean> direction = new Setting<>("Direction", true);
    private static final RenderItem itemRender = mc.getRenderItem();
    public static HUD INSTANCE;
    private String direction1;
    private String nesw;
    private int y;

    public HUD() {
        super("HUD", "Toggle HockeyWare's HUD", Category.Client);
        INSTANCE = this;
    }

    private static int TEXT_COLOR() {
        return ClickGUI.color.getValue().getRGB();
    }

    private String getDirection() {
        if (mc.player.getAdjustedHorizontalFacing().getAxisDirection().toString().equals("Towards positive")) {
            direction1 = "+";
        } else if (mc.player.getAdjustedHorizontalFacing().getAxisDirection().toString().equals("Towards negative")) {
            direction1 = "-";
        }
        return direction1;
    }

    private String getAxis() {
        return mc.player.getHorizontalFacing().getAxis().getName().toUpperCase();
    }

    private String getNESW() {
        if (getAxis().equals("X") && getDirection().equals("+")) {
            nesw = "East";
        }
        if (getAxis().equals("X") && getDirection().equals("-")) {
            nesw = "West";
        }
        if (getAxis().equals("Z") && getDirection().equals("+")) {
            nesw = "South";
        }
        if (getAxis().equals("Z") && getDirection().equals("-")) {
            nesw = "North";
        }
        return nesw;
    }

    public String assembleDirection() {
        return getNESW() + ChatFormatting.GRAY + " [" + ChatFormatting.RESET + getDirection() + getAxis() + ChatFormatting.GRAY + "]" + ChatFormatting.RESET;
    }

    public int getDirectionY() {
        if (mc.currentScreen instanceof GuiChat && coords.getValue()) {
            y = 33;
        }
        if (mc.currentScreen instanceof GuiChat && !coords.getValue()) {
            y = 24;
        }
        if (!(mc.currentScreen instanceof GuiChat) && coords.getValue()) {
            y = 19;
        }
        if (!(mc.currentScreen instanceof GuiChat) && !coords.getValue()) {
            y = 10;
        }
        return y;
    }

    @Override
    public void onRender2D() {
        GlStateManager.pushMatrix();

        ScaledResolution resolution = new ScaledResolution(Globals.mc);

        if (watermark.getValue()) {
            if (CustomFont.INSTANCE.isOn()) {
                HockeyWare.INSTANCE.fontManager.drawStringWithShadow(HockeyWare.NAME + " " + HockeyWare.VERSION, 2.0f, 2.0f, TEXT_COLOR());
            } else {
                Globals.mc.fontRenderer.drawStringWithShadow(HockeyWare.NAME + " " + HockeyWare.VERSION, 2.0f, 2.0f, TEXT_COLOR());
            }
        }

        if (coords.getValue()) {

            long netherX = (long) (mc.player.posX / 8);
            long netherY = (long) (mc.player.posY);
            long netherZ = (long) (mc.player.posZ / 8);

            long currX = (long) (mc.player.posX);
            long currY = (long) (mc.player.posY);
            long currZ = (long) (mc.player.posZ);

            long netherX2 = (long) (Math.round((netherX) * 10.0) / 10.0);
            long netherY2 = (long) (Math.round((netherY) * 10.0) / 10.0);
            long netherZ2 = (long) (Math.round((netherZ) * 10.0) / 10.0);

            long currX2 = (long) (Math.round((currX) * 10.0) / 10.0);
            long currY2 = (long) (Math.round((currY) * 10.0) / 10.0);
            long currZ2 = (long) (Math.round((currZ) * 10.0) / 10.0);

            String s = ChatFormatting.GRAY + "XYZ " + ChatFormatting.RESET + currX2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currY2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currZ2 + ChatFormatting.GRAY + " [" + ChatFormatting.RESET + netherX2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + netherY2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + netherZ2 + ChatFormatting.GRAY + "]" + ChatFormatting.RESET;
            String s1 = ChatFormatting.GRAY + "XYZ " + ChatFormatting.RESET + currX2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currY2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currZ2 + ChatFormatting.GRAY + " [" + ChatFormatting.RESET + currX2 * 8 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currY2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currZ2 * 8 + ChatFormatting.GRAY + "]" + ChatFormatting.RESET;
            String s2 = ChatFormatting.GRAY + "XYZ " + ChatFormatting.RESET + currX2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currY2 + ChatFormatting.GRAY + ", " + ChatFormatting.RESET + currZ2 + ChatFormatting.RESET;
            if (CustomFont.INSTANCE.isOn()) {
                if (mc.player.dimension == 0) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(s, 2.0f, mc.currentScreen instanceof GuiChat ? resolution.getScaledHeight() - 24 : resolution.getScaledHeight() - 10, 0xFFFFFF);
                } else if (mc.player.dimension == -1) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(s1, 2.0f, mc.currentScreen instanceof GuiChat ? resolution.getScaledHeight() - 24 : resolution.getScaledHeight() - 10, 0xFFFFFF);
                } else if (mc.player.dimension == 1) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(s2, 2.0f, mc.currentScreen instanceof GuiChat ? resolution.getScaledHeight() - 24 : resolution.getScaledHeight() - 10, 0xFFFFFF);
                }
            } else {
                if (mc.player.dimension == 0) {
                    mc.fontRenderer.drawStringWithShadow(s, 2.0f, mc.currentScreen instanceof GuiChat ? resolution.getScaledHeight() - 24 : resolution.getScaledHeight() - 10, 0xFFFFFF);
                } else if (mc.player.dimension == -1) {
                    mc.fontRenderer.drawStringWithShadow(s1, 2.0f, mc.currentScreen instanceof GuiChat ? resolution.getScaledHeight() - 24 : resolution.getScaledHeight() - 10, 0xFFFFFF);
                } else if (mc.player.dimension == 1) {
                    mc.fontRenderer.drawStringWithShadow(s2, 2.0f, mc.currentScreen instanceof GuiChat ? resolution.getScaledHeight() - 24 : resolution.getScaledHeight() - 10, 0xFFFFFF);
                }
            }
        }
        //fps and watermark broken
        if (direction.getValue()) {
            if (CustomFont.INSTANCE.isOn()) {
                if (mc.player.dimension == 0) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(assembleDirection(), 2.0f, resolution.getScaledHeight() - getDirectionY(), 0xFFFFFF);
                } else if (mc.player.dimension == -1) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(assembleDirection(), 2.0f, resolution.getScaledHeight() - getDirectionY(), 0xFFFFFF);
                } else if (mc.player.dimension == 1) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(assembleDirection(), 2.0f, resolution.getScaledHeight() - getDirectionY(), 0xFFFFFF);
                }
            } else {
                if (mc.player.dimension == 0) {
                    mc.fontRenderer.drawStringWithShadow(assembleDirection(), 2.0f, resolution.getScaledHeight() - getDirectionY(), 0xFFFFFF);
                } else if (mc.player.dimension == -1) {
                    mc.fontRenderer.drawStringWithShadow(assembleDirection(), 2.0f, resolution.getScaledHeight() - getDirectionY(), 0xFFFFFF);
                } else if (mc.player.dimension == 1) {
                    mc.fontRenderer.drawStringWithShadow(assembleDirection(), 2.0f, resolution.getScaledHeight() - getDirectionY(), 0xFFFFFF);
                }
            }
        }

        if (armor.getValue()) {
            GlStateManager.enableTexture2D();
            int i = resolution.getScaledWidth() / 2;
            int iteration = 0;
            int y = resolution.getScaledHeight() - 55;
            for (ItemStack is : mc.player.inventory.armorInventory) {
                ++iteration;
                if (is.isEmpty()) continue;
                int x = i - 90 + (9 - iteration) * 20 + 2;
                GlStateManager.enableDepth();
                HUD.itemRender.zLevel = 200.0f;
                itemRender.renderItemAndEffectIntoGUI(is, x, y);
                itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
                HUD.itemRender.zLevel = 0.0f;
                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                String s = is.getCount() > 1 ? is.getCount() + "" : "";
                if (CustomFont.INSTANCE.isOn()) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(s, (float) (x + 19 - 2 - HockeyWare.INSTANCE.fontManager.getStringWidth(s)), (float) (y + 9), 0xFFFFFF);
                } else {
                    mc.fontRenderer.drawStringWithShadow(s, (float) (x + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (float) (y + 9), 0xFFFFFF);
                }
                if (!percentage.getValue()) continue;
                float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                float red = 1.0f - green;
                int dmg = 100 - (int) (red * 100.0f);
                if (CustomFont.INSTANCE.isOn()) {
                    HockeyWare.INSTANCE.fontManager.drawStringWithShadow(dmg + "", (float) (x + 8 - HockeyWare.INSTANCE.fontManager.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toHex((int) (red * 255.0f), (int) (green * 255.0f), 0));
                } else {
                    mc.fontRenderer.drawStringWithShadow(dmg + "", (float) (x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2), (float) (y - 11), ColorUtil.toHex((int) (red * 255.0f), (int) (green * 255.0f), 0));
                }
            }
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        if (arrayList.getValue()) {
            List<Module> modules = getHockey().moduleManager.getModules().stream()
                    .filter((module) -> module.isToggled(true) && module.isDrawn())
                    .collect(Collectors.toList());

            if (!modules.isEmpty()) {
                modules.sort(Comparator.comparingInt((mod) -> -(CustomFont.INSTANCE.isOn() ? HockeyWare.INSTANCE.fontManager.getStringWidth(mod.getFullDisplay()) : Globals.mc.fontRenderer.getStringWidth(mod.getFullDisplay()))));

                float y = 2.0f;
                for (Module module : modules) {
                    String display = module.getFullDisplay();
                    if (CustomFont.INSTANCE.isOn()) {
                        HockeyWare.INSTANCE.fontManager.drawStringWithShadow(display, resolution.getScaledWidth() - HockeyWare.INSTANCE.fontManager.getStringWidth(display) - 2.0f, y, TEXT_COLOR());
                        y += HockeyWare.INSTANCE.fontManager.getTextHeight() + 1.5f;
                    } else {
                        Globals.mc.fontRenderer.drawStringWithShadow(display, resolution.getScaledWidth() - Globals.mc.fontRenderer.getStringWidth(display) - 2.0f, y, TEXT_COLOR());
                        y += Globals.mc.fontRenderer.FONT_HEIGHT + 1.5f;
                    }
                }
            }
        }
        GlStateManager.popMatrix();
    }
}