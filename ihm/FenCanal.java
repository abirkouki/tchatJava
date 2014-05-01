package ihm;

import java.awt.EventQueue;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JEditorPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JList;
import java.awt.Color;
import javax.swing.AbstractListModel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;

public class FenCanal {

	private JFrame frame;
	private Socket sockConnexion;
	private JTextField saiMesg;

	/**
	 * Launch the application.
	 */
	public void ouvrirFenetre(){
		this.frame.setVisible(true);
	}
	

	/**
	 * Create the application.
	 */
	public FenCanal(Socket sockConnexion) {
		this.sockConnexion = sockConnexion;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 1024, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JTextPane txtTchat = new JTextPane();
		txtTchat.setBounds(23, 27, 747, 547);
		panel.add(txtTchat);
		
		JList listListeUsers = new JList();
		listListeUsers.setModel(new AbstractListModel() {
			String[] values = new String[] {};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		listListeUsers.setBackground(Color.LIGHT_GRAY);
		listListeUsers.setBounds(820, 489, 155, -433);
		panel.add(listListeUsers);
		
		saiMesg = new JTextField();
		saiMesg.setFont(new Font("Liberation Serif", Font.PLAIN, 17));
		saiMesg.setBounds(23, 586, 621, 35);
		panel.add(saiMesg);
		saiMesg.setColumns(10);
		
		JButton btnEnvoyer = new JButton("Envoyer");
		btnEnvoyer.setFont(new Font("Liberation Serif", Font.BOLD, 15));
		btnEnvoyer.setBounds(656, 592, 117, 25);
		panel.add(btnEnvoyer);
		
		JLabel libNomCanal = new JLabel("Vous êtes actuellement sur le canal : ");
		libNomCanal.setFont(new Font("Liberation Serif", Font.BOLD, 17));
		libNomCanal.setBounds(23, 0, 747, 27);
		panel.add(libNomCanal);
	}
}
