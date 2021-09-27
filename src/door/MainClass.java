package door;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import fox.IOM;
import fox.Out;
import fox.ResManager;
import gui.StartMenuFrame;
import media.FoxAudioProcessor;


public class MainClass {
	private static JFrame logoFrame;
	private static float frameOpacity = 0.1f;
	private static Image im;
	
	private static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private static GraphicsDevice grDev = ge.getDefaultScreenDevice();
	private static GraphicsConfiguration grConf;
	private static Thread logoThread;
	
	
	public static void main(String[] args) {
		try {UIManager.setLookAndFeel(new NimbusLookAndFeel());
		} catch (Exception e) {System.err.println("Couldn't get specified look and feel, for some reason.");}
		
		grConf = grDev.getDefaultConfiguration();
		
		Out.Print(MainClass.class, Out.LEVEL.INFO, "Запуск программы.");
		loadUserData();
	
		ResManager.setDebugOn(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "ResourceManagerDebugLogEnabled"));
		IOM.setConsoleOutOn(IOM.getBoolean(IOM.HEADERS.LAST_USER, "IOMDebugLogEnabled"));
		Out.setEnabled(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "LogEnabled(global)"));

		
		if (IOM.getBoolean(IOM.HEADERS.USER_SAVE, "showStartLogo")) {
			logoThread = new Thread(() -> {
				showLogo();
				Out.Print(MainClass.class, Out.LEVEL.INFO, "Logo has ended.");
			});
			logoThread.start();
		}
		
		loadResourses();
		controlsRegistration();

		if (logoThread != null) {while (logoThread.isAlive()) {try {logoThread.join();} catch (InterruptedException e) {e.printStackTrace();}}}
		
		Out.Print(MainClass.class, 0, "Launch the StartMenu...");
		new StartMenuFrame(grConf);
	}

	@SuppressWarnings("serial")
	private static void showLogo() {
		Out.Print(MainClass.class, 0, "Showing Logo...");
		FoxAudioProcessor.playSound("launchSound", 0.05D);
		
		logoFrame = new JFrame();
		logoFrame.setUndecorated(true);
		logoFrame.setBackground(new Color(0,0,0,0));
		logoFrame.setOpacity(frameOpacity);

		try {im = new ImageIcon("./resourse/pictures/logo0").getImage();
		} catch (Exception e) {
			Out.Print(MainClass.class, 3, "ERROR: Logo image not ready.");
			e.printStackTrace();
		}
			
		logoFrame.add(new JPanel() {
			{setPreferredSize(new Dimension(im.getWidth(null), im.getHeight(null)));}
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				
				logoFrame.setPreferredSize(new Dimension(im.getWidth(null), im.getHeight(null)));
				logoFrame.pack();
				logoFrame.setLocationRelativeTo(null);
				g2D.drawImage(im, 0, 0, im.getWidth(null), im.getHeight(null), null);				
			}
		});
		
		logoFrame.pack();
		logoFrame.setLocationRelativeTo(null);
		logoFrame.setVisible(true);
		
		while (frameOpacity < 1.0f) {
			Thread.yield();
			frameOpacity += 0.002f;
			if (frameOpacity > 1.0f) {frameOpacity = 1.0f;}
			logoFrame.setOpacity(frameOpacity);
		}
		
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		
		while (frameOpacity > 0.0f) {
			Thread.yield();
			frameOpacity -= 0.006f;
			if (frameOpacity < 0.0f) {frameOpacity = 0.0f;}
			logoFrame.setOpacity(frameOpacity);
		}
		
		logoFrame.dispose();
	}
	
	private static void loadUserData() {
		Out.Print(MainClass.class, 0, "Load IOM...");
		
		IOM.add(IOM.HEADERS.LAST_USER, new File("./user/data"));
		if (!IOM.getBoolean(IOM.HEADERS.LAST_USER, "lastUser")) {IOM.set(IOM.HEADERS.LAST_USER, "lastUser", "NonameUser");}
		
		IOM.add(IOM.HEADERS.USER_SAVE, new File("./user/" + IOM.getString(IOM.HEADERS.LAST_USER, "lastUser") + ".conf"));
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "gameTheme")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "gameTheme", "HOLO");}

		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "showStartLogo(global)"))	{IOM.set(IOM.HEADERS.USER_SAVE, "showStartLogo", "true");}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "LogEnabled(global)"))	 		{IOM.set(IOM.HEADERS.USER_SAVE, "LogEnabled(global)", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "OutDebugLogEnabled"))		{IOM.set(IOM.HEADERS.USER_SAVE, "OutDebugLogEnabled", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "OutErrorLevelName")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "OutErrorLevelName", Out.LEVEL.INFO);}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "ResourceManagerDebugLogEnabled")) {IOM.set(IOM.HEADERS.USER_SAVE, "ResourceManagerDebugLogEnabled", "true");}	
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "IOMDebugLogEnabled"))		{IOM.set(IOM.HEADERS.USER_SAVE, "IOMDebugLogEnabled", "true");}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "nextFigureShow"))				{IOM.set(IOM.HEADERS.USER_SAVE, "nextFigureShow", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "specialBlocksEnabled"))		{IOM.set(IOM.HEADERS.USER_SAVE, "specialBlocksEnabled", "true");}
		
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "AutoChangeMelody"))			{IOM.set(IOM.HEADERS.USER_SAVE, "AutoChangeMelody", "true");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "hardcoreMode"))					{IOM.set(IOM.HEADERS.USER_SAVE, "hardcoreMode", "false");}
		if (!IOM.existKey(IOM.HEADERS.USER_SAVE.name(), "Lightcore"))							{IOM.set(IOM.HEADERS.USER_SAVE, "Lightcore", "false");}
		
		FoxAudioProcessor.setMusicEnabled(!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "musicMute"));
		FoxAudioProcessor.setSoundEnabled(!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "soundMute"));		
	}

	private static void controlsRegistration() {
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_LEFT")) 					{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_LEFT", KeyEvent.VK_LEFT);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_RIGHT")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_RIGHT", KeyEvent.VK_RIGHT);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_DOWN")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_DOWN", KeyEvent.VK_DOWN);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_STUCK")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_STUCK", KeyEvent.VK_UP);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD", 0);}
		
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_ROTATE")) 				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_ROTATE", KeyEvent.VK_Z);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_PAUSE")) 					{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_PAUSE", KeyEvent.VK_ESCAPE);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD")) 		{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE"))				{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE", KeyEvent.VK_BACK_QUOTE);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD")) 	{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD", 0);}
		
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN"))			{IOM.set(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN", KeyEvent.VK_F);}
		if (!IOM.getBoolean(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD")) {IOM.set(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD", 0);}
	}
	
	private static void loadResourses() {
		Out.Print(MainClass.class, 0, "Loading media resourses....");
		
		try {
			// pictures and icons (cashed):
			remAdd("hardcore", "./resource/pictures/icons/hardcore.png");
			remAdd("hardcore_off", "./resource/pictures/icons/hardcore_off.png");

			remAdd("spec", "./resource/pictures/icons/spec.png");
			remAdd("spec_off", "./resource/pictures/icons/spec_off.png");

			remAdd("tips", "./resource/pictures/icons/tips.png");
			remAdd("tips_off", "./resource/pictures/icons/tips_off.png");

			remAdd("life", "./resource/pictures/icons/life.png");
			remAdd("bonus", "./resource/pictures/icons/bonus.png");

			remAdd("autoMusic", "./resource/pictures/icons/autoMusic.png");
			remAdd("autoMusic_off", "./resource/pictures/icons/autoMusic_off.png");

			remAdd("lightcore", "./resource/pictures/icons/lightcore.png");
			remAdd("lightcore_off", "./resource/pictures/icons/lightcore_off.png");

			remAdd("autoMusic", "./resource/pictures/icons/autoMusic.png");

			remAdd("gameIcon", "./resource/pictures/gameIcon");

			remAdd("backAbout", "./resource/pictures/about/000");
			remAdd("starsAbout", "./resource/pictures/about/001");
			remAdd("bAbout", "./resource/pictures/about/002");

			remAdd("buttonProto", "./resource/pictures/buttonProto");
			remAdd("buttonProtoOver", "./resource/pictures/buttonProtoOver");
			remAdd("buttonProtoPress", "./resource/pictures/buttonProtoPress");

			remAdd("victoryImage", "./resource/pictures/victoryImage");
			remAdd("gameoverImage", "./resource/pictures/gameoverImage");
			remAdd("pauseImage", "./resource/pictures/pauseImage");
			remAdd("finalWinImage", "./resource/pictures/finalImage");

			remAdd("switchOff", "./resource/pictures/switchOff");
			remAdd("switchOn", "./resource/pictures/switchOn");
			remAdd("stageLabel", "./resource/pictures/stage");

			remAdd("logoFoxList", "./resource/pictures/sprites/logoFoxList");
			remAdd("MBSL", "./resource/pictures/sprites/MBSL");
			remAdd("unibutton", "./resource/pictures/sprites/unibutton");
			remAdd("numbers", "./resource/pictures/sprites/numbers");

			List<Path> musics = Files.list(Paths.get("./resource/music")).collect(Collectors.toList());
			for (Path file : musics) {
				FoxAudioProcessor.addMusic(file.toFile().getName(), file.toFile());
			}
			
		} catch (Exception e) {
			Out.Print(MainClass.class, 3, "ERROR: with ResourseManager by cause: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		Out.Print(MainClass.class, 0, "Loading media resourses has accomplish!");
	}

	private static void remAdd(String name, String path) throws Exception {
		ResManager.add(name, new File(path), false);
	}

	public static GraphicsConfiguration getGraphicConfig() {return grConf;}
	public static GraphicsDevice getGraphicDevice() {return grDev;}
}
