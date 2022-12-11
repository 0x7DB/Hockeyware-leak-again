package org.hockey.hockeyware.client.features.module;

import org.hockey.hockeyware.client.HockeyWare;

import java.util.ArrayList;
import java.util.List;

public enum Category {
    Combat, Misc, Movement, Player, Render, Chat, Client;

    public static int amountPerCategory(Category category) {
        List<Module> categoryModules = new ArrayList<>();
        for (Module module : HockeyWare.INSTANCE.moduleManager.getModules()) {
            if (module.getCategory().equals(category)) {
                categoryModules.add(module);
            }
        }
        return categoryModules.size();
    }

    public static Category getCategoryFromString(String id) {
        Category finalCategory = null;
        for (Category category : Category.values()) {
            if (category.toString().equalsIgnoreCase(id)) {
                finalCategory = category;
                break;
            }
        }
        return finalCategory;
    }
}
