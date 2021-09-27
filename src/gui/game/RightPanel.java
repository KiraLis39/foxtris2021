package gui.game;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import door.MainClass;
import fox.FoxFontBuilder;
import fox.IOM;
import fox.ResManager;
import gui.game.GameFrame.KeyLabel;
import registry.Registry;

public class RightPanel extends JPanel {
	private JPanel rightInfosPane;
	
	private static BufferedImage rightGrayBase, lifeHeartImage, bonusKristalImage;
	private String tmpSpeed;
	private static int allLinesDestroyCounter, bonusCounter;
	private static Color panelColor = new Color(0.3f, 0.3f, 0.3f, 0.85f);
	private static String userName;

	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		revalidate();
		repaint();

		prepareBaseImageBuffers();
	 }
	
	public RightPanel() {
		userName = IOM.getString(IOM.HEADERS.LAST_USER, "lastUser");

		setLayout(new BorderLayout());
		setIgnoreRepaint(true);
		setOpaque(false);
		
		initializeRightPanel();
		
		float leftShift = 21f;
		rightInfosPane = new JPanel(new BorderLayout()) {
			{setIgnoreRepaint(true);}
			
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				
				render(g2D);
				drawGrayBack(this, g2D);
				drawControlInfo(g2D);
				
				g2D.dispose();
			}	

			private void render(Graphics2D g2D) {
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//				g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//				g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//				g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//				g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//				g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			}

			private void drawControlInfo(Graphics2D g2D) {
				tmpSpeed = String.format("%(.2fx", GameFrame.getSpeed());
				g2D.setColor(Color.BLACK);
				g2D.setFont(Registry.simpleFontB);
				
				float spacing = GameFrame.fontIncreaseMod * 2f;
				float headerAlign = (float) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "Управление:").getWidth() / 2D);
				
				g2D.drawString("Управление:", headerAlign, spacing * 13f);
				g2D.drawString("_________________", headerAlign, spacing * 14f);
				g2D.setColor(Color.RED);
				g2D.drawString("Управление:", headerAlign + 2, spacing * 13f);
				g2D.drawString("_________________", headerAlign + 1, spacing * 14f);

				g2D.setColor(Color.GREEN);
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Влево:", leftShift, spacing * 25f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.LEFT), 
						84 * GameFrame.fontIncreaseMod , spacing * 25f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Вправо:", leftShift, spacing * 35f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.RIGHT), 
						84 * GameFrame.fontIncreaseMod , spacing * 35f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Вниз:", leftShift, spacing * 45f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.DOWN), 
						84 * GameFrame.fontIncreaseMod , spacing * 45f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Поворот:", leftShift, spacing * 55f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.ROTATE), 
						84 * GameFrame.fontIncreaseMod , spacing * 55f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Экран:", leftShift, spacing * 65f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.FULLSCREEN), 
						84 * GameFrame.fontIncreaseMod , spacing * 65f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Консоль:", leftShift, spacing * 75f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.CONSOLE), 
						84 * GameFrame.fontIncreaseMod , spacing * 75f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Сброс:", leftShift, spacing * 85f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.STUCK), 
						84 * GameFrame.fontIncreaseMod , spacing * 85f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Пауза:", leftShift, spacing * 95f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(GameFrame.getKeyLabel(KeyLabel.PAUSE), 
						84 * GameFrame.fontIncreaseMod , spacing * 95f);
				//
				
				//
				g2D.setFont(Registry.simpleFontB);
				g2D.setColor(Color.BLACK);
				
				headerAlign = (float) (getWidth() / 2 - FoxFontBuilder.getStringBounds(g2D, "Информация:").getWidth() / 2D);
				
				g2D.drawString("Информация:", headerAlign, spacing * 114f);
				g2D.drawString("__________________", headerAlign, spacing * 115f);
				g2D.setColor(Color.RED);
				g2D.drawString("Информация: ", headerAlign + 1, spacing * 114f + 1);
				g2D.drawString("__________________", headerAlign + 1, spacing * 115f + 1);

				
				g2D.setColor(Color.GREEN);
				g2D.drawString("Линии:", leftShift, spacing * 125f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(String.valueOf(allLinesDestroyCounter),
						84 * GameFrame.fontIncreaseMod, spacing * 125f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Скорость:", leftShift, spacing * 135f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(tmpSpeed,
						84 * GameFrame.fontIncreaseMod, spacing * 135f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Очки:", leftShift, spacing * 145f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(String.valueOf(CenterPanel.getBalls()), 
						84 * GameFrame.fontIncreaseMod, spacing * 145f);
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Цель:", leftShift, spacing * 155f);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(String.valueOf(GameFrame.stages[GameFrame.stageCounter]), 
						84 * GameFrame.fontIncreaseMod, spacing * 155f);
				
				
				g2D.setFont(Registry.simpleFontB);
				g2D.setColor(Color.YELLOW);
				g2D.drawString("Бонусы:", leftShift, spacing * 178f);
				for (int i = 0; i < bonusCounter; i++) {
					g2D.drawImage(
							bonusKristalImage, 
							(int) (80 * GameFrame.fontIncreaseMod + (19 * i)), (int) (spacing * 171f), 
							(int) (16 * GameFrame.fontIncreaseMod), (int) (16 * GameFrame.fontIncreaseMod), 
							null);
				}
				
				g2D.setColor(Color.RED);
				if (GameFrame.getLifes() <= 0) {g2D.setColor(Color.RED);}
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Жизни:", leftShift, spacing * 190f);
				for (int i = 0; i < GameFrame.lifes; i++) {
					g2D.drawImage(
							lifeHeartImage, 
							(int) (80 * GameFrame.fontIncreaseMod + (19 * i)), (int) (spacing * 183f), 
							(int) (24 * GameFrame.fontIncreaseMod), (int) (16 * GameFrame.fontIncreaseMod), 
							null);
				}
				
				
				g2D.setColor(Color.WHITE);
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Игрок:", leftShift, getHeight() - 70);
				g2D.setFont(Registry.simpleFont);
				g2D.drawString(userName,
						84 * GameFrame.fontIncreaseMod, getHeight() - 70);				
				
				
				g2D.setFont(Registry.simpleFontB);
				g2D.drawString("Время в игре:", leftShift, getHeight() - 50);
				g2D.setFont(Registry.simpleFont);
				if (!GameFrame.isPaused()) {
					g2D.drawString(
							GameFrame.dateFormat.format(System.currentTimeMillis() - GameFrame.was), 
							leftShift, getHeight() - 30);
					}
			}
		};
		
		add(rightInfosPane, BorderLayout.CENTER);
	}
	
	private void drawGrayBack(JPanel panel, Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(2));
		g2D.setRenderingHints(GameFrame.getRender());
		
		g2D.drawImage(rightGrayBase, 
				3, 3, 
				panel.getWidth() - 6, panel.getHeight() - 6, 
				null);
		
		g2D.drawRoundRect(6, 6, panel.getWidth() - 12, panel.getHeight() - 12, 10, 10);
	}

	private void initializeRightPanel() {
		grayRectangleReDraw();
		prepareBaseImageBuffers();
	}

	private void prepareBaseImageBuffers() {
		try {			
			lifeHeartImage		= ResManager.getBImage("life", true, MainClass.getGraphicConfig());
			bonusKristalImage	= ResManager.getBImage("bonus", true, MainClass.getGraphicConfig());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void grayRectangleReDraw() {
		rightGrayBase = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2D = rightGrayBase.createGraphics();
		g2D.setRenderingHints(GameFrame.getRender());
		
		g2D.setColor(panelColor);
		g2D.fillRoundRect(0, 0, 600, 600, 20, 20);
		
		g2D.dispose();
	}
	
	public static void addOneDestroyLine() {allLinesDestroyCounter++;}
	
	public static void bonusCounterAdd() {bonusCounter++;}
	public static int getBonusCount() {return bonusCounter;}
	public static void resetBonusCounter() {bonusCounter = 0;}
}
