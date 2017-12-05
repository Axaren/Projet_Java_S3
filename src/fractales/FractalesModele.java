package fractales;

import java.awt.image.BufferedImage;
import java.util.Observable;

import org.apache.commons.math3.complex.Complex;

public class FractalesModele extends Observable {

	private int m_iterations_max;
	
	private int m_largeur_fractale;
	private int m_hauteur_fractale;
	
	private Complex m_point_min;
	private Complex m_point_max;
	
	BufferedImage m_image;
	
	public BufferedImage get_image() {
		return m_image;
	}

	public void set_image(BufferedImage image) {
		m_image = image;
	}

	public FractalesModele() {
		m_iterations_max = 10;
		m_point_min = new Complex(-2, -1.25);
		m_point_max = new Complex(2, 1.25);
	}
	
	public Complex get_point_min() {
		return m_point_min;
	}

	public void set_point_min(Complex point_min) {
		m_point_min = point_min;
		setChanged();
		notifyObservers();
	}

	public Complex get_point_max() {
		return m_point_max;
	}

	public void set_point_max(Complex point_max) {
		m_point_max = point_max;
		setChanged();
		notifyObservers();
	}

	public int get_largeur_fractale() {
		return m_largeur_fractale;
	}

	public void set_largeur_fractale(int largeur_fractale) {
		m_largeur_fractale = largeur_fractale;
		setChanged();
		notifyObservers();
	}

	public int get_hauteur_fractale() {
		return m_hauteur_fractale;
	}

	public void set_hauteur_fractale(int longueur_fractale) {
		m_hauteur_fractale = longueur_fractale;
		setChanged();
		notifyObservers();
	}

	public int get_iterations_max() {
		return m_iterations_max;
	}

	public void set_iterations_max(int iterations_max) {
		m_iterations_max = iterations_max;
		setChanged();
		notifyObservers();
	}
	
	public void inc_iterations_max(int n)
	{
		m_iterations_max += n;
		setChanged();
		notifyObservers();
	}

}
