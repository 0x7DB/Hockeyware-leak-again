package org.hockey.hockeyware.client.gui.loading;

import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.client.resources.IResource;
import javax.annotation.Nonnull;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.gui.FontRenderer;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.io.IOUtils;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageIO;

import org.hockey.hockeyware.client.util.render.Shader2D;
import org.lwjgl.BufferUtils;
import net.minecraftforge.fml.common.asm.FMLSanityChecker;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import org.lwjgl.util.glu.GLU;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraft.client.renderer.GlStateManager;
import java.io.Writer;
import java.io.Reader;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import org.lwjgl.opengl.GL20;
import net.minecraftforge.fml.common.ProgressManager;
import java.awt.Color;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.opengl.Display;
import net.minecraft.crash.CrashReport;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import net.minecraft.util.ResourceLocation;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.client.FMLClientHandler;
import java.io.IOException;
import net.minecraftforge.fml.common.FMLLog;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.File;
import net.minecraft.client.Minecraft;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;
import java.util.Properties;
import net.minecraft.client.resources.IResourcePack;
import java.util.concurrent.locks.Lock;
import org.lwjgl.opengl.Drawable;

public class CustomSplashProgress
{
    private static Drawable d;
    private static volatile boolean pause;
    private static volatile boolean done;
    private static Thread thread;
    private static volatile Throwable threadError;
    private static int angle;
    private static final Lock lock;
    private static SplashFontRenderer fontRenderer;
    private static final IResourcePack mcPack;
    private static final IResourcePack fmlPack;
    private static IResourcePack miscPack;
    private static Texture fontTexture;
    private static Texture logoTexture;
    private static Texture forgeTexture;
    private static Properties config;
    private static boolean enabled;
    private static boolean rotate;
    private static int logoOffset;
    private static int backgroundColor;
    private static int fontColor;
    private static int barBorderColor;
    private static int barColor;
    private static int barBackgroundColor;
    private static boolean showMemory;
    private static int memoryGoodColor;
    private static int memoryWarnColor;
    private static int memoryLowColor;
    private static float memoryColorPercent;
    private static long memoryColorChangeTime;
    static boolean isDisplayVSyncForced;
    private static final int TIMING_FRAME_COUNT = 200;
    private static final int TIMING_FRAME_THRESHOLD = 1000000000;
    static final Semaphore mutex;
    private static int max_texture_size;
    private static final IntBuffer buf;

    private static String getString(final String name, final String def) {
        final String value = CustomSplashProgress.config.getProperty(name, def);
        CustomSplashProgress.config.setProperty(name, value);
        return value;
    }

    private static boolean getBool(final String name, final boolean def) {
        return Boolean.parseBoolean(getString(name, Boolean.toString(def)));
    }

    private static int getInt(final String name, final int def) {
        return Integer.decode(getString(name, Integer.toString(def)));
    }

    private static int getHex(final String name, final int def) {
        return Integer.decode(getString(name, "0x" + Integer.toString(def, 16).toUpperCase()));
    }

