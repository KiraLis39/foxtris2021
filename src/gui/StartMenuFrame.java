package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import door.ExitClass;
import door.MainClass;
import fox.adds.IOM;
import fox.adds.Out;
import fox.builders.ResourceManager;
import media.FoxAudioProcessor;
import modalFrames.OptionsDialog;
import registry.Registry;
import subPanels.CenterPanel;


@SuppressWarnings("serial")
public class StartMenuFrame extends JFrame implements MouseListener, MouseMotionListener {
	private final int FRAME_WIDTH = 800, FRAME_HEIGHT = 600;
	
	private Canvas canvas;
	private FontMetrics fm;
	private BufferStrategy bs;
	private Rectangle2D startB, optionB, exitB, userChanger;
	
	private VolatileImage background;
	private BufferedImage[] sp;
	
	private Point cursor, startButtonPaint;

	private Color colorBackground = new Color(0.3f, 0.3f, 0.3f, 1.0f);
	private Font f0 = Registry.ffb.setFoxFont(6, 21, false);
	private Font f1 = Registry.ffb.setFoxFont(5, 20, true);

	private Thread repTh;

	private Boolean repThRun = true, startPress = false, startOver = false, optionsPress = false, 	optionsOver = false, exitPress = false, exitOver = false;
	private int tmpInt0, tmpInt1;
	
	
	public StartMenuFrame() {this(MainClass.getGraphicConfig());}

