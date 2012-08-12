package map.ksj;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlCurve implements Data, Serializable {
	
	private int n;
	private int[] x;
	private int[] y;
	
	public GmlCurve() {
	}

	public GmlCurve(Point[] points) {
		assert(points.length > 0);
		this.n = points.length;
		this.x = new int[this.n];
		this.y = new int[this.n];
		for (int i = 0; i < this.n; i++) {
			this.x[i] = points[i].x;
			this.y[i] = points[i].y;
		}
	}

	public int[] getArrayX() {
		return this.x;
	}
	
	public int[] getArrayY() {
		return this.y;
	}
	
	public int getArrayLength() {
		return this.n;
	}
	
	public Point getFirstPoint() {
		return new Point(this.x[0], this.y[0]);
	}
	
	public Point getLastPoint() {
		return new Point(this.x[this.n - 1], this.y[this.n - 1]);
	}
	
	public void draw(Graphics2D g) {
		g.drawPolyline(this.x, this.y, this.n);
	}

	public void link(String tag, Object obj) {
		if (obj instanceof Point[]) {
			Point[] points = (Point[]) obj;
			assert(points.length > 0);
			this.n = points.length;
			this.x = new int[points.length];
			this.y = new int[points.length];
			for (int i = 0; i < this.n; i++) {
				this.x[i] = points[i].x;
				this.y[i] = points[i].y;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			ret = (Arrays.equals(this.x, curve.x) && Arrays.equals(this.y, curve.y)) ||
					(Arrays.equals(this.x, reverseArray(curve.x)) && Arrays.equals(this.y, reverseArray(curve.y)));
		}
		return ret;
	}
	
	private static int[] reverseArray(int[] ary) {
		int[] ret = new int[ary.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ary[ret.length - i - 1];
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("pts: ");
		for (int i = 0; i < this.n; i++) {
			sb.append('[');
			sb.append(this.x[i]);
			sb.append(", ");
			sb.append(this.y[i]);
			sb.append("], ");
		}
		return "points: "+ sb.toString();
	}

	@Override
	public int hashCode() {
		return this.x[0] + this.y[0] + this.x[this.n - 1] + this.y[this.n - 1];
	}
}
