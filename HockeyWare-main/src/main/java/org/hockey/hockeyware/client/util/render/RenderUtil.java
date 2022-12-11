package org.hockey.hockeyware.client.util.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.util.client.ClientMessage;
import org.hockey.hockeyware.client.util.math.GLUProjection;
import org.hockey.hockeyware.client.util.math.Vec2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;


@SuppressWarnings("Duplicates")
public class RenderUtil implements Globals {
    private static ScaledResolution res;

    private static final BlockPos.MutableBlockPos RENDER_BLOCK_POS = new BlockPos.MutableBlockPos();
    private static final MutableBoundingBox RENDER_BOUNDING_BOX = new MutableBoundingBox();

    private static final VertexBuffer BLOCK_FILL_BUFFER = new VertexBuffer(DefaultVertexFormats.POSITION);
    private static final VertexBuffer BLOCK_OUTLINE_BUFFER = new VertexBuffer(DefaultVertexFormats.POSITION);

    public final static FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    public final static IntBuffer viewport = BufferUtils.createIntBuffer(16);
    public final static FloatBuffer viewportFloat = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
    public final static FloatBuffer projection = BufferUtils.createFloatBuffer(16);
    public static ICamera camera = new Frustum();

    private static final Frustum FRUSTUM = new Frustum();

    static {
        res = new ScaledResolution(mc);
        genOpenGlBuffers();
    }

    public static void updateMatrices() {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        // GL11.glGetFloat(GL_VIEWPORT, viewportFloat);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        final ScaledResolution res = new ScaledResolution(mc);
        GLUProjection.getInstance().updateMatrices(viewport, modelView, projection,
                (float) res.getScaledWidth() / (float) mc.displayWidth,
                (float) res.getScaledHeight() / (float) mc.displayHeight);
    }

    public static Entity getEntity() {
        return mc.getRenderViewEntity() == null
                ? mc.player
                : mc.getRenderViewEntity();
    }

    public static void genOpenGlBuffers() {
        if (OpenGlHelper.vboSupported) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, 1, 1); // one block
            // filled box
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();

            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            // contains all position data for drawing a filled cube
            bufferBuilder.finishDrawing();
            bufferBuilder.reset();
            ByteBuffer byteBuffer = bufferBuilder.getByteBuffer();
            // BLOCK_FILL_BUFFER.bindBuffer();
            BLOCK_FILL_BUFFER.bufferData(byteBuffer);
            // BLOCK_FILL_BUFFER.unbindBuffer();

            bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            bufferBuilder.finishDrawing();
            bufferBuilder.reset();
            ByteBuffer outlineBuffer = bufferBuilder.getByteBuffer();
            // BLOCK_OUTLINE_BUFFER.bindBuffer();
            BLOCK_OUTLINE_BUFFER.bufferData(outlineBuffer);
            // BLOCK_OUTLINE_BUFFER.unbindBuffer();

        } else {
            ClientMessage.sendMessage("VBOs not supported, skipping.");
        }
    }

    public static void renderBox(double x, double y, double z) {
        startRender();
        BLOCK_FILL_BUFFER.bindBuffer();
        double viewX = mc.getRenderManager().renderPosX;
        double viewY = mc.getRenderManager().renderPosY;
        double viewZ = mc.getRenderManager().renderPosZ;
        GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
        BLOCK_FILL_BUFFER.drawArrays(GL11.GL_QUADS);
        BLOCK_FILL_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        endRender();
    }

    public static void startRenderBox() {
        startRender();
        BLOCK_FILL_BUFFER.bindBuffer();
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
    }

    public static void endRenderBox() {
        BLOCK_FILL_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        endRender();
    }

    public static void doRenderBox(double x, double y, double z) {
        double viewX = mc.getRenderManager().renderPosX;
        double viewY = mc.getRenderManager().renderPosY;
        double viewZ = mc.getRenderManager().renderPosZ;
        GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        BLOCK_FILL_BUFFER.drawArrays(GL11.GL_QUADS);
        GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
    }

    public static void renderBoxes(Vec3d[] vectors) {
        startRender();
        BLOCK_FILL_BUFFER.bindBuffer();
        double viewX = mc.getRenderManager().renderPosX;
        double viewY = mc.getRenderManager().renderPosY;
        double viewZ = mc.getRenderManager().renderPosZ;
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
        for (Vec3d vec : vectors) {
            double x = vec.x;
            double y = vec.y;
            double z = vec.z;
            GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            BLOCK_FILL_BUFFER.drawArrays(GL11.GL_QUADS);
            GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        }
        BLOCK_FILL_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        endRender();
    }

    public static void renderOutline(double x, double y, double z) {
        startRender();
        BLOCK_OUTLINE_BUFFER.bindBuffer();
        double viewX = mc.getRenderManager().renderPosX;
        double viewY = mc.getRenderManager().renderPosY;
        double viewZ = mc.getRenderManager().renderPosZ;
        GL11.glTranslated(x - viewX, y - viewY, z - viewZ);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0);
        BLOCK_OUTLINE_BUFFER.drawArrays(GL11.GL_QUADS);
        BLOCK_OUTLINE_BUFFER.unbindBuffer();
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glTranslated(-(x - viewX), -(y - viewY), -(z - viewZ));
        endRender();
    }

    public static void renderBox(BlockPos pos, Color color, float height) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        AxisAlignedBB bb = Interpolation.interpolatePos(pos, height);
        startRender();
        drawOutline(bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 76);
        startRender();
        drawBox(bb, boxColor);
        endRender();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderBox(BlockPos pos,
                                 Color color,
                                 float height,
                                 int boxAlpha) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        AxisAlignedBB bb = Interpolation.interpolatePos(pos, height);
        startRender();
        drawOutline(bb, 1.5f, color);
        endRender();
        Color boxColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha);
        startRender();
        drawBox(bb, boxColor);
        endRender();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void renderBox(AxisAlignedBB bb,
                                 Color color,
                                 Color outLineColor,
                                 float lineWidth) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        startRender();
        drawOutline(bb, lineWidth, outLineColor);
        endRender();
        startRender();
        drawBox(bb, color);
        endRender();

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb) {
        GL11.glPushMatrix();
        glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        fillBox(bb);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LIGHTING);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb, Color color) {
        GL11.glPushMatrix();
        glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        color(color);
        fillBox(bb);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LIGHTING);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawOutline(AxisAlignedBB bb, float lineWidth) {
        GL11.glPushMatrix();
        glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        fillOutline(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LIGHTING);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawOutline(AxisAlignedBB bb, float lineWidth, Color color) {
        GL11.glPushMatrix();
        glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        color(color);
        fillOutline(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_LIGHTING);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }


    public static void fillOutline(AxisAlignedBB bb) {
        if (bb != null) {
            GL11.glBegin(GL11.GL_LINES);
            {
                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);

                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            }
            GL11.glEnd();
        }
    }

    public static void fillBox(AxisAlignedBB boundingBox) {
        if (boundingBox != null) {
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glEnd();

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
            GL11.glVertex3d((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glVertex3d((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
            GL11.glEnd();
        }
    }

    public static void prepare(float x,
                               float y,
                               float x1,
                               float y1,
                               float lineWidth,
                               int color,
                               int color1,
                               int color2) {
        startRender();
        prepare(x, y, x1, y1, color2, color1);
        color(color);
        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        endRender();
    }

    // only for nametags, i will clean up the render utils one day, but i had to get rid of those unnecessary calls ASAP
    public static void drawRect(float x, float y, float x1, float y1, float lineWidth, int color, int color1) {
        color(color1);
        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void prepare(float x,
                               float y,
                               float x1,
                               float y1,
                               float lineWidth,
                               int color,
                               int color1) {
        startRender();
        prepare(x, y, x1, y1, color);
        color(color1);
        glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color, int color1) {
        startRender();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        color(color);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        color(color1);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color) {
        startRender();
        color(color);
        scissor(x, y, x1, y1);
        endRender();
    }

    public static void scissor(float x, float y, float x1, float y1) {
        res = new ScaledResolution(mc);
        int scale = res.getScaleFactor();
        GL11.glScissor((int) (x * scale),
                (int) ((res.getScaledHeight() - y1) * scale),
                (int) ((x1 - x) * scale),
                (int) ((y1 - y) * scale));
    }

    public static void color(Color color) {
        GL11.glColor4f(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    public static void color(int color) {
        float[] color4f = ColorUtil2.toArray(color);
        GL11.glColor4f(color4f[0], color4f[1], color4f[2], color4f[3]);
    }

    public static void color(float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
    }

    public static void startRender() {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        glEnable(GL11.GL_CULL_FACE);
        glEnable(GL11.GL_LINE_SMOOTH);
        glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void endRender() {
        glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        glEnable(GL11.GL_TEXTURE_2D);
        glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void doPosition(AxisAlignedBB bb) {
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glEnd();
    }

    public static boolean mouseWithinBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX >= x && mouseX <= (x + width)) && (mouseY >= y && mouseY <= (y + height));
    }

    public static Vec2d to2D(double x, double y, double z) {
        GLUProjection.Projection projection = GLUProjection.getInstance().project(x, y, z, GLUProjection.ClampMode.ORTHOGONAL, true);
        return new Vec2d(projection.getX(), projection.getY());
    }

    protected static void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    protected static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    }

    public static boolean isInFrustum(AxisAlignedBB bb) {
        Entity renderEntity = getEntity();
        if (renderEntity == null) {
            return false;
        }

        Interpolation.setFrustum(FRUSTUM, renderEntity);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    public static void drawText(BlockPos renderPosition, String valueOf) {
    }

    public static void drawBox(AxisAlignedBB generateBB, float v, float v1, float v2, float v3) {
    }

    public static void drawBoxESPFlat(BlockPos pos, boolean box, boolean outline, Color color, Color outlineColor, float lineWidth) {
        if (box)
            drawBoxFlat(pos, color);
        if (outline)
            drawBlockOutlineFlat(pos, outlineColor, lineWidth);
    }

    public static void drawBlockOutlineFlat(final BlockPos pos, final Color color, final float lineWidth) {
        final IBlockState iblockstate = mc.world.getBlockState(pos);
        if (mc.world.getWorldBorder().contains(pos)) {
            final Vec3d interp = interpolateEntity(mc.player, mc.getRenderPartialTicks());
            drawBlockOutlineFlat(iblockstate.getSelectedBoundingBox(mc.world, pos).grow(0.0020000000949949026).offset(-interp.x, -interp.y, -interp.z), color, lineWidth);
        }
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    public static void drawBoxFlat(final BlockPos pos, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + mc.getRenderManager().viewerPosX, bb.minY + mc.getRenderManager().viewerPosY, bb.minZ + mc.getRenderManager().viewerPosZ, bb.maxX + mc.getRenderManager().viewerPosX, bb.maxY + mc.getRenderManager().viewerPosY, bb.maxZ + mc.getRenderManager().viewerPosZ))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            glEnable(2848);
            glHint(3154, 4354);
            RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawBlockOutlineFlat(final AxisAlignedBB bb, final Color color, final float lineWidth) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        glEnable(2848);
        glHint(3154, 4354);
        GL11.glLineWidth(lineWidth);
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawOutlineRect(double left, double top, double right, double bottom, Color color, float lineWidth) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float) (color.getRGB() >> 24 & 255) / 255.0F;
        float f = (float) (color.getRGB() >> 16 & 255) / 255.0F;
        float f1 = (float) (color.getRGB() >> 8 & 255) / 255.0F;
        float f2 = (float) (color.getRGB() & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(lineWidth);
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, h, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(w, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}