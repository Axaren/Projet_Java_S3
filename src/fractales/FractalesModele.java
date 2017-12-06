package fractales;

import java.awt.image.BufferedImage;
import java.util.Observable;

import org.apache.commons.math3.complex.Complex;

public class FractalesModele extends Observable {

	private int m_iterations_max;
	public static int ITERATIONS_MIN = 50;
	public static int ZOOM_MIN = 50;
	public static int FACTEUR_ZOOM = 2;
	public static double FACTEUR_DEPLACEMENT = 1;
	public static double XYPOS_MIN = -2;
	public static double XYPOS_MAX = 2;
	
	private int m_largeur_fractale;
	private int m_hauteur_fractale;
	
	private Complex m_point_min;
	private Complex m_point_max;
	private double m_x_pos;
	private double m_y_pos;
	
	private int m_zoom;

	BufferedImage m_image;
	
	public FractalesModele() {
		m_iterations_max = ITERATIONS_MIN;
		m_x_pos = 0;
		m_y_pos = 0;
		m_zoom = 100;
		
	}
	
	public int getZoom() {
		return m_zoom;
	}

	public void setZoom(int zoom) {
		m_zoom = zoom;
		setChanged();
		notifyObservers();
	}
	
	public void incZoom(int facteur_zoom)
	{
		m_zoom *= FACTEUR_ZOOM * facteur_zoom;
		if (m_zoom < ZOOM_MIN)
			m_zoom = ZOOM_MIN;
		setChanged();
		notifyObservers();
	}
	
	public double get_Xpos() {
		return m_x_pos;
	}

	public void set_Xpos(double x_pos) {
		m_x_pos = x_pos;
		if (m_x_pos > 2.5)
			m_x_pos = 2.5;
		if (m_x_pos < -2.5)
			m_x_pos = -2.5;
		setChanged();
		notifyObservers();
	}

	public double get_Ypos() {
		return m_y_pos;
	}

	public void set_Ypos(double y_pos) {
		m_y_pos = y_pos;
		if (m_y_pos > 2.5)
			m_y_pos = 2.5;
		if (m_y_pos < -2.5)
			m_y_pos = -2.5;
		setChanged();
		notifyObservers();
	}
	
	public BufferedImage get_image() {
		return m_image;
	}

	public void set_image(BufferedImage image) {
		m_image = image;
		setChanged();
		notifyObservers();
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
		if (m_iterations_max + n > ITERATIONS_MIN)
		m_iterations_max += n;
		else
			m_iterations_max = ITERATIONS_MIN;
		setChanged();
		notifyObservers();
	}

}
