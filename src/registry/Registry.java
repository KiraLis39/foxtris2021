package registry;

import java.awt.Font;
import fox.FoxFontBuilder;
import gui.game.GameFrame;


public class Registry {
	public static String name = "Foxtris 2021";
	public static String verse = "2.9.0";

	public static Font downInfoPaneFont = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 16 + GameFrame.fontIncreaseMod, true);
	public static Font aboutDialogFont = FoxFontBuilder.setFoxFont(0, 24, true);
	public static Font aboutDialogFontB = FoxFontBuilder.setFoxFont(0, 16, false);
	
	public static Font simpleFont;
	public static Font simpleFontB;
}
