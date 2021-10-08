package modalFrames;

import fox.FoxFontBuilder;
import fox.IOM;
import fox.Out;
import fox.ResManager;
import gui.game.GameFrame;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ControlsDialog extends JDialog implements ActionListener  {
	private int DIALOG_WIDTH = 300, DIALOG_HEIGHT = 400;
	private Font f0 = FoxFontBuilder.setFoxFont(5, 18, true);
	private Font f1 = FoxFontBuilder.setFoxFont(5, 18, true);
	private static Dialog pak;
	
	
	public ControlsDialog(Dialog parent) {
		super(parent, true);

		Out.Print(ControlsDialog.class, 0, "Building the ControlsDialog...");
		
		setTitle("Окно управления:");
		try {setIconImage(ResManager.getBImage("gameIcon"));
		} catch (Exception e) {e.printStackTrace();}
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setModal(true);
		setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
		setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
		setLocationRelativeTo(null);
		setResizable(false);
		
		JPanel baseControlPane = new JPanel(new BorderLayout()) {
			{
				setBackground(Color.DARK_GRAY);
				setForeground(Color.WHITE);
				
				JPanel controlsGridPane = new JPanel(new GridLayout(8, 1)) {
					{
						setBorder(new EmptyBorder(0, 6, 0, 0));
						setBackground(Color.DARK_GRAY);
						setForeground(Color.WHITE);
						setFont(f0);
						
						add(controlLine("KEY_LEFT", 	"Влево:", IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_LEFT"), 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD")));
						add(controlLine("KEY_RIGHT", 	"Вправо:", IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_RIGHT"), IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD")));
						add(controlLine("KEY_DOWN", 	"Вниз:", 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_DOWN"),  IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD")));
						add(controlLine("KEY_STUCK", 	"Сброс:", IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_STUCK"), IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD")));
						
						add(controlLine("KEY_ROTATE", 		"Поворот:", 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_ROTATE"), 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD")));
						add(controlLine("KEY_PAUSE", 			"Пауза:", 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_PAUSE"), 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD")));
						add(controlLine("KEY_CONSOLE", 	"Консоль:", 	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE"), 		IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD")));
						add(controlLine("KEY_FULLSCREEN", "Экран:", 		IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN"),	IOM.getInt(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD")));
					}

					private Component controlLine(String name, String description, int key, int mod) {
						return new JPanel(new BorderLayout()) {
							{
								setBackground(Color.DARK_GRAY);
								setForeground(Color.WHITE);
								
								JTextField nameField = new JTextField() {
									{
										setBackground(Color.DARK_GRAY);
										setForeground(Color.WHITE);
										setBorder(null);
										setName(name);
										setFont(f0);
										setText(description);
										setEditable(false);
										setFocusable(false);
									}
								};
								
								JButton keyBut = new JButton() {
									{
										setBackground(Color.BLACK);
										setForeground(Color.WHITE);
										setFont(f1);
										setName(key + "_" + mod);
										setText("' " + KeyEvent.getKeyText(key) + " '" + (mod == 0 ? "" : " + '" + KeyEvent.getModifiersExText(mod) + "'"));
										setPreferredSize(new Dimension(DIALOG_WIDTH / 2, 0));
										
										addActionListener(ControlsDialog.this);
									}
								};
								
								add(nameField, BorderLayout.CENTER);
								add(keyBut, BorderLayout.EAST);
							}
						};
					}
				};
				
				JPanel buttonsDownPane = new JPanel(new BorderLayout()) {
					{
						setBackground(Color.DARK_GRAY);
						setForeground(Color.WHITE);						
						setBorder(new EmptyBorder(3, 3, 6, 3));
						
						add(new JButton("Принять") {
							{
								setBackground(new Color(0.75f, 1.0f, 0.75f));
								setPreferredSize(new Dimension(150, 30));
								addActionListener(new ActionListener() {									
									@Override
									public void actionPerformed(ActionEvent e) {dispose();}
								});
							}
						}, BorderLayout.WEST);
						
						add(new JButton("Сброс") {
							{
								setBackground(new Color(1.0f, 0.75f, 0.75f));
								setPreferredSize(new Dimension(100, 30));
								addActionListener(new ActionListener() {									
									@Override
									public void actionPerformed(ActionEvent e) {resetControls();}
								});
							}
						}, BorderLayout.EAST);
					}
				};
				
				add(controlsGridPane, BorderLayout.CENTER);
				add(buttonsDownPane, BorderLayout.SOUTH);
			}
		};
		
		add(baseControlPane);
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		pak = new Dialog(this, null, true) {
			final int DIALOG_W = 300, DIALOG_H = 100;
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2D = (Graphics2D) g;
				
				g2D.setStroke(new BasicStroke(3));
				g2D.setColor(Color.GRAY);
				g2D.drawRoundRect(5, 5, DIALOG_W - 10, DIALOG_H - 10, 5, 5);
				
				g2D.dispose();
			}
			
			{
				setSize(DIALOG_W, DIALOG_H);
				setUndecorated(true);
				setLocationRelativeTo(null);
				setBackground(Color.DARK_GRAY);
				
				add(new JLabel("Нажми нужную клавишу...") {
					{
						setFont(f0);
						setForeground(Color.WHITE);
						setHorizontalAlignment(CENTER);
					}
				});
			}
		};
		pak.addKeyListener(new KeyAdapterHandle((JButton) e.getSource()));
		pak.setVisible(true);
	}

	private void resetControls() {
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_LEFT", KeyEvent.VK_LEFT);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_LEFT_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_RIGHT", KeyEvent.VK_RIGHT);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_RIGHT_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_DOWN", KeyEvent.VK_DOWN);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_DOWN_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_STUCK", KeyEvent.VK_UP);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_STUCK_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_ROTATE", KeyEvent.VK_Z);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_ROTATE_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_PAUSE", KeyEvent.VK_ESCAPE);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_PAUSE_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE", KeyEvent.VK_BACK_QUOTE);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_CONSOLE_MOD", 0);
		
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN", KeyEvent.VK_F);
		IOM.set(IOM.HEADERS.USER_SAVE, "KEY_FULLSCREEN_MOD", 0);
		
		IOM.saveAll();
		dispose();
	}
	
	public static class KeyAdapterHandle extends KeyAdapter {
		private JPanel parentPane;
		private JButton tmpBut;
		private String paramName;
		
		public KeyAdapterHandle(JButton _tmpBut) {tmpBut = _tmpBut;}
	
		@Override
		public void keyReleased(KeyEvent e) {
			parentPane = (JPanel) tmpBut.getParent();
			paramName = null;
			
			for (Component comp : parentPane.getComponents()) {
				if (comp instanceof JTextField) {
					paramName = parentPane.getComponent(0).getName();
					break;
				}
			}
			
			IOM.set(IOM.HEADERS.USER_SAVE, paramName + "_MOD", e.getModifiersEx());
			IOM.set(IOM.HEADERS.USER_SAVE, paramName, e.getKeyCode());			
			
			tmpBut.setText("' " + KeyEvent.getKeyText(e.getKeyCode()) + (KeyEvent.getModifiersExText(e.getModifiersEx()).equals("") ? " '" : " ' + '" + KeyEvent.getModifiersExText(e.getModifiersEx()) + "'"));

			IOM.saveAll();
			pak.dispose();
			
			if (GameFrame.isGameActive()) {
                GameFrame.reloadControls();}
		}
	}
}
