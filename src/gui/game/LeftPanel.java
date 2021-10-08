package gui.game;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JPanel;
import door.MainClass;
import fox.IOM;
import fox.Out;
import fox.ResManager;
import images.FoxSpritesCombiner;
import media.FoxAudioProcessor;
import modalFrames.AboutDialog;
import registry.Registry;
import subComponents.OptionsDialog;


public class LeftPanel extends JPanel implements MouseListener {
	private BufferedImage hardcoreBufferIco, hardcoreBufferIco_off, specialBufferIco, specialBufferIco_off, nextBufferIco;
	private BufferedImage nextBufferIco_off, lightModeIco, lightModeIco_off, autoMusicChangeIco, autoMusicChangeIco_off;
	private BufferedImage buttonBufferIm, buttonOverBufferIm, buttonPressBufferIm, leftGrayBase, stage;
	private BufferedImage[] sp;
	
	private Boolean exitButOver = false, exitButPress = false, musButOver = false, musButPress = false;
	private Boolean optButOver = false, optButPress = false, aboButOver = false, aboButPress = false;
	
	public static int nextbrickDim;
	
	private static JPanel leftNFigurePane, leftInformPane, leftButtonsPane;
	private JButton exitButton, autorsButton, optionsButton, musicButton;
	private String exitButtonText = "Выход", optionsButtonText = "Настройки", musicButtonText = "Музыка >>", autorsButtonText = "Об игре";
	
	private static Color panelColor = new Color(0.3f, 0.3f, 0.3f, 0.85f);
	
	
	@Override
	 public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		revalidate();
		repaint();
		
		leftNFigurePane.setPreferredSize(new Dimension(nextbrickDim * 4, nextbrickDim * 4));
		leftButtonsPane.setPreferredSize(new Dimension(0, (int) (GameFrame.getGameFrameSize().getHeight() / 6f)));
		
