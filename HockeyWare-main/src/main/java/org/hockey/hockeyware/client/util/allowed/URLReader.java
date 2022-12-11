package org.hockey.hockeyware.client.util.allowed;

import org.hockey.hockeyware.client.manager.AllowedManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLReader {

    public static List<String> readURL() {
        List<String> s = new ArrayList<>();
        try {
            final URL url = new URL(AllowedManager.pastebinURL);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String hwid;
            while ((hwid = bufferedReader.readLine()) != null) {
                s.add(hwid);
            }
        } catch (Exception e) {
        }
        return s;
    }
}
