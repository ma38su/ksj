package map.ksj;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlPolygon extends Polygon implements Data, Serializable {
	
	public static List<Polygon> getOpt(List<Polygon> polygons) {
		Area area = new Area();
		for (int i = 0; i < polygons.size(); i++) {
			System.out.printf("area add: %d / %d\n", i, polygons.size());
			Polygon polygon = polygons.get(i);
			area.add(new Area(new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints)));
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
					list.add(new GmlPolygon(aryX.length, aryX, aryY));
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
	
	public GmlPolygon() {
		super();
	}

	public GmlPolygon(int n, int[] x, int[] y) {
		super(x, y, n);
	}
	
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			this.xpoints = curve.getArrayX();
			this.ypoints = curve.getArrayY();
			this.npoints = curve.getArrayLength();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean ret = this == obj;
		if (!ret && obj instanceof Polygon) {
			Polygon polygon = (Polygon) obj;
			ret = (Arrays.equals(this.xpoints, polygon.xpoints) && Arrays.equals(this.ypoints, polygon.ypoints)) ||
					(Arrays.equals(this.xpoints, reverseArray(polygon.xpoints)) && Arrays.equals(this.ypoints, reverseArray(polygon.ypoints)));
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
		for (int i = 0; i < this.npoints; i++) {
			sb.append('[');
			sb.append(this.xpoints[i]);
			sb.append(", ");
			sb.append(this.ypoints[i]);
			sb.append("], ");
		}
		return "points: "+ sb.toString();
	}
	
	public Polygon join(Polygon polygon) {
		int i = 0;
		int j = 0;
		while (i < this.npoints) {
			while (j < polygon.npoints) {
				if (this.xpoints[i] == polygon.xpoints[j] && this.ypoints[i] == polygon.ypoints[j]) {
					int k = 1;
					while (i + k < this.npoints && j + k < this.npoints && this.xpoints[i + k] == polygon.xpoints[j + k] && this.ypoints[i + k] == polygon.ypoints[j + k]) k++;
					if (k > 1) {
						int n = this.npoints + polygon.npoints - k;
						int[] x = new int[n];
						int[] y = new int[n];
						for (int l = 0; l < i; l++) {
							x[l] = this.xpoints[l];
							y[l] = this.ypoints[l];
						}
						for (int l = 0; l < polygon.npoints; l++) {
							x[l + i] = polygon.xpoints[(j + l) % polygon.npoints];
							y[l + i] = polygon.ypoints[(j + l) % polygon.npoints];
						}
						for (int l = i + k; l < this.npoints; l++) {
							x[l + polygon.npoints - k] = this.xpoints[l];
							y[l + polygon.npoints - k] = this.ypoints[l];
						}
						return new Polygon(x, y, n);
					}
					while (i + k < this.npoints && j - k >= 0 && this.xpoints[i + k] == polygon.xpoints[j - k] && this.ypoints[i + k] == polygon.ypoints[j - k]) k++;
					if (k > 1) {
						int n = this.npoints + polygon.npoints - k;
						int[] x = new int[n];
						int[] y = new int[n];
						for (int l = 0; l < i; l++) {
							x[l] = this.xpoints[l];
							y[l] = this.ypoints[l];
						}
						for (int l = 0; l < polygon.npoints; l++) {
							x[l + i] = polygon.xpoints[(j - l) % polygon.npoints];
							y[l + i] = polygon.ypoints[(j - l) % polygon.npoints];
						}
						for (int l = i + k; l < this.npoints; l++) {
							x[l + polygon.npoints - k] = this.xpoints[l];
							y[l + polygon.npoints - k] = this.ypoints[l];
						}
						return new Polygon(x, y, n);
					}
					while (i - k < this.npoints && j + k >= 0 && this.xpoints[i - k] == polygon.xpoints[j + k] && this.ypoints[i - k] == polygon.ypoints[j + k]) k++;
					if (k > 1) {
						int n = this.npoints + polygon.npoints - k;
						int[] x = new int[n];
						int[] y = new int[n];
						for (int l = 0; l <= i - k; l++) {
							x[l] = this.xpoints[l];
							y[l] = this.ypoints[l];
						}
						for (int l = 0; l < polygon.npoints; l++) {
							x[l + i - k + 1] = this.xpoints[(j + l) % polygon.npoints];
							y[l + i - k + 1] = this.ypoints[(j + l) % polygon.npoints];
						}
						for (int l = i + 1; l < this.npoints; l++) {
							x[l + polygon.npoints - k] = this.xpoints[l];
							y[l + polygon.npoints - k] = this.ypoints[l];
						}
						return new Polygon(x, y, n);
					}
				}
				j++;
			}
			i++;
		}
		return null;
	}

	@Override
	public int hashCode() {
		return this.xpoints[0] + this.ypoints[0] + this.xpoints[this.npoints - 1] + this.ypoints[this.npoints - 1];
	}
	
}
