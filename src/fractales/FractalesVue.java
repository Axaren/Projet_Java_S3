package fractales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FractalesVue extends JFrame implements Observer, ActionListener {
	private FractalesControleur m_controleur;
	private FractalesModele m_modele;
	private int m_largeur_fenetre;
	private int m_hauteur_fenetre;
	private JFrame m_fenetre;
	private JPanel m_UI;
	private ImageIcon m_conteneur_image;
	private BufferedImage m_image;

	public FractalesVue(String titre, FractalesModele modele, FractalesControleur controleur, int largeur, int hauteur) {
		super();
		m_modele = modele;
		m_controleur = controleur;
		m_largeur_fenetre = largeur;
		m_hauteur_fenetre = hauteur;
		initVue(titre);
	}
	
	public FractalesVue(String titre, FractalesModele modele, FractalesControleur controleur)
	{
		super();
		m_modele = modele;
		m_controleur = controleur;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		m_largeur_fenetre = gd.getDisplayMode().getWidth();
		m_hauteur_fenetre = gd.getDisplayMode().getHeight();
		initVue(titre);
	}

	private void initVue(String titre) {
		initFenetre(titre);
		initUI();
		initImage();
		m_fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
		m_fenetre.setBackground(Color.BLACK);
		m_fenetre.setVisible(true);
	}

	private void initImage() {
		m_modele.set_largeur_fractale(m_largeur_fenetre - m_UI.getWidth());
		m_modele.set_hauteur_fractale(m_hauteur_fenetre - m_UI.getHeight());
		m_controleur.calculer_image_fractale();
		m_image = m_modele.get_image();
		m_conteneur_image = new ImageIcon(m_image);
		m_fenetre.add(new JLabel(m_conteneur_image));
	}

	private void initUI() {
		m_UI = new JPanel();
		m_UI.setBounds(0, 0, m_largeur_fenetre/8, m_hauteur_fenetre/8);
		JButton zoom = new JButton("+");
		JButton dezoom = new JButton("-");
		zoom.setActionCommand("Zoom");
		dezoom.setActionCommand("Dezoom");
		zoom.addActionListener(this);
		dezoom.addActionListener(this);
		m_UI.add(zoom);
		m_UI.add(dezoom);
		m_fenetre.add(m_UI,BorderLayout.WEST);
	}

	private void initFenetre(String titre) {
		m_fenetre = new JFrame(titre);
		m_fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_fenetre.setBounds(0, 0, m_largeur_fenetre, m_hauteur_fenetre);
		m_fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(WindowEvent winEvt) {
	            m_fenetre.dispose();
	            System.exit(0);
	        }
	    });
	}

	@Override
	public void update(Observable o, Object arg) {
		m_image = m_modele.get_image();
		m_conteneur_image = new ImageIcon(m_image);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand())
		{
		case "Zoom":
			m_controleur.inc_iterations_max(10);
			break;
		case "Dezoom":
			m_controleur.inc_iterations_max(-10);
			break;
		}
	}

}
