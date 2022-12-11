package org.hockey.hockeyware.client.gui.mainMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.util.math.RenderUtil;
import org.hockey.hockeyware.loader.License;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

public class customMainMenu extends GuiScreen {

    private final ResourceLocation greyGalaxyResourceLocation = new ResourceLocation("textures/galaxy.jpg");
    private final ResourceLocation resourceLocation = new ResourceLocation("textures/galaxy.jpg");
    private int y;
    private int x;
    private int singleplayerWidth;
    private int multiplayerWidth;
    private int settingsWidth;
    private int exitWidth;
    private int textHeight;
    private float xOffset;
    private float yOffset;

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }

    public void initGui() {
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
        this.buttonList.add(new TextButton(0, this.x, this.y + 20, "Singleplayer"));
        this.buttonList.add(new TextButton(1, this.x, this.y + 44, "Multiplayer"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 66, "Settings"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 88, "Exit"));
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (CustomFont.INSTANCE.isOn()) {
            if (customMainMenu.isHovered(this.x - HockeyWare.fontManager.getStringWidth("Singleplayer") / 2, this.y + 20, HockeyWare.fontManager.getStringWidth("Singleplayer"), HockeyWare.fontManager.getTextHeight(), mouseX, mouseY)) {
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
            } else if (customMainMenu.isHovered(this.x - HockeyWare.fontManager.getStringWidth("Multiplayer") / 2, this.y + 44, HockeyWare.fontManager.getStringWidth("Multiplayer"), HockeyWare.fontManager.getTextHeight(), mouseX, mouseY)) {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
            } else if (customMainMenu.isHovered(this.x - HockeyWare.fontManager.getStringWidth("Settings") / 2, this.y + 66, HockeyWare.fontManager.getStringWidth("Settings"), HockeyWare.fontManager.getTextHeight(), mouseX, mouseY)) {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            } else if (customMainMenu.isHovered(this.x - HockeyWare.fontManager.getStringWidth("Exit") / 2, this.y + 88, HockeyWare.fontManager.getStringWidth("Exit"), HockeyWare.fontManager.getTextHeight(), mouseX, mouseY)) {
                this.mc.shutdown();
            }
        } else {
            if (customMainMenu.isHovered(this.x - mc.fontRenderer.getStringWidth("Singleplayer") / 2, this.y + 20, mc.fontRenderer.getStringWidth("Singleplayer"), mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
            } else if (customMainMenu.isHovered(this.x - mc.fontRenderer.getStringWidth("Multiplayer") / 2, this.y + 44, mc.fontRenderer.getStringWidth("Multiplayer"), mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
            } else if (customMainMenu.isHovered(this.x - mc.fontRenderer.getStringWidth("Settings") / 2, this.y + 66, mc.fontRenderer.getStringWidth("Settings"), mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            } else if (customMainMenu.isHovered(this.x - mc.fontRenderer.getStringWidth("Exit") / 2, this.y + 88, mc.fontRenderer.getStringWidth("Exit"), mc.fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
                this.mc.shutdown();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.xOffset = -1.0f * (((float) mouseX - (float) this.width / 2.0f) / ((float) this.width / 32.0f));
        this.yOffset = -1.0f * (((float) mouseY - (float) this.height / 2.0f) / ((float) this.height / 18.0f));
        this.x = this.width / 2;
        this.y = this.height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        this.mc.getTextureManager().bindTexture(this.greyGalaxyResourceLocation);
        customMainMenu.drawCompleteImage(-16.0f + this.xOffset, -9.0f + this.yOffset, this.width + 32, this.height + 18);
        super.drawScreen(mouseX, mouseY, partialTicks);

        RenderUtil.drawString2(5, HockeyWare.NAME, this.width / 10 - this.fontRenderer.getStringWidth(HockeyWare.NAME) / 2,
                this.height / 30, new Color(255, 255, 255).getRGB());

        if (CustomFont.INSTANCE.isOn()) {
            int currentX = 10;
            HockeyWare.fontManager.drawString("Signed In As: " + mc.session.getUsername() + "(", (float)currentX, 13.0f, Color.WHITE.getRGB(), false);

            HockeyWare.fontManager.drawString(License.getInstance().getName(), (float)(currentX += mc.fontRenderer.getStringWidth("Signed In As: " + mc.session.getUsername() + "(")), 13.0f, getNameColor(License.getInstance().getAccountType()), false);

            HockeyWare.fontManager.drawString(")", (float)(currentX += mc.fontRenderer.getStringWidth(License.getInstance().getName())), 13.0f, Color.WHITE.getRGB(), false);
        } else {
            int currentX = 10;
            mc.fontRenderer.drawString("Signed In As: " + mc.session.getUsername() + "(", (float)currentX, 13.0f, Color.WHITE.getRGB(), false);

            mc.fontRenderer.drawString(License.getInstance().getName(), (float)(currentX += mc.fontRenderer.getStringWidth("Signed In As: " + mc.session.getUsername() + "(")), 13.0f, getNameColor(License.getInstance().getAccountType()), false);

            mc.fontRenderer.drawString(")", (float)(currentX += mc.fontRenderer.getStringWidth(License.getInstance().getName())), 13.0f, Color.WHITE.getRGB(), false);
        }
    }

    public BufferedImage parseBackground(BufferedImage background) {
        int height;
        int width = 1920;
        int srcWidth = background.getWidth();
        int srcHeight = background.getHeight();
        for (height = 1080; width < srcWidth || height < srcHeight; width *= 2, height *= 2) {
        }
        BufferedImage imgNew = new BufferedImage(width, height, 2);
        Graphics g = imgNew.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.dispose();
        return imgNew;
    }

    private static class TextButton
            extends GuiButton {
        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, HockeyWare.fontManager.getStringWidth(buttonText), HockeyWare.fontManager.getTextHeight(), buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.enabled = true;
                this.hovered = (float) mouseX >= (float) this.x - (float) HockeyWare.fontManager.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                if (CustomFont.INSTANCE.isOn()) {
                    HockeyWare.fontManager.drawStringWithShadow(this.displayString, (float) this.x - (float) HockeyWare.fontManager.getStringWidth(this.displayString) / 2.0f, this.y, Color.WHITE.getRGB());
                } else
                    mc.fontRenderer.drawStringWithShadow(this.displayString, (float) this.x - (float) mc.fontRenderer.getStringWidth(this.displayString) / 2.0f, this.y, Color.WHITE.getRGB());

                if (this.hovered) {
                    RenderUtil.drawLine((float) (this.x - 1) - (float) HockeyWare.fontManager.getStringWidth(this.displayString) / 2.0f, this.y + 2 + HockeyWare.fontManager.getTextHeight(), (float) this.x + (float) HockeyWare.fontManager.getStringWidth(this.displayString) / 2.0f + 1.0f, this.y + 2 + HockeyWare.fontManager.getTextHeight(), 1.0f, Color.WHITE.getRGB());
                }
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return this.enabled && this.visible && (float) mouseX >= (float) this.x - (float) HockeyWare.fontManager.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }

    public int getNameColor(String type)
    {
        int code = 0;
        switch(type)
        {
            case "Normal":
            {
                code = new Color(0, 170, 170).getRGB();
                break;
            }
            case "Beta":
            {
                code = new Color(255, 170, 0).getRGB();
                break;
            }
            case "Developer":
                code = new Color(170, 0, 0).getRGB();
                break;
        }
        return code;
    }
}