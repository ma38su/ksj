package map.ksj;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlPolygon implements Data, Serializable {
	
	public static List<Polygon> get(GmlPolygon[] polygons) {
		Area area = new Area();
		for (GmlPolygon polygon : polygons) {
			area.add(new Area(new Polygon(polygon.x, polygon.y, polygon.n)));
		}
		return toPolygonList(area);
	}
	
	/**
	 * AreaをList<Polygon>に変換します。
	 * @param area 変換するAreaインスタンス
	 * @return 変換したPolygonのList
	 */
	private static List<Polygon> toPolygonList(Area area) {
		List<Polygon> list = new ArrayList<Polygon>();
		PathIterator itr = area.getPathIterator(new AffineTransform());
		List<Integer> x = new ArrayList<Integer>();
		List<Integer> y = new ArrayList<Integer>();
		while (!itr.isDone()) {
			double[] coords = new double[6];
			int type = itr.currentSegment(coords);
			switch (type) {
				case PathIterator.SEG_MOVETO: 
					if (x.size() != y.size() || x.size() != 0) {
						throw new IllegalArgumentException("エラー");
					}
					x.add((int) (coords[0] + 0.5f));
					y.add((int) (coords[1] + 0.5f));
					break;
				case PathIterator.SEG_CUBICTO:
				case PathIterator.SEG_QUADTO:
				case PathIterator.SEG_LINETO:
					if (x.size() != y.size() || x.size() == 0) {
						throw new IllegalArgumentException("エラー");
					}
					x.add((int) (coords[0] + 0.5));
					y.add((int) (coords[1] + 0.5));
					break;
				case PathIterator.SEG_CLOSE:
					int[] aryX = new int[x.size()];
					int[] aryY = new int[y.size()];
					for (int i = 0; i < aryX.length; i++) {
						aryX[i] = x.get(i);
						aryY[i] = y.get(i);
					}
					list.add(new Polygon(aryX, aryY, aryX.length));
					x.clear();
					y.clear();
					break;
				default:
					throw new IllegalArgumentException();
			}
			itr.next();
		}
		return list;
	}
	
	private int n;
	private int[] x;
	private int[] y;
	
	private transient Rectangle bounds;
	
	public GmlPolygon() {
	}

	public GmlPolygon(int n, int[] x, int[] y) {

		this.n = n;
		this.x = x;
		this.y = y;
		initBounds();
	}
	
	private void initBounds() {
		assert(this.x[0] == this.x[this.n - 1] && this.y[0] == this.y[this.n - 1]);

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = 0, maxY = 0;
		for (int i = 0; i < this.n; i++) {
			if (minX > this.x[i]) minX = this.x[i];
			if (maxX < this.x[i]) maxX = this.x[i];
			if (minY > this.y[i]) minY = this.y[i];
			if (maxY < this.y[i]) maxY = this.y[i];
		}
		this.bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public Rectangle getBounds() {
		return this.bounds;
	}

	public int getArrayLength() {
		return this.n;
	}
	
	public int[] getArrayX() {
		return this.x;
	}
	
	public int[] getArrayY() {
		return this.y;
	}
	
	public void draw(Graphics2D g) {
		g.drawPolyline(x, y, n);
	}

	public void fill(Graphics2D g) {
		g.fillPolygon(x, y, n);
	}

	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			this.x = curve.getArrayX();
			this.y = curve.getArrayY();
			this.n = curve.getArrayLength();
			initBounds();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlPolygon) {
			GmlPolygon curve = (GmlPolygon) obj;
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
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		System.out.println("initBounds");
		initBounds();
	}
	
}
