package fractales;

public interface IFractales {
	public void inc_zoom(int n);
	public void inc_iterations_max(int n);
	public void deplacement(Direction dir);
	public void calculer_image_fractale();
	public void reinitialiserParametres();
}
