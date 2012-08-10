package map.ksj;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlCurve implements Data, Serializable {
	
	private Point[] points;
	
	public List<Integer> links = new ArrayList<Integer>();
	
	public void addLink(int idx) {
		this.links.add(idx);
	}
	
	public int[] getLinks() {
		int[] ret = new int[this.links.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = this.links.get(i);
		}
		return ret;
	}
	
	public Point[] getPoints() {
		return this.points;
	}
	
	public Point getFirstPoint() {
		return this.points[0];
	}
	
	public Point getLastPoint() {
		return this.points[this.points.length - 1];
	}
	
	public void link(String tag, Object obj) {
		if (obj instanceof Point[]) {
			this.points = (Point[]) obj;
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
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
