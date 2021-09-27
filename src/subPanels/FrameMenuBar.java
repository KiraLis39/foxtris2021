package subPanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.GameFrame;
import modalFrames.AboutDialog;


@SuppressWarnings("serial")
public class FrameMenuBar extends JMenuBar {

	
	public FrameMenuBar() {

		JMenu subMenu0 = new JMenu("Общее") {
			{
				setMnemonic(KeyEvent.VK_1);
				
				JMenuItem item0 = new JMenuItem("Выход из игры");
				item0.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
				item0.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {GameFrame.exitConfirm();}
				});
				
				add(item0);
			}
		};
		
		JMenu subMenu1 = new JMenu("Дополнительно") {
			{
				setMnemonic(KeyEvent.VK_2);
				
				JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Фоновое изображение");
				rbMenuItem.setSelected(true);
				rbMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
				rbMenuItem.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {GameFrame.setUseBackImage(true);}
					}
				});
				
				JRadioButtonMenuItem rbMenuItem2 = new JRadioButtonMenuItem("Стиль по умолчанию");
				rbMenuItem2.setSelected(false);
				rbMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
				rbMenuItem2.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {GameFrame.setUseBackImage(false);}
					}
				});
				
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
				
				JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Менять мелодию при смене уровня");
				cbMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
				cbMenuItem.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {GameFrame.setAutoMelodyChange(true);
						} else {GameFrame.setAutoMelodyChange(false);}
					}
				});
				
				add(cbMenuItem);
				
				cbMenuItem = new JCheckBoxMenuItem("Режим новичка (лёгкий)");
				cbMenuItem.setMnemonic(KeyEvent.VK_H);
				cbMenuItem.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {GameFrame.setLightcore(true);
						} else {GameFrame.setLightcore(false);}
					}
				});
				
				add(cbMenuItem);
			}
		};		
		
		JMenu subMenu2 = new JMenu("Справка") {
			{
				setMnemonic(KeyEvent.VK_3);
				
				JMenuItem item2 = new JMenuItem("Об игре", null);
				item2.setAccelerator(KeyStroke.getKeyStroke("F1"));
				item2.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
				item2.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AboutDialog aDia;
						try {
							aDia = new AboutDialog(GameFrame.getFrame());
							aDia.setVisible(true);
						} catch (Exception e1) {	e1.printStackTrace();}
						
						GameFrame.setPaused(false);
					}
				});
			
				JMenuItem item3 = new JMenuItem("Обновления", null);
				item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.ALT_MASK));
				item3.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
				item3.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
					}
				});
				
				JMenuItem item4 = new JMenuItem("Обратная связь", null);
				item4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
				item4.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
				item4.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						
					}
				});
				
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