package subPanels;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import door.MainClass;
import fox.FoxFontBuilder;
import fox.IOM;
import fox.Out;
import fox.ResManager;
import gui.GameFrame;
import media.FoxAudioProcessor;


@SuppressWarnings("serial")
public class CenterPanel extends JPanel {
	private enum Movings {DOWN, LEFT, RIGHT;}

	private static int LINES_COUNT, COLUMN_COUNT;
	private static Random r = new Random();

	private BufferedImage victoryLabelBuffer, gameoverLabelBuffer, pauseLabelBuffer, finalWinLabelBuffer;
	public static BufferedImage proto, NoneOneBrick, GreenOneBrick, OrangeOneBrick, PurpleOneBrick, YellowOneBrick, BlueOneBrick,
	RedOneBrick, BlackOneBrick;

	private static String currentFiguresName = "", currentFiguresNameOver = "";

	private Font f0 = FoxFontBuilder.setFoxFont(0, 34, true);

	public static int brickDim;
	private int tipLifeTime = 7;
	private static int balls, bombDestroyLineMarker = -1, fullLineCheck, startX, startY, linesDestroy;
	static int[][] nextFigureFuture;
	private static int[][] next, figureD, figureB, figureY, figureI, figureZ, figureO, figureL, figurenL, figurenZ;
	private static int[][][] gameFieldMassive;
	public static float gamePanelsSpacingLR = 0.0f, gamePanelsSpacingUp = 0.0f;
	
	private static Boolean gameover = false, victory = false, finGame = false;
	private static Boolean bonusAchieved = false, totalDestroyBonus = false, powerfullDamageBonus = false, cleanFieldBonusFlag = false;
	private static Boolean animationOn = false, readyToNextFigure = true, skipOneFrame = false, shiftLocker = false, shifted = true;	
	private static Boolean hardcoreFlag, showNextFigureFlag, specialBlockOnFlag;

	
	public CenterPanel(int columns, int lines) {
		LINES_COUNT = lines;
		COLUMN_COUNT = columns;
		
		setOpaque(false);
		setIgnoreRepaint(true);
		
		initializateGameFieldPanel();
	}
		
	private void initializateGameFieldPanel() {
		reCreateGameFieldMassive();
		
		buildFiguresMatrixes();
		prepareBaseImageBuffers();
		setSpecialBlocksEnabledFlag(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "specialBlocksEnabled"));
		
