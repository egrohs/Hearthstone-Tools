package hcs;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

public class GUI extends JFrame {
	public GUI() {
		setTitle("HCS");
		setLayout(new FlowLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(200, 200);
		add(new JButton(new AbstractAction("Jogos") {
			public void actionPerformed(ActionEvent arg0) {
				//
			}
		}));

		setVisible(true);
	}

	public static void main(String[] args) {
		new GUI();
	}
}
