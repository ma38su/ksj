package map.ksj;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlPolygons implements Data {
	
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
		int total = 0;
		for (Polygon p : polygons) {
			tmpX[0] = p.xpoints[0] / reductionScale;
			tmpY[0] = p.ypoints[0] / reductionScale;
			int newN = 0;
			for (int i = 1; i < p.npoints; i++) {
				int nx = p.xpoints[i] / reductionScale;
				int ny = p.ypoints[i] / reductionScale;
				if (nx != tmpX[newN] || ny != tmpY[newN]) {
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
				for (int i = 0; i < newN; i++) {
					newX[i] = tmpX[i] * reductionScale + reductionScale / 2;
					newY[i] = tmpY[i] * reductionScale + reductionScale / 2;
				}
				ret.add(new Polygon(newX, newY, newN));
			} else {
				reduceIsland++;
			}
			total += p.npoints;
		}
		
		System.out.println("reduceIsland: "+ reduceIsland);
		System.out.println("reduceCount:  "+ reduceCount + " / "+ total);
		return ret;
	}
	
	public static List<Polygon> getOpt(List<Polygon> polygons) {
		LinkedList<Area> areaList = new LinkedList<Area>();
		for (Polygon p : polygons) {
			areaList.add(new Area(p));
		}
		while (areaList.size() > 1) {
			System.out.printf("polygon opt: %d / %d\n", areaList.size(), polygons.size());
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

	private List<Polygon> polygons;
	
	public GmlPolygons() {
		this.polygons = new ArrayList<Polygon>();
	}
	
	public List<Polygon> getPolygons() {
		return this.polygons;
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

			this.polygons.add(new Polygon(newX, newY, newN));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean ret = this == obj;
		if (!ret && obj instanceof GmlPolygons) {
			GmlPolygons gml = (GmlPolygons) obj;
			ret = equalsPolygons(this.polygons, gml.polygons);
		}
		return ret;
	}
	
	private static boolean equalsPolygons(List<Polygon> ps1, List<Polygon> ps2) {
		boolean ret = ps1.size() == ps2.size();
		if (ret) {
			int size = ps1.size();
			for (int i = 0; i < size; i++) {
				Polygon p1 = ps1.get(i);
				Polygon p2 = ps2.get(i);
				ret &= equalsPolygon(p1, p2);
			}
			if (ret) return ret;
			ret = true;
			for (int i = 0; i < size; i++) {
				Polygon p1 = ps1.get(i);
				Polygon p2 = ps2.get(size - 1 - i);
				ret &= equalsPolygon(p1, p2);
			}
		}
		return ret;
	}
	
	private static boolean equalsPolygon(Polygon p1, Polygon p2) {
		return p1.npoints == p2.npoints &&
				((Arrays.equals(p1.xpoints, p2.xpoints) && Arrays.equals(p1.ypoints, p2.ypoints)) ||
				(Arrays.equals(p1.xpoints, reverseArray(p2.xpoints)) && Arrays.equals(p1.ypoints, reverseArray(p2.ypoints))));
	}

	private static int[] reverseArray(int[] ary) {
		int[] ret = new int[ary.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ary[ret.length - i - 1];
		}
		return ret;
	}

	@Override
	public int hashCode() {
		int ret = 0;
		for (Polygon p : this.polygons) {
			ret += (p.xpoints[0] + p.ypoints[0] + p.xpoints[p.npoints - 1] + p.ypoints[p.npoints - 1]);
		}
		return ret;
	}
	
}
