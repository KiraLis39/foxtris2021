package menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import gui.game.GameFrame;
import modalFrames.AboutDialog;

public class FrameMenuBar extends JMenuBar {

	public FrameMenuBar() {
		JMenu subMenu0 = new JMenu("Общее") {
			{
				setMnemonic(KeyEvent.VK_1);
				
				JMenuItem item0 = new JMenuItem("Выход из игры");
				item0.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
				item0.addActionListener(e -> GameFrame.exitConfirm());
				
				add(item0);
			}
		};
		
		JMenu subMenu1 = new JMenu("Дополнительно") {
			{
				setMnemonic(KeyEvent.VK_2);
				
				JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Фоновое изображение") {
					{
						setSelected(true);
						setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
						addChangeListener(e -> {
							if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {GameFrame.setUseBackImage(true);}
						});
					}
				};
				
				JRadioButtonMenuItem rbMenuItem2 = new JRadioButtonMenuItem("Стиль по умолчанию") {
					{
						setSelected(false);
						setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
						addChangeListener(e -> {
							if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {GameFrame.setUseBackImage(false);}
						});
					}
				};
				
				new ButtonGroup() {
					{
//						setDefaultLookAndFeelDecorated(true);
						add(rbMenuItem);
						add(rbMenuItem2);
					}
				};
			
				add(rbMenuItem);
				add(rbMenuItem2);
				addSeparator();
				
				JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Менять мелодию при смене уровня") {
					{
						setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
						addChangeListener(e -> {
							if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {GameFrame.setAutoMelodyChange(true);
							} else {GameFrame.setAutoMelodyChange(false);}
						});
					}
				};
				
				add(cbMenuItem);
				
				cbMenuItem = new JCheckBoxMenuItem("Режим новичка (лёгкий)") {
					{
						setMnemonic(KeyEvent.VK_H);
						addChangeListener(e -> {
							if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {GameFrame.setLightcore(true);
							} else {GameFrame.setLightcore(false);}
						});
					}
				};
				
				add(cbMenuItem);
			}
		};		
		
		JMenu subMenu2 = new JMenu("Справка") {
			{
				setMnemonic(KeyEvent.VK_3);
				
				JMenuItem item2 = new JMenuItem("Об игре", null) {
					{
						setAccelerator(KeyStroke.getKeyStroke("F1"));
						getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
						addActionListener(e -> {
							AboutDialog aDia;
							try {
								aDia = new AboutDialog(GameFrame.getFrame());
								aDia.setVisible(true);
							} catch (Exception e1) {	e1.printStackTrace();}

							GameFrame.setPaused(false);
						});
					}
				};
			
				JMenuItem item3 = new JMenuItem("Обновления", null) {
					{
						setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.ALT_MASK));
						getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
						addActionListener(e -> {

						});
					}
				};
				
				JMenuItem item4 = new JMenuItem("Обратная связь", null) {
					{
						setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
						getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
						addActionListener(e -> {

						});
					}
				};
				
				add(item2);
				add(item3);
				add(item4);
			}
		};
	
		add(subMenu0);
		add(subMenu1);
		add(subMenu2);
	}	
}
