package map.ksj;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlCurve implements Data, Serializable {
	
	public static GmlCurve[] join(List<GmlCurve> curves) {
		List<GmlCurve> ret = new ArrayList<GmlCurve>();
		
		LinkedList<GmlCurve> list = new LinkedList<GmlCurve>(curves);
		while (!list.isEmpty()) {
			GmlCurve curve = list.poll();
			
			Iterator<GmlCurve> itr = list.iterator();
			while (itr.hasNext()) {
				GmlCurve c = itr.next();
				GmlCurve joined = curve.join(c);
				if (joined != null) {
					itr.remove();
					curve = joined;
					itr = list.iterator();
				}
			}
			
			ret.add(curve);
		}
		return ret.toArray(new GmlCurve[ret.size()]);
	}
	
	private int npoints;
	private int[] xpoints;
	private int[] ypoints;
	
	private transient Rectangle bounds;
	
	public GmlCurve() {
	}

	public GmlCurve(int[] xpoints, int[] ypoints, int npoints) {
		this.xpoints = xpoints;
		this.ypoints = ypoints;
		this.npoints = npoints;
		this.initBounds();
	}
	
	public GmlCurve(Point[] points) {
		assert(points.length > 0);
		this.npoints = points.length;
		this.xpoints = new int[this.npoints];
		this.ypoints = new int[this.npoints];
		for (int i = 0; i < this.npoints; i++) {
			this.xpoints[i] = points[i].x;
			this.ypoints[i] = points[i].y;
		}
		this.initBounds();
	}
	
	private void initBounds() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = 0, maxY = 0;
		for (int i = 0; i < this.npoints; i++) {
			if (minX > this.xpoints[i]) minX = this.xpoints[i];
			if (maxX < this.xpoints[i]) maxX = this.xpoints[i];
			if (minY > this.ypoints[i]) minY = this.ypoints[i];
			if (maxY < this.ypoints[i]) maxY = this.ypoints[i];
		}
		this.bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	public int[] getArrayX() {
		return this.xpoints;
	}
	
	public int[] getArrayY() {
		return this.ypoints;
	}
	
	public int getArrayLength() {
		return this.npoints;
	}
	
	public Point getFirstPoint() {
		return new Point(this.xpoints[0], this.ypoints[0]);
	}
	
	public Point getLastPoint() {
		return new Point(this.xpoints[this.npoints - 1], this.ypoints[this.npoints - 1]);
	}
	
	public void draw(Graphics2D g) {
		g.drawPolyline(this.xpoints, this.ypoints, this.npoints);
	}

	public void link(String tag, Object obj) {
		if (obj instanceof Point[]) {
			Point[] points = (Point[]) obj;
			assert(points.length > 0);
			this.npoints = points.length;
			this.xpoints = new int[points.length];
			this.ypoints = new int[points.length];
			for (int i = 0; i < this.npoints; i++) {
				this.xpoints[i] = points[i].x;
				this.ypoints[i] = points[i].y;
			}
			initBounds();
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			ret = (Arrays.equals(this.xpoints, curve.xpoints) && Arrays.equals(this.ypoints, curve.ypoints)) ||
					(Arrays.equals(this.xpoints, reverseArray(curve.xpoints)) && Arrays.equals(this.ypoints, reverseArray(curve.ypoints)));
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

	public Rectangle getBounds() {
		return this.bounds;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("pts: ");
		for (int i = 0; i < this.npoints; i++) {
			sb.append('[');
			sb.append(this.xpoints[i]);
			sb.append(", ");
			sb.append(this.ypoints[i]);
			sb.append("], ");
		}
		return "points: "+ sb.toString();
	}

	@Override
	public int hashCode() {
		return this.xpoints[0] + this.ypoints[0] + this.xpoints[this.npoints - 1] + this.ypoints[this.npoints - 1];
	}
	
	protected boolean isConnected(GmlCurve curve) {
		Point p1 = this.getFirstPoint();
		Point p2 = this.getLastPoint();
		Point p3 = curve.getFirstPoint();
		Point p4 = curve.getLastPoint();
		
		return (p1.equals(p3) || p1.equals(p4)
				|| p2.equals(p3) || p2.equals(p4));
	}
	
	public GmlCurve join(GmlCurve curve) {

		Point p1 = this.getFirstPoint();
		Point p2 = this.getLastPoint();
		Point p3 = curve.getFirstPoint();
		Point p4 = curve.getLastPoint();

		int n = this.npoints + curve.npoints - 1;
		int[] xary = new int[n];
		int[] yary = new int[n];

		GmlCurve ret = null;
		if (p1.equals(p3)) {
			for (int i = 0; i < this.npoints; i++) {
				xary[i] = this.xpoints[this.npoints - 1 - i];
				yary[i] = this.ypoints[this.npoints - 1 - i];
			}
			for (int i = 1; i < curve.npoints; i++) {
				xary[this.npoints + i - 1] = curve.xpoints[i];
				yary[this.npoints + i - 1] = curve.ypoints[i];
			}
			ret = new GmlCurve(xary, yary, n);
		} else if (p1.equals(p4)) {
			for (int i = 0; i < this.npoints; i++) {
				xary[i] = this.xpoints[this.npoints - 1 - i];
				yary[i] = this.ypoints[this.npoints - 1 - i];
			}
			for (int i = 1; i < curve.npoints; i++) {
				xary[this.npoints + i - 1] = curve.xpoints[curve.npoints - 1 - i];
				yary[this.npoints + i - 1] = curve.ypoints[curve.npoints - 1 - i];
			}
			ret = new GmlCurve(xary, yary, n);
		} else if (p2.equals(p3)) {
			for (int i = 0; i < this.npoints; i++) {
				xary[i] = this.xpoints[i];
				yary[i] = this.ypoints[i];
			}
			for (int i = 1; i < curve.npoints; i++) {
				xary[this.npoints + i - 1] = curve.xpoints[i];
				yary[this.npoints + i - 1] = curve.ypoints[i];
			}
			ret = new GmlCurve(xary, yary, n);
		} else if (p2.equals(p4)) {
			for (int i = 0; i < this.npoints; i++) {
				xary[i] = this.xpoints[i];
				yary[i] = this.ypoints[i];
			}
			for (int i = 1; i < curve.npoints; i++) {
				xary[this.npoints + i - 1] = curve.xpoints[curve.npoints - 1 - i];
				yary[this.npoints + i - 1] = curve.ypoints[curve.npoints - 1 - i];
			}
			ret = new GmlCurve(xary, yary, n);
		}
		return ret;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		initBounds();
	}
}
