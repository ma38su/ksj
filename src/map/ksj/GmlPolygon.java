package map.ksj;

import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlPolygon implements Data, Serializable {
	
	private Point[] points;
	
	public Point[] getPoints() {
		return this.points;
	}
	
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			this.points = ((GmlCurve) obj).getPoints();
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlPolygon) {
			GmlPolygon curve = (GmlPolygon) obj;
			ret = Arrays.equals(this.points, curve.points) || Arrays.equals(this.points, curve.reversePoints());
		}
		return ret;
	}
	
	private Point[] reversePoints() {
		Point[] ret = new Point[this.points.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = this.points[ret.length - i - 1];
		}
		return ret;
	}

	@Override
	public String toString() {
		return "points: "+ Arrays.toString(this.points);
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (Point p : this.points) {
			hashCode += p.hashCode();
		}
		return hashCode;
	}
}
