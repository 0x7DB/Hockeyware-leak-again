package org.hockey.hockeyware.client.setting;

import org.hockey.hockeyware.client.features.Globals;
import org.hockey.hockeyware.client.util.setting.SettingBundle;

import java.util.ArrayList;
import java.util.Arrays;

public class Configurable implements Globals {
    protected final ArrayList<Setting> settings = new ArrayList<>();

    public void register() {
        Arrays.stream(getClass().getDeclaredFields())
                .filter((field) -> Setting.class.isAssignableFrom(field.getType()) || SettingBundle.class.isAssignableFrom(field.getType()))
                .forEach((field) -> {
                    field.setAccessible(true);
                    Object object;
                    try {
                        object = field.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                    if(object instanceof SettingBundle){
                        SettingBundle settingBundle = (SettingBundle) object;
                        settingBundle.setConfigurable(this);
                        this.settings.addAll(settingBundle.getSettings());
                        return;
                    }
                    this.settings.add((Setting) object);
                });
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }
}
