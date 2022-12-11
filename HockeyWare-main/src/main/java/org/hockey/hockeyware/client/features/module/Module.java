package org.hockey.hockeyware.client.features.module;

import com.mojang.realmsclient.gui.ChatFormatting;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.PacketEvent;
import org.hockey.hockeyware.client.events.client.ModuleToggleEvent;
import org.hockey.hockeyware.client.setting.Configurable;
import org.hockey.hockeyware.client.setting.Keybind;
import org.hockey.hockeyware.client.setting.Setting;
import org.lwjgl.input.Keyboard;

public abstract class Module extends Configurable {
    private final String name;
    private String description = "";
    private final Category category;
    private final Keybind keybind = new Keybind("Keybind", Keyboard.KEY_NONE);
    private final Setting<Boolean> drawn = new Setting<>("Drawn", true);
    private boolean toggled = false;
    // exempt from being reloaded
    private boolean exempt;

    public Module(String name, String description, Category category) {
        this(name, description, category, Keyboard.KEY_NONE);
    }

    public Module(String name, String description, Category category, int code) {
        this.name = name;
        this.category = category;
        this.description = description;

        keybind.setValue(code);

        settings.add(keybind);
        settings.add(drawn);
    }

    public String getDisplayInfo() {
        return null;
    }

    public void onPacketSend(PacketEvent.Send event) {
    }

    public String getFullDisplay() {
        String display = getName();
        if (getDisplayInfo() != null) {
            display += (" " + ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + getDisplayInfo() + ChatFormatting.GRAY + "]");
        }
        return display;
    }

    public void onUpdate() {

    }

    public void onRender3D() {

    }

    public void onRender2D() {

    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    public void onTick() {
    }


    public boolean isOn() {
        return this.toggled;
    }


    public void toggle(boolean silent) {
        toggled = !toggled;
        if (toggled) {
            HockeyWare.EVENT_BUS.register(this);
            onEnable();
        } else {
            HockeyWare.EVENT_BUS.unregister(this);
            onDisable();
        }
        if (!silent) {
            HockeyWare.EVENT_BUS.post(new ModuleToggleEvent(this));
        }
    }

    public boolean isDrawn() {
        return drawn.getValue();
    }

    public boolean isToggled(boolean b) {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        if (toggled && !this.isOn()) {
            this.toggle(false);
        }
        if (!toggled && this.isOn()) {
            this.toggle(false);
        }
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }


    public int getKeybind() {
        return keybind.getValue();
    }

    public void setKeybind(int code) {keybind.setValue(code);
    }

    public void setExempt(boolean in) {
        exempt = in;
    }
}