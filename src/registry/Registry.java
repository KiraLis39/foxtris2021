package registry;

import java.awt.Font;
import java.io.File;
import fox.builders.FoxFontBuilder;
import fox.games.FoxSpritesCombiner;
import gui.GameFrame;


public class Registry {
	public static String verse = "2.8.1";
	
	public static FoxFontBuilder ffb = new FoxFontBuilder(new File("./resourse/fonts/"));
	public static FoxSpritesCombiner sprites = new FoxSpritesCombiner();
	
//	public static Font unicodeFont = ffb.setFoxFont(FoxFontBuilder.FONT.UNIVERSALIA, 14 * (GameFrame.fontIncreaseMod), true);
	public static Font downInfoPaneFont = ffb.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 16 + GameFrame.fontIncreaseMod, true);
	public static Font aboutDialogFont = ffb.setFoxFont(0, 24, true);
	public static Font aboutDialogFontB = ffb.setFoxFont(0, 16, false);
	
	public static Font simpleFont;
	public static Font simpleFontB;
}
