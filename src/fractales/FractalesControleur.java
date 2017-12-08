package fractales;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.awt.Graphics2D;
import java.awt.Point;

import org.apache.commons.math3.complex.Complex;

/**
 * Controleur du projet, il réalise toute la logique du code et traite les
 * événements de la vue, pour modifier le modèle en conséquence.
 * 
 * @authors Nathan Lesne Bérénice Rose
 *
 */

public class FractalesControleur implements IFractales {
	private FractalesModele m_modele;
	// private Complex[][] m_pixels_complexes;
	private Graphics2D graphic;

	public FractalesControleur(FractalesModele modele) {
		m_modele = modele;
	}

	/**
	 * Convertit un pixel aux coordonnées x,y en un nombre complexe en fonction du
	 * déplacement (xpos,ypos) et du zoom. Pour les nombres complexes, nous
	 * utilisons la librairie d'Apache math3.Complex dont la javadoc est disponible
	 * ci-dessousS
	 * http://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html
	 * 
	 * @param x
	 *            coordonnée en x du pixel sur l'écran
	 * @param y
	 *            coordonnée en y du pixel sur l'écran
	 * @return Un nombre complexe
	 */
	public Complex calculer_complexe(double x, double y) {
		double reel = m_modele.get_Xpos() + (x - (m_modele.get_largeur_fractale() / 2)) / m_modele.getZoom();
		double imaginaire = m_modele.get_Ypos() + (y - (m_modele.get_hauteur_fractale() / 2)) / m_modele.getZoom();
		return new Complex(reel, imaginaire);
	}

	/**
	 * Cette action change la couleur du pixel passé en paramètre sur l'image passée
	 * en paramètre en fonction du nombre d'itérations que le pixel a atteint, si il
	 * a atteint le maximum d'itérations alors on considère qu'il est à l'intérieur
	 * du set et on le colorie en blanc.
	 * 
	 * @param new_img
	 *            l'image sur laquelle on change la couleur des pixels.
	 * @param x
	 *            coordonnée en x du pixel sur l'écran
	 * @param y
	 *            coordonnée en y du pixel sur l'écran
	 * @param iteration
	 *            le nombre d'itérations que le pixel a atteint
	 */
	private void calculer_couleur_point(BufferedImage new_img, int x, int y, int iteration) {

		if (iteration < m_modele.get_iterations())
			// on change le canal vert de la couleur en fonction de l'itération du pixel
			new_img.setRGB(x, y, new Color(0, (iteration * 15) % 254, 0).getRGB());
		else
			// sinon il est dans le set, on le colorie alors en blanc
			new_img.setRGB(x, y, Color.WHITE.getRGB());
	}

	/**
	 * 
	 * @param point
	 *            le point sur le plan complexe, correspond a un pixel sur l'image
	 * @return le nombre d'itérations qu'a atteint ce point.
	 */
	private int calculer_iterations(Complex point) {
		int iterations = 0;

		/**
		 * on fait un rapide calcul pour savoir si le point est dans le set. Cela
		 * augmente la vitesse du programme car on n'itère pas inutilement.
		 */
		if (estDansMandelbrot(point))
			iterations = m_modele.get_iterations();
		else {
			Complex pointIter = point;
			/**
			 * Pour calculer les itérations des points, on fait le calcul zn+1 = zn^2 + C ou
			 * zn est le nombre complexe qu'on modifie et C celui de départ La condition
			 * d'arret est que la distance au centre du point soit supérieure à 2 auquel cas
			 * il n'est pas dans le set de Mandelbrot.
			 */
			while (pointIter.abs() < 2 && iterations < m_modele.get_iterations()) {
				pointIter = pointIter.multiply(pointIter).add(point);
				iterations++;
			}
		}
		return iterations;
	}

	/**
	 * Cette fonction calcule si un point complexe peut se trouver dans le set, pour
	 * cela on teste si le point est dans la forme que crée la fonction la
	 * représentation de la fonction. Cela améliore les significativement les
	 * performances. Source :
	 * https://en.wikipedia.org/wiki/Mandelbrot_set#Cardioid_/_bulb_checking
	 * 
	 * @param point
	 *            un complexe qu'on veut tester
	 * @return Vrai si ce point peut être dans le set de Mandelbrot
	 */
	private boolean estDansMandelbrot(Complex point) {
		double q = ((point.getReal() - 0.25) * (point.getReal() - 0.25))
				+ (point.getImaginary() * point.getImaginary());
		return (q * (q + (point.getReal() - 0.25))) < (0.25 * (point.getImaginary() * point.getImaginary()));
	}

