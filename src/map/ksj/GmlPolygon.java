package map.ksj;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlPolygon extends Polygon implements Data, Serializable {
	
	/**
	 * 座標系を量子化してポリゴンを圧縮する
	 * @param reductionScale 桁の数
	 * @param polygons 対象のポリゴンのリスト
	 * @return 圧縮したポリゴンのリスト
	 */
	public static List<Polygon> getReduction(int reductionScale, List<Polygon> polygons) {
		List<Polygon> ret = new ArrayList<Polygon>();
		
		int maxN = 0;
		for (Polygon p : polygons) {
			if (maxN < p.npoints) {
				maxN = p.npoints;
			}
		}

		int[] tmpX = new int[maxN];
		int[] tmpY = new int[maxN];
		int reduceCount = 0;
		int reduceIsland = 0;
		for (Polygon p : polygons) {
			tmpX[0] = p.xpoints[0] / reductionScale;
			tmpY[0] = p.ypoints[0] / reductionScale;
			int newN = 0;
			for (int i = 1; i < p.npoints; i++) {
				int nx = p.xpoints[i] / reductionScale;
				int ny = p.ypoints[i] / reductionScale;
				if (nx != tmpX[i] || ny != tmpY[i]) {
					newN++;
					tmpX[newN] = nx;
					tmpY[newN] = ny;
				} else {
					reduceCount++;
				}
			}
			if (maxN > 1) {
				// 三角形以上
				int[] newX = new int[newN];
				int[] newY = new int[newN];
				for (int i = 1; i < newN; i++) {
					newX[i] = tmpX[i];
					newY[i] = tmpY[i];
				}
				ret.add(new Polygon(newX, newY, newN));
			} else {
				reduceIsland++;
			}
		}
		
		System.out.println("reduceIsland: "+ reduceIsland);
		System.out.println("reduceCount:  "+ reduceCount);
		return ret;
	}
	
	public static List<Polygon> getOptBK(List<Polygon> polygons) {
		Area area = new Area();
		for (int i = 0; i < polygons.size(); i++) {
			System.out.printf("area add: %d / %d\n", i, polygons.size());
			Polygon polygon = polygons.get(i);
			area.add(new Area(polygon));
		}
		return toPolygonList(area);
	}

	public static List<Polygon> getOpt(List<Polygon> polygons) {
		LinkedList<Area> areaList = new LinkedList<Area>();
		for (Polygon p : polygons) {
			areaList.add(new Area(p));
		}
		while (areaList.size() > 1) {
			//System.out.printf("area add: %d / %d\n", areaList.size(), polygons.size());
			Area area = areaList.poll();
			area.add(areaList.poll());
			areaList.add(area);
		}
		return toPolygonList(areaList.poll());
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
	
	public GmlPolygon() {
		super();
	}

	public GmlPolygon(int n, int[] x, int[] y) {
		super(x, y, n);
	}
	
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			int[] x = curve.getArrayX();
			int[] y = curve.getArrayY();
			int n = curve.getArrayLength();

			assert(x[0] == x[n - 1] && y[0] == y[n - 1]);

			int[] newX = new int[n - 1];
			int[] newY = new int[n - 1];
			int newN = n - 1;
			for (int i = 0; i < newN; i++) {
				newX[i] = x[i];
				newY[i] = y[i];
			}
			
			this.xpoints = newX;
			this.ypoints = newY;
			this.npoints = newN;
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
