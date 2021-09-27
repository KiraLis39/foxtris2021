package modalFrames;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JFrame;
import door.MainClass;
import fox.ResManager;
import images.FoxSpritesCombiner;
import media.FoxAudioProcessor;
import registry.Registry;


@SuppressWarnings("serial")
public class AboutDialog extends JDialog implements KeyListener {
	private KeyListener kList;
	
	private Canvas canvas;
	private FontMetrics fm;
	private RenderingHints d2DRender;
	private BufferStrategy bs;

	private BufferedImage[] sp;
	private BufferedImage backSkyBuffer, middleStarsBuffer, bAbout, grayBaseImage;
	
	private Color color0 = new Color(0.2f, 0.2f, 0.2f, 0.9f);
	private Color color1 = new Color(0.35f, 0.35f, 0.35f, 1);
	private Color color2 = new Color(1, 0.75f, 1, 0.95f);
	private Color color3 = new Color(0.5f, 1, 1, 0.95f);
	
	private Double rotator = 0.0D;
	private float landRound = 0.0f, figuresRound = 0.0f, shiftFloat0 = 0f, shiftFloat1 = 0f;
	private int randFig0 = 0, randFig1 = 0, integer0, integer1;
	private long repaintTime = 17;
	
	String tmp0 = "Информация об игре:";
	String tmp1 = "Руководитель: KiraLis39";
	String tmp2 = "Программист: KiraLis39";
	String tmp3 = "Графика: KiraLis39 и интернет";
	String tmp4 = "Музыка: KiraLis39 и KeyGen";
	String tmp5 = "Тестирование: @FoxGroup";
	String tmp6 = "2019 год";
	String tmp7 = "Multiverse_39 Corp.";
	String tmp9 = "Alt + F4 - меню выхода;";
	String tmp13 = "F1 - справка Об игре.";
	