	/**
	 * La fonction qui calcule l'image de la fractale Mandelbrot avec les paramètres
	 * entrés par l'utilisateur. On parcourt tout les pixels de l'écran, on les
	 * convertit en nombre complexe, calcule leurs itérations et colorie alors le
	 * pixel en fonction de celles-ci. On envoie alors l'image au modèle.
	 * L'algorithme utilisé est décrit plus en détail ici :
	 * https://en.wikipedia.org/wiki/Mandelbrot_set#Escape_time_algorithm
	 */
	public void calculer_image_mandelbrot() {
		long start = System.currentTimeMillis();
		BufferedImage new_img = new BufferedImage(m_modele.get_largeur_fractale(), m_modele.get_hauteur_fractale(),
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < m_modele.get_largeur_fractale(); x++) {
			for (int y = 0; y < m_modele.get_hauteur_fractale(); y++) {
				int iteration = calculer_iterations(calculer_complexe((double) x, (double) y));
				calculer_couleur_point(new_img, x, y, iteration);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Temps de calcul de l'image : " + (end - start) / 1000.0 + "s");
		m_modele.set_image(new_img);
	}

	/**
	 * Cette fonction change le nombre d'itérations faites suivant le type de
	 * fractale, car les 2 sont très différentes et demandent des paramètres
	 * différents.
	 * 
	 * @param facteur
	 *            Ce paramètre prends les valeurs 1 ou -1, en fonction de l'action
	 *            demmandée, une augmentation ou une diminution des itérations;
	 */
	@Override
	public void inc_iterations(int facteur) {
		switch (m_modele.getTypeFractale()) {
		case MANDELBROT:
			m_modele.inc_iterations(FractalesModele.INCREMENTATION_ITERATIONS_MANDELBROT * facteur);
			break;
		case FLOCON_KOCH:
			m_modele.inc_iterations(FractalesModele.INCREMENTATION_ITERATIONS_FLOCONS_KOCH * facteur);
			break;

		}
	}

	/**
	 * Change le zoom en fonction du paramètre et augmente également les itérations
	 * pour le Flocon de Koch.
	 * 
	 * @param facteur
	 *            Ce paramètre prends les valeurs 1 ou -1 en fonction de l'action
	 *            demandée, un zoom ou un dezoom
	 */
	@Override
	public void inc_zoom(int facteur) {
		m_modele.incZoom(facteur);
		if (m_modele.getTypeFractale() == TypeFractale.FLOCON_KOCH)
			m_modele.inc_iterations(FractalesModele.INCREMENTATION_ITERATIONS_FLOCONS_KOCH * facteur);

	}

	/**
	 * @param dir
	 *            La direction dans laquelle l'utilisateur veut déplacer la fractale
	 *            sur l'écran Vers le haut, la gauche, le bas ou la droite. Cela se
	 *            fait en fonction de la fractale car elles utilisent des plans
	 *            différents.
	 */
	@Override
	public void deplacement(Direction dir) {
		switch (m_modele.getTypeFractale()) {
		case MANDELBROT:
			switch (dir) {
			case HAUT:
				m_modele.set_Ypos(
						m_modele.get_Ypos() - (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			case GAUCHE:
				m_modele.set_Xpos(
						m_modele.get_Xpos() - (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			case BAS:
				m_modele.set_Ypos(
						m_modele.get_Ypos() + (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			case DROITE:
				m_modele.set_Xpos(
						m_modele.get_Xpos() + (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			}
			break;

		case FLOCON_KOCH:
			switch (dir) {
			case HAUT:
				m_modele.set_y_deplacement(
						m_modele.get_y_deplacement() - (int) (FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			case GAUCHE:
				m_modele.set_x_deplacement(
						m_modele.get_x_deplacement() - (int) (FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			case BAS:
				m_modele.set_y_deplacement(
						m_modele.get_y_deplacement() + (int) (FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			case DROITE:
				m_modele.set_x_deplacement(
						m_modele.get_x_deplacement() + (int) (FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			}
			break;
		}
	}

	/**
	 * Calcule l'image du Flocon de Koch. Tout d'abord, on place les 3 points de
	 * départ proche du centre de la fenêtre de facon à former un triangle
	 * équilatéral, c'est le point de départ de l'algorithme pour créer les flocons.
	 * On lance ensuite l'algorithme récursif sur les 3 lignes (voir ci dessous),
	 * une fois l'image calculée, on la transmet au modèle.
	 */
	public void calculer_image_flocon() {
		BufferedImage new_img = new BufferedImage(m_modele.get_largeur_fractale(), m_modele.get_hauteur_fractale(),
				BufferedImage.TYPE_INT_RGB);

		graphic = new_img.createGraphics();

		int largeur = (m_modele.get_largeur_fractale() / 3) * (int) (m_modele.getZoom() / 100);
		Point a = new Point(m_modele.get_largeur_fractale() / 2 + m_modele.get_x_deplacement(),
				m_modele.get_hauteur_fractale() - m_modele.get_hauteur_fractale() / 3
						- (int) (m_modele.get_largeur_fractale() / (2 * Math.sqrt(3))) + m_modele.get_y_deplacement());
		Point b = new Point(a.x - largeur / 2, a.y + (int) (largeur * Math.sqrt(3) / 2));
		Point c = new Point(a.x + largeur / 2, b.y);

		int iterations = m_modele.get_iterations();
		calculer_flocon(iterations, a, b);
		calculer_flocon(iterations, b, c);
		calculer_flocon(iterations, c, a);

		System.out.println(a.distance(b)); // Tests
		System.out.println(a.distance(c));
		System.out.println(c.distance(b));

		m_modele.set_image(new_img);
	}

	/**
	 * L'algorithme récursif permettant de construire le Flocon de Koch. Une brève
	 * description de l'algorithme est disponible ici:
	 * https://fr.wikipedia.org/wiki/Flocon_de_Koch
	 * 
	 * @param iterations
	 *            // le nombre d'itérations
	 * @param a
	 *            Point de la ligne
	 * @param e
	 *            Point de la ligne
	 */
	public void calculer_flocon(int iterations, Point a, Point e) {
		// On dessine la ligne entre les 2 points, (seulement si au moins 1 est
		// visible), une fois qu'on est arrivé à la fin (itération 0).
		if (iterations == 0) {
			if ((a.x > 0 && a.x < m_modele.get_largeur_fractale()) || (a.y > 0 && a.y < m_modele.get_hauteur_fractale())
					|| (e.x > 0 && e.x < m_modele.get_largeur_fractale())
					|| (e.y > 0 && e.y < m_modele.get_hauteur_fractale())) {
				graphic.drawLine(a.x, a.y, e.x, e.y);
			}
		} else {
			// Si l'on est encore à plus d'une itération, on calcule les coordonnées du
			// nouveau triangle, puis fais un appel récursif avec chacune des lignes.
			int distanceX = e.x - a.x;
			int distanceY = e.y - a.y;

			Point c = new Point((int) (0.5 * (a.x + e.x) + Math.sqrt(3) * (a.y - e.y) / 6),
					(int) (0.5 * (a.y + e.y) + Math.sqrt(3) * (e.x - a.x) / 6));
			Point b = new Point(a.x + distanceX / 3, a.y + distanceY / 3);
			Point d = new Point(a.x + 2 * distanceX / 3, a.y + 2 * distanceY / 3);

			calculer_flocon(iterations - 1, a, b);
			calculer_flocon(iterations - 1, b, c);
			calculer_flocon(iterations - 1, c, d);
			calculer_flocon(iterations - 1, d, e);
		}

	}

	/**
	 * Calcule l'image en fonction du type de fractale sélectionnée
	 */
	@Override
	public void calculer_image_fractale() {
		switch (m_modele.getTypeFractale()) {
		case MANDELBROT:
			calculer_image_mandelbrot();
			break;
		case FLOCON_KOCH:
			calculer_image_flocon();
			break;
		}
	}

	/**
	 * Réinitialise les attributs du modèle à leurs valeurs de départ, utilisé quand
	 * on change de fractale.
	 */
	@Override
	public void reinitialiserParametres() {
		m_modele.setZoom(FractalesModele.ZOOM_MIN);
		m_modele.set_Xpos(0);
		m_modele.set_Ypos(0);
		m_modele.set_x_deplacement(0);
		m_modele.set_x_deplacement(0);
	}
}
