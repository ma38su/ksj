package map.ksj;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 曲線型
 * @author fujiwara
 */
public class GmlPolygon implements Data, Serializable {
	
	private int n;
	private int[] x;
	private int[] y;

	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			this.x = curve.getArrayX();
			this.y = curve.getArrayY();
			this.n = curve.getArrayLength();
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
}
