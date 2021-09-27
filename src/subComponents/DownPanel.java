package subComponents;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import registry.Registry;

public class DownPanel extends JPanel {
	public DownPanel() {
		setBackground(Color.BLACK);
		setIgnoreRepaint(true);
		add(new JLabel() {
			{
				setText("-= " + Registry.name + " =-  @FoxGroup Multiverse-39");
				setForeground(Color.YELLOW.darker());
				setFont(Registry.downInfoPaneFont);
				setAlignmentX(CENTER_ALIGNMENT);
			}
		});
	}
}