    public static void start() throws IOException {
        final File configFile = new File(Minecraft.getMinecraft().gameDir, "config/splash.properties");
        final File parent = configFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        CustomSplashProgress.config = new Properties();
        try (final Reader r = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            CustomSplashProgress.config.load(r);
        }
        catch (IOException e3) {
            FMLLog.log.info("Could not load splash.properties, will create a default one");
        }
        final boolean defaultEnabled = true;
        CustomSplashProgress.enabled = (getBool("enabled", defaultEnabled) && (!FMLClientHandler.instance().hasOptifine() || Launch.blackboard.containsKey("optifine.ForgeSplashCompatible")));
        CustomSplashProgress.rotate = getBool("rotate", false);
        CustomSplashProgress.showMemory = getBool("showMemory", true);
        CustomSplashProgress.logoOffset = getInt("logoOffset", 0);
        CustomSplashProgress.backgroundColor = getHex("background", 16777215);
        CustomSplashProgress.fontColor = getHex("font", 0);
        CustomSplashProgress.barBorderColor = getHex("barBorder", 12632256);
        CustomSplashProgress.barColor = getHex("bar", 13319477);
        CustomSplashProgress.barBackgroundColor = getHex("barBackground", 16777215);
        CustomSplashProgress.memoryGoodColor = getHex("memoryGood", 7916340);
        CustomSplashProgress.memoryWarnColor = getHex("memoryWarn", 15132746);
        CustomSplashProgress.memoryLowColor = getHex("memoryLow", 14954287);
        final ResourceLocation fontLoc = new ResourceLocation(getString("fontTexture", "textures/font/ascii.png"));
        final ResourceLocation logoLoc = new ResourceLocation("textures/gui/title/mojang.png");
        final ResourceLocation forgeLoc = new ResourceLocation(getString("forgeTexture", "fml:textures/gui/forge.png"));
        final ResourceLocation forgeFallbackLoc = new ResourceLocation("fml:textures/gui/forge.png");
        final File miscPackFile = new File(Minecraft.getMinecraft().gameDir, getString("resourcePackPath", "resources"));
        try (final Writer w = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            CustomSplashProgress.config.store(w, "Splash screen properties");
        }
        catch (IOException e) {
            FMLLog.log.error("Could not save the splash.properties file", (Throwable)e);
        }
        CustomSplashProgress.miscPack = createResourcePack(miscPackFile);
        FMLCommonHandler.instance().registerCrashCallable((ICrashCallable)new ICrashCallable() {
            public String call() throws Exception {
                return "' Vendor: '" + GL11.glGetString(7936) + "' Version: '" + GL11.glGetString(7938) + "' Renderer: '" + GL11.glGetString(7937) + "'";
            }

            public String getLabel() {
                return "GL info";
            }
        });
        final CrashReport report = CrashReport.makeCrashReport(new Throwable(), "Loading screen debug info");
        final StringBuilder systemDetailsBuilder = new StringBuilder();
        report.getCategory().appendToStringBuilder(systemDetailsBuilder);
        FMLLog.log.info(systemDetailsBuilder.toString());
        try {
            CustomSplashProgress.d = (Drawable)new SharedDrawable(Display.getDrawable());
            Display.getDrawable().releaseContext();
            CustomSplashProgress.d.makeCurrent();
        }
        catch (LWJGLException e2) {
            FMLLog.log.error("Error starting SplashProgress:", (Throwable)e2);
            disableSplash((Exception)e2);
        }
        getMaxTextureSize();
        final Shader2D glslShader = new Shader2D("/assets/hockeyware/shaders/loadingScreen/vertex.glsl", "/assets/hockeyware/shaders/loadingScreen/fragment/2D/og.glsl");
        final long startTime = System.currentTimeMillis();
        (CustomSplashProgress.thread = new Thread(new Runnable() {
            private final int barWidth = 400;
            private final int barHeight = 20;
            private final int textHeight2 = 20;
            private final int barOffset = 55;
            private long updateTiming;
            private long framecount;

            @Override
            public void run() {
                this.setGL();
                CustomSplashProgress.fontTexture = new Texture(fontLoc, null);
                CustomSplashProgress.logoTexture = new Texture(logoLoc, null, false);
                CustomSplashProgress.forgeTexture = new Texture(forgeLoc, forgeFallbackLoc);
                GL11.glEnable(3553);
                CustomSplashProgress.fontRenderer = new SplashFontRenderer();
                GL11.glDisable(3553);
                final Color barColor = new Color(2068861008, true);
                while (!CustomSplashProgress.done) {
                    ++this.framecount;
                    ProgressManager.ProgressBar first = null;
                    ProgressManager.ProgressBar penult = null;
                    ProgressManager.ProgressBar last = null;
                    final Iterator<ProgressManager.ProgressBar> i = (Iterator<ProgressManager.ProgressBar>)ProgressManager.barIterator();
                    while (i.hasNext()) {
                        if (first == null) {
                            first = i.next();
                        }
                        else {
                            penult = last;
                            last = i.next();
                        }
                    }
                    GL11.glPushMatrix();
                    GL11.glClear(16384);
                    final int w = Display.getWidth();
                    final int h = Display.getHeight();
                    GL11.glViewport(0, 0, w, h);
                    GL11.glMatrixMode(5889);
                    GL11.glLoadIdentity();
                    GL11.glOrtho((double)(320 - w / 2), (double)(320 + w / 2), (double)(240 + h / 2), (double)(240 - h / 2), -1.0, 1.0);
                    GL11.glMatrixMode(5888);
                    GL11.glLoadIdentity();
                    final int left = 320 - w / 2;
                    final int right = 320 + w / 2;
                    final int bottom = 240 + h / 2;
                    final int top = 240 - h / 2;
                    GL11.glPushMatrix();
                    this.setColor(16777215);
                    glslShader.useShader(Math.abs(320 + w / 2 - (320 - w / 2)), Math.abs(240 + h / 2 - (240 - h / 2)), (System.currentTimeMillis() - startTime) / 69.0f);
                    GL11.glBegin(7);
                    GL11.glVertex2f((float)left, (float)bottom);
                    GL11.glVertex2f((float)right, (float)bottom);
                    GL11.glVertex2f((float)right, (float)top);
                    GL11.glVertex2f((float)left, (float)top);
                    GL11.glEnd();
                    GL20.glUseProgram(0);
                    GL11.glPopMatrix();
                    this.setColor(16777215);
                    final float midX = right - (right - left) / 2.0f;
                    final float midY = bottom - (bottom - top) / 2.0f;
                    final float mrLeanThiccness = h * 0.7f;
                    GL11.glColor4f(barColor.getRed() / 255.0f, barColor.getGreen() / 255.0f, barColor.getBlue() / 255.0f, barColor.getAlpha() / 255.0f);
                    final int barTop = bottom - 75;
                    GL11.glBegin(7);
                    GL11.glVertex2f((float)left, (float)bottom);
                    GL11.glVertex2f((float)right, (float)bottom);
                    GL11.glVertex2f((float)right, (float)barTop);
                    GL11.glVertex2f((float)left, (float)barTop);
                    GL11.glEnd();
                    GL11.glPushMatrix();
                    final int scale = 2;
                    final int fontY = bottom - 37 - CustomSplashProgress.fontRenderer.FONT_HEIGHT;
                    final int fontX = left + 10;
                    String progress = "Error";
                    if (first != null) {
                        progress = first.getTitle() + " - " + first.getMessage();
                    }
                    this.setColor(16777215);
                    GL11.glScalef((float)scale, (float)scale, 1.0f);
                    GL11.glEnable(3553);
                    CustomSplashProgress.fontRenderer.drawString("HockeyWare | " + progress, fontX / scale, fontY / scale, 16777215);
                    GL11.glDisable(3553);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    CustomSplashProgress.mutex.acquireUninterruptibly();
                    final long updateStart = System.nanoTime();
                    Display.update();
                    final long dur = System.nanoTime() - updateStart;
                    if (this.framecount < 200L) {
                        this.updateTiming += dur;
                    }
                    CustomSplashProgress.mutex.release();
                    if (CustomSplashProgress.pause) {
                        this.clearGL();
                        this.setGL();
                    }
                    if (this.framecount >= 200L && this.updateTiming > 1000000000L) {
                        if (!CustomSplashProgress.isDisplayVSyncForced) {
                            CustomSplashProgress.isDisplayVSyncForced = true;
                            FMLLog.log.info("Using alternative sync timing : {} frames of Display.update took {} nanos", (Object)200, (Object)this.updateTiming);
                        }
                        try {
                            Thread.sleep(16L);
                        }
                        catch (InterruptedException ex) {}
                    }
                    else {
                        if (this.framecount == 200L) {
                            FMLLog.log.info("Using sync timing. {} frames of Display.update took {} nanos", (Object)200, (Object)this.updateTiming);
                        }
                        Display.sync(100);
                    }
                }
                this.clearGL();
            }

            private void setColor(final int color) {
                GL11.glColor3ub((byte)(color >> 16 & 0xFF), (byte)(color >> 8 & 0xFF), (byte)(color & 0xFF));
            }

            private void drawBox(final int w, final int h) {
                GL11.glBegin(7);
                GL11.glVertex2f(0.0f, 0.0f);
                GL11.glVertex2f(0.0f, (float)h);
                GL11.glVertex2f((float)w, (float)h);
                GL11.glVertex2f((float)w, 0.0f);
                GL11.glEnd();
            }

            private void drawBar(final ProgressManager.ProgressBar b) {
                GL11.glPushMatrix();
                this.setColor(CustomSplashProgress.fontColor);
                GL11.glScalef(2.0f, 2.0f, 1.0f);
                GL11.glEnable(3553);
                CustomSplashProgress.fontRenderer.drawString(b.getTitle() + " - " + b.getMessage(), 0, 0, 0);
                GL11.glDisable(3553);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0f, 20.0f, 0.0f);
                this.setColor(CustomSplashProgress.barBorderColor);
                this.drawBox(400, 20);
                this.setColor(CustomSplashProgress.barBackgroundColor);
                GL11.glTranslatef(1.0f, 1.0f, 0.0f);
                this.drawBox(398, 18);
                this.setColor(CustomSplashProgress.barColor);
                this.drawBox(398 * (b.getStep() + 1) / (b.getSteps() + 1), 18);
                final String progress = "" + b.getStep() + "/" + b.getSteps();
                GL11.glTranslatef(199.0f - CustomSplashProgress.fontRenderer.getStringWidth(progress), 2.0f, 0.0f);
                this.setColor(CustomSplashProgress.fontColor);
                GL11.glScalef(2.0f, 2.0f, 1.0f);
                GL11.glEnable(3553);
                CustomSplashProgress.fontRenderer.drawString(progress, 0, 0, 0);
                GL11.glPopMatrix();
            }

            private void drawMemoryBar() {
                final int maxMemory = bytesToMb(Runtime.getRuntime().maxMemory());
                final int totalMemory = bytesToMb(Runtime.getRuntime().totalMemory());
                final int freeMemory = bytesToMb(Runtime.getRuntime().freeMemory());
                final int usedMemory = totalMemory - freeMemory;
                final float usedMemoryPercent = usedMemory / (float)maxMemory;
                GL11.glPushMatrix();
                this.setColor(CustomSplashProgress.fontColor);
                GL11.glScalef(2.0f, 2.0f, 1.0f);
                GL11.glEnable(3553);
                CustomSplashProgress.fontRenderer.drawString("Used / Total", 0, 0, 0);
                GL11.glDisable(3553);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0f, 20.0f, 0.0f);
                this.setColor(CustomSplashProgress.barBorderColor);
                this.drawBox(400, 20);
                this.setColor(CustomSplashProgress.barBackgroundColor);
                GL11.glTranslatef(1.0f, 1.0f, 0.0f);
                this.drawBox(398, 18);
                final long time = System.currentTimeMillis();
                if (usedMemoryPercent > CustomSplashProgress.memoryColorPercent || time - CustomSplashProgress.memoryColorChangeTime > 1000L) {
                    CustomSplashProgress.memoryColorChangeTime = time;
                    CustomSplashProgress.memoryColorPercent = usedMemoryPercent;
                }
                int memoryBarColor;
                if (CustomSplashProgress.memoryColorPercent < 0.75f) {
                    memoryBarColor = CustomSplashProgress.memoryGoodColor;
                }
                else if (CustomSplashProgress.memoryColorPercent < 0.85f) {
                    memoryBarColor = CustomSplashProgress.memoryWarnColor;
                }
                else {
                    memoryBarColor = CustomSplashProgress.memoryLowColor;
                }
                this.setColor(CustomSplashProgress.memoryLowColor);
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(398 * totalMemory / maxMemory - 2), 0.0f, 0.0f);
                this.drawBox(2, 18);
                GL11.glPopMatrix();
                this.setColor(memoryBarColor);
                this.drawBox(398 * usedMemory / maxMemory, 18);
                final String progress = this.getMemoryString(usedMemory) + " / " + this.getMemoryString(maxMemory);
                GL11.glTranslatef(199.0f - CustomSplashProgress.fontRenderer.getStringWidth(progress), 2.0f, 0.0f);
                this.setColor(CustomSplashProgress.fontColor);
                GL11.glScalef(2.0f, 2.0f, 1.0f);
                GL11.glEnable(3553);
                CustomSplashProgress.fontRenderer.drawString(progress, 0, 0, 0);
                GL11.glPopMatrix();
            }

            private String getMemoryString(final int memory) {
                return StringUtils.leftPad(Integer.toString(memory), 4, ' ') + " MB";
            }

            private void setGL() {
                System.out.println("Setting HockeyWare Loading Screen!");
                CustomSplashProgress.lock.lock();
                try {
                    Display.getDrawable().makeCurrent();
                }
                catch (LWJGLException e) {
                    FMLLog.log.error("Error setting GL context:", (Throwable)e);
                    throw new RuntimeException((Throwable)e);
                }
                CustomSplashProgress.backgroundColor = Color.cyan.getRGB();
                GL11.glClearColor((CustomSplashProgress.backgroundColor >> 16 & 0xFF) / 255.0f, (CustomSplashProgress.backgroundColor >> 8 & 0xFF) / 255.0f, (CustomSplashProgress.backgroundColor & 0xFF) / 255.0f, 1.0f);
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
            }

            private void clearGL() {
                final Minecraft mc = Minecraft.getMinecraft();
                mc.displayWidth = Display.getWidth();
                mc.displayHeight = Display.getHeight();
                mc.resize(mc.displayWidth, mc.displayHeight);
                GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glEnable(2929);
                GL11.glDepthFunc(515);
                GL11.glEnable(3008);
                GL11.glAlphaFunc(516, 0.1f);
                try {
                    Display.getDrawable().releaseContext();
                }
                catch (LWJGLException e) {
                    FMLLog.log.error("Error releasing GL context:", (Throwable)e);
                    throw new RuntimeException((Throwable)e);
                }
                finally {
                    CustomSplashProgress.lock.unlock();
                }
            }
        })).setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                FMLLog.log.error("Splash thread Exception", e);
                CustomSplashProgress.threadError = e;
            }
        });
        CustomSplashProgress.thread.start();
        checkThreadState();
    }

    public static int getMaxTextureSize() {
        if (CustomSplashProgress.max_texture_size != -1) {
            return CustomSplashProgress.max_texture_size;
        }
        for (int i = 16384; i > 0; i >>= 1) {
            GlStateManager.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (IntBuffer)null);
            if (GlStateManager.glGetTexLevelParameteri(32868, 0, 4096) != 0) {
                return CustomSplashProgress.max_texture_size = i;
            }
        }
        return -1;
    }

    private static void checkThreadState() {
        if (CustomSplashProgress.thread.getState() == Thread.State.TERMINATED || CustomSplashProgress.threadError != null) {
            throw new IllegalStateException("Splash thread", CustomSplashProgress.threadError);
        }
    }

    @Deprecated
    public static void pause() {
        checkThreadState();
        CustomSplashProgress.pause = true;
        CustomSplashProgress.lock.lock();
        try {
            CustomSplashProgress.d.releaseContext();
            Display.getDrawable().makeCurrent();
        }
        catch (LWJGLException e) {
            FMLLog.log.error("Error setting GL context:", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
    }

    @Deprecated
    public static void resume() {
        checkThreadState();
        CustomSplashProgress.pause = false;
        try {
            Display.getDrawable().releaseContext();
            CustomSplashProgress.d.makeCurrent();
        }
        catch (LWJGLException e) {
            FMLLog.log.error("Error releasing GL context:", (Throwable)e);
            throw new RuntimeException((Throwable)e);
        }
        CustomSplashProgress.lock.unlock();
    }

    public static void finish() {
        try {
            checkThreadState();
            CustomSplashProgress.done = true;
            CustomSplashProgress.thread.join();
            GL11.glFlush();
            CustomSplashProgress.d.releaseContext();
            Display.getDrawable().makeCurrent();
            CustomSplashProgress.fontTexture.delete();
            CustomSplashProgress.logoTexture.delete();
            CustomSplashProgress.forgeTexture.delete();
        }
        catch (Exception e) {
            FMLLog.log.error("Error finishing SplashProgress:", (Throwable)e);
            disableSplash(e);
        }
    }

    private static boolean disableSplash(final Exception e) {
        if (disableSplash()) {
            throw new EnhancedRuntimeException(e) {
                protected void printStackTrace(final EnhancedRuntimeException.WrappedPrintStream stream) {
                    stream.println("SplashProgress has detected a error loading Minecraft.");
                    stream.println("This can sometimes be caused by bad video drivers.");
                    stream.println("We have automatically disabled the new Splash Screen in config/splash.properties.");
                    stream.println("Try reloading minecraft before reporting any errors.");
                }
            };
        }
        throw new EnhancedRuntimeException(e) {
            protected void printStackTrace(final EnhancedRuntimeException.WrappedPrintStream stream) {
                stream.println("SplashProgress has detected a error loading Minecraft.");
                stream.println("This can sometimes be caused by bad video drivers.");
                stream.println("Please try disabling the new Splash Screen in config/splash.properties.");
                stream.println("After doing so, try reloading minecraft before reporting any errors.");
            }
        };
    }

    private static boolean disableSplash() {
        final File configFile = new File(Minecraft.getMinecraft().gameDir, "config/splash.properties");
        final File parent = configFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        CustomSplashProgress.enabled = false;
        CustomSplashProgress.config.setProperty("enabled", "false");
        try (final Writer w = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
            CustomSplashProgress.config.store(w, "Splash screen properties");
        }
        catch (IOException e) {
            FMLLog.log.error("Could not save the splash.properties file", (Throwable)e);
            return false;
        }
        return true;
    }

    private static IResourcePack createResourcePack(final File file) {
        if (file.isDirectory()) {
            return (IResourcePack)new FolderResourcePack(file);
        }
        return (IResourcePack)new FileResourcePack(file);
    }

    public static void drawVanillaScreen(final TextureManager renderEngine) throws LWJGLException {
        if (!CustomSplashProgress.enabled) {
            Minecraft.getMinecraft().drawSplashScreen(renderEngine);
        }
    }

    public static void clearVanillaResources(final TextureManager renderEngine, final ResourceLocation mojangLogo) {
        if (!CustomSplashProgress.enabled) {
            renderEngine.deleteTexture(mojangLogo);
        }
    }

    public static void checkGLError(final String where) {
        final int err = GL11.glGetError();
        if (err != 0) {
            throw new IllegalStateException(where + ": " + GLU.gluErrorString(err));
        }
    }

    private static InputStream open(final ResourceLocation loc, @Nullable final ResourceLocation fallback, final boolean allowResourcePack) throws IOException {
        if (!allowResourcePack) {
            return CustomSplashProgress.mcPack.getInputStream(loc);
        }
        if (CustomSplashProgress.miscPack.resourceExists(loc)) {
            return CustomSplashProgress.miscPack.getInputStream(loc);
        }
        if (CustomSplashProgress.fmlPack.resourceExists(loc)) {
            return CustomSplashProgress.fmlPack.getInputStream(loc);
        }
        if (!CustomSplashProgress.mcPack.resourceExists(loc) && fallback != null) {
            return open(fallback, null, true);
        }
        return CustomSplashProgress.mcPack.getInputStream(loc);
    }

    private static int bytesToMb(final long bytes) {
        return (int)(bytes / 1024L / 1024L);
    }

    static {
        CustomSplashProgress.pause = false;
        CustomSplashProgress.done = false;
        CustomSplashProgress.angle = 0;
        lock = new ReentrantLock(true);
        mcPack = (IResourcePack)Minecraft.getMinecraft().defaultResourcePack;
        fmlPack = createResourcePack(FMLSanityChecker.fmlLocation);
        CustomSplashProgress.isDisplayVSyncForced = false;
        mutex = new Semaphore(1);
        CustomSplashProgress.max_texture_size = -1;
        buf = BufferUtils.createIntBuffer(4194304);
    }

    private static class Texture
    {
        private final ResourceLocation location;
        private final int name;
        private final int width;
        private final int height;
        private final int frames;
        private final int size;

        public Texture(final ResourceLocation location, @Nullable final ResourceLocation fallback) {
            this(location, fallback, true);
        }

        public Texture(final ResourceLocation location, @Nullable final ResourceLocation fallback, final boolean allowRP) {
            InputStream s = null;
            try {
                this.location = location;
                s = open(location, fallback, allowRP);
                final ImageInputStream stream = ImageIO.createImageInputStream(s);
                final Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
                if (!readers.hasNext()) {
                    throw new IOException("No suitable reader found for image" + location);
                }
                final ImageReader reader = readers.next();
                reader.setInput(stream);
                int frames = reader.getNumImages(true);
                BufferedImage[] images = new BufferedImage[frames];
                for (int i = 0; i < frames; ++i) {
                    images[i] = reader.read(i);
                }
                reader.dispose();
                this.width = images[0].getWidth();
                int height = images[0].getHeight();
                if (height > this.width && height % this.width == 0) {
                    frames = height / this.width;
                    final BufferedImage original = images[0];
                    height = this.width;
                    images = new BufferedImage[frames];
                    for (int j = 0; j < frames; ++j) {
                        images[j] = original.getSubimage(0, j * height, this.width, height);
                    }
                }
                this.frames = frames;
                this.height = height;
                int size;
                for (size = 1; size / this.width * (size / height) < frames; size *= 2) {}
                this.size = size;
                GL11.glEnable(3553);
                synchronized (CustomSplashProgress.class) {
                    GL11.glBindTexture(3553, this.name = GL11.glGenTextures());
                }
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexImage2D(3553, 0, 6408, size, size, 0, 32993, 33639, (IntBuffer)null);
                CustomSplashProgress.checkGLError("Texture creation");
                for (int j = 0; j * (size / this.width) < frames; ++j) {
                    for (int k = 0; j * (size / this.width) + k < frames && k < size / this.width; ++k) {
                        CustomSplashProgress.buf.clear();
                        final BufferedImage image = images[j * (size / this.width) + k];
                        for (int l = 0; l < height; ++l) {
                            for (int m = 0; m < this.width; ++m) {
                                CustomSplashProgress.buf.put(image.getRGB(m, l));
                            }
                        }
                        CustomSplashProgress.buf.position(0).limit(this.width * height);
                        GL11.glTexSubImage2D(3553, 0, k * this.width, j * height, this.width, height, 32993, 33639, CustomSplashProgress.buf);
                        CustomSplashProgress.checkGLError("Texture uploading");
                    }
                }
                GL11.glBindTexture(3553, 0);
                GL11.glDisable(3553);
            }
            catch (IOException e) {
                FMLLog.log.error("Error reading texture from file: {}", (Object)location, (Object)e);
                throw new RuntimeException(e);
            }
            finally {
                IOUtils.closeQuietly(s);
            }
        }

        public ResourceLocation getLocation() {
            return this.location;
        }

        public int getName() {
            return this.name;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getFrames() {
            return this.frames;
        }

        public int getSize() {
            return this.size;
        }

        public void bind() {
            GL11.glBindTexture(3553, this.name);
        }

        public void delete() {
            GL11.glDeleteTextures(this.name);
        }

        public float getU(final int frame, final float u) {
            return this.width * (frame % (this.size / this.width) + u) / this.size;
        }

        public float getV(final int frame, final float v) {
            return this.height * (frame / (this.size / this.width) + v) / this.size;
        }

        public void texCoord(final int frame, final float u, final float v) {
            GL11.glTexCoord2f(this.getU(frame, u), this.getV(frame, v));
        }
    }

    private static class SplashFontRenderer extends FontRenderer
    {
        public SplashFontRenderer() {
            super(Minecraft.getMinecraft().gameSettings, CustomSplashProgress.fontTexture.getLocation(), (TextureManager)null, false);
            super.onResourceManagerReload((IResourceManager)null);
        }

        protected void bindTexture(@Nonnull final ResourceLocation location) {
            if (location != this.locationFontTexture) {
                throw new IllegalArgumentException();
            }
            CustomSplashProgress.fontTexture.bind();
        }

        @Nonnull
        protected IResource getResource(@Nonnull final ResourceLocation location) throws IOException {
            final DefaultResourcePack pack = Minecraft.getMinecraft().defaultResourcePack;
            return (IResource)new SimpleResource(pack.getPackName(), location, pack.getInputStream(location), (InputStream)null, (MetadataSerializer)null);
        }
    }
}

