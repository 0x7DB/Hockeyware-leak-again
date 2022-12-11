package org.hockey.hockeyware.client.util.setting;

import org.hockey.hockeyware.client.setting.Configurable;
import org.hockey.hockeyware.client.setting.Setting;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public abstract class SettingBundle {

    protected Configurable configurable;

    protected final List<Setting<?>> settings;

    public SettingBundle() {
        this.settings = new Vector<>();
        this.registerAll();
    }

    protected final <T> Setting<T> register(Setting<T> setting){
        this.settings.add(setting);
        return setting;
    }

    protected final void registerAll(){
        Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> Setting.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        settings.add((Setting) field.get(this));
                    } catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                });
    }

    public Configurable getConfigurable() {
        return configurable;
    }

    public void setConfigurable(Configurable configurable) {
        this.configurable = configurable;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }
}
