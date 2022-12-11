package org.hockey.hockeyware.client.features.module.modules.Render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.FramebufferShader;
import org.hockey.hockeyware.client.features.module.modules.Render.shaderChams.shaders.*;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.MathUtil;

import java.awt.*;
import java.util.Objects;

public class ShaderChams extends Module {
    public final Setting<ShaderModes> mode = new Setting<>("Mode", ShaderModes.DarkMatter);
    private final Setting<Boolean> players = new Setting<>("Players", false);
    private final Setting<Boolean> crystals = new Setting<>("Crystals", true);
    private final Setting<Boolean> mobs = new Setting<>("Mobs", false);
    private final Setting<Boolean> animals = new Setting<>("Animals", false);
    private final Setting<Boolean> enderPearls = new Setting<>("Ender Pearls", false);
    private final Setting<Boolean> itemsEntity = new Setting<>("Items(Entity)", false);
    public final Setting<Boolean> items = new Setting<>("Items", true);

    private final Setting<Integer> animationSpeed = new Setting<>("Animation Speed", 0, 1, 10);

//    private final Setting<Boolean> blur = new Setting<>("Blur", true);
//    private final Setting<Float> radius = new Setting<>("Radius", 2f, 0.1f, 10);
//    private final Setting<Float> mix = new Setting<>("Mix", 1f, 0, 1);
//    private final Setting<Float> red = new Setting<>("Red", 1f, 0, 1);
//    private final Setting<Float> green = new Setting<>("Green", 1f, 0, 1);
//    private final Setting<Float> blue = new Setting<>("Blue", 1f, 0, 1);
//    private final Setting<Integer> quality = new Setting<>("Quality",1, 0, 20);
//    private final Setting<Boolean> gradientAlpha = new Setting<>("Gradient Alpha", true);
//    private final Setting<Integer> alphaGradient = new Setting<>("Alpha Gradient Value",255, 0, 255);
//    private final Setting<Float> duplicateOutline = new Setting<>("Duplicate Outline",1f, 0, 20);
//    private final Setting<Float> moreGradientOutline = new Setting<>("More Gradient",1f, 0, 10);
//    private final Setting<Float> creepyOutline = new Setting<>("Creepy",1f, 0, 20);
//    private final Setting<Float> alpha = new Setting<>("Alpha", 1f, 0, 1);
//    private final Setting<Integer> numOctavesOutline = new Setting<>("Num Octaves",1, 1, 30);
//    private final Setting<Float> speedOutline = new Setting<>("Speed", 0f, 0.001, 0.1);

    public static ShaderChams instance;
    public static boolean itemsFix = true;

    private boolean criticalSection = false;