		nextFigureFuture = getFiguresMatrixByIndex(getNextRandomInteger());
		skipOneFrame = true;
		readyToNextFigure = true;
	}

	@Override
 	protected void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHints(GameFrame.getRender());
		
		if (!isAnimationOn()) {
			if (isGameOver()) {
				GameFrame.setSpeedUp(true);
				drawGameover(g2D);
				g2D.dispose();
				return;
			} else {GameFrame.setSpeedUp(false);}
			
			if (isFinGame()) {
				drawFinVictory(g2D);
				g2D.dispose();
				return;
			}
			
			if (isVictory()) {
				drawVictory(g2D);
				g2D.dispose();
				return;
			}
			
			if (GameFrame.isPaused()) {
				drawPauseLabel(g2D);
				g2D.dispose();
				return;
			}
		}
		
		try {backFieldDraw(g2D);} catch (Exception e1) {e1.printStackTrace();}
		try {drawLandedFigures(g2D);} catch (Exception e1) {e1.printStackTrace();}
		try {drawActiveFigure(g2D);} catch (Exception e1) {e1.printStackTrace();}

		if (!isAnimationOn()) {drawBonus(g2D);}

		g2D.dispose();
	}

	private void prepareBaseImageBuffers() {
//		RightPanel.grayRectangleReDraw();
//		LeftPanel.grayRectangleReDraw();
		
		try {
			victoryLabelBuffer 		= ResManager.getBImage("victoryImage", true, MainClass.getGraphicConfig());
			gameoverLabelBuffer 	= ResManager.getBImage("gameoverImage", true, MainClass.getGraphicConfig());
			pauseLabelBuffer 		= ResManager.getBImage("pauseImage", true, MainClass.getGraphicConfig());
			finalWinLabelBuffer 	= ResManager.getBImage("finalWinImage", true, MainClass.getGraphicConfig());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void drawPauseLabel(Graphics2D g2D) {
		g2D.drawImage(
				pauseLabelBuffer, 
				getWidth() / 2 - pauseLabelBuffer.getWidth() / 2, 
				getHeight() / 2 - pauseLabelBuffer.getHeight() / 2, 
				this);
	}

	private void backFieldDraw(Graphics2D g2D) throws Exception {
		if (GameFrame.isUseBackImage()) {g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));}

		for (int i = 0; i < LINES_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				g2D.drawImage(
					proto, 
					(int) ((j * brickDim * 1f) + gamePanelsSpacingLR), (int) ((i * brickDim * 1f) + (gamePanelsSpacingUp / LINES_COUNT)), 
					brickDim, brickDim, 
					null);
			}
		}
		
		if (GameFrame.isUseBackImage()) {g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));}
	}
		
	private void drawLandedFigures(Graphics2D g2D) throws Exception {
		if (GameFrame.getTheme().equals(GameFrame.THEME.GLASS)) {
			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		}
		
		for (int i = 0; i < LINES_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				if (gameFieldMassive[i][j][0] != 0) {
					g2D.drawImage(
						getBrickByIndex(gameFieldMassive[i][j][0]), 
						(int) (j * brickDim + gamePanelsSpacingLR), (int) (i * brickDim + (gamePanelsSpacingUp / LINES_COUNT)), 
						brickDim, brickDim, 
						null
					);
				}
			}
		}
	}
			
	private void drawActiveFigure(Graphics2D g2D) throws Exception {
		for (int i = 0; i < LINES_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				if (gameFieldMassive[i][j][1] != 0) {
					g2D.drawImage(
						getBrickByIndex(gameFieldMassive[i][j][1]), 
						(int) (j * brickDim + gamePanelsSpacingLR), (int) (i * brickDim + (gamePanelsSpacingUp / LINES_COUNT)), 
						brickDim, brickDim, 
						null
					);
				}
			}
		}
		
		if (GameFrame.getTheme().equals(GameFrame.THEME.GLASS)) {g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));}
	}
	
	private void drawGameover(Graphics2D g2D) {
		try{fillAreaRandomBricks(g2D);} catch (Exception e1) {e1.printStackTrace();}
		
		g2D.drawImage(
				gameoverLabelBuffer, 
				getWidth() / 2 - gameoverLabelBuffer.getWidth() / 2, 
				getHeight() / 2 - gameoverLabelBuffer.getHeight() / 2, 
				null);
		
		GameFrame.setPaused(true);
	}

	private void drawFinVictory(Graphics2D g2D) {
		try {
			backFieldDraw(g2D);
			recolorAlltoWhite(g2D);
		} catch (Exception e) {e.printStackTrace();}
		
		g2D.drawImage(
				finalWinLabelBuffer, 
				getWidth() / 2 - finalWinLabelBuffer.getWidth() / 2, 
				getHeight() / 2 - finalWinLabelBuffer.getHeight() / 2, 
				null);

		GameFrame.setPaused(true);
	}
			
	private void drawVictory(Graphics2D g2D) {
		try {
			backFieldDraw(g2D);
			recolorAlltoWhite(g2D);
		} catch (Exception e) {e.printStackTrace();}

		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g2D.drawImage(
				victoryLabelBuffer, 
				getWidth() / 2 - victoryLabelBuffer.getWidth() / 2, 
				getHeight() / 2 - victoryLabelBuffer.getHeight() / 2, 
				null);

		GameFrame.setPaused(true);
	}

	private void drawBonus(Graphics2D g2D) {
		if (!bonusAchieved) {return;}
		
		tipLifeTime--;
		
		if (tipLifeTime <= 0) {
			GameFrame.getFrame().repaint();
			resetBonusFlags();
		} else {
			if (totalDestroyBonus) {
				g2D.setColor(Color.DARK_GRAY);
				g2D.setFont(FoxFontBuilder.setFoxFont(0, 34, true));
				g2D.drawString(
						"TOTAL DESTROY!", 
						GameFrame.getGameFrameSize().width / 6 - 2 + tipLifeTime, 
						GameFrame.getGameFrameSize().height / 3 + 2 + tipLifeTime
				);
				
				g2D.setColor(Color.RED);
				g2D.setFont(f0);
				g2D.drawString(
						"TOTAL DESTROY!", 
						GameFrame.getGameFrameSize().width / 6 + tipLifeTime, 
						GameFrame.getGameFrameSize().height / 3 + tipLifeTime
				);
			}
			
			if (powerfullDamageBonus) {
				g2D.setColor(Color.DARK_GRAY);
				g2D.setFont(FoxFontBuilder.setFoxFont(0, 34, true));
				g2D.drawString("POWERFULL!!!", GameFrame.getGameFrameSize().width / 6 - 2 + tipLifeTime, GameFrame.getGameFrameSize().height / 2 + 2 + tipLifeTime);
				
				g2D.setColor(Color.RED);
				g2D.setFont(f0);
				g2D.drawString("POWERFULL!!!", GameFrame.getGameFrameSize().width / 6 + tipLifeTime, GameFrame.getGameFrameSize().height / 2 + tipLifeTime);
			}
		}
	}

	private void resetBonusFlags() {
		tipLifeTime = 7;
		bonusAchieved = false;
		totalDestroyBonus = false;
		powerfullDamageBonus = false;
	}

	private void recolorAlltoWhite(Graphics2D g2D) throws Exception {
		for (int i = 0; i < LINES_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				if (gameFieldMassive[i][j][0] != 0) {
					gameFieldMassive[i][j][0] = 8;
					g2D.drawImage(
						getBrickByIndex(gameFieldMassive[i][j][0]), 
						j * brickDim, i * brickDim, 
						brickDim, brickDim, 
						null
					);
				}
			}
		}
	}
	
	private void fillAreaRandomBricks(Graphics2D g2D) throws Exception {
		for (int i = 0; i < LINES_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				g2D.drawImage(
					getBrickByIndex(getNextRandomInteger(7)), 
					j * brickDim, i * brickDim, 
					brickDim, brickDim, 
					null
				);
			}
		}
	}
	
	
	public static void shiftDown() {
		if (shiftLocker || GameFrame.isPaused() || gameover || animationOn) {return;}
		
		shiftLocker = true;
		shifted = false;
				
		if (checkForMovingAccept(Movings.DOWN)) {
			if (currentFiguresNameOver.equals("B")) {FoxAudioProcessor.playSound("tipSound", 0.5D);}			
			moveDown();			
			shifted = true;
		} else {
			if (currentFiguresNameOver.equals("B")) {
				currentFiguresNameOver = "";				
				bombDestroyLineMarker = startY;
				gameFieldMassive[startY][startX][1] = 0;
				if (balls >= 5) {balls -= 5;}
			}
		}
		
		GameFrame.basePane.repaint(); // отрисовываем игру для отображения изменений..
		
		shiftLocker = false;
		if (!shifted) {onFigureLanding();}
	}

	public static void stuckToGround() {
		if (shiftLocker || GameFrame.isPaused() || gameover || readyToNextFigure || animationOn || currentFiguresNameOver.equals("B")) {return;}
		shiftLocker = true;		
		while (checkForMovingAccept(Movings.DOWN)) {moveDown();}		
		shiftLocker = false;
		onFigureLanding();
	}
	
	private static void moveDown() {
		for (int column = 0; column < COLUMN_COUNT; column++) {
			for (int line = LINES_COUNT - 1; line >= 0; line--) {
				if (gameFieldMassive[line][column][1] != 0) {
					gameFieldMassive[line + 1][column][1] = gameFieldMassive[line][column][1];
					gameFieldMassive[line][column][1] = 0;
				}
			}
		}
		
		startY++;
	}

	private static void mergeFields() {
		for (int i = 0; i < LINES_COUNT ; i++) {
			for (int j = 0; j < COLUMN_COUNT ; j++) {
				if (gameFieldMassive[i][j][1] != 0) {
					gameFieldMassive[i][j][0] = gameFieldMassive[i][j][1];
					gameFieldMassive[i][j][1] = 0;
				}
			}
		}
	}
	
	private static void onFigureLanding() {
		FoxAudioProcessor.playSound("stuckSound", 3D);

		try {
			mergeFields(); // сливаем падающую фигуру с игровым полем..
			checkLines(); //проверяем, нет ли какой полной линии..
//			checkWin(); // проверка на победу...
		} catch (Exception e) {e.printStackTrace();}
		
		GameFrame.basePane.repaint();
		readyToNextFigure = true;
		skipOneFrame = true;
	}

	public static void onRotateFigure() {
		if (shiftLocker || GameFrame.isPaused() || gameover ) {return;}
		if (currentFiguresNameOver.equals("B") || currentFiguresNameOver.equals("O")) {return;}
		
		rotation();
		
		GameFrame.basePane.repaint(); // отрисовываем игру для отображения изменений..
	}
	
	private static void rotation() {
		int minX = COLUMN_COUNT, minY = LINES_COUNT;
//		System.out.println("rotation: Rotation try (COLUMN_COUNT: " + COLUMN_COUNT + ", LINE_COUNT: " + LINES_COUNT + ")...");		
		int activePointsMassive[][] = new int[3][3];
		for (int i = 0; i < LINES_COUNT; i++) {
			for (int j = 0; j < COLUMN_COUNT; j++) {
				if (gameFieldMassive[i][j][1] != 0) {
					if (i < minX) {minX = i;}
					if (j < minY) {minY = j;}
				}
			}
		}

		try {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {activePointsMassive[i][j] = gameFieldMassive[minX + i][minY + j][1];}
			}
		} catch (Exception ReadFigureException0) {
			try {
//				System.out.println("\nRead figure exception: wall to right");
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {activePointsMassive[i][j] = gameFieldMassive[minX + i][minY + (j - 1)][1];}
				}
			} catch (Exception ReadFigureException1) {
//				System.out.println("\nRead figure exception: down wall here");
				return;
			}
		}

		// rotate virtual figure:
		activePointsMassive = getRotateRightMatrix(activePointsMassive);
		
		// fix empty left column:
		if (activePointsMassive[0][0] == 0 && activePointsMassive[1][0] == 0 && activePointsMassive[2][0] == 0) {
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					try {activePointsMassive[i][j] = activePointsMassive[i][j + 1];} catch (Exception e) {activePointsMassive[i][j] = 0;}
				}
			}
		}
		
		try {
			if (minX + 2 >= LINES_COUNT) {
//				System.out.println("\nHand-catched exception: down wall detected.");
				return;
			}
			
			// check for other falled block existing:
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (gameFieldMassive[minX + i][minY + j][0] != 0) {
//						System.out.println("\nHand-catched exception: other block cell detected.");
						return;
					}
				}
			}
			
			// final write to game matrix:
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {gameFieldMassive[minX + i][minY + j][1] = activePointsMassive[i][j];}
			}
		} catch (Exception WriteFigureException0) {
			try {
//				System.out.println("\nWrite figure exception: wall to right");
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
//						System.out.println("set gameField line " + (minX + i) + ", column " + (minY + (j - 1)) + " not line " + (minX + i) + ", column " + (minY + j));
						gameFieldMassive[minX + i][minY + (j - 1)][1] = activePointsMassive[i][j];
					}
				}
			} catch (Exception WriteFigureException1) {
//				System.out.println("\nWrite figure exception: down wall here");
				return;
			}
		}
		
