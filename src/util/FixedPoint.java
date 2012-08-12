package util;

public class FixedPoint {

	public static final int SHIFT = 3600000;
	public static final double SHIFT_DOUBLE = 3600000.0;

	public static double parseDouble(int fixedInt) {
		return fixedInt / SHIFT_DOUBLE;
	}

	public static void main(String[] args) {
		parseFixedPoint("141.91969800");
	}

	public static int parseFixedPoint(String str) {
		
		String[] param = str.split("\\.");
		int ret = Integer.parseInt(param[0]) * SHIFT;
		if (param.length == 2) {
			long mul = SHIFT;
			int div = 1;
			int size = param[1].length() > 5 ? 5 : param[1].length();
			for (int i = 0; i < size; i++) {
				mul /= 10;
			}
			assert(mul > 0);
			if (param[1].length() > 5) {
				for (int i = param[1].length() - 6; i >= 0; i--) {
					div *= 10;
				}
			}
			ret += Long.parseLong(param[1]) * mul / div;
		}

		int val = (int) (Double.parseDouble(str) * SHIFT + 0.5);
		
		assert(Math.abs(val - ret) < 2) : str + ": "+ val + " <=> "+ ret;
		
		return val;
	}
	
}
