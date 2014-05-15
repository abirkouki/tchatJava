package ihm;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JButton;

import client.ConnexionClient;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Interface graphique de la première fenêtre, permettant de choisir entre une inscription et une connexion.
 * @author florian
 *
 */
public class FenMenuPrincipal {
	
	/**
	 * Frame de la fenêtre.
	 */
	private JFrame frmApplicationtchatStri;

	/**
	 * Créer la fenêtre et l'ouvre.
	 */
	public void ouvrirFenetre() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FenMenuPrincipal window = new FenMenuPrincipal();
					window.frmApplicationtchatStri.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialise la fenêtre
	 */
	public FenMenuPrincipal() {
		initialize();
	}
	
	/**
	 * Ferme la fenêtre
	 */
	public void fermerFenetre(){
		this.frmApplicationtchatStri.setVisible(false);
		this.frmApplicationtchatStri.dispose();
	}

	/**
	 * Initialise la fenêtre avec les différents éléments qui la compose.
	 */
	private void initialize() {
		
		frmApplicationtchatStri = new JFrame();
		frmApplicationtchatStri.setTitle("Application Tchat STRI");
		frmApplicationtchatStri.setResizable(false);
		frmApplicationtchatStri.setBounds(100, 100, 1024, 700);
		frmApplicationtchatStri.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frmApplicationtchatStri.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel libTitle = new JLabel("MENU PRINCIPAL");
		libTitle.setForeground(Color.RED);
		libTitle.setFont(new Font("Liberation Serif", Font.BOLD, 23));
		libTitle.setBounds(402, 12, 218, 35);
		panel.add(libTitle);
		
		JButton btnConnexion = new JButton("Connexion");
		btnConnexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* On lance la connexion au serveur */
				ConnexionClient conCli = new ConnexionClient();
				conCli.connect(1);
				FenConnexion fenConnexion = new FenConnexion(conCli.getSock());
				fenConnexion.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnConnexion.setSelectedIcon(null);
		btnConnexion.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnConnexion.setBounds(409, 164, 204, 35);
		panel.add(btnConnexion);
		
		JButton btnInscription = new JButton("Inscription");
		/* Quand on clique sur le bouton Inscription on ouvre la fenêtre inscription et on ferme celle-ci */
		btnInscription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/* On lance la connexion au serveur */
				ConnexionClient conCli = new ConnexionClient();
				conCli.connect(2);
				/* On lance ensuite la fenêtre d'inscription */
				FenInscription fenInscr = new FenInscription(conCli.getSock());
				fenInscr.ouvrirFenetre();
				fermerFenetre();
			}
		});
		btnInscription.setSelectedIcon(null);
		btnInscription.setFont(new Font("Liberation Serif", Font.BOLD, 25));
		btnInscription.setBounds(409, 305, 204, 35);
		panel.add(btnInscription);
		
		JLabel libInfo = new JLabel("(Pour utiliser l'application en tant que visiteur, cliquez sur \"Connexion\" puis sélectionner le mode \"Visiteur\" )");
		libInfo.setFont(new Font("Liberation Serif", Font.BOLD | Font.ITALIC, 18));
		libInfo.setBounds(88, 476, 845, 26);
		panel.add(libInfo);
		
		JLabel libCopyright = new JLabel("Copyright STRI 2014 _ Tous droits réservés");
		libCopyright.setFont(new Font("Liberation Serif", Font.ITALIC, 13));
		libCopyright.setBounds(354, 632, 314, 15);
		panel.add(libCopyright);
		
	}
}
