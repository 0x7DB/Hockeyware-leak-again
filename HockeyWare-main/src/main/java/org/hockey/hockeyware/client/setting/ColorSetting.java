package org.hockey.hockeyware.client.setting;

import org.hockey.hockeyware.client.events.client.SettingEvent;

import java.awt.*;

public class ColorSetting extends Setting<Color> {
    private int red;
    private int green;
    private int blue;
    private int alpha;
    private boolean sync;
    private boolean rainbow;
    private boolean staticRainbow;
    private Color staticColor;
    private float rainbowSpeed = 100.f;
    private float rainbowSaturation = 100.f;
    private float rainbowBrightness = 100.f;
    private Color mutableInitial;

    public ColorSetting(String nameIn, Color initialValue) {
        super(nameIn, initialValue);
        this.mutableInitial = initialValue;
        this.staticColor = initialValue;
        this.red = initialValue.getRed();
        this.green = initialValue.getGreen();
        this.blue = initialValue.getBlue();
        this.alpha = initialValue.getAlpha();
    }

    public void setInitial(Color color) {
        this.mutableInitial = color;
    }

    public void setValueNoStatic(Color value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Color value) {
        super.setValue(value);
        staticColor = value;
    }

    @Override
    public void setValue(Color value, boolean withEvent) {
        if (withEvent) {
            SettingEvent<Color> event = onChange(new SettingEvent<>(this, value));
            if (!event.isCanceled()) {
                setValueRGBA(event.getValue());
            }
        } else {
            setValueRGBA(value);
        }
    }

    public void setValueAlpha(Color value) {
        final Color newColor = new Color(value.getRed(), value.getGreen(), value.getBlue(), getValue().getAlpha());
        setValueRGBANoStatic(newColor);
    }

    public SettingResult fromString(String string) {
        if (string.contains("-")) {
            final String[] values = string.split("-");
            if (values.length > 6) {

                int color;

                try {
                    color = (int) Long.parseLong(values[0], 16);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }

                setValue(new Color(color, values[0].length() > 6));

                boolean syncBuf;
                try {
                    syncBuf = Boolean.parseBoolean(values[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                setSync(syncBuf);

                boolean rainbowBuf;
                try {
                    rainbowBuf = Boolean.parseBoolean(values[2]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                setRainbow(rainbowBuf);

                boolean rainbowStaticBuf;
                try {
                    rainbowStaticBuf = Boolean.parseBoolean(values[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }
                setStaticRainbow(rainbowStaticBuf);

                float speed;

                try {
                    speed = (int) Float.parseFloat(values[4]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }

                setRainbowSpeed(speed);

                float saturation;

                try {
                    saturation = (int) Float.parseFloat(values[5]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }

                setRainbowSaturation(saturation);

                float brightness;

                try {
                    brightness = (int) Float.parseFloat(values[6]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new SettingResult(false, e.getMessage());
                }

                setRainbowBrightness(brightness);
            }
        } else {
            int color;

            try {
                color = (int) Long.parseLong(string, 16);
            } catch (Exception e) {
                e.printStackTrace();
                return new SettingResult(false, e.getMessage());
            }

            setValue(new Color(color, string.length() > 6));
        }
        return SettingResult.SUCCESSFUL;
    }

    public int getRed() {
        return red;
    }

    public float getR() {
        return red / 255.0f;
    }

    public void setRed(int red) {
        this.red = red;
        this.setValue(new Color(red, blue, green, alpha));
    }

    public int getGreen() {
        return green;
    }

    public float getG() {
        return green / 255.0f;
    }

    public void setGreen(int green) {
        this.green = green;
        this.setValue(new Color(red, blue, green, alpha));
    }

    public int getBlue() {
        return blue;
    }

    public float getB() {
        return blue / 255.0f;
    }

    public void setBlue(int blue) {
        this.blue = blue;
        this.setValue(new Color(red, blue, green, alpha));
    }

    public int getAlpha() {
        return alpha;
    }

    public float getA() {
        return alpha / 255.0f;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        this.setValue(new Color(red, blue, green, alpha));
    }

    public int getRGB() {
        return this.value.getRGB();
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public float getRainbowSpeed() {
        return rainbowSpeed;
    }

    public void setRainbowSpeed(float rainbowSpeed) {
        this.rainbowSpeed = rainbowSpeed;
    }

    public float getRainbowSaturation() {
        return rainbowSaturation;
    }

    public void setRainbowSaturation(float rainbowSaturation) {
        this.rainbowSaturation = rainbowSaturation;
    }

    public float getRainbowBrightness() {
        return rainbowBrightness;
    }

    public void setRainbowBrightness(float rainbowBrightness) {
        this.rainbowBrightness = rainbowBrightness;
    }

    public boolean isStaticRainbow() {
        return staticRainbow;
    }

    public void setStaticRainbow(boolean staticRainbow) {
        this.staticRainbow = staticRainbow;
    }

    private void setValueRGBA(Color value) {
        this.value = value;
        this.red = value.getRed();
        this.blue = value.getBlue();
        this.green = value.getGreen();
        this.alpha = value.getAlpha();
        this.staticColor = value;
    }

    private void setValueRGBANoStatic(Color value) {
        this.value = value;
        this.red = value.getRed();
        this.blue = value.getBlue();
        this.green = value.getGreen();
        this.alpha = value.getAlpha();
    }

    public Color getStaticColor() {
        return staticColor;
    }
}
