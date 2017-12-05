package fractales;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Math;

import org.apache.commons.math3.complex.Complex;

public class FractalesControleur {
	private FractalesModele m_modele;
	// private Complex[][] m_pixels_complexes;

	public FractalesControleur(FractalesModele modele) {
		m_modele = modele;
	}

	public Complex calculer_complexe(double x, double y) {
		double réel = m_modele.get_point_min().getReal() + ((x / m_modele.get_largeur_fractale()) * (m_modele.get_point_max().getReal() - m_modele.get_point_min().getReal()));
		double imaginaire = m_modele.get_point_max().getImaginary()
				- ((y / m_modele.get_hauteur_fractale()) * (m_modele.get_point_max().getImaginary() - m_modele.get_point_min().getImaginary()));
		return new Complex(réel, imaginaire);
	}

	private void calculer_couleur_point(BufferedImage new_img, int x, int y, int iteration) {

		if (iteration < m_modele.get_iterations_max())
			new_img.setRGB(x, y, new Color(0, (iteration * 3) % 254, 0).getRGB());
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

	public void calculer_image_fractale() {
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
	
	public void inc_iterations_max(int n)
	{
		m_modele.inc_iterations_max(n);
	}

}