		prepareBaseImageBuffers();
	}

	public LeftPanel() {
		setLayout(new BorderLayout());
		setIgnoreRepaint(true);
		setOpaque(false);
		
		initializeLeftPanel();
		
		leftNFigurePane = new JPanel(new BorderLayout()) {
			{
				setIgnoreRepaint(true);
				
				add(new JPanel(new BorderLayout()) {
					{setIgnoreRepaint(true);}
					
					@Override
					public void paintComponent(Graphics g) {
						Graphics2D g2D = (Graphics2D) g;
						drawGrayBack(this, g2D);
															
						// draw left next figure:
						g2D = drawLeftNextFigureArea(g2D);
						
						g2D.dispose();
					}

					private Graphics2D drawLeftNextFigureArea(Graphics2D g2D) {
						int toRightShift, toDownShift;
						int modX = (int) (getWidth() / 2 - nextbrickDim * 1.5f), modY = (int) (getHeight() / 2 - (nextbrickDim * 1.5f));
						
						for (int nextCellsDrawX = 0; nextCellsDrawX < 3; nextCellsDrawX++) {
							for (int nextCellsDrawY = 0; nextCellsDrawY < 3; nextCellsDrawY++) {
								toRightShift = (nextbrickDim * nextCellsDrawX) + modX;
								toDownShift = (nextbrickDim * nextCellsDrawY) + modY;
								
								g2D.setColor(Color.GRAY);
								g2D.drawRect(toRightShift, toDownShift, nextbrickDim, nextbrickDim);
								
								g2D.setColor(Color.BLACK);
								g2D.fillRect(toRightShift + 1, toDownShift + 1, nextbrickDim - 2, nextbrickDim - 2);
								
								if(CenterPanel.isShowNextBlockEnabledFlag() && CenterPanel.nextFigureFuture != null) {
									if (CenterPanel.nextFigureFuture[nextCellsDrawY][nextCellsDrawX] != 0) {
										try {
											g2D.drawImage(
												CenterPanel.getBrickByIndex(CenterPanel.nextFigureFuture[nextCellsDrawY][nextCellsDrawX]), 
												toRightShift, toDownShift, 
												nextbrickDim, nextbrickDim, 
												null
											);
										} catch (Exception e) {e.printStackTrace();}
									}
								} else {
									try {g2D.drawImage(CenterPanel.getBrickByIndex(0), toRightShift, toDownShift, nextbrickDim, nextbrickDim, null);
									} catch (Exception e) {e.printStackTrace();}
								}
							}
						}
						return g2D;
					};
				});
			}
		};
		
		leftInformPane = new JPanel(new BorderLayout()) {
			{setIgnoreRepaint(true);}
			
			@Override
			public void paintComponent(Graphics g) {
				int numberSize = 78;
				int modX = (int) (getWidth() / 2f - (numberSize / 2f * GameFrame.fontIncreaseMod)), 
				modY = (int) (numberSize * GameFrame.fontIncreaseMod / 1.5f);
				
				Graphics2D g2D = (Graphics2D) g;
				render(g2D);
				drawGrayBack(this, g2D);
				
				g2D.drawImage(stage, 
						(int) (getWidth() / 2f - 32f), (int) (18f * GameFrame.fontIncreaseMod), 
						64, 27, 
						null);
				
				g2D.drawImage(sp[GameFrame.stageCounter + 1], 
						modX, modY, 
						(int) (numberSize * GameFrame.fontIncreaseMod), (int) (numberSize * GameFrame.fontIncreaseMod), 
						null);
				
				drawLeftIndicatorPictures(g2D);
				
				g2D.dispose();
			}

			private void drawLeftIndicatorPictures(Graphics2D g2D) {
				float picSize = getWidth() / 6f, 
						spacing = picSize / 7f,
						middleLine = getWidth() / 2f - picSize / 2f;
				
				g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
				g2D.fillRoundRect(
						10, (int) (getHeight() * 0.78f), 
						getWidth() - 20, (int) (picSize + picSize / 3f), 
						27, 27);
				g2D.setColor(Color.DARK_GRAY);
				g2D.drawRoundRect(
						10, (int) (getHeight() * 0.78f), 
						getWidth() - 20, (int) (picSize + picSize / 3f), 
						27, 27);

				
				g2D.drawImage(CenterPanel.isSpecialBlocksEnabled() ? specialBufferIco : specialBufferIco_off,	
						(int) (picSize / 2f - spacing * 2f), (int) (getHeight() * 0.8f), 
						(int) (picSize), (int) (picSize), null);
				
				g2D.drawImage(CenterPanel.isShowNextBlockEnabledFlag() ? nextBufferIco : nextBufferIco_off, 
						(int) (middleLine - picSize - spacing), (int) (getHeight() * 0.8f), 
						(int) (picSize), (int) (picSize), null);
				
				//
				g2D.drawImage(GameFrame.isHardcore() ? hardcoreBufferIco : hardcoreBufferIco_off, (int) (middleLine), (int) (getHeight() * 0.8f), (int) (picSize), (int) (picSize), null);
				//
				
				g2D.drawImage(GameFrame.isLightcore() ? lightModeIco : lightModeIco_off, 
						(int) (middleLine + picSize + spacing), (int) (getHeight() * 0.8f), 
						(int) (picSize), (int) (picSize), null);
				
				g2D.drawImage(GameFrame.isAutoMelodyChange() ? autoMusicChangeIco : autoMusicChangeIco_off, 
						(int) ((getWidth() - picSize) - picSize / 2f + spacing * 2f), (int) (getHeight() * 0.8f), 
						(int) (picSize), (int) (picSize), null);
			}
		};
		
		leftButtonsPane = new JPanel(new GridLayout(4,0,2,2)) {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2D = (Graphics2D) g;
				drawGrayBack(this, g2D);
				
				drawButton(g2D, musicButton, musButOver, musButPress, musicButtonText, 7, 9);
				drawButton(g2D, optionsButton, optButOver, optButPress, optionsButtonText, 7, 7 + musicButton.getHeight());
				drawButton(g2D, autorsButton, aboButOver, aboButPress, autorsButtonText, 7, 5 + optionsButton.getHeight() * 2);
				drawButton(g2D, exitButton, exitButOver, exitButPress, exitButtonText, 7, 3 + autorsButton.getHeight() * 3);
				
				g2D.dispose();
			}
			
			{
				setOpaque(false);
				setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
				setIgnoreRepaint(true);
												
				musicButton = new JButton() {
					{
						setName("mus");
						setOpaque(false);
						setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
						setToolTipText("Следующая мелодия");
						setFocusPainted(false);
						setFocusable(false);
						addMouseListener(LeftPanel.this);
						addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								FoxAudioProcessor.playSound("clickSound", 1D);
								FoxAudioProcessor.nextMusic();
							}
						});
					}
				};
									
				optionsButton = new JButton() {
					{
						setName("opt");
						setOpaque(false);
						setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
						setToolTipText("Опции и создатели игры");
						setFocusPainted(false);
						setFocusable(false);
						addMouseListener(LeftPanel.this);
						addActionListener(e -> {
							FoxAudioProcessor.playSound("clickSound", 3D);

							new OptionsDialog(GameFrame.getFrame()).setVisible(true);

							Out.Print("Out of pause...");
							GameFrame.setPaused(false);
							IOM.saveAll();
						});
					}
				};

				autorsButton = new JButton() {
					{
						setName("aut");
						setOpaque(false);
						setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
						setToolTipText("Информация об игре");
						setFocusPainted(false);
						setFocusable(false);
						addMouseListener(LeftPanel.this);
						addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								FoxAudioProcessor.playSound("clickSound", 3D);
								GameFrame.setPaused(true);
								
								try {new AboutDialog(GameFrame.getFrame()).setVisible(true);
								} catch (Exception e1) {e1.printStackTrace();}

								Out.Print("Out of pause...");
								GameFrame.setPaused(false);
							}
						});
					}
				};
				
				exitButton = new JButton() {
					{
						setName("exi");
						setOpaque(false);
						setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
						setToolTipText("<html>Выход в меню. <br>Альтернатива клавише Esc</html>");
						setFocusPainted(false);
						setFocusable(false);
						addMouseListener(LeftPanel.this);
						addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {GameFrame.exitConfirm();}
						});
					}
				};
				
				add(musicButton);
				add(optionsButton);
				add(autorsButton);
				add(exitButton);
			}
		};
								
		add(leftNFigurePane, BorderLayout.NORTH);
		add(leftInformPane, BorderLayout.CENTER);
		add(leftButtonsPane, BorderLayout.SOUTH);
	}

	private void render(Graphics2D g2D) {
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//		g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		g2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
	}
	
	private void initializeLeftPanel() {
		try {sp = FoxSpritesCombiner.addSpritelist("numbers", ResManager.getBImage("numbers", true, MainClass.getGraphicConfig()), 10, 1);
		} catch (Exception e) {e.printStackTrace();}
		
		grayRectangleReDraw();
		prepareBaseImageBuffers();
	}

	private void prepareBaseImageBuffers() {
		try {
			stage					= ResManager.getBImage("stageLabel", true, MainClass.getGraphicConfig());
			buttonBufferIm 			= ResManager.getBImage("buttonProto", true, MainClass.getGraphicConfig());
			buttonOverBufferIm 		= ResManager.getBImage("buttonProtoOver", true, MainClass.getGraphicConfig());
			buttonPressBufferIm		= ResManager.getBImage("buttonProtoPress", true, MainClass.getGraphicConfig());
			
			hardcoreBufferIco 		= ResManager.getBImage("hardcore", true, MainClass.getGraphicConfig());
			hardcoreBufferIco_off 	= ResManager.getBImage("hardcore_off", true, MainClass.getGraphicConfig());
			
			specialBufferIco 		= ResManager.getBImage("spec", true, MainClass.getGraphicConfig());
			specialBufferIco_off 	= ResManager.getBImage("spec_off", true, MainClass.getGraphicConfig());
			
			nextBufferIco 			= ResManager.getBImage("tips", true, MainClass.getGraphicConfig());
			nextBufferIco_off 		= ResManager.getBImage("tips_off", true, MainClass.getGraphicConfig());
			
			lightModeIco 			= ResManager.getBImage("lightcore", true, MainClass.getGraphicConfig());
			lightModeIco_off 		= ResManager.getBImage("lightcore_off", true, MainClass.getGraphicConfig());
			
			autoMusicChangeIco 		= ResManager.getBImage("autoMusic", true, MainClass.getGraphicConfig());
			autoMusicChangeIco_off 	= ResManager.getBImage("autoMusic_off", true, MainClass.getGraphicConfig());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private void grayRectangleReDraw() {
		leftGrayBase = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g2D = leftGrayBase.createGraphics();
		g2D.setRenderingHints(GameFrame.getRender());
		
		g2D.setColor(panelColor);
		g2D.fillRoundRect(0, 0, 600, 600, 20, 20);
		
		g2D.dispose();
	}

	private void drawGrayBack(JPanel panel, Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(2));
		g2D.setRenderingHints(GameFrame.getRender());
		
		g2D.drawImage(leftGrayBase, 
				3, 3, 
				panel.getWidth() - 6, panel.getHeight() - 6, 
				null);
		
		g2D.drawRoundRect(6, 6, panel.getWidth() - 12, panel.getHeight() - 12, 10, 10);
	}
	
	private void drawButton(Graphics2D g2D, JButton button, Boolean over, Boolean press, String buttonText, int x, int y) {
		g2D.setRenderingHints(GameFrame.getRender());
		
		try {
			Rectangle2D textBounds;

			g2D.setFont(Registry.simpleFont);
			textBounds = g2D.getFontMetrics().getStringBounds(buttonText, g2D);
		
			if (press) {
				g2D.drawImage(buttonPressBufferIm, x + 2, y, button.getWidth() - x * 2 - 2, button.getHeight() - 3, this);	
				g2D.setColor(Color.DARK_GRAY);
				g2D.drawString(buttonText, 
						(int) (button.getWidth() / 2 - textBounds.getWidth() / 2 - 3), 
						button.getHeight() / 2 + 1 + y);
				
				g2D.setColor(Color.WHITE);			
				g2D.drawString(buttonText, 
						(int) (button.getWidth() / 2 - textBounds.getWidth() / 2), 
						button.getHeight() / 2 + 4 + y);
			} else {
				BufferedImage bdi = buttonBufferIm;
				if (over) {
					bdi = buttonOverBufferIm;
				}

				g2D.drawImage(bdi, x + 2, y, button.getWidth() - x * 2 - 2, button.getHeight() - 3, null);
				g2D.setColor(Color.DARK_GRAY);
				g2D.drawString(buttonText,
						(int) (button.getWidth() / 2 - textBounds.getWidth() / 2 - 2),
						button.getHeight() / 2 + 2 + y);

				g2D.setColor(Color.WHITE);
				g2D.drawString(
						buttonText,
						(int) (button.getWidth() / 2 - textBounds.getWidth() / 2),
						button.getHeight() / 2 + 4 + y
				);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	
	// Listeners:
		@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getComponent().getName()) {
		case "mus": musButPress = true;
			break;
		case "opt": optButPress = true;
			break;
		case "aut": aboButPress = true;
			break;
		case "exi": exitButPress = true;
			break;
		default: System.err.println("e.getComponent().getName() has returned uncknown value: " + e.getComponent().getName());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getComponent().getName()) {
		case "mus": musButPress = false;
			break;
		case "opt": optButPress = false;
			break;
		case "aut": aboButPress = false;
			break;
		case "exi": exitButPress = false;
			break;
		default:  System.err.println("e.getComponent().getName() has returned uncknown value: " + e.getComponent().getName());
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		switch (e.getComponent().getName()) {
		case "mus": musButOver = true;
			break;
		case "opt": optButOver = true;
			break;
		case "aut": aboButOver = true;
			break;
		case "exi": exitButOver = true;
			break;
		default:  System.err.println("e.getComponent().getName() has returned uncknown value: " + e.getComponent().getName());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		switch (e.getComponent().getName()) {
		case "mus": musButOver = false;
			break;
		case "opt": optButOver = false;
			break;
		case "aut": aboButOver = false;
			break;
		case "exi": exitButOver = false;
			break;
		default:  System.err.println("e.getComponent().getName() has returned uncknown value: " + e.getComponent().getName());
		}
	}
	public void mouseClicked(MouseEvent e) {}

	public void setNextBrickDim(int nbd) {
		nextbrickDim = nbd;
		leftNFigurePane.repaint();
	}
}
