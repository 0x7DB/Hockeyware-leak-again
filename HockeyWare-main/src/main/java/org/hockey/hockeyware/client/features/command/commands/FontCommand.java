package org.hockey.hockeyware.client.features.command.commands;


import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.features.command.Command;
import org.hockey.hockeyware.client.features.module.modules.Client.CustomFont;
import org.hockey.hockeyware.client.util.client.ClientMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FontCommand extends Command {
    public FontCommand() {
        super("font", "Allows You To Change The Font Of The Client", "font [set/size/list/reset] [font name/size]");
    }

    String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    String info = "";

    @Override
    public void runCommand(List<String> args) {
        if (args.size() >= 1) {
            String type = args.get(0);
            if (args.size() >= 2) {
                info = args.get(1);
                info = info.replaceAll("_", " ");
            }

            if (type.equals("reset")) {
                HockeyWare.INSTANCE.fontManager.reset();
                ClientMessage.sendMessage("Reset To Default Custom Font");
                return;
            }

            if (type.equals("list")) {
                    ClientMessage.sendMessage("Arial");
                    ClientMessage.sendMessage("ArialBlack");
                    ClientMessage.sendMessage("Bahnschrift");
                    ClientMessage.sendMessage("Calibri");
                    ClientMessage.sendMessage("CalibriLight");
                    ClientMessage.sendMessage("Cambria");
                    ClientMessage.sendMessage("CambriaMath");
                    ClientMessage.sendMessage("Candara");
                    ClientMessage.sendMessage("CandaraLight");
                    ClientMessage.sendMessage("CascadiaCode");
                    ClientMessage.sendMessage("CascadiaMono");
                    ClientMessage.sendMessage("ComicSansMS");
                    ClientMessage.sendMessage("Consolas");
                    ClientMessage.sendMessage("Constantia");
                    ClientMessage.sendMessage("Corbel");
                    ClientMessage.sendMessage("CorbelLight");
                    ClientMessage.sendMessage("CourierNew");
                    ClientMessage.sendMessage("Dialog");
                    ClientMessage.sendMessage("DialogInput");
                    ClientMessage.sendMessage("Ebrima");
                    ClientMessage.sendMessage("FranklinGothicMedium");
                    ClientMessage.sendMessage("Gabriola");
                    ClientMessage.sendMessage("Gadugi");
                    ClientMessage.sendMessage("Georgia");
                    ClientMessage.sendMessage("HoloLensMDL2Assets");
                    ClientMessage.sendMessage("Impact");
                    ClientMessage.sendMessage("InkFree");
                    ClientMessage.sendMessage("JavaneseText");
                    ClientMessage.sendMessage("LeelawadeeUI");
                    ClientMessage.sendMessage("LeelawadeeUISemilight");
                    ClientMessage.sendMessage("LucidaConsole");
                    ClientMessage.sendMessage("LucidaSansUnicode");
                    ClientMessage.sendMessage("MalgunGothic");
                    ClientMessage.sendMessage("MalgunGothicSemilight");
                    ClientMessage.sendMessage("Marlett");
                    ClientMessage.sendMessage("MicrosoftHimalaya");
                    ClientMessage.sendMessage("MicrosoftJhengHei");
                    ClientMessage.sendMessage("MicrosoftJhengHeiLight");
                    ClientMessage.sendMessage("MicrosoftJhengHeiUI");
                    ClientMessage.sendMessage("MicrosoftJhengHeiUI");
                    ClientMessage.sendMessage("MicrosoftNewTaiLue");
                    ClientMessage.sendMessage("MicrosoftPhagsPa");
                    ClientMessage.sendMessage("MicrosoftSansSerif");
                    ClientMessage.sendMessage("MicrosoftTaiLe");
                    ClientMessage.sendMessage("MicrosoftYaHei");
                    ClientMessage.sendMessage("MicrosoftYaHeiLight");
                    ClientMessage.sendMessage("MicrosoftYaHeiUI");
                    ClientMessage.sendMessage("MicrosoftYaHeiUILight");
                    ClientMessage.sendMessage("MicrosoftYiBaiti");
                    ClientMessage.sendMessage("MingLiUExtB");
                    ClientMessage.sendMessage("MingLiUHKSCSExtB");
                    ClientMessage.sendMessage("MongolianBaiti");
                    ClientMessage.sendMessage("Monospaced");
                    ClientMessage.sendMessage("MSGothic");
                    ClientMessage.sendMessage("MSPGothic");
                    ClientMessage.sendMessage("MVBoli");
                    ClientMessage.sendMessage("MyanmarText");
                    ClientMessage.sendMessage("NirmalaUI");
                    ClientMessage.sendMessage("NirmalaUISemilight");
                    ClientMessage.sendMessage("NSimSun");
                    ClientMessage.sendMessage("PalatinoLinotype");
                    ClientMessage.sendMessage("PMingLiUExtB");
                    ClientMessage.sendMessage("SansSerif");
                    ClientMessage.sendMessage("SegoeMDL2Assets");
                    ClientMessage.sendMessage("SegoePrint");
                    ClientMessage.sendMessage("SegoeScript");
                    ClientMessage.sendMessage("SegoeUI");
                    ClientMessage.sendMessage("SegoeUIBlack");
                    ClientMessage.sendMessage("SegoeUIEmoji");
                    ClientMessage.sendMessage("SegoeUIHistoric");
                    ClientMessage.sendMessage("SegoeUILight");
                    ClientMessage.sendMessage("SegoeUISemibold");
                    ClientMessage.sendMessage("SegoeUISemilight");
                    ClientMessage.sendMessage("SegoeUISymbol");
                    ClientMessage.sendMessage("Serif");
                    ClientMessage.sendMessage("SimSun");
                    ClientMessage.sendMessage("SimSunExtB");
                    ClientMessage.sendMessage("SitkaBanner");
                    ClientMessage.sendMessage("SitkaDisplay");
                    ClientMessage.sendMessage("SitkaHeading");
                    ClientMessage.sendMessage("SitkaSmall");
                    ClientMessage.sendMessage("SitkaSubheading");
                    ClientMessage.sendMessage("SitkaText");
                    ClientMessage.sendMessage("Sylfaen");
                    ClientMessage.sendMessage("Symbol");
                    ClientMessage.sendMessage("Tahoma");
                    ClientMessage.sendMessage("TimesNewRoman");
                    ClientMessage.sendMessage("TrebuchetMS");
                    ClientMessage.sendMessage("Verdana");
                    ClientMessage.sendMessage("Webdings");
                    ClientMessage.sendMessage("Wingdings");
                    ClientMessage.sendMessage("YuGothic");
                    ClientMessage.sendMessage("YuGothicLight");
                    ClientMessage.sendMessage("YuGothicMedium");
                    ClientMessage.sendMessage("YuGothicUI");
                    ClientMessage.sendMessage("YuGothicUILight");
                    ClientMessage.sendMessage("YuGothicUILight");
                    ClientMessage.sendMessage("YuGothicUISemibold");
                    ClientMessage.sendMessage("YuGothicUISemilight");
                return;
            }

            if (type.equals("set") && !Objects.equals(info, "")) {
                HockeyWare.INSTANCE.fontManager.setFont(info);
                for (CustomFont.Fonts font : CustomFont.Fonts.values()) {
                    if (font.getName().equalsIgnoreCase(info)) {
                        CustomFont.font.setValue(font);
                        ClientMessage.sendMessage("Font Set To " + info);
                        return;
                    }
                }
                return;
            }

            if (type.equals("size") && !Objects.equals(info, "")) {
                for (char character : info.toCharArray()) {
                    if (!Arrays.asList(numbers).contains(String.valueOf(character))) {
                        ClientMessage.sendMessage("Invalid Size");
                        return;
                    }
                }
//                FencingFPlus2.INSTANCE.fontManager.setFontSize(Integer.parseInt(info));
                CustomFont.size.setValue(Integer.parseInt(info));
                ClientMessage.sendMessage("Font Size Set To " + info);
                return;
            }

            ClientMessage.sendMessage(getSyntax());
        } else {
            ClientMessage.sendMessage(getSyntax());
        }
    }
}