	String tmp139 = "Предложения и жалобы: AngelicaLis39@mail.ru";
	
	
	public AboutDialog(JFrame parent) throws Exception {
		super(parent, true);
		
		kList = this;
		init();
		
		setTitle("Об игре:");
		setIconImage(ResManager.getBImage("gameIcon"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
		setLocationRelativeTo(null);
		setSize(new Dimension(600, 600));
		setLocationRelativeTo(null);
		setResizable(false);

		randFig0 = getRandomFigure();
		randFig1 = getRandomFigure();
		shiftFloat0 = new Random().nextInt(8) + 4;
		shiftFloat1 = new Random().nextInt(8) + 4;
		
		canvas = new Canvas(MainClass.getGraphicConfig());
		canvas.addKeyListener(kList);
		
		add(canvas);
		
		addKeyListener(kList);
		
		Thread backAniThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!AboutDialog.this.isVisible()) {Thread.yield();}
				if (bs == null) {
					try {canvas.createBufferStrategy(3);} catch (Exception e) {canvas.createBufferStrategy(2);}
					try {bs = canvas.getBufferStrategy();
					} catch (Exception e) {e.printStackTrace();}
				}				
				
				while (AboutDialog.this.isVisible()) {
					if (canvas != null) {
						integer0 = (int) (-180f + figuresRound + shiftFloat0 * 7);
						integer1 = (int) (-180f + figuresRound + shiftFloat1 * 8);
						
						Graphics2D g2D = (Graphics2D) canvas.getGraphics();
						redrawCanvas(g2D);						
						g2D.dispose();
						
						try {Thread.sleep(repaintTime);} catch (Exception e) {}
					} else {deInit();}
				}
				FoxAudioProcessor.nextMusic();
			}

			private void redrawCanvas(Graphics2D g2D) {
				do {
		    		do {
		    			g2D = (Graphics2D) bs.getDrawGraphics();
		    			g2D.setFont(Registry.aboutDialogFont);
		    			fm = g2D.getFontMetrics();
		    			g2D.addRenderingHints(d2DRender);
		    			
		    			drawSky(g2D);
						
		    			drawFallingFigures(g2D);
						
		    			drawContentInfo(g2D);
												
						if (landRound < 595f) {landRound += 1.2f;} else {landRound = 0f;}
						if (figuresRound + 1.75f < 690f) {figuresRound += 1.75f;} else {
							figuresRound = 20f;
							randFig0 = getRandomFigure();
							randFig1 = getRandomFigure();
							shiftFloat0 = new Random().nextInt(16) + 4;
							shiftFloat1 = new Random().nextInt(16) + 4;
						}
		    		} while (bs.contentsRestored());
		    	} while (bs.contentsLost());
				
		    	bs.show();
			}

			private void drawSky(Graphics2D g2D) {
				g2D.drawImage(backSkyBuffer, 0, 0, backSkyBuffer.getWidth() - 15, backSkyBuffer.getHeight(), null);
				
//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.65f));
				g2D.drawImage(middleStarsBuffer, 
						0, 0, 	600 - 15, 600, 
						0, (int) (0f + landRound), 
						middleStarsBuffer.getWidth(), (int) (600f + landRound), null);
//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
			}
			
			private void drawFallingFigures(Graphics2D g2D) {
				rotator += 0.005D;
				
				int x = ((int) (5f + shiftFloat0) + sp[randFig0].getWidth() / 2);
				int y = (integer0 + sp[randFig0].getHeight() / 2);
				
				g2D.translate(x, y);
				g2D.rotate(-rotator);
				g2D.drawImage(sp[randFig0], -(sp[randFig0].getWidth() / 2), -(sp[randFig0].getHeight() / 2), null);
				g2D.rotate(rotator);
				g2D.translate(-x, -y);
				
				int x2 = ((int) (475f + shiftFloat1) + sp[randFig1].getWidth() / 2);
				int y2 = (integer1 + sp[randFig1].getHeight() / 2);
				
				g2D.translate(x2, y2);
				g2D.rotate(rotator);
				g2D.drawImage(sp[randFig1], -(sp[randFig1].getWidth() / 2), -(sp[randFig1].getHeight() / 2), null);
				g2D.rotate(-rotator);
				g2D.translate(-x2, -y2);
			}
		
			private void drawContentInfo(Graphics2D g2D) {
				g2D.drawImage(grayBaseImage, 100, 0, 390, 560, null);
				
				g2D.setColor(color2);
				g2D.drawString(tmp0, 300 - fm.stringWidth(tmp0) / 2, 45);
				g2D.drawString("_____________________", 300 - fm.stringWidth("_____________________") / 2 - 5, 65);

				g2D.setColor(color3);
				g2D.setFont(Registry.aboutDialogFontB);
				fm = g2D.getFontMetrics();
				
				g2D.drawString(tmp1, 300 - fm.stringWidth(tmp1) / 2, 100);
				g2D.drawString(tmp2, 300 - fm.stringWidth(tmp2) / 2, 130);
				g2D.drawString(tmp3, 300 - fm.stringWidth(tmp3) / 2, 160);
				g2D.drawString(tmp4, 300 - fm.stringWidth(tmp4) / 2, 190);
				g2D.drawString(tmp5, 300 - fm.stringWidth(tmp5) / 2, 220);
				
				g2D.drawString(tmp6, 300, 260);
				g2D.drawString(tmp7, 300, 280);
				
				g2D.setColor(color2);
				g2D.setFont(Registry.aboutDialogFont);
				fm = g2D.getFontMetrics();
				
				
				g2D.drawString("_____________________", 300 - fm.stringWidth("_____________________") / 2 - 5, 460);
				
				g2D.setColor(color3);
				g2D.setFont(Registry.aboutDialogFontB);
				fm = g2D.getFontMetrics();
				
				g2D.drawString(tmp9, 300 - fm.stringWidth(tmp9) / 2, 480);
				g2D.drawString(tmp13, 300 - fm.stringWidth(tmp13) / 2, 500);
				
				g2D.setColor(color1);						
				g2D.drawString(tmp139, 300 - fm.stringWidth(tmp139) / 2 - 4, 540);
			}
		});
		backAniThread.start();
		
//		Media.nextMusicPlay();
	}

	private int getRandomFigure() {return new Random().nextInt(4);}

	private void init() throws Exception {
		bAbout = ResManager.getBImage("bAbout");
		backSkyBuffer = ResManager.getBImage("backAbout");
		middleStarsBuffer = ResManager.getBImage("starsAbout");
		
		if (grayBaseImage == null) {
			grayBaseImage = new BufferedImage(450, 590, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2D = (Graphics2D) grayBaseImage.createGraphics();
			
			g2D.setColor(color0);
			g2D.fillRect(0, 0, grayBaseImage.getTileWidth(), grayBaseImage.getHeight());
			
			g2D.setColor(color1);
			g2D.drawRoundRect(9, 9, grayBaseImage.getTileWidth() - 18, grayBaseImage.getHeight() - 18, 10, 10);
			
			g2D.dispose();
		}
		
		d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));

		sp = FoxSpritesCombiner.addSpritelist("bAbout", bAbout, 2, 2);
	}
	
	private void deInit() {
		sp = null;
		fm = null;
		canvas = null;
		d2DRender = null;
		backSkyBuffer = null;
		middleStarsBuffer = null;
		
		System.gc();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			kList = null;
			deInit();
			
			dispose();
		}
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
}