	public StartMenuFrame(GraphicsConfiguration grConf) {
		super(grConf);		
		Out.Print(StartMenuFrame.class, 0, "Building the StartMenu...");		
		
		initialization();		
		
		setTitle("Меню игры:");
		try {setIconImage(ResourceManager.getBufferedImage("gameIcon", true, MainClass.getGraphicConfig()));} catch (Exception e1) {/* IGNORE */}
		setResizable(false);
		setIgnoreRepaint(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(colorBackground);
		
		canvas = new Canvas(grConf);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		
		add(canvas);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		createBackBuffer();
		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();
		
		int descret = MainClass.getGraphicDevice().getDisplayMode().getRefreshRate();
		Out.Print("\nnmRefresh rate current monitor is " + descret);
		
		FoxAudioProcessor.nextMusic();
		
		repTh = new Thread(new Runnable() {
			@Override
			public void run() {
				Out.Print(StartMenuFrame.class, 0, "Launch draw-thread...");
				while (repThRun) {
					if (bs == null || canvas == null) {return;}
					
					Graphics2D g2D = (Graphics2D) bs.getDrawGraphics();
					
					try {
						do {
				    		do {
				    			g2D = (Graphics2D) bs.getDrawGraphics();
								g2D.drawImage(background, 0, 0, FRAME_WIDTH - 16, FRAME_HEIGHT - 40, null);
								g2D.drawImage(startPress 	? sp[8] : startOver 		? sp[7] : sp[6], startButtonPaint.x, startButtonPaint.y, tmpInt0, tmpInt1 + 64, 0, 0, 512, 64, null);
								g2D.drawImage(optionsPress 	? sp[5] : optionsOver 	? sp[4] : sp[3], startButtonPaint.x, startButtonPaint.y + 84, tmpInt0, tmpInt1 + 148, 0, 0, 512, 64, null);
								g2D.drawImage(exitPress 		? sp[2] : exitOver 			? sp[1]	 : sp[0], startButtonPaint.x, startButtonPaint.y + 168, tmpInt0, tmpInt1 + 232, 0, 0, 512, 64, null);
				    		} while (bs.contentsRestored());
				    	} while (bs.contentsLost());
						
				    	bs.show();
					} catch (Exception e) {/* IGNORE */} finally {g2D.dispose();}
			    	
					Toolkit.getDefaultToolkit().sync();
					
					if (descret <= 60) {try {Thread.sleep(33);} catch (InterruptedException e) {}
					} else if (descret <= 72) {try {Thread.sleep(30);} catch (InterruptedException e) {}
					} else {try {Thread.sleep(24);} catch (InterruptedException e) {}}
				}
				
				Out.Print(StartMenuFrame.class, 0, "Draw-thread has stop correctly.");
			}
		});
		repTh.start();	
		
		Out.Print(StartMenuFrame.class, 0, "StartMenu was been builded.");
	}

	private void initialization() {
		Out.Print(StartMenuFrame.class, 0, "Initialization StartMenu...");
		
		try {sp = Registry.sprites.addSpritelist("MainMenuButtons", ResourceManager.getBufferedImage("MBSL", true, MainClass.getGraphicConfig()), 3, 3);
		} catch (Exception e) {e.printStackTrace();}
		
		startButtonPaint = new Point(FRAME_WIDTH / 4, FRAME_HEIGHT / 3);
		tmpInt0 = FRAME_WIDTH / 4 * 3;
		tmpInt1 = FRAME_HEIGHT / 3;

		startB 		= new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY(), FRAME_WIDTH / 2, 64);
		optionB 	= new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY() + 84, FRAME_WIDTH / 2, 64);
		exitB 		= new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY() + 168, FRAME_WIDTH / 2, 64);
		userChanger = new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY() - 120, FRAME_WIDTH / 2, 64);
		
		CenterPanel.setShowNextBlockEnabledFlag(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "nextFigureShow"));
		CenterPanel.setHardcoreEnabledFlag(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "hardcoreMode"));
		CenterPanel.setSpecialBlocksEnabledFlag(IOM.getBoolean(IOM.HEADERS.USER_SAVE, "specialBlocksEnabled"));
	}

	private void deInitialization() {
		Out.Print(StartMenuFrame.class, 1, "De-inititialization of the StartMenuFrame...");
		
		repTh.interrupt();
		sp = null;
		startButtonPaint = null;
		background = null;
		canvas = null;

		dispose();
		Out.Print(StartMenuFrame.class, 1, "De-inititialization accomlish. Lets GC...");
		System.gc();
	}
	
	private void createBackBuffer() {
		Out.Print(StartMenuFrame.class, 0, "Creating the BackBuffer...");
		
		RenderingHints d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
		d2DRender.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		
//		buffBackground = ResourceManager.getBufferedImage("mainBackground", true, MainClass.getGraphicConfig());
		Image imIc = new ImageIcon("./resourse/pictures/mainBackground").getImage();
		background = MainClass.getGraphicConfig().createCompatibleVolatileImage(imIc.getWidth(null), imIc.getHeight(null), 2);
		if (background.validate(MainClass.getGraphicConfig()) == VolatileImage.IMAGE_INCOMPATIBLE) {Out.Print("WARN: createBackBuffer: IMAGE_INCOMPATIBLE");}
		
		Graphics2D g2D = (Graphics2D) background.createGraphics();
		g2D.addRenderingHints(d2DRender);
		
		g2D.drawImage(imIc, 0, 0, canvas);

		g2D.setStroke(new BasicStroke(3.0f));
		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		
		g2D.setColor(Color.GRAY);
		g2D.fillRoundRect(startButtonPaint.x - 7, startButtonPaint.y - 6, 431, 277, 25, 25);
		
		g2D.setColor(Color.BLACK);
		g2D.drawRoundRect(startButtonPaint.x - 2, startButtonPaint.y, 420, 266, 25, 25);
		
		g2D.setFont(f0);
		fm = g2D.getFontMetrics();
		g2D.setColor(Color.WHITE);
		g2D.drawString("v" + Registry.verse, 20, 40);		
		g2D.drawString("Multiverse_39 @FoxGroup, 2020", getWidth() - 330, getHeight() - 20);
		
		
		g2D.setColor(Color.GRAY);
		g2D.fillRoundRect(startButtonPaint.x - 7, startButtonPaint.y / 3, 431, 90, 25, 25);
		
		g2D.setColor(Color.DARK_GRAY);
		g2D.drawRoundRect(startButtonPaint.x - 2, startButtonPaint.y / 3 + 10, 420, 75, 25, 25);
		
		g2D.setFont(f1);
		fm = g2D.getFontMetrics();
		g2D.setColor(Color.BLACK);
		String userName = "Игрок: " + IOM.getString(IOM.HEADERS.LAST_USER, "lastUser");
		g2D.drawString(userName, 
				getWidth() / 2 - fm.stringWidth(userName) / 2 - 4, 
				getHeight() / 6 + 2);
		g2D.drawString("(жми сюда, если это не ты)", 
				getWidth() / 2 - fm.stringWidth("(жми сюда, если это не ты)") / 2 - 2, 
				getHeight() / 6 + 32 + 4);
				
		g2D.setColor(Color.GREEN);
		g2D.drawString(userName, 
				getWidth() / 2 - fm.stringWidth(userName) / 2 - 2, getHeight() / 6);
		g2D.setColor(Color.WHITE);
		g2D.drawString("(жми сюда, если это не ты)", 
				getWidth() / 2 - fm.stringWidth("(жми сюда, если это не ты)") / 2, 
				getHeight() / 6 + 32);
	
		g2D.dispose();
		
		Out.Print(StartMenuFrame.class, 0, "BackBuffer was created succefull.");
	}
	
	private void changeUser() {
		String newUserName = JOptionPane.showInputDialog(
				this, "Как тебя зовут?", "Новый игрок:", 
				JOptionPane.QUESTION_MESSAGE);		
		if (newUserName == null || newUserName.equals("")) {return;
		} else {
			IOM.set(IOM.HEADERS.LAST_USER, "lastUser", newUserName);			
			deInitialization();			
			new StartMenuFrame(MainClass.getGraphicConfig());
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		cursor = e.getPoint();
		
		if (startB.contains(cursor)) {if (!startPress) {startPress = true; repaint();}}
		if (optionB.contains(cursor)) {if (!optionsPress) {optionsPress = true; repaint();}}
		if (exitB.contains(cursor)) {if (!exitPress) {exitPress = true; repaint();}}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		cursor = e.getPoint();
		
		if (startB.contains(cursor)) {
			deInitialization();
			new GameFrame();
		}
		
		if (optionB.contains(cursor)) {
			FoxAudioProcessor.playSound("clickSound", 2D);
			new OptionsDialog(this).setVisible(true);
		}
		
		if (exitB.contains(cursor)) {
			deInitialization();
			ExitClass.exit(0);
		}
		
		if (userChanger.contains(cursor)) {changeUser();}
		
		startPress = false;
		optionsPress = false;
		exitPress = false;
		
		repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		cursor = e.getPoint();
		
		if (startB.contains(cursor)) {if (!startOver) {startOver = true; repaint();}} else {if (startOver) {startOver = false; repaint();}}
		if (optionB.contains(cursor)) {if (!optionsOver) {optionsOver = true; repaint();}} else {if (optionsOver) {optionsOver = false; repaint();}}
		if (exitB.contains(cursor)) {if (!exitOver) {exitOver = true; repaint();}} else {if (exitOver) {exitOver = false; repaint();}}
	}
	
	public void mouseDragged(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}