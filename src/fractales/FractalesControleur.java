package fractales;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.awt.Graphics2D;
import java.awt.Point;

import org.apache.commons.math3.complex.Complex;

public class FractalesControleur implements IFractales {
	private FractalesModele m_modele;
	// private Complex[][] m_pixels_complexes;
	private Graphics2D graphic;

	public FractalesControleur(FractalesModele modele) {
		m_modele = modele;
	}

	public Complex calculer_complexe(double x, double y) {
		double reel = m_modele.get_Xpos() + (x - (m_modele.get_largeur_fractale() / 2)) / m_modele.getZoom();
		double imaginaire = m_modele.get_Ypos() + (y - (m_modele.get_hauteur_fractale() / 2)) / m_modele.getZoom();
		return new Complex(reel, imaginaire);
	}

	private void calculer_couleur_point(BufferedImage new_img, int x, int y, int iteration) {

		if (iteration < m_modele.get_iterations_max())
			new_img.setRGB(x, y, new Color(0, (iteration * 15) % 254, 0).getRGB());
			//new_img.setRGB(x, y, Color.WHITE.getRGB());
		else
			new_img.setRGB(x, y, Color.WHITE.getRGB());
	}

	private int calculer_iterations(Complex point) {
		int iterations = 0;
		
		if (estPossible(point))
			iterations = m_modele.get_iterations_max();
		else
		{
		Complex pointIter = point;
		while (pointIter.abs() < 2 && iterations < m_modele.get_iterations_max()) {
			pointIter = pointIter.multiply(pointIter).add(point);
			iterations++;
		}
		}
		return iterations;
	}

	private boolean estPossible(Complex point) {
		double q = ((point.getReal() - 0.25) * (point.getReal() - 0.25)) + (point.getImaginary() * point.getImaginary());
		return (q * (q + (point.getReal() - 0.25))) < (0.25 * (point.getImaginary() * point.getImaginary()));
	}

	public void calculer_image_mandelbrot() {
		long start = System.currentTimeMillis();
		BufferedImage new_img = new BufferedImage(m_modele.get_largeur_fractale(), m_modele.get_hauteur_fractale(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < m_modele.get_largeur_fractale(); x++) {
			for (int y = 0; y < m_modele.get_hauteur_fractale(); y++) {
				int iteration = calculer_iterations(calculer_complexe((double)x, (double)y));
				calculer_couleur_point(new_img, x, y, iteration);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Temps de calcul de l'image : " + (end - start)/1000.0 + "s");
		m_modele.set_image(new_img);
	}
	
	@Override
	public void inc_iterations_max(int facteur)
	{
		if (m_modele.estMandelbrot())
		m_modele.inc_iterations_max(FractalesModele.INCREMENTATION_ITERATIONS_MANDELBROT * facteur);
		else
			m_modele.inc_iterations_max(FractalesModele.INCREMENTATION_ITERATIONS_FLOCONS_KOCH * facteur);
	}
	
	@Override
	public void inc_zoom(int n)
	{
		m_modele.incZoom(n);
		if(!m_modele.estMandelbrot())
			m_modele.inc_iterations_max(1);
	}
	
	@Override
	public void deplacement(Direction dir)
	{
		if(m_modele.estMandelbrot()){
			switch(dir)
			{
			case HAUT:
				m_modele.set_Ypos(m_modele.get_Ypos() - (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			case GAUCHE:
				m_modele.set_Xpos(m_modele.get_Xpos() - (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			case BAS:
				m_modele.set_Ypos(m_modele.get_Ypos() + (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
				break;
			case DROITE:
				m_modele.set_Xpos(m_modele.get_Xpos() + (FractalesModele.FACTEUR_DEPLACEMENT * 100 / m_modele.getZoom()));
			break;
			}
		}
		else{
			switch(dir)
			{
			case HAUT:
				m_modele.set_y_deplacement(m_modele.get_y_deplacement() - (int)(FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			case GAUCHE:
				m_modele.set_x_deplacement(m_modele.get_x_deplacement() - (int)(FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			case BAS:
				m_modele.set_y_deplacement(m_modele.get_y_deplacement() + (int)(FractalesModele.FACTEUR_DEPLACEMENT) * 100);
				break;
			case DROITE:
				m_modele.set_x_deplacement(m_modele.get_x_deplacement() + (int)(FractalesModele.FACTEUR_DEPLACEMENT) * 100);
			break;
			}
		}
	}

	public void calculer_image_flocon(){
		BufferedImage new_img = new BufferedImage(m_modele.get_largeur_fractale(), m_modele.get_hauteur_fractale(), BufferedImage.TYPE_INT_RGB);
		
		graphic = new_img.createGraphics();
		//graphic.clearRect(0, 0, m_modele.get_largeur_fractale(), m_modele.get_hauteur_fractale());
		
		int largeur = (m_modele.get_largeur_fractale()/3)*(int)(m_modele.getZoom()/100);
		Point a = new Point(m_modele.get_largeur_fractale()/2 + m_modele.get_x_deplacement() ,
				m_modele.get_hauteur_fractale() - m_modele.get_hauteur_fractale()/3 
				- (int)(m_modele.get_largeur_fractale() / (2*Math.sqrt(3))) 
				+ m_modele.get_y_deplacement());
		Point b = new Point(a.x - largeur/2 , a.y + (int)(largeur*Math.sqrt(3)/2));
		Point c = new Point(a.x + largeur/2 , b.y );

		int iterations = m_modele.get_iterations_max();
		calculer_flocon(iterations, a, b);
		calculer_flocon(iterations, b, c);
		calculer_flocon(iterations, c, a);
		
		System.out.println(a.distance(b));  //Tests
		System.out.println(a.distance(c));
		System.out.println(c.distance(b));
		
		m_modele.set_image(new_img);		
	}
	
	public void calculer_flocon(int n, Point a, Point e){
		if(n == 0){
			graphic.drawLine(a.x, a.y, e.x, e.y);
		}
		else{
			int distanceX = e.x - a.x;
			int distanceY = e.y - a.y;
			
			Point c = new Point((int)(0.5*(a.x + e.x)+Math.sqrt(3)*(a.y - e.y)/6), (int)(0.5*(a.y + e.y)+Math.sqrt(3)*(e.x - a.x)/6));
			Point b = new Point(a.x + distanceX/3, a.y + distanceY/3);
			Point d = new Point(a.x + 2*distanceX/3, a.y + 2*distanceY/3);
			
			calculer_flocon(n-1, a, b);
			calculer_flocon(n-1, b, c);
			calculer_flocon(n-1, c, d);
			calculer_flocon(n-1, d, e);
		}
		
	}
	
	@Override
	public void calculer_image_fractale()
	{
		if (!m_modele.estMandelbrot())
			calculer_image_flocon();
		else
			calculer_image_mandelbrot();
	}
	
	@Override
	public void reinitialiserParametres()
	{
		m_modele.setZoom(FractalesModele.ZOOM_MIN);
		m_modele.set_Xpos(0);
		m_modele.set_Ypos(0);
	}
}
