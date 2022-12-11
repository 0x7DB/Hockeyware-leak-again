package org.hockey.hockeyware.client.features.module.modules.Render;


import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class CrystalModifier extends Module {
    public static CrystalModifier instance;

    public static int rotatingSide = 0;

    public static long lastTime = 0;


    public static final int ANIMATION_LENGTH = 400;
    public static final double CUBELET_SCALE = 0.4;

    public Setting<Boolean> rubiksCrystal = new Setting<>("Rubiks Crystal", false);
    public Setting<RubiksCrystalRotationDirection> rubiksCrystalRotationDirection = new Setting<>("Rubiks Crystal Rotation Direction", RubiksCrystalRotationDirection.Left);
    public Setting<Boolean> rubiksCrystalInside = new Setting<>("Rubiks Crystal Inside", true);
    public Setting<Boolean> rubiksCrystalOutside = new Setting<>("Rubiks Crystal Outside", false);
    public Setting<Boolean> rubiksCrystalOutside2 = new Setting<>("Rubiks Crystal Outside 2", false);

    public Setting<Boolean> scale = new Setting<>("Scale", false);
    public Setting<Float> scaleX = new Setting<>("Scale X", 1f, 0.1, 2);
    public Setting<Float> scaleY = new Setting<>("Scale Y", 1f, 0.1, 2);
    public Setting<Float> scaleZ = new Setting<>("Scale Z", 1f, 0.1, 2);

    public Setting<Boolean> translate = new Setting<>("Translate", false);
    public Setting<Float> translateX = new Setting<>("Translate X", 0f, -2, 2);
    public Setting<Float> translateY = new Setting<>("Translate Y", 0f, -2, 2);
    public Setting<Float> translateZ = new Setting<>("Translate Z", 0f, -2, 2);


    public Setting<Boolean> base = new Setting<>("Base", true);
    public Setting<Boolean> alwaysBase = new Setting<>("Always Base", false);


    public Setting<CubeModes> insideCube = new Setting<>("Inside Tex", CubeModes.In);
    public Setting<ModelModes> insideModel = new Setting<>("Inside Model", ModelModes.Cube);
    public Setting<CubeModes> outsideCube = new Setting<>("Outside Tex", CubeModes.Out);
    public Setting<ModelModes> outsideModel = new Setting<>("Outside Model", ModelModes.Glass);
    public Setting<CubeModes> outsideCube2 = new Setting<>("Outside 2 Tex", CubeModes.Out);
    public Setting<ModelModes> outsideModel2 = new Setting<>("Outside 2 Model", ModelModes.Glass);

    public Setting<Float> speed = new Setting<>("Spin Speed", 3f, 0, 50);
    public Setting<Float> bounce = new Setting<>("Bounce Speed", 0.2f, 0.0, 10);

    public CrystalModifier() {
        super("CrystalModifier", "Allows You To Have Custom Crystal Renders", Category.Render);
        instance = this;
    }

    public enum RubiksCrystalRotationDirection {Left, Right}

    public enum CubeModes {Off, In, Out}

    public enum ModelModes {Cube, Glass}
}