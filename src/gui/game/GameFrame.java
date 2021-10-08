package gui.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import door.MainClass;
import fox.*;
import gui.StartMenuFrame;
import media.FoxAudioProcessor;
import modalFrames.AboutDialog;
import registry.Registry;
import subComponents.FrameMenuBar;

public class GameFrame extends JFrame {
	public enum THEME  {TECHNO, GLASS, HOLO, OTIME, SIMPLE, ASPHALT}
	private static THEME theme = THEME.GLASS;
	public enum KeyLabel {LEFT, RIGHT, DOWN, STUCK, ROTATE, PAUSE, CONSOLE, FULLSCREEN}
	
	private final static Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
	private static JFrame gameGUIframe;
	
	public static JPanel basePane;
	private static LeftPanel leftPanel;
	private static CenterPanel centerPanel;
	private static RightPanel rightPanel;
	private static DownPanel downPanel;
	
	private static ExecutorService tickPool;
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	public static VolatileImage wallpaper;
	
	private static Boolean gameIsActive = false, hardMode = false, lightMode = false, isInitialized = false, useBackImage = true, speedUp = false;
	private static Boolean paused = false, isFullscreen = false, autoMelodyChange = true;
	
	public static long was;
	private static long deltaTime = 1000L;
	
	public static float fontIncreaseMod = 1;
	private static float FRAME_HEIGHT_ETALON;
	private static float gameFrameWidth, panelsLeftAndRightWidth, gameFrameHeight;
	
	public static int lifes, stageCounter;
	public static int[] stages;
	private static int COLUMN_COUNT, LINES_COUNT;
	private volatile static int[] KEY_LEFT = new int[2], KEY_RIGHT= new int[2], KEY_DOWN= new int[2], KEY_STUCK= new int[2], KEY_ROTATE= new int[2], KEY_PAUSE= new int[2], KEY_CONSOLE= new int[2], KEY_FULLSCREEN= new int[2];
	
	
	private void preInitialization() {
		Out.Print(GameFrame.class, 0, "Start pre-Initialization...");
		dateFormat.setTimeZone(TimeZone.getTimeZone("+3"));
		theme = THEME.valueOf(IOM.getString(IOM.HEADERS.USER_SAVE, "gameTheme"));
		FRAME_HEIGHT_ETALON = (float) screenDimension.getHeight();
//		ffb.setFontsDirectory(new File("./resourse/fonts/"));
		
		// calculate lines, columns, gameFieldMassive`s size:
		COLUMN_COUNT = 12; LINES_COUNT = 14;
		isFullscreen = IOM.getBoolean(IOM.HEADERS.USER_SAVE, "fullscreen");
		setLightcore(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "Lightcore"));
		
		// levels stages set, lifes set, etc:
		lifes = 3;
		stages = new int[] {
				90, 		100, 		120, 		150, 		190, 		240, 		300, 		370, 		450, 		540, 
				640, 	750, 		870, 		1000, 	1150, 	1300, 	1450, 	1600, 	2000, 	3000,
				4500,	6000,	9999};

