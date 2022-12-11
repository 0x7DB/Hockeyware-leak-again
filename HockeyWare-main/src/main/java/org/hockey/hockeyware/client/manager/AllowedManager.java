package org.hockey.hockeyware.client.manager;

import org.hockey.hockeyware.client.util.allowed.DisplayUtil;
import org.hockey.hockeyware.client.util.allowed.NoStackTraceThrowable;
import org.hockey.hockeyware.client.util.allowed.SystemUtil;
import org.hockey.hockeyware.client.util.allowed.URLReader;

import java.util.ArrayList;
import java.util.List;

public class AllowedManager {


    public static final String pastebinURL = "https://pastebin.com/raw/rtZjK0B1";

    public static List<String> hwids = new ArrayList<>();

    public static void allowedCheck() {
        hwids = URLReader.readURL();
        boolean isHwidPresent = hwids.contains(SystemUtil.getSystemInfo());
        if (!isHwidPresent) {
            DisplayUtil.Display();
            throw new NoStackTraceThrowable("hockeyl8");
        }
    }
}
