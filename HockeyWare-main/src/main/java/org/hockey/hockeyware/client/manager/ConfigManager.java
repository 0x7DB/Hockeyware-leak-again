package org.hockey.hockeyware.client.manager;

import com.google.gson.*;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.manager.friend.Friend;
import org.hockey.hockeyware.client.manager.friend.FriendManager;
import org.hockey.hockeyware.client.setting.ColorSetting;
import org.hockey.hockeyware.client.setting.Setting;
import org.hockey.hockeyware.client.util.client.EnumConverter;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

    private static final List<String> paths = Arrays.asList(
            "HockeyWare/",
            "HockeyWare/Configs"
    );

    public static void createDirectory() throws IOException {
        for (final String path : paths) {
            final Path dir = Paths.get(path);
            if (!Files.exists(dir))
                Files.createDirectories(dir);
        }
    }

    public static void registerFile(final String name, final String path) throws IOException {
        boolean extension = true;
        Path newPath;
        if (name == "HockeyWare") {
            newPath = Paths.get("HockeyWare/" + path + "/" + name + (".config"));
        } else {
            newPath = Paths.get("HockeyWare/" + path + "/" + name + (".json"));
        }

        if (!Files.exists(newPath))
            Files.createFile(newPath);
        else {
            final File file = new File(newPath.toUri());
            file.delete();
            Files.createFile(newPath);
        }
    }

    public static void saveConfig() {
        try {
            saveFriends();
            savePrefix();
            saveModules("HockeyWare", true, true);
        } catch (final IOException ignored) {
        }
    }

    public static void loadConfig() {
        try {
            createDirectory();
            loadModules("HockeyWare");
            loadFriends();
            loadPrefix();
        } catch (IOException ignored) {
        }
    }

    public static void saveModules(final String name, final boolean saveKeybind, final boolean saveDrawn) throws IOException {
        registerFile(name, "Configs");
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("HockeyWare/Configs/" + name + ".config")), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();

        for (final Module module : HockeyWare.INSTANCE.moduleManager.getModules()) {
            if (module == null)
                continue;

            final JsonObject featureObject = new JsonObject();
            final JsonObject settingObject = new JsonObject();

            featureObject.add("Enabled", new JsonPrimitive(module.isOn()));
            if (saveKeybind)
                featureObject.add("Keybind", new JsonPrimitive(module.getKeybind()));

            for (final Setting<?> setting : module.getSettings()) {
                if (setting == null)
                    continue;

                if (setting.isBoolean() && (!setting.getName().equals("Drawn") || !saveDrawn))
                    settingObject.add(setting.getName(), new JsonPrimitive((Boolean) setting.getValue()));

                if (setting.isNumber())
                    settingObject.add(setting.getName(), new JsonPrimitive((Number) setting.getValue()));

                if (setting.isEnum())
                    settingObject.add(setting.getName(), new JsonPrimitive(((Enum<?>) setting.getValue()).name()));

                if (setting.isColor())
                    settingObject.add(setting.getName(), new JsonPrimitive(((ColorSetting) setting).getValue().getRGB()));
            }

            featureObject.add("Settings", settingObject);
            mainObject.add(module.getName(), featureObject);
        }

        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        outputStreamWriter.write(jsonString);
        outputStreamWriter.close();
    }

    public static void loadModules(final String name) throws IOException {
        if (!Files.exists(Paths.get("HockeyWare/Configs/" + name + ".config")))
            throw new IOException("Could not find the file");

        final InputStream inputStream = Files.newInputStream(Paths.get("HockeyWare/Configs/" + name + ".config"));
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject == null)
            throw new IOException("Main object is null");

        for (final Module module : HockeyWare.INSTANCE.moduleManager.getModules()) {
            try {
                if (module == null)
                    continue;

                final JsonObject featureObject = mainObject.getAsJsonObject(module.getName());
                final JsonObject settingObject = featureObject.getAsJsonObject("Settings");

                for (final Setting<?> setting : module.getSettings()) {
                    if (setting == null)
                        continue;

                    final JsonElement settingValueObject = settingObject.get(setting.getName());

                    if (settingValueObject == null)
                        continue;

                    if (setting.isBoolean()) {
                        final Setting<Boolean> something = (Setting<Boolean>) setting;
                        something.setValue(settingValueObject.getAsBoolean());
                    }

                    if (setting.isNumber()) {
                        if (setting.getValue() instanceof Integer) {
                            final Setting<Integer> something = (Setting<Integer>) setting;
                            something.setValue(settingValueObject.getAsInt());
                        }

                        if (setting.getValue() instanceof Double) {
                            final Setting<Double> something = (Setting<Double>) setting;
                            something.setValue(settingValueObject.getAsDouble());
                        }

                        if (setting.getValue() instanceof Float) {
                            final Setting<Float> something = (Setting<Float>) setting;
                            something.setValue(settingValueObject.getAsFloat());
                        }

                        if (setting.getValue() instanceof Long) {
                            final Setting<Long> something = (Setting<Long>) setting;
                            something.setValue(settingValueObject.getAsLong());
                        }

                        if (setting.getValue() instanceof Byte) {
                            final Setting<Byte> something = (Setting<Byte>) setting;
                            something.setValue(settingValueObject.getAsByte());
                        }

                        if (setting.getValue() instanceof Short) {
                            final Setting<Short> something = (Setting<Short>) setting;
                            something.setValue(settingValueObject.getAsShort());
                        }
                    }

                    if (setting.isEnum()) {
                        final EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                        final Setting<Enum> something = (Setting<Enum>) setting;
                        something.setValue(converter.doBackward(settingValueObject));
                    }

                    if (setting.isColor()) {
                        final ColorSetting colorSetting = (ColorSetting) setting;
                        colorSetting.setValue(new Color(settingValueObject.getAsInt(), true));
                    }
                }

                if (featureObject.get("Enabled").getAsBoolean() && !module.isOn())
                    module.setToggled(true);
                else if (featureObject.get("Enabled").getAsBoolean() && module.isOn())
                    module.setToggled(false);
            } catch (final Exception ignored) {
            }
        }
    }

    public static void saveFriends() throws IOException {
        registerFile("Friends", "");

        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("HockeyWare/Friends.json")), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray friendArray = new JsonArray();

        FriendManager.getFriends().forEach(friend -> {
            if (friend != null)
                friendArray.add(friend.getAlias());
        });

        mainObject.add("Friends", friendArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        outputStreamWriter.write(jsonString);
        outputStreamWriter.close();
    }

    public static void loadFriends() throws IOException {
        final Path path = Paths.get("HockeyWare/Friends.json");
        if (!Files.exists(path))
            return;

        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Friends") == null)
            return;

        final JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();

        friendObject.forEach(object -> FriendManager.add(new Friend(object.getAsString())));

        inputStream.close();
    }

    public static void savePrefix() throws IOException {
        registerFile("Prefix", "");

        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("HockeyWare/Prefix.json")), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();

        mainObject.add("Prefix", new JsonPrimitive(CommandManager.prefix));
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }

    public static void loadPrefix() throws IOException {
        final Path path = Paths.get("HockeyWare/Prefix.json");
        if (!Files.exists(path))
            return;

        final InputStream inputStream = Files.newInputStream(path);
        final JsonObject mainObject = new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject();

        if (mainObject.get("Prefix") != null)
            CommandManager.setPrefix(mainObject.get("Prefix").getAsJsonPrimitive().getAsString());

        inputStream.close();
    }

    public static boolean hasRan() throws IOException {
        final String FILE_NAME = "HockeyWare/HasRan.txt";
        if (new File(FILE_NAME).isFile()) {
            return true;
        } else {
            Path newFilePath = Paths.get(FILE_NAME);
            Files.createFile(newFilePath);
            return false;
        }
    }
}