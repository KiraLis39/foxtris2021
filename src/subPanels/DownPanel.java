package subPanels;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import registry.Registry;


@SuppressWarnings("serial")
public class DownPanel extends JPanel {
	
	public DownPanel() {
		setBackground(Color.BLACK);
		setIgnoreRepaint(true);
		add(new JLabel() {
			{
				setText("-= FOX-TRISS 2018 =-  @FoxGroup Multiverse tech.");
				setForeground(Color.YELLOW.darker());
				setFont(Registry.downInfoPaneFont);
				setAlignmentX(CENTER_ALIGNMENT);
			}
		});
	}
}
