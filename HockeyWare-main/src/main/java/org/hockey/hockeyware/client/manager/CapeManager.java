package org.hockey.hockeyware.client.manager;

import org.hockey.hockeyware.client.features.Globals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeManager implements Globals {

    private final List<UUID> capes = new ArrayList<>();

    public CapeManager() {
        try {
            URL capesList = new URL("https://pastebin.com/raw/7BeHLSqe");
            BufferedReader in = new BufferedReader(new InputStreamReader(capesList.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                capes.add(UUID.fromString(inputLine));
            }
        } catch (Exception ignored) {
        }
    }

    public boolean hasCape(UUID uuid) {
        return this.capes.contains(uuid);
    }
}