//		System.out.println("rotation: Rotation accept.\n");
		FoxAudioProcessor.playSound("roundSound", 0.5D);
	}
	
	private static int [][] getRotateRightMatrix(int [][] matrix) {
        int [] tmpArray = new int[9];
        int indexArray = 0;
        
        for (int [] row : matrix) {
            for (int elem : row) {tmpArray[indexArray++] = elem;}
        }
        
        indexArray = 0;
        for (int i = 2; i >= 0; i--) {
            for (int j = 0; j < 3; j++) {matrix[j][i] = tmpArray[indexArray++];}
        }
        
        return matrix;
    }
	
	public static void shiftLeft() {
		if (shiftLocker || GameFrame.isPaused() || gameover) {return;}
		shiftLocker = true;
		
		if (checkForMovingAccept(Movings.LEFT)) {
			for (int column = 0; column < COLUMN_COUNT; column++) {
				for (int line = LINES_COUNT - 1; line >= 0; line--) {
					if (gameFieldMassive[line][column][1] != 0) {
						gameFieldMassive[line][column - 1][1] = gameFieldMassive[line][column][1];
						gameFieldMassive[line][column][1] = 0;
					}
				}
			}
			startX--;			
		}

		GameFrame.basePane.repaint(); // отрисовываем игру для отображения изменений..
		shiftLocker = false;
	}
	
	public static void shiftRight() {
		if (shiftLocker || GameFrame.isPaused() || gameover) {return;}
		shiftLocker = true;

		if (checkForMovingAccept(Movings.RIGHT)) {
			for (int column = COLUMN_COUNT - 1; column >= 0; column--) {
				for (int line = 0; line < LINES_COUNT; line++) {
					if (gameFieldMassive[line][column][1] != 0) {
						gameFieldMassive[line][column + 1][1] = gameFieldMassive[line][column][1];
						gameFieldMassive[line][column][1] = 0;
					}
				}
			}
			startX++;
		}
		
		GameFrame.basePane.repaint(); // отрисовываем игру для отображения изменений..
		shiftLocker = false;
	}

	private static Boolean checkForMovingAccept(Movings m) {
		switch (m) {
			case DOWN:
				for (int column = 0; column < COLUMN_COUNT; column++) {
					for (int line = LINES_COUNT - 1; line >= 0; line--) {
						if (gameFieldMassive[line][column][1] != 0) {
							if (line + 1 >= LINES_COUNT) {return false;}
							if (gameFieldMassive[line + 1][column][0] != 0) {return false;}
						}
					}
				}
				break;
			case LEFT:
				for (int column = 0; column < COLUMN_COUNT; column++) {
					for (int line = LINES_COUNT - 1; line >= 0; line--) {
						if (gameFieldMassive[line][column][1] != 0) {
							if (column - 1 < 0) {return false;}
							try {if (gameFieldMassive[line][column - 1][0] != 0) {return false;}	
							} catch (Exception e) {return false;}
						}
					}
				}
				break;
			case RIGHT:
				for (int column = COLUMN_COUNT - 1; column >= 0; column--) {
					for (int line = 0; line < LINES_COUNT; line++) {
						if (gameFieldMassive[line][column][1] != 0) {
							if (column + 1 >= COLUMN_COUNT) {return false;}
							try {if (gameFieldMassive[line][column + 1][0] != 0) {return false;}
							} catch (Exception e) {return false;}
						}
					}
				}
				break;
			default: 
				Out.Print("WARN: Default swith out in checkForMovingAccept...");
				return false;
		}
		
		return true;
	}
	
	
	private static void checkLines() {
		int line = 0, column = 0;
		ExecutorService aniPool = Executors.newFixedThreadPool(1);
		Runnable rn = new Runnable() {
			@Override
			public void run() {
				try {while (!aniPool.awaitTermination(30, TimeUnit.MILLISECONDS)) {GameFrame.basePane.repaint();}} catch (InterruptedException e) {e.printStackTrace();}
				
				//bonus check:
				if (linesDestroy >= 3) {
					RightPanel.bonusCounterAdd();
					FoxAudioProcessor.playSound("achiveSound", 0.5D);
					balls += 10;
					bonusAchieved = true;
					powerfullDamageBonus = true;
				}
				
				animationOn = false;
				cleanFieldCheck();
				checkWin();
			}
		};
		linesDestroy = 0;
		fullLineCheck = 0;
		
		if (bombDestroyLineMarker > -1) {
			final int marker = bombDestroyLineMarker;
			aniPool.execute(new Runnable() {
				@Override
				public void run() {
					animationOn = true;
					if (marker < 0) {throw new RuntimeException("Var 'line' can`t be less 0");}
					destroyLine(marker);
					linesDestroy++;
					RightPanel.addOneDestroyLine();
//					balls += 5;
				}				
			});
			aniPool.shutdown();
		} else {
			for (line = 0; line < LINES_COUNT; line++) {
				fullLineCheck = 0;
				for (column = 0; column < COLUMN_COUNT; column++) {
					if (gameFieldMassive[line][0][0] == 0) {break;}					
					if (gameFieldMassive[line][column][0] != 0) {
						fullLineCheck++;
						if (fullLineCheck == COLUMN_COUNT) {
							animationOn = true;
							final int marker = line;
							aniPool.execute(new Runnable() {
								@Override
								public void run() {
									destroyLine(marker);
									linesDestroy++;
									RightPanel.addOneDestroyLine();
									balls += 5;
								}
							});
							break;
						}
					}
				}
			
			}
			aniPool.shutdown();
		}
		
		Thread th = new Thread(rn);
		th.start();
		
		skipOneFrame = true;
		bombDestroyLineMarker = -1;
	}
	
	private synchronized static void destroyLine(int line) {
		FoxAudioProcessor.playSound("fullineSound", 0.5D);

		for (int k = 0; k < COLUMN_COUNT; k++) {
			gameFieldMassive[line][k][0] = 6;
			GameFrame.basePane.repaint();
			try {Thread.sleep(18);} catch (InterruptedException e) {e.printStackTrace();}
		}

		try {Thread.sleep(150);} catch (InterruptedException e) {e.printStackTrace();}
		
		for (int k = 0; k < COLUMN_COUNT; k++) {
			gameFieldMassive[line][k][0] = 8;
			GameFrame.basePane.repaint();
			try {Thread.sleep(14);} catch (InterruptedException e) {e.printStackTrace();}
		}

		for (int k = 0; k < COLUMN_COUNT; k++) {
			gameFieldMassive[line][k][0] = 0;
			GameFrame.basePane.repaint();
			Thread.yield();
			
			for (int fallLine = line; fallLine > 1; fallLine--) {
				gameFieldMassive[fallLine][k][0] = gameFieldMassive[fallLine - 1][k][0];
				gameFieldMassive[fallLine - 1][k][0] = 0;
				GameFrame.basePane.repaint();
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	
	private static void checkWin() {
		if (balls >= GameFrame.stages[GameFrame.stageCounter]) {
			FoxAudioProcessor.playSound("winSound", 2D);
			victory = true;
			
			return;
		}
		
		if (RightPanel.getBonusCount() >= 5) {GameFrame.lifeUp();}
	}

	private static void cleanFieldCheck() {
		//clean bonus check:
		cleanFieldBonusFlag = true;		
		for (int bonusLineChecker = 0; bonusLineChecker < LINES_COUNT; bonusLineChecker++) {
			for (int column = 0; column < COLUMN_COUNT; column++) {
				if (gameFieldMassive[bonusLineChecker][column][0] != 0) {cleanFieldBonusFlag = false;}
			}
		}
		
		if (cleanFieldBonusFlag) {
			RightPanel.bonusCounterAdd();
			FoxAudioProcessor.playSound("achiveSound", 2D);
			balls += 25;
			bonusAchieved = true;
			totalDestroyBonus = true;
		}
	}
	
	static BufferedImage getBrickByIndex(Integer integer) {
		switch(integer) {
			case 0: return 	NoneOneBrick;
			
			case 1: return 	GreenOneBrick;
			case 2: return 	OrangeOneBrick;
			case 3: return 	PurpleOneBrick;
			case 4: return 	YellowOneBrick;
			case 5: return 	BlueOneBrick;
			case 6: return 	RedOneBrick;
			case 7: return 	BlackOneBrick;
			
		default: return 		NoneOneBrick;
		}
	}
		
	private static int[][] getFiguresMatrixByIndex(Integer integer) {
		switch(integer) {
			case 0: 
				currentFiguresName = "Z";
				return figureZ;
			case 1: 
				currentFiguresName = "O";
				return figureO;
			case 2: 
				currentFiguresName = "L";
				return figureL;
			case 3: 
				currentFiguresName = "nL";
				return figurenL;
			case 4: 
				currentFiguresName = "nZ";
				return figurenZ;
			case 5: 
				currentFiguresName = "I";
				return figureI;
			case 6: 
				currentFiguresName = "Y";
				return figureY;
			case 7: 
				if (isSpecialBlocksEnabled()) {
					if (getNextRandomInteger(10) >= 5) {
						currentFiguresName = "B";
						return figureB;
					} else {return getFiguresMatrixByIndex(getNextRandomInteger(8));}
				} else {return getFiguresMatrixByIndex(getNextRandomInteger(8));}
		default: 
			currentFiguresName = "D";
			return figureD;
		}
	}

	private static Integer getNextRandomInteger() {return r.nextInt(8);}
	
	private static Integer getNextRandomInteger(int n) {return r.nextInt(n);}

	private void buildFiguresMatrixes() {
		figureZ = new int[][] {
			{1, 1, 0}, 
			{0, 1, 1}, 
			{0, 0, 0}};
		
		figureO = new int[][] {
			{2, 2, 0}, 
			{2, 2, 0}, 
			{0, 0, 0}};
		
		figureL = new int[][] {
			{3, 3, 3}, 
			{3, 0, 0}, 
			{0, 0, 0}};
		
		figurenL = new int[][] {
			{4, 4, 4}, 
			{0, 0, 4}, 
			{0, 0, 0}};
		
		figurenZ = new int[][] {
			{5, 0, 0}, 
			{5, 5, 0}, 
			{0, 5, 0}};
		
		figureI = new int[][] {
			{6, 6, 6}, 
			{0, 0, 0}, 
			{0, 0, 0}};
		
		figureY = new int[][] {
			{8, 8, 8}, 
			{0, 8, 0}, 
			{0, 0, 0}};
		
		figureB = new int[][] {
			{7, 0, 0}, 
			{0, 0, 0}, 
			{0, 0, 0}};
		
		figureD = new int[][] {
			{1,  1, 1}, 
			{0, 1, 0}, 
			{1,  1, 1}};
	}
	
	public static void createNewFigure() {
		System.out.println("createNewFigure...");
		if (gameover) {return;}
		
		if (gameFieldMassive[0][COLUMN_COUNT / 2][0] != 0 ||
			gameFieldMassive[0][COLUMN_COUNT / 2 - 1][0] != 0 ||
			gameFieldMassive[0][COLUMN_COUNT / 2 + 1][0] != 0) {
			if (GameFrame.lifes == 0) {gameover = true;} else {GameFrame.lifeLost();}
			FoxAudioProcessor.playSound("loseSound", 2D);
		}
		
		next = nextFigureFuture;
		currentFiguresNameOver = currentFiguresName;
		
		if (currentFiguresNameOver.equals("B")) {FoxAudioProcessor.playSound("warnSound", 1.0D);
		} else {FoxAudioProcessor.playSound("spawnSound", 1.0D);}
	
		nextFigureFuture = getFiguresMatrixByIndex(getNextRandomInteger());
	
		if (COLUMN_COUNT % 2 == 0) {
			for (int x = 0; x < next.length; x++) {
				for (int y = 0; y < next.length; y++) {
					gameFieldMassive[0 + x][COLUMN_COUNT / 2 - 1 + y][1] = next[x][y];
				}
			}
			
			startX = COLUMN_COUNT / 2 - 1;
			startY = 0;
		} else {
			for (int x = 0; x < next.length; x++) {
				for (int y = 0; y < next.length; y++) {
					gameFieldMassive[0 + x][COLUMN_COUNT / 2 + y][1] = next[x][y];
				}
			}
			
			startX = COLUMN_COUNT / 2;
			startY = 0;
		}
		
		readyToNextFigure = false;
	}

	
	public static boolean isSpecialBlocksEnabled() {return specialBlockOnFlag;}
	public static void setSpecialBlocksEnabledFlag(boolean _spetialBlocksEnabledFlag) {
		specialBlockOnFlag = _spetialBlocksEnabledFlag;
		IOM.set(IOM.HEADERS.USER_SAVE, "specialBlocksEnabled", _spetialBlocksEnabledFlag);
	}
	
	public static boolean isShowNextBlockEnabledFlag() {return showNextFigureFlag;}
	public static void setShowNextBlockEnabledFlag(boolean _showNextFlag) {
		showNextFigureFlag = _showNextFlag;
		System.out.println("nextFigureShow is now: " + _showNextFlag);
		IOM.set(IOM.HEADERS.USER_SAVE, "nextFigureShow", _showNextFlag);
	}
	
	public static boolean isHardcoreEnabledFlag() {return hardcoreFlag;}
	public static void setHardcoreEnabledFlag(boolean _hardcoreFlag) {hardcoreFlag = _hardcoreFlag;}
	
	
	public static void skipOneFrame() {skipOneFrame = true;}
	public static void setSkipOneFrame(boolean skip) {skipOneFrame = skip;}
	public static Boolean isSkipOneFrame() {return skipOneFrame;}

	public static Boolean isReadyToNextFigure() {return readyToNextFigure;}
	public static Boolean isAnimationOn() {return animationOn;}
	public static Boolean isGameOver() {return gameover;}
	public static Boolean isFinGame() {return finGame;}
	public static Boolean isVictory() {return victory;}

	public static void reCreateGameFieldMassive() {gameFieldMassive = new int[LINES_COUNT][COLUMN_COUNT][COLUMN_COUNT];}

	public static void resetVictory() {
		if (GameFrame.stageCounter < GameFrame.stages.length - 1) {
			GameFrame.stageCounter++;
			victory = false;
			resetBalls();
		} else {
			FoxAudioProcessor.playSound("finalMusic", 2D);
			finGame = true;
		}
	}
	public static void resetBalls() {balls = 0;}
	public static int getBalls() {return balls;}
	public static void setBalls(int _balls) {balls = _balls;}

	public static void removeDownLine() {
		bombDestroyLineMarker = LINES_COUNT - 1;
		checkLines();
	}
}