		Registry.simpleFontB = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 14 * (GameFrame.fontIncreaseMod), true);
		Registry.simpleFont = FoxFontBuilder.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 14 * (GameFrame.fontIncreaseMod), false);

		// load keys from IOM:
		reinitializateControlKeys();
		
		// prepare to building GUI:
		Out.Print(GameFrame.class, 0, "Building the GameFrame...");
		gameGUIframe.setTitle("Foxtris 2021 " + Registry.verse);
		try {gameGUIframe.setIconImage(ResManager.getBImage("gameIcon", true, MainClass.getGraphicConfig()));} catch (Exception e1) {/* IGNORE */}
		gameGUIframe.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		gameGUIframe.setResizable(false);
		
		// charge inAc to gameGUIframe:
		inputActionCharger();
		
		// tune view and animation of the GUI:
		setHardcore(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "hardcoreMode"));
		
		isInitialized = true;
	}
	
	public GameFrame() {
		gameGUIframe = this;
		preInitialization();
		
		centerPanel = new CenterPanel(COLUMN_COUNT, LINES_COUNT);
		downPanel = new DownPanel();
		leftPanel = new LeftPanel();
		rightPanel = new RightPanel();
		
		if (wallpaper == null) {reloadTheme();}
		basePane = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				if (!useBackImage) {gameGUIframe.repaint(); return;}
				if (wallpaper == null) {reloadTheme();}
				g.drawImage(wallpaper, 0, 0, (int) gameFrameWidth, (int) gameFrameHeight, null);
			}
			
			{
				setIgnoreRepaint(true);
				
				add(leftPanel, 		BorderLayout.WEST);
				add(centerPanel, 	BorderLayout.CENTER);
				add(rightPanel, 	BorderLayout.EAST);
				add(downPanel, 		BorderLayout.SOUTH);
			}
		};
		add(basePane);
		setJMenuBar(new FrameMenuBar());

		startGame();
	}
		
	private void startGame() {
		setFullscreen(isFullscreen);
		gameIsActive = true;
		was = System.currentTimeMillis();
		
		tickPool = Executors.newSingleThreadExecutor();
		tickPool.execute(() -> {
			Out.Print(GameFrame.class, 0, "Launch the tick-Pool...");
			int descret = MainClass.getGraphicDevice().getDisplayMode().getRefreshRate();

			while (gameIsActive) {
				try {
					tick();

					if (!CenterPanel.isAnimationOn()) {
						try {
							if (lightMode) {Thread.sleep(deltaTime * 2);
							} else if (hardMode) {Thread.sleep(deltaTime / 2);
							} else if (speedUp) {Thread.sleep(deltaTime / 10);
							} else {Thread.sleep(deltaTime);}
						} catch (Exception e) {/* IGNORE */}
					} else {
						if (descret <= 60) {Thread.sleep(33);
						} else if (descret <= 72) {Thread.sleep(30);
						} else {Thread.sleep(24);}
					}
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		});
		tickPool.shutdown();
		
		leftPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));
		rightPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));
		
		FoxAudioProcessor.nextMusic();
	}

	private static void setFullscreen(Boolean isFullscreen) {
		if (gameGUIframe == null) {return;}
		
		Out.Print(GameFrame.class, 0, "Change Fullscreen mode to " + isFullscreen);
		gameGUIframe.dispose();
		gameGUIframe.setUndecorated(isFullscreen);
		downPanel.setVisible(!isFullscreen);
		
		if (isFullscreen) {
			gameFrameWidth		= (float) screenDimension.getWidth();
			gameFrameHeight 	= (float) screenDimension.getHeight();
			CenterPanel.brickDim = (int) ((gameFrameHeight - 15f) / LINES_COUNT);
		} else {
			gameFrameWidth 	= FRAME_HEIGHT_ETALON;
			float f0 = (gameFrameWidth - 87f) / LINES_COUNT;
			float f1 = (gameFrameWidth / 5f * 3f) / COLUMN_COUNT;
			CenterPanel.brickDim = (int) (f0 / 2f + f1 / 2f) - 10;
			gameFrameHeight = CenterPanel.brickDim * (LINES_COUNT + 2) + 6;
		}
		
		// final sets fonts and next figure view:
		fontIncreaseMod = gameFrameWidth / gameFrameHeight - 0.24f;
		panelsLeftAndRightWidth = (gameFrameWidth - (COLUMN_COUNT * CenterPanel.brickDim)) / 2f;
		
		CenterPanel.gamePanelsSpacingUp = ((gameFrameHeight - (LINES_COUNT * CenterPanel.brickDim)) / 2f) + 48f;
		CenterPanel.gamePanelsSpacingLR = ((gameFrameWidth - panelsLeftAndRightWidth * 2f - (COLUMN_COUNT * CenterPanel.brickDim)) / 2f) + 3;
		System.out.println("gamePanelsSpacingUp: " + CenterPanel.gamePanelsSpacingUp + "; gamePanelsSpacingLR: " + CenterPanel.gamePanelsSpacingLR);
		
		// correction of sizes:
		int minWidthNeed = CenterPanel.brickDim * 21;
		if (gameFrameWidth < minWidthNeed) {
			gameFrameWidth = minWidthNeed;
		}
		
		int minHeightNeed = CenterPanel.brickDim * 11 + 30;
		if (gameFrameHeight < minHeightNeed) {
			gameFrameHeight = minHeightNeed;
		}

		// set size of NextBrickViewer:
		int nextBreakViewSize = CenterPanel.brickDim + 15;
		while (nextBreakViewSize * 3 + 15 >= panelsLeftAndRightWidth) {nextBreakViewSize -= 2;}
		leftPanel.setNextBrickDim(nextBreakViewSize);
		
		// set frame back to visible:
		gameGUIframe.setExtendedState(isFullscreen ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
		if (!isFullscreen) {
			gameGUIframe.setMinimumSize(new Dimension((int) gameFrameWidth + CenterPanel.brickDim / 3, (int) gameFrameHeight));
			gameGUIframe.setSize(new Dimension((int) gameFrameWidth + CenterPanel.brickDim / 3, (int) gameFrameHeight));
		}
		gameGUIframe.setLocationRelativeTo(null);
		gameGUIframe.setVisible(true);

		// reload back image dims:
		reloadWallpaper();
		
		leftPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));
		rightPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));

		// final:
		IOM.set(IOM.HEADERS.USER_SAVE, "fullscreen", isFullscreen);
	}

	private static void reloadTheme() {
		Out.Print(GameFrame.class, 0, "Theme tuner start...");
				
		try {
			String themeName = null;
			if (theme.equals(THEME.TECHNO)) {themeName = ("techno");
			} else if (theme.equals(THEME.GLASS)) {themeName = ("glass");
			} else if (theme.equals(THEME.HOLO)) {themeName = ("holo");
			} else if (theme.equals(THEME.OTIME)) {themeName = ("otime");
			} else if (theme.equals(THEME.SIMPLE)) {themeName = ("simple");
			} else if (theme.equals(THEME.ASPHALT)) {themeName = ("asphalt");}

			reloadThemeResource(themeName);
		} catch (Exception e) {
			Out.Print(GameFrame.class, 3, "ERROR: ResourseManager report about: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		IOM.set(IOM.HEADERS.USER_SAVE, "gameTheme", getTheme().name());
		Out.Print(GameFrame.class, 0, "Theme tune has complete. Now its '" + getTheme().name() + "'.");

		if (GameFrame.isInitialized()) {
			try {reloadWallpaper();} catch (Exception e) {e.printStackTrace();}
			FoxAudioProcessor.nextMusic();
		}

		setFullscreen(!isFullscreen);
		setFullscreen(isFullscreen);
	}
	
	private static void reloadThemeResource(String themeName) {
		if (themeName == null) {Out.Print("reloadThemeResource(): Income themeName is NULL", Out.LEVEL.ERROR, null);}
		Out.Print("Loading Theme '" + themeName + "'...");

		try {
			ResManager.add("theme", 			new File("./resource/pictures/themes/" + themeName + "/theme.png"), true);
			ResManager.add("proto", 			new File("./resource/pictures/themes/" + themeName + "/proto.png"), true);
			ResManager.add("NoneOneBrick", 	new File("./resource/pictures/themes/" + themeName + "/noneOne.png"), true);
			ResManager.add("GreenOneBrick", 	new File("./resource/pictures/themes/" + themeName + "/greenOne.png"), true);
			ResManager.add("OrangeOneBrick",	new File("./resource/pictures/themes/" + themeName + "/orangeOne.png"), true);
			ResManager.add("PurpleOneBrick", 	new File("./resource/pictures/themes/" + themeName + "/purpleOne.png"), true);
			ResManager.add("YellowOneBrick",	new File("./resource/pictures/themes/" + themeName + "/yellowOne.png"), true);
			ResManager.add("BlueOneBrick", 	new File("./resource/pictures/themes/" + themeName + "/blueOne.png"), true);
			ResManager.add("RedOneBrick", 	new File("./resource/pictures/themes/" + themeName + "/redOne.png"), true);
			ResManager.add("BlackOneBrick", 	new File("./resource/pictures/themes/" + themeName + "/blackOne.png"), true);
			
			CenterPanel.proto 				= ResManager.getBImage("proto", true, MainClass.getGraphicConfig());
			CenterPanel.NoneOneBrick 		= ResManager.getBImage("NoneOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.GreenOneBrick 		= ResManager.getBImage("GreenOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.OrangeOneBrick 		= ResManager.getBImage("OrangeOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.PurpleOneBrick 		= ResManager.getBImage("PurpleOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.YellowOneBrick		= ResManager.getBImage("YellowOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.BlueOneBrick 		= ResManager.getBImage("BlueOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.RedOneBrick 		= ResManager.getBImage("RedOneBrick", true, MainClass.getGraphicConfig());
			CenterPanel.BlackOneBrick 		= ResManager.getBImage("BlackOneBrick", true, MainClass.getGraphicConfig());
			
			FoxAudioProcessor.addSound("spawnSound", 		new File("./resource/sounds/" + themeName + "/spawnSound.mp3"));
			FoxAudioProcessor.addSound("roundSound", 		new File("./resource/sounds/" + themeName + "/roundSound.mp3"));
			FoxAudioProcessor.addSound("stuckSound", 		new File("./resource/sounds/" + themeName + "/stuckSound.mp3"));
			FoxAudioProcessor.addSound("fullineSound", 	new File("./resource/sounds/" + themeName + "/fullineSound.mp3"));
			FoxAudioProcessor.addSound("loseSound", 		new File("./resource/sounds/" + themeName + "/loseSound.mp3"));
			FoxAudioProcessor.addSound("winSound", 		new File("./resource/sounds/" + themeName + "/winSound.mp3"));
			FoxAudioProcessor.addSound("achiveSound", 	new File("./resource/sounds/" + themeName + "/achiveSound.mp3"));
			FoxAudioProcessor.addSound("tipSound", 		new File("./resource/sounds/" + themeName + "/tipSound.mp3"));
			FoxAudioProcessor.addSound("warnSound", 		new File("./resource/sounds/" + themeName + "/warnSound.mp3"));
		} catch (Exception e) {e.printStackTrace();
		} finally {reloadWallpaper();}
	}
	
	private static void reinitializateControlKeys() {
		InputAction.clearAll();
		
		KEY_LEFT[0] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_LEFT");
		KEY_LEFT[1] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD");
		
		KEY_RIGHT[0] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_RIGHT");
		KEY_RIGHT[1] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD");
		
		KEY_DOWN[0] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_DOWN");
		KEY_DOWN[1] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD");
		
		KEY_STUCK[0] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_STUCK");
		KEY_STUCK[1] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD");
		
		KEY_PAUSE[0]		 	= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_PAUSE");
		KEY_PAUSE[1]		 	= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD");
		
		KEY_ROTATE[0] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_ROTATE");
		KEY_ROTATE[1] 			= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD");
		
		KEY_CONSOLE[0] 		= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE");
		KEY_CONSOLE[1] 		= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD");
		
		KEY_FULLSCREEN[0]	= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN");
		KEY_FULLSCREEN[1]	= IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD");		
	}
	
	private static void inputActionCharger() {
		InputAction.add("gameframe", gameGUIframe);
		InputAction.set("gameframe", "arrowLeft", 	KEY_LEFT[0], 			KEY_LEFT[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {CenterPanel.shiftLeft();}
		});
		InputAction.set("gameframe", "arrowRight", 	KEY_RIGHT[0], 			KEY_RIGHT[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {CenterPanel.shiftRight();}
		});
		InputAction.set("gameframe", "arrowDown", 	KEY_DOWN[0], 			KEY_DOWN[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CenterPanel.shiftDown();
				CenterPanel.skipOneFrame();
			}
		});
		InputAction.set("gameframe", "arrowUp", 		KEY_STUCK[0], 		KEY_STUCK[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {CenterPanel.stuckToGround();}
		});
		InputAction.set("gameframe", "rotateZ", 		KEY_ROTATE[0], 		KEY_ROTATE[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {CenterPanel.onRotateFigure();}
		});
		InputAction.set("gameframe", "fullscreen", 	KEY_FULLSCREEN[0], 	KEY_FULLSCREEN[1],  new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isFullscreen = !isFullscreen;
				setFullscreen(isFullscreen);
			}
		});
		InputAction.set("gameframe", "escape", 			KEY_PAUSE[0], 		KEY_PAUSE[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {setPaused(!paused);}
		});
		InputAction.set("gameframe", "console", 			KEY_CONSOLE[0], 	KEY_CONSOLE[1], new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				if(!console.isVisible()) {console.setVisible(true); console.changeInputAreaText(null);}
			}
		});

		InputAction.set("gameframe", "altF4", 			KeyEvent.VK_F4, 	KeyEvent.ALT_DOWN_MASK, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {exitConfirm();}
		});
		InputAction.set("gameframe", "f1", 				KeyEvent.VK_F1, 			0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FoxAudioProcessor.playSound("clickSound", 3D);
				setPaused(true);
				
				try {new AboutDialog(gameGUIframe).setVisible(true);
				} catch (Exception e1) {e1.printStackTrace();}				

				Out.Print("Out of pause...");
				setPaused(false);
			}
		});
		InputAction.set("gameframe", "victoryN", 		KeyEvent.VK_N, 			0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {nextLevel();}
		});
		InputAction.set("gameframe", "failNewH", 		KeyEvent.VK_H, 			0, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (CenterPanel.isGameOver()) {
					FoxAudioProcessor.playSound("clickSound", 1D);
					restartGame();
				}
			}
		});		
	}
	
	public static void reloadControls() {
		reinitializateControlKeys();		
		inputActionCharger();
	}

	public void tick() {
		if (paused || CenterPanel.isAnimationOn()) {basePane.repaint(); return;}

		if (CenterPanel.isReadyToNextFigure()) {CenterPanel.createNewFigure();}
		
		if (CenterPanel.isSkipOneFrame()) {CenterPanel.setSkipOneFrame(false);
		} else {CenterPanel.shiftDown();}

		repaint();
	}
	
	public static void reloadWallpaper() {
		if (gameGUIframe == null) {return;}
		
		if (getGameFrameSize().width <= 0 || getGameFrameSize().height <= 0) {System.err.println("Dims wallpaper can`t be less than '1 px'"); return;}
		try {
			Out.Print("GameFrame: reloadWallpaper: Create the wallpaper...");
			wallpaper = MainClass.getGraphicConfig().createCompatibleVolatileImage(screenDimension.width, screenDimension.height);
			BufferedImage imIc = ResManager.getBImage("theme");
			float imageWidth = imIc.getWidth(), imageHeight = imIc.getHeight();
			float realFrameHeigthMinus = isFullscreen() ? 10f : 100f;
			float sideShift = (float) ((imageWidth - getGameFrameSize().getWidth()) / 6f);
			float heightShift = (float) ((imageHeight - getGameFrameSize().getHeight()) / 2f);
			Graphics2D g2D = (Graphics2D) wallpaper.getGraphics();
			g2D.setColor(Color.BLACK);
			g2D.fillRect(0, 0, wallpaper.getWidth(), wallpaper.getHeight());
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			
			if (imageWidth > imageHeight) {
				// ширина фона больше чем высота. Выравниваем ВЫСОТУ по окну игры:
				if (gameGUIframe.getWidth() <= imageWidth) {
					g2D.drawImage(imIc, 0, 0, wallpaper.getWidth(), wallpaper.getHeight(), 
							(int) sideShift, 0,
							(int) (imageWidth - sideShift), (int) (imageHeight + realFrameHeigthMinus),
							null);
				} else {
					sideShift = -sideShift / 3f;
					g2D.drawImage(imIc, 0, 0, wallpaper.getWidth(), wallpaper.getHeight(), 
							(int) (sideShift * 2f), (int) sideShift,
							(int) (imageWidth - sideShift * 2f), (int) (imageHeight + realFrameHeigthMinus - sideShift),
							null);
				}
			} else {
				// высота фона больше, чем ширина. Выравниваем ШИРИНУ по окну игры:
				g2D.drawImage(imIc, 0, 0, wallpaper.getWidth(), wallpaper.getHeight(), 
						0, (int) heightShift,
						(int) imageWidth, (int) (imageHeight - heightShift),
						null);
			}			

			g2D.dispose();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	// checkers and tuners:
	public static void lifeUp() {
		FoxAudioProcessor.playSound("achiveSound", 1D);
		RightPanel.resetBonusCounter();
		FoxAudioProcessor.playSound("achiveSound", 2D);
		lifes++;
		FoxAudioProcessor.playSound("achiveSound", 3D);
	}
	
	public static void lifeLost() {
		lifes--;
		if (CenterPanel.getBalls() <= 50) {CenterPanel.resetBalls();
		} else {CenterPanel.setBalls(CenterPanel.getBalls() - 50);}
		CenterPanel.reCreateGameFieldMassive();
	}
	
	private static void nextLevel() {
		if (CenterPanel.isGameOver() && !CenterPanel.isVictory()) {return;}
		
		FoxAudioProcessor.playSound("clickSound", 1D);
		speedUp();
		CenterPanel.resetVictory();		
		setPaused(false);
		
		basePane.repaint(); // отрисовываем игру для отображения изменений..
		if (IOM.getBoolean(IOM.HEADERS.USER_SAVE, "AutoChangeMelody")) {FoxAudioProcessor.nextMusic();}
		CenterPanel.removeDownLine();
	}
	
	private static void restartGame() {
		gameIsActive = false;

		InputAction.clearAll();
		
		gameGUIframe.dispose();
		
		if (theme != null) {new GameFrame();
		} else {exit();}
	}
		
	public static void exitConfirm() {
		FoxAudioProcessor.playSound("clickSound", 1D);
		paused = true;
		
		Object[] choices = {"Выход", "Отмена"};
		Object defaultChoice = choices[0];
		
		int closeChoise = JOptionPane.showOptionDialog(
				centerPanel, 
				"<html>Пауза<br>(нажми 'Отмена' для возврата) <hr> Выйти в меню?<br>(нажми 'Выход' для выхода)</html>", 
				"Выбор за тобой:", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE,
				null, choices, defaultChoice
		);
		
		switch (closeChoise) {
			case 0: 
				Out.Print(GameFrame.class, 0, "Dispose and back to the StartMenuFrame...");
				exit();
				break;
			case 1: 
				default: paused = false;
		}									
	}
	
	private static void exit() {
		Out.Print(GameFrame.class, 1, "De-initialization...");
		gameIsActive = false;

		InputAction.clearAll();
		tickPool.shutdownNow();
		gameGUIframe.dispose();
		
		new StartMenuFrame();
	}
	
	
	// getters and setters:
	public static boolean isHardcore() {return hardMode;}
	public static void setHardcore(boolean hardcoreActiveFlag) {
		hardMode = hardcoreActiveFlag;
		IOM.set(IOM.HEADERS.USER_SAVE, "hardcoreMode", hardcoreActiveFlag);
	}
	
	
	public static boolean isLightcore() {return lightMode;}
	public static void setLightcore(boolean lightcoreActiveFlag) {
		lightMode = lightcoreActiveFlag;
		IOM.set(IOM.HEADERS.USER_SAVE, "Lightcore", lightcoreActiveFlag);
		
		if (lightMode) {
			hardMode = false;
			IOM.set(IOM.HEADERS.USER_SAVE, "hardcoreMode", "false");
		}
	}
	
	public static boolean isAutoMelodyChange() {return autoMelodyChange;}
	public static void setAutoMelodyChange(boolean amc) {
		autoMelodyChange = amc;
		IOM.set(IOM.HEADERS.USER_SAVE, "AutoChangeMelody", amc);
	}

	public static Map<?, ?> getRender() {
		RenderingHints d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
		d2DRender.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		return d2DRender;
	}

	public static void speedUp() {if (deltaTime > 100) deltaTime -= 50;}
	public static float getSpeed() {
		if (hardMode) {return 1000f / (deltaTime / 2f);
		} else if (lightMode) {return 1000f / (deltaTime * 2f);
		} else {return 1000f / deltaTime;}}
	
	public static Dimension getGameFrameSize() {return gameGUIframe.getSize();}
	
	public static void setPaused(Boolean _paused) {
		Out.Print("GameFrame in the pause: " + _paused);
		paused = _paused;
	}
	public static void setSpeedUp(boolean su) {speedUp = su;}
	
	public static Boolean isPaused() {return paused;}
	public static Boolean isGameActive() {return gameIsActive;}
	public static Boolean isFullscreen() {return isFullscreen;}
	public static Boolean isInitialized() {return isInitialized;}
	public static boolean isUseBackImage() {return useBackImage;}
	public static void setUseBackImage(boolean ubi) {useBackImage = ubi;}

	public static JFrame getFrame() {return gameGUIframe;}

	public static int getLifes() {return lifes;}

	public static THEME getTheme() {	return theme;}
	public static void setTheme(THEME _theme) {
		theme = _theme;
		reloadTheme();

		Out.Print("Out of pause...");
		GameFrame.setPaused(false);
	}

	public static String getKeyLabel(KeyLabel keyLabel) {
		switch (keyLabel) {
			case LEFT: 		return "' " + KeyEvent.getKeyText(KEY_LEFT[0]) + " '" + (KEY_LEFT[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_LEFT[1]) + "'");
			case RIGHT: 	return "' " + KeyEvent.getKeyText(KEY_RIGHT[0]) + " '" + (KEY_RIGHT[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_RIGHT[1]) + "'");
			case DOWN: 		return "' " + KeyEvent.getKeyText(KEY_DOWN[0]) + " '" + (KEY_DOWN[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_DOWN[1]) + "'");
			case ROTATE: 	return "' " + KeyEvent.getKeyText(KEY_ROTATE[0]) + " '" + (KEY_ROTATE[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_ROTATE[1]) + "'");
			case STUCK: 	return "' " + KeyEvent.getKeyText(KEY_STUCK[0]) + " '" + (KEY_STUCK[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_STUCK[1]) + "'");
			case CONSOLE: 	return "' " + KeyEvent.getKeyText(KEY_CONSOLE[0]) + " '" + (KEY_CONSOLE[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_CONSOLE[1]) + "'");
			case PAUSE: 	return "' " + KeyEvent.getKeyText(KEY_PAUSE[0]) + " '" + (KEY_PAUSE[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_PAUSE[1]) + "'");
			case FULLSCREEN:return "' " + KeyEvent.getKeyText(KEY_FULLSCREEN[0]) + " '" + (KEY_FULLSCREEN[1] == 0 ? "" : " + '" + KeyEvent.getModifiersExText(KEY_FULLSCREEN[1]) + "'");
			default: return "-none-";
		}
	}
}
