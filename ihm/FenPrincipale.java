package ihm;

import java.awt.EventQueue;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class FenPrincipale {

	private JFrame frame;
	private Socket sockConnexion;

	/**
	 * Launch the application.
	 */
	public void ouvrirFenetre(){
		this.frame.setVisible(true);
	}
	

	/**
	 * Create the application.
	 */
	public FenPrincipale(Socket sockConnexion, String infosUser) {
		this.sockConnexion = sockConnexion;
		String[] infosDecomp = infosUser.split("/");
		if(Integer.parseInt(infosDecomp[5]) == 1){
			/* On créer un membre */
		}else{
			/* On créer un Administrateur */
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblWelcome = new JLabel("Welcome");
		frame.getContentPane().add(lblWelcome, BorderLayout.CENTER);
	}

}
