package org.hockey.hockeyware.client.features.module.modules.Chat;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class ChatAppend extends Module {

    // modify these to your liking
    private static final String UNICODE = toUnicode(HockeyWare.NAME);

    private static final String NORMAL = HockeyWare.NAME;

    private final Setting<AppendMode> mode = new Setting<>("Mode", AppendMode.Line);
    private final Setting<Boolean> strict = new Setting<>("Strict", false);

    public ChatAppend() {
        super("ChatAppend", "Appends Chat Messages With A HockeyWare Suffix", Category.Chat);
    }

    @Override
    protected void onEnable() {
        if (fullNullCheck())
            return;
        HockeyWare.EVENT_BUS.register(this);
    }

    @Override
    protected void onDisable() {
        HockeyWare.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientChat(ClientChatEvent event) {
        if (event.isCanceled() || event.getMessage().startsWith("/"))
            return;
        if (event.isCanceled() || event.getMessage().startsWith("."))
            return;
        if (event.isCanceled() || event.getMessage().startsWith("*"))
            return;
        event.setMessage(event.getMessage() + " " + mode.getValue().getString() + " " + (strict.getValue() ? NORMAL : UNICODE));
    }

    private enum AppendMode {
        Line("|"),
        Arrows(">>"),
        Colon(":"),
        HashTag("#");

        private final String string;

        AppendMode(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    private static String toUnicode(String message) {
        return message.toLowerCase()
                .replace("a", "\u1d00")
                .replace("b", "\u0299")
                .replace("c", "\u1d04")
                .replace("d", "\u1d05")
                .replace("e", "\u1d07")
                .replace("f", "\ua730")
                .replace("g", "\u0262")
                .replace("h", "\u029c")
                .replace("i", "\u026a")
                .replace("j", "\u1d0a")
                .replace("k", "\u1d0b")
                .replace("l", "\u029f")
                .replace("m", "\u1d0d")
                .replace("n", "\u0274")
                .replace("o", "\u1d0f")
                .replace("p", "\u1d18")
                .replace("q", "\u01eb")
                .replace("r", "\u0280")
                .replace("s", "\ua731")
                .replace("t", "\u1d1b")
                .replace("u", "\u1d1c")
                .replace("v", "\u1d20")
                .replace("w", "\u1d21")
                .replace("x", "\u02e3")
                .replace("y", "\u028f")
                .replace("z", "\u1d22");
    }
}
