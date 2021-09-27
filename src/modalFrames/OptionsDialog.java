package modalFrames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fox.FoxFontBuilder;
import fox.IOM;
import fox.Out;
import fox.ResManager;
import gui.game.GameFrame;
import images.FoxSpritesCombiner;
import media.FoxAudioProcessor;
import gui.game.CenterPanel;


@SuppressWarnings("serial")
public class OptionsDialog extends JDialog implements MouseListener, KeyListener, MouseMotionListener {
	private MouseMotionListener mmList;
	private MouseListener mList;
	private KeyListener kList;

	private Font f0 = FoxFontBuilder.setFoxFont(0, 24, true), f1 = FoxFontBuilder.setFoxFont(0, 16, false);
	private FontMetrics fm;
	private BufferedImage[] sp;
	private BufferedImage[] detailsBuffer = new BufferedImage[2];
	private RenderingHints d2DRender;
	private Boolean themeButPress = false, themeButOver = false;
	private Boolean controlsButPress = false, controlsButOver = false;
	private int horizontalStartX = 300, horizontalEndX = 350;
	
	
	public OptionsDialog(JFrame parent) {
		super(parent, true);
		
		Out.Print(OptionsDialog.class, 0, "Building the OptionsDialog...");
		
		try {sp = FoxSpritesCombiner.addSpritelist("uni", ResManager.getBImage("unibutton"), 3, 1);
		} catch (Exception e) {e.printStackTrace();}
		mmList = this;
		mList = this;
		kList = this;
		
		try {
			detailsBuffer[0] = ResManager.getBImage("switchOn");
			detailsBuffer[1] = ResManager.getBImage("switchOff");
		} catch (Exception e1) {e1.printStackTrace();}
		
		d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
		d2DRender.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
		d2DRender.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
//		d2DRender.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
//		d2DRender.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE));
		
		setTitle("Окно настроек:");
		try {setIconImage(ResManager.getBImage("gameIcon"));
		} catch (Exception e) {e.printStackTrace();}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
		setSize(new Dimension(390, 620));
		setLocationRelativeTo(null);
		setResizable(false);
		
		add(new JPanel(new BorderLayout()) {
			{
				setBackground(Color.DARK_GRAY);
				setIgnoreRepaint(true);
				setFocusable(true);
				addKeyListener(kList);
				addMouseListener(mList);
				addMouseMotionListener(mmList);
			}
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHints(d2DRender);
				
				fm = g2d.getFontMetrics(f0);
				
				g2d.setFont(f0);
				g2d.setColor(Color.BLACK);
				g2d.drawString("Настройки игры:", getSize().width / 2 - fm.stringWidth("Настройки игры:") / 2 - 2, 30 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString("Настройки игры:", getSize().width / 2 - fm.stringWidth("Настройки игры:") / 2, 30);
				
				g2d.setColor(Color.BLACK);
				g2d.drawRoundRect(10, 45, getSize().width - 20 - 1, getSize().height - 55 + 1, 10, 10);
				g2d.setColor(Color.GREEN);
				g2d.drawRoundRect(10, 45, getSize().width - 20, getSize().height - 55, 10, 10);
				
	
				g2d.setFont(f1);
				fm = g2d.getFontMetrics(f1);
				g2d.setColor(Color.BLACK);
				g2d.drawString("Включить звук:", 20 - 2, 80 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString("Включить звук:", 20, 80);

				try {g2d.drawImage(FoxAudioProcessor.getSoundEnabled() ? detailsBuffer[0] : detailsBuffer[1], 
						(int) (getSize().width * 0.75f), 60 - 2, 70, 40, null);
				} catch (Exception e) {e.printStackTrace();}
				
				
				g2d.setColor(Color.BLACK);
				g2d.drawString("Включить музыку:", 20 - 2, 155 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString("Включить музыку:", 20, 155);

				try {g2d.drawImage(FoxAudioProcessor.getMusicEnabled() ? detailsBuffer[0] : detailsBuffer[1], 
						(int) (getSize().width * 0.75f), 135 - 2, 
						70, 40, null);
				} catch (Exception e) {e.printStackTrace();}
				
				
				g2d.setColor(Color.BLACK);
				g2d.drawString("Следующая фигура:", 20 - 2, 230 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString("Следующая фигура:", 20, 230);

				try {g2d.drawImage(CenterPanel.isShowNextBlockEnabledFlag() ? detailsBuffer[0] : detailsBuffer[1], 
						(int) (getSize().width * 0.75f), 210 - 2, 
						70, 40, null);
				} catch (Exception e) {e.printStackTrace();}
				
				
				g2d.setColor(Color.BLACK);
				g2d.drawString("Спец. блоки:", 20 - 2, 305 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString("Спец. блоки:", 20, 305);
				
				try {g2d.drawImage(CenterPanel.isSpecialBlocksEnabled() ? detailsBuffer[0] : detailsBuffer[1], 
						(int) (getSize().width * 0.75f), 285 - 2, 
						70, 40, null);
				} catch (Exception e) {e.printStackTrace();}
				
					
				g2d.setColor(Color.BLACK);
				g2d.drawString("Хардкор:", 20 - 2, 380 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString("Хардкор:", 20, 380);
				
				try {g2d.drawImage(GameFrame.isHardcore() ? detailsBuffer[0] : detailsBuffer[1],
						(int) (getSize().width * 0.75f), 360 - 2, 
						70, 40, null);
				} catch (Exception e) {e.printStackTrace();}
				
				
				g2d.setColor(Color.BLACK);
				g2d.drawString(
						"Тема: '" + GameFrame.getTheme().name() + "' (" + (GameFrame.THEME.valueOf(GameFrame.getTheme().name()).ordinal() + 1) +
						"/" + GameFrame.THEME.values().length + ")", 	20 - 2, 455 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString(
						"Тема: '" + GameFrame.getTheme().name() + "' (" + (GameFrame.THEME.valueOf(GameFrame.getTheme().name()).ordinal() + 1) +
						"/" + GameFrame.THEME.values().length + ")", 20, 455);

				g2d.drawImage(themeButPress ? sp[2] : themeButOver ? sp[0] : sp[1], 
						(int) (getSize().getWidth() * 0.8f), 425, 
						50, 50, null);
				
				
				g2d.setColor(Color.BLACK);
				g2d.drawString(
						"Управление:", 
						20 - 2, 530 + 2);
				g2d.setColor(Color.GREEN);
				g2d.drawString(
						"Управление:", 
						20, 530);
				
				g2d.drawImage(controlsButPress ? sp[2] : controlsButOver ? sp[0] : sp[1], 
						(int) (getSize().getWidth() * 0.8f), 500, 
						50, 50, 
						null);
				
			g2d.dispose();
			}
		});

		GameFrame.setPaused(true);
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			mList = null;
			kList = null;
			dispose();
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {
		int tmpX = e.getPoint().x, tmpY = e.getPoint().y;
		
		if (tmpX > horizontalStartX && tmpX < horizontalEndX) {			
			if (tmpY > 55 && tmpY < 95) {
				if (FoxAudioProcessor.getSoundEnabled()) {
					Out.Print(OptionsDialog.class, 0, "Disable sounds...");
					FoxAudioProcessor.setSoundEnabled(false);
					IOM.set(IOM.HEADERS.USER_SAVE, "soundMute", "true");
				} else {
					Out.Print(OptionsDialog.class, 0, "Enable sounds...");
					FoxAudioProcessor.setSoundEnabled(true);
					IOM.set(IOM.HEADERS.USER_SAVE, "soundMute", "false");
				}
				repaint();
			} else if (tmpY > 135 && tmpY < 170) {
				if (FoxAudioProcessor.getMusicEnabled()) {
					Out.Print(OptionsDialog.class, 0, "Disable music...");
					IOM.set(IOM.HEADERS.USER_SAVE, "musicMute", "true");
					FoxAudioProcessor.setMusicEnabled(false);
					FoxAudioProcessor.pauseMusic();
				} else {
					Out.Print(OptionsDialog.class, 0, "Enable music...");
					IOM.set(IOM.HEADERS.USER_SAVE, "musicMute", "false");
					FoxAudioProcessor.setMusicEnabled(true);
					FoxAudioProcessor.resumeMusic();
					FoxAudioProcessor.nextMusic();
				}
				
				repaint();
			} else if (tmpY > 210 && tmpY < 240) {
				if (CenterPanel.isShowNextBlockEnabledFlag()) {
					Out.Print(OptionsDialog.class, 0, "Disable next figures...");
					CenterPanel.setShowNextBlockEnabledFlag(false);
				} else {
					Out.Print(OptionsDialog.class, 0, "Enable next figures...");
					CenterPanel.setShowNextBlockEnabledFlag(true);
				}
				
				repaint();
			} else if (tmpY > 285 && tmpY < 320) {
				if (CenterPanel.isSpecialBlocksEnabled()) {
					Out.Print(OptionsDialog.class, 0, "Enable special blocks...");
					CenterPanel.setSpecialBlocksEnabledFlag(false);
				} else {
					Out.Print(OptionsDialog.class, 0, "Disable special blocks...");
					CenterPanel.setSpecialBlocksEnabledFlag(true);
				}
				repaint();
			} else if (tmpY > 360 && tmpY < 390) {
				if (GameFrame.isHardcore()) {
					Out.Print(OptionsDialog.class, 0, "Enable hardcore...");
					GameFrame.setHardcore(false);
				} else {
					Out.Print(OptionsDialog.class, 0, "Disable hardcore...");
					GameFrame.setHardcore(true);
				}
				repaint();
			} else if (tmpY > 430 && tmpY < 470) {
				controlsButPress = false;
				if (!themeButPress) {themeButPress = true;}
				
				Out.Print(OptionsDialog.class, 0, "Changing theme...");
				try {GameFrame.setTheme(GameFrame.THEME.values()[GameFrame.getTheme().ordinal() + 1]);
				} catch (Exception e2) {GameFrame.setTheme(GameFrame.THEME.values()[0]);}

				repaint();
			} else if (tmpY > 500 && tmpY < 540) {
				themeButPress = false;
				if (!controlsButPress) {controlsButPress = true;}
				
				Out.Print(OptionsDialog.class, 0, "Changing controls...");
				
				FoxAudioProcessor.playSound("clickSound", 3D);				
				new ControlsDialog(this).setVisible(true);

				repaint();
			}

			repaint();
		} else {
			if (themeButPress) {themeButPress = false; repaint();}
			if (controlsButPress) {controlsButPress = false; repaint();}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (themeButPress) {themeButPress = false;}
		if (controlsButPress) {controlsButPress = false;}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int tmpX = e.getPoint().x, tmpY = e.getPoint().y;
		
		if (tmpX > horizontalStartX && tmpX < horizontalEndX) {
			if (tmpY > 420 && tmpY < 480) {
				if (!themeButOver) {themeButOver = true;}
			} else {themeButOver = false;}
			
			if (tmpY > 500 && tmpY < 540) {
				if (!controlsButOver) {controlsButOver = true;}
			} else {controlsButOver = false;}
			repaint();
		} else {
			if (themeButOver) {themeButOver = false; repaint();}
			if (controlsButOver) {controlsButOver = false; repaint();}
		}
	}

	public void mouseClicked(MouseEvent m) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseDragged(MouseEvent e) {}
}
