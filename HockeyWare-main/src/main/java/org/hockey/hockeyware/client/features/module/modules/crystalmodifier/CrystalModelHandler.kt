package org.hockey.hockeyware.client.features.module.modules.crystalmodifier

import net.minecraft.client.model.ModelEnderCrystal
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.util.math.MathHelper
import org.hockey.hockeyware.client.features.module.modules.Render.CrystalModifier
import org.hockey.hockeyware.client.features.module.modules.crystalmodifier.RubiksCrystalUtil.*
import org.hockey.hockeyware.client.util.Globals.mc
import org.lwjgl.util.vector.Quaternion
import java.lang.Math.toRadians
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.cos
import kotlin.math.sin

class CrystalModelHandler(private val renderBase : Boolean) : ModelEnderCrystal(0f, renderBase) {
    private val insideCube = ModelRenderer(this, "cube").setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val insideGlass = ModelRenderer(this, "glass").setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val outsideCube = ModelRenderer(this, "cube").setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val outsideGlass = ModelRenderer(this, "glass").setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8)
    private val bottom = ModelRenderer(this, "base").setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12)

    override fun render(
        entity : Entity,
        limbSwing : Float,
        limbSwingAmount : Float,
        ageInTicks : Float,
        netHeadYaw : Float,
        headPitch : Float,
        scale : Float
    ) {
        if(CrystalModifier.instance.isOn && entity is EntityEnderCrystal) {
            val ticks = mc.renderPartialTicks
            val defaultSpinSpeed = entity.innerRotation + ticks
            var defaultBounceSpeed = MathHelper.sin(defaultSpinSpeed * 0.2f) / 2.0f + 0.5f
            defaultBounceSpeed += defaultBounceSpeed * defaultBounceSpeed

            val customSpinSpeed = CrystalModifier.instance.speed.value.toFloat()
            val customBounceSpeed = CrystalModifier.instance.bounce.value.toFloat()

            val spinSpeed = defaultSpinSpeed * customSpinSpeed
            val bounceSpeed = defaultBounceSpeed * customBounceSpeed

            pushMatrix()
            scale(2.0f * getScaleX(), 2.0f * getScaleY(), 2.0f * getScaleZ())
            translate(getTranslateX(), -0.5f + getTranslateY(), getTranslateZ())
            if (needToRenderBase()) bottom.render(scale)
            scale(getScaleX(), getScaleY(), getScaleZ())
            rotate(spinSpeed, 0.0f, 1.0f, 0.0f)
            translate(getTranslateX(), 0.8f + bounceSpeed + getTranslateY(), getTranslateZ())
            rotate(60.0f, 0.7071f, 0.0f, 0.7071f)
            if (CrystalModifier.instance.outsideCube.value != CrystalModifier.CubeModes.Off) drawCube(getOutsideBox()!!, 2, scale)//getOutsideBox()?.render(scale)
            scale(0.875f * getScaleX(), 0.875f * getScaleY(), 0.875f * getScaleZ())
            rotate(60.0f, 0.7071f, 0.0f, 0.7071f)
            rotate(spinSpeed, 0.0f, 1.0f, 0.0f)
            if (CrystalModifier.instance.outsideCube2.value != CrystalModifier.CubeModes.Off) drawCube(getOutsideBox2()!!, 3, scale)//getOutsideBox2()?.render(scale)
            scale(0.875f * getScaleX(), 0.875f * getScaleY(), 0.875f * getScaleZ())
            rotate(60.0f, 0.7071f, 0.0f, 0.7071f)
            rotate(spinSpeed, 0.0f, 1.0f, 0.0f)
            if (CrystalModifier.instance.insideCube.value != CrystalModifier.CubeModes.Off) drawCube(getInsideBox()!!, 1, scale)//drawRubiksBox(getInsideBox()!!, scale)
            popMatrix()
        } else {
            super.render(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale
            )
        }
    }

    private fun getInsideBox() : ModelRenderer? {
        return getRenderer(CrystalModifier.instance.insideCube.value as CrystalModifier.CubeModes, CrystalModifier.instance.insideModel.value as CrystalModifier.ModelModes)
    }

    private fun getOutsideBox() : ModelRenderer? {
        return getRenderer(CrystalModifier.instance.outsideCube.value as CrystalModifier.CubeModes, CrystalModifier.instance.outsideModel.value as CrystalModifier.ModelModes)
    }

    private fun getOutsideBox2() : ModelRenderer? {
        return getRenderer(CrystalModifier.instance.outsideCube2.value as CrystalModifier.CubeModes, CrystalModifier.instance.outsideModel2.value as CrystalModifier.ModelModes)
    }

    /**
     * cubeID:
     *
     * 1 - inside cube
     *
     * 2 - first outside cube
     *
     * 3 - second outside cube
     */
    private fun drawCube(cube : ModelRenderer, cubeID : Int, scale : Float) {
        if(CrystalModifier.instance.rubiksCrystal.value && (if(cubeID == 2) CrystalModifier.instance.rubiksCrystalOutside.value else if(cubeID == 3) CrystalModifier.instance.rubiksCrystalOutside2.value else CrystalModifier.instance.rubiksCrystalInside.value)) {
            drawRubiksBox(cube, scale)
        } else {
            cube.render(scale)
        }
    }

    private fun drawRubiksBox(cube : ModelRenderer, scale : Float) {
        if (CrystalModifier.instance.rubiksCrystal.value) {
            scale(
                CrystalModifier.CUBELET_SCALE,
                CrystalModifier.CUBELET_SCALE,
                CrystalModifier.CUBELET_SCALE
            )
            val scaleNew = scale * (CrystalModifier.CUBELET_SCALE * 2).toFloat()

            val currentTime = System.currentTimeMillis()
            if (currentTime - CrystalModifier.ANIMATION_LENGTH > CrystalModifier.lastTime) {
                val currentSide = cubeSides[CrystalModifier.rotatingSide]
                val cubletsTemp = arrayOf(
                    cubeletStatus[currentSide[0]],
                    cubeletStatus[currentSide[1]],
                    cubeletStatus[currentSide[2]],
                    cubeletStatus[currentSide[3]],
                    cubeletStatus[currentSide[4]],
                    cubeletStatus[currentSide[5]],
                    cubeletStatus[currentSide[6]],
                    cubeletStatus[currentSide[7]],
                    cubeletStatus[currentSide[8]]
                )

                // rotation direction
                if (CrystalModifier.instance.rubiksCrystalRotationDirection.value === CrystalModifier.RubiksCrystalRotationDirection.Left) {
                    cubeletStatus[currentSide[0]] = cubletsTemp[6]
                    cubeletStatus[currentSide[1]] = cubletsTemp[3]
                    cubeletStatus[currentSide[2]] = cubletsTemp[0]
                    cubeletStatus[currentSide[3]] = cubletsTemp[7]
                    cubeletStatus[currentSide[4]] = cubletsTemp[4]
                    cubeletStatus[currentSide[5]] = cubletsTemp[1]
                    cubeletStatus[currentSide[6]] = cubletsTemp[8]
                    cubeletStatus[currentSide[7]] = cubletsTemp[5]
                    cubeletStatus[currentSide[8]] = cubletsTemp[2]
                } else if (CrystalModifier.instance.rubiksCrystalRotationDirection.value === CrystalModifier.RubiksCrystalRotationDirection.Right) {
                    cubeletStatus[currentSide[0]] = cubletsTemp[2]
                    cubeletStatus[currentSide[1]] = cubletsTemp[5]
                    cubeletStatus[currentSide[2]] = cubletsTemp[8]
                    cubeletStatus[currentSide[3]] = cubletsTemp[1]
                    cubeletStatus[currentSide[4]] = cubletsTemp[4]
                    cubeletStatus[currentSide[5]] = cubletsTemp[7]
                    cubeletStatus[currentSide[6]] = cubletsTemp[0]
                    cubeletStatus[currentSide[7]] = cubletsTemp[3]
                    cubeletStatus[currentSide[8]] = cubletsTemp[6]
                }
                val trans = cubeSideTransforms[CrystalModifier.rotatingSide]
                for (x in -1..1) for (y in -1..1) for (z in -1..1) if (x != 0 || y != 0 || z != 0) applyCubeletRotation(
                    x, y, z,
                    trans[0],
                    trans[1],
                    trans[2]
                )
                CrystalModifier.rotatingSide = ThreadLocalRandom.current().nextInt(0, 5 + 1)
                CrystalModifier.lastTime = currentTime
            }

            // Draw non-rotating cubes
            for (x in -1..1) for (y in -1..1) for (z in -1..1) if (x != 0 || y != 0 || z != 0) drawCubeletStatic(
                cube,
                scaleNew,
                x, y, z
            )


            // Draw rotating cubes
            val trans = cubeSideTransforms[CrystalModifier.rotatingSide]
            pushMatrix()
            translate(
                trans[0] * CrystalModifier.CUBELET_SCALE,
                trans[1] * CrystalModifier.CUBELET_SCALE,
                trans[2] * CrystalModifier.CUBELET_SCALE
            )
            val rotationAngle = toRadians(easeInOutCubic(((currentTime - CrystalModifier.lastTime).toFloat() / CrystalModifier.ANIMATION_LENGTH).toDouble()) * 90).toFloat()
            val xx = (trans[0] * sin((rotationAngle / 2).toDouble())).toFloat()
            val yy = (trans[1] * sin((rotationAngle / 2).toDouble())).toFloat()
            val zz = (trans[2] * sin((rotationAngle / 2).toDouble())).toFloat()
            val ww = cos((rotationAngle / 2).toDouble()).toFloat()
            val q = Quaternion(xx, yy, zz, ww)
            rotate(q)
            for (x in -1..1) for (y in -1..1) for (z in -1..1) if (x != 0 || y != 0 || z != 0) drawCubeletRotating(
                cube,
                scaleNew,
                x, y, z
            )
            popMatrix()
        } else cube.render(scale)
    }

    private fun getRenderer(tex : CrystalModifier.CubeModes, model : CrystalModifier.ModelModes) : ModelRenderer? {
        return when(tex) {
            CrystalModifier.CubeModes.In -> if(model == CrystalModifier.ModelModes.Cube) insideCube else insideGlass
            CrystalModifier.CubeModes.Out -> if(model == CrystalModifier.ModelModes.Cube) outsideCube else outsideGlass
            CrystalModifier.CubeModes.Off -> null
        }
    }

    private fun needToRenderBase() : Boolean = if (CrystalModifier.instance.base.value) CrystalModifier.instance.alwaysBase.value || renderBase else false

    private fun getTranslateX() : Double = if (CrystalModifier.instance.translate.value) CrystalModifier.instance.translateX.value.toDouble() else 0.0
    private fun getTranslateY() : Double = if (CrystalModifier.instance.translate.value) CrystalModifier.instance.translateY.value.toDouble() else 0.0
    private fun getTranslateZ() : Double = if (CrystalModifier.instance.translate.value) CrystalModifier.instance.translateZ.value.toDouble() else 0.0

    private fun getScaleX() : Double = if (CrystalModifier.instance.scale.value) CrystalModifier.instance.scaleX.value.toDouble() else 1.0
    private fun getScaleY() : Double = if (CrystalModifier.instance.scale.value) CrystalModifier.instance.scaleY.value.toDouble() else 1.0
    private fun getScaleZ() : Double = if (CrystalModifier.instance.scale.value) CrystalModifier.instance.scaleZ.value.toDouble() else 1.0
}
