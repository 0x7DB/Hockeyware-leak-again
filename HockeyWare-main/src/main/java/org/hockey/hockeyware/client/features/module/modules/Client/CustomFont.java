package org.hockey.hockeyware.client.features.module.modules.Client;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.hockey.hockeyware.client.HockeyWare;
import org.hockey.hockeyware.client.events.client.OptionChangeEvent;
import org.hockey.hockeyware.client.features.module.Category;
import org.hockey.hockeyware.client.features.module.Module;
import org.hockey.hockeyware.client.setting.Setting;

public class CustomFont extends Module {


    public static CustomFont INSTANCE;

    public CustomFont() {
        super("CustomFont", "Allows You To Change The Font Of The Client", Category.Client);
        INSTANCE = this;
    }

    public static final Setting<Fonts> font = new Setting<>("Font", Fonts.Verdana);
    public static final Setting<Integer> size = new Setting<>("Size", 18, 0, 30);

    @Override
    public void onEnable() {
    }

    @SubscribeEvent
    public void onOption(OptionChangeEvent event) {
        if (event.getOption().equals(font) || event.getOption().equals(size)) {
            HockeyWare.INSTANCE.fontManager.setFont(font.getValue().getName());
            HockeyWare.INSTANCE.fontManager.setFontSize(size.getValue());
        }
    }

    public enum Fonts {
        Arial("Arial"), ArialBlack("ArialBlack"), Bahnschrift("Bahnschrift"), Calibri("Calibri"), CalibriLight("CalibriLight"), Cambria("Cambria"), CambriaMath("CambriaMath"), Candara("Candara"), CandaraLight("CandaraLight"), CascadiaCode("CascadiaCode"), CascadiaMono("CascadiaMono"), ComicSansMS("ComicSansMS"), Consolas("Consolas"), Constantia("Constantia"), Corbel("Corbel"), CorbelLight("CorbelLight"), CourierNew("CourierNew"), Dialog("Dialog"), DialogInput("DialogInput"), Ebrima("Ebrima"), FranklinGothicMedium("FranklinGothicMedium"), Gabriola("Gabriola"), Gadugi("Gadugi"), Georgia("Georgia"), HoloLensMDL2Assets("HoloLensMDL2Assets"), Impact("Impact"), InkFree("InkFree"), JavaneseText("JavaneseText"), LeelawadeeUI("LeelawadeeUI"), LeelawadeeUISemilight("LeelawadeeUISemilight"), LucidaConsole("LucidaConsole"), LucidaSansUnicode("LucidaSansUnicode"), MalgunGothic("MalgunGothic"), MalgunGothicSemilight("MalgunGothicSemilight"), Marlett("Marlett"), MicrosoftHimalaya("MicrosoftHimalaya"), MicrosoftJhengHei("MicrosoftJhengHei"), MicrosoftJhengHeiLight("MicrosoftJhengHeiLight"), MicrosoftJhengHeiUI("MicrosoftJhengHeiUI"), MicrosoftJhengHeiUILight("MicrosoftJhengHeiUILight"), MicrosoftNewTaiLue("MicrosoftNewTaiLue"), MicrosoftPhagsPa("MicrosoftPhagsPa"), MicrosoftSansSerif("MicrosoftSansSerif"), MicrosoftTaiLe("MicrosoftTaiLe"), MicrosoftYaHei("MicrosoftYaHei"), MicrosoftYaHeiLight("MicrosoftYaHeiLight"), MicrosoftYaHeiUI("MicrosoftYaHeiUI"), MicrosoftYaHeiUILight("MicrosoftYaHeiUILight"), MicrosoftYiBaiti("MicrosoftYiBaiti"), MingLiUExtB("MingLiUExtB"), MingLiUHKSCSExtB("MingLiUHKSCSExtB"), MongolianBaiti("MongolianBaiti"), Monospaced("Monospaced"), MSGothic("MSGothic"), MSPGothic("MSPGothic"), MSUIGothic("MSUIGothic"), MVBoli("MVBoli"), MyanmarText("MyanmarText"), NirmalaUI("NirmalaUI"), NirmalaUISemilight("NirmalaUISemilight"), NSimSun("NSimSun"), PalatinoLinotype("PalatinoLinotype"), PMingLiUExtB("PMingLiUExtB"), SansSerif("SansSerif"), SegoeMDL2Assets("SegoeMDL2Assets"), SegoePrint("SegoePrint"), SegoeScript("SegoeScript"), SegoeUI("SegoeUI"), SegoeUIBlack("SegoeUIBlack"), SegoeUIEmoji("SegoeUIEmoji"), SegoeUIHistoric("SegoeUIHistoric"), SegoeUILight("SegoeUILight"), SegoeUISemibold("SegoeUISemibold"), SegoeUISemilight("SegoeUISemilight"), SegoeUISymbol("SegoeUISymbol"), Serif("Serif"), SimSun("SimSun"), SimSunExtB("SimSunExtB"), SitkaBanner("SitkaBanner"), SitkaDisplay("SitkaDisplay"), SitkaHeading("SitkaHeading"), SitkaSmall("SitkaSmall"), SitkaSubheading("SitkaSubheading"), SitkaText("SitkaText"), Sylfaen("Sylfaen"), Symbol("Symbol"), Tahoma("Tahoma"), TimesNewRoman("TimesNewRoman"), TrebuchetMS("TrebuchetMS"), Verdana("Verdana"), Webdings("Webdings"), Wingdings("Wingdings"), YuGothic("YuGothic"), YuGothicLight("YuGothicLight"), YuGothicMedium("YuGothicMedium"), YuGothicUI("YuGothicUI"), YuGothicUILight("YuGothicUILight"), YuGothicUISemibold("YuGothicUISemibold"), YuGothicUISemilight("YuGothicUISemilight");

        private final String name;

        Fonts(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}