    public ShaderChams() {
        super("ShaderChams", "Allows You To Render Shaders On Items And Entities", Category.Render);

        instance = this;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(items.getValue() && itemsFix && (!criticalSection)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        try {
            {
                FramebufferShader framebufferShader = null;
                boolean itemglow = false, gradient = false, glow = false, outline = false;

                switch (mode.getValue().name()) {
                    case "Outline":
                        framebufferShader = OutlineShader.Outline_Shader;
                        outline = true;
                        break;
                    case "ItemGlow":
                        framebufferShader = ItemGlowShader.ItemGlow_Shader;
                        itemglow = true;
                        break;
                    case "Glow":
                        framebufferShader = GlowShader.Glow_Shader;
                        glow = true;
                        break;
                    case "Gradient":
                        framebufferShader = GradientOutlineShader.INSTANCE;
                        gradient = true;
                        break;
                    case "Aqua":
                        framebufferShader = AquaShader.Aqua_Shader;
                        break;
                    case "Red":
                        framebufferShader = RedShader.Red_Shader;
                        break;
                    case "Smoke":
                        framebufferShader = SmokeShader.Smoke_Shader;
                        break;
                    case "Flow":
                        framebufferShader = FlowShader.Flow_Shader;
                        break;
                    case "Purple":
                        framebufferShader = PurpleShader.Purple_Shader;
                        break;
                    case "BlueFlames":
                        framebufferShader = BlueFlamesShader.BlueFlames_Shader;
                        break;
                    case "AquaSmoke":
                        framebufferShader = AquaSmokeShader.AquaSmoke_Shader;
                        break;
                    case "Galaxy":
                        framebufferShader = GalaxyShader.Galaxy_Shader;
                        break;
                    case "RedGalaxy":
                        framebufferShader = RedGalaxyShader.RedGalaxy_Shader;
                        break;
                    case "DarkMatter":
                        framebufferShader = DarkMatterShader.DarkMatter_Shader;
                        break;
                    case "Vapor":
                        framebufferShader = VaporShader.Vapor_Shader;
                        break;
                    case "PurpleGalaxy":
                        framebufferShader = PurpleGalaxyShader.PurpleGalaxy_Shader;
                        break;
                    case "RainbowGalaxy":
                        framebufferShader = RainbowGalaxyShader.RainbowGalaxy_Shader;
                        break;
                    case "Smoky":
                        framebufferShader = SmokyShader.Smoky_Shader;
                        break;
                    case "Snow":
                        framebufferShader = SnowShader.Snow_Shader;
                        break;
                    case "Techno":
                        framebufferShader = TechnoShader.Techno_Shader;
                        break;
                }

                if (framebufferShader == null) return;

                framebufferShader.animationSpeed = animationSpeed.getValue();

                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.pushMatrix();
//                if (itemglow) {
//                    ((ItemGlowShader) framebufferShader).red = getColor().getRed() / 255f;
//                    ((ItemGlowShader) framebufferShader).green = getColor().getGreen() / 255f;
//                    ((ItemGlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
//                    ((ItemGlowShader) framebufferShader).radius = radius.getValue();
//                    ((ItemGlowShader) framebufferShader).quality = quality.getValue();
//                    ((ItemGlowShader) framebufferShader).blur = blur.getValue();
//                    ((ItemGlowShader) framebufferShader).mix = mix.getValue();
//                    ((ItemGlowShader) framebufferShader).alpha = 1f;
//                    ((ItemGlowShader) framebufferShader).useImage = false;
//                } else if (gradient) {
//                    ((GradientOutlineShader) framebufferShader).color = getColor();
//                    ((GradientOutlineShader) framebufferShader).radius = radius.getValue();
//                    ((GradientOutlineShader) framebufferShader).quality = quality.getValue();
//                    ((GradientOutlineShader) framebufferShader).gradientAlpha = gradientAlpha.getValue();
//                    ((GradientOutlineShader) framebufferShader).alphaOutline = alphaGradient.getValue();
//                    ((GradientOutlineShader) framebufferShader).duplicate = duplicateOutline.getValue();
//                    ((GradientOutlineShader) framebufferShader).moreGradient = moreGradientOutline.getValue();
//                    ((GradientOutlineShader) framebufferShader).creepy = creepyOutline.getValue();
//                    ((GradientOutlineShader) framebufferShader).alpha = alpha.getValue();
//                    ((GradientOutlineShader) framebufferShader).numOctaves = numOctavesOutline.getValue();
//                } else if (glow) {
//                    ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
//                    ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
//                    ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
//                    ((GlowShader) framebufferShader).radius = radius.getValue();
//                    ((GlowShader) framebufferShader).quality = quality.getValue();
//                } else if (outline) {
//                    ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
//                    ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
//                    ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
//                    ((OutlineShader) framebufferShader).radius = radius.getValue();
//                    ((OutlineShader) framebufferShader).quality = quality.getValue();
//                }
                framebufferShader.startDraw(event.getPartialTicks());
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity == mc.player || entity == mc.getRenderViewEntity()) continue;
                    if (!((entity instanceof EntityPlayer && players.getValue())
                            || (entity instanceof EntityEnderCrystal && crystals.getValue())
                            || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValue())
                            || ((entity instanceof EntityEnderPearl) && enderPearls.getValue())
                            || ((entity instanceof EntityItem) && itemsEntity.getValue())
                            || (entity instanceof EntityAnimal && animals.getValue()))) continue;
                    Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                    Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                }
                framebufferShader.stopDraw();
//                if (gradient) ((GradientOutlineShader) framebufferShader).update(speedOutline.getValue());
                GlStateManager.color(1f, 1f, 1f);
                GlStateManager.matrixMode(5889);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.popMatrix();
            }

            if (items.getValue() && mc.gameSettings.thirdPersonView == 0) {
                    FramebufferShader framebufferShader = null;
                    boolean itemglow = false, gradient = false, glow = false, outline = false;
                    switch (mode.getValue().name()) {
                        case "Outline":
                            framebufferShader = OutlineShader.Outline_Shader;
                            outline = true;
                            break;
                        case "ItemGlow":
                            framebufferShader = ItemGlowShader.ItemGlow_Shader;
                            itemglow = true;
                            break;
                        case "Glow":
                            framebufferShader = GlowShader.Glow_Shader;
                            glow = true;
                            break;
                        case "Gradient":
                            framebufferShader = GradientOutlineShader.INSTANCE;
                            gradient = true;
                            break;
                        case "DarkMatter":
                            framebufferShader = DarkMatterShader.DarkMatter_Shader;
                            break;
                        case "Smoky":
                            framebufferShader = SmokyShader.Smoky_Shader;
                            break;
                        case "Smoke":
                            framebufferShader = SmokeShader.Smoke_Shader;
                            break;
                        case "Aqua":
                            framebufferShader = AquaShader.Aqua_Shader;
                            break;
                        case "AquaSmoke":
                            framebufferShader = AquaSmokeShader.AquaSmoke_Shader;
                            break;
                        case "Techno":
                            framebufferShader = TechnoShader.Techno_Shader;
                            break;
                        case "Red":
                            framebufferShader = RedShader.Red_Shader;
                            break;
                        case "Flow":
                            framebufferShader = FlowShader.Flow_Shader;
                            break;
                        case "Purple":
                            framebufferShader = PurpleShader.Purple_Shader;
                            break;
                        case "BlueFlames":
                            framebufferShader = BlueFlamesShader.BlueFlames_Shader;
                            break;
                        case "Galaxy":
                            framebufferShader = GalaxyShader.Galaxy_Shader;
                            break;
                        case "RedGalaxy":
                            framebufferShader = RedGalaxyShader.RedGalaxy_Shader;
                            break;
                        case "PurpleGalaxy":
                            framebufferShader = PurpleGalaxyShader.PurpleGalaxy_Shader;
                            break;
                        case "RainbowGalaxy":
                            framebufferShader = RainbowGalaxyShader.RainbowGalaxy_Shader;
                            break;
                        case "HolyFuck":
                            framebufferShader = VaporShader.Vapor_Shader;
                            break;
                        case "Snow":
                            framebufferShader = SnowShader.Snow_Shader;
                            break;
                    }

                    if (framebufferShader == null) return;
                    GlStateManager.pushMatrix();
                    GlStateManager.pushAttrib();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableAlpha();
//                    if (itemglow) {
//                        ((ItemGlowShader) framebufferShader).red = getColor().getRed() / 255f;
//                        ((ItemGlowShader) framebufferShader).green = getColor().getGreen() / 255f;
//                        ((ItemGlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
//                        ((ItemGlowShader) framebufferShader).radius = radius.getValue();
//                        ((ItemGlowShader) framebufferShader).quality = 1;
//                        ((ItemGlowShader) framebufferShader).blur = blur.getValue();
//                        ((ItemGlowShader) framebufferShader).mix = mix.getValue();
//                        ((ItemGlowShader) framebufferShader).alpha = 1f;
//                        ((ItemGlowShader) framebufferShader).useImage = false;
//                    } else if (gradient) {
//                        ((GradientOutlineShader) framebufferShader).color = getColor();
//                        ((GradientOutlineShader) framebufferShader).radius = radius.getValue();
//                        ((GradientOutlineShader) framebufferShader).quality = quality.getValue();
//                        ((GradientOutlineShader) framebufferShader).gradientAlpha = gradientAlpha.getValue();
//                        ((GradientOutlineShader) framebufferShader).alphaOutline = alphaGradient.getValue();
//                        ((GradientOutlineShader) framebufferShader).duplicate = duplicateOutline.getValue();
//                        ((GradientOutlineShader) framebufferShader).moreGradient = moreGradientOutline.getValue();
//                        ((GradientOutlineShader) framebufferShader).creepy = creepyOutline.getValue();
//                        ((GradientOutlineShader) framebufferShader).alpha = alpha.getValue();
//                        ((GradientOutlineShader) framebufferShader).numOctaves = numOctavesOutline.getValue();
//                    } else if (glow) {
//                        ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
//                        ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
//                        ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
//                        ((GlowShader) framebufferShader).radius = radius.getValue();
//                        ((GlowShader) framebufferShader).quality = quality.getValue();
//                    } else if (outline) {
//                        ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
//                        ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
//                        ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
//                        ((OutlineShader) framebufferShader).radius = radius.getValue();
//                        ((OutlineShader) framebufferShader).quality = quality.getValue();
//                    }
                    criticalSection = true;
                    framebufferShader.startDraw(event.getPartialTicks());
                    mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
                    framebufferShader.stopDraw();
                    criticalSection = false;
//                    if (gradient) ((GradientOutlineShader) framebufferShader).update(speedOutline.getValue());
                    GlStateManager.disableBlend();
                    GlStateManager.disableAlpha();
                    GlStateManager.disableDepth();
                    GlStateManager.popAttrib();
                    GlStateManager.popMatrix();
            }
        } catch (Exception ignored) {
        }
    }

//    public Color getColor() {
//        return new Color(red.getValue(), green.getValue(), blue.getValue());
//    }

    public enum ShaderModes {
        DarkMatter, Smoky, Smoke, Aqua, AquaSmoke, Snow, Techno, Red, Flow, Purple, BlueFlames, Galaxy, RedGalaxy, PurpleGalaxy, RainbowGalaxy, Vapor
        //Outline, ItemGlow, Glow, Gradient,
    }
}