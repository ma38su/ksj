package database;

public class FixedPoint {

	public static final int SHIFT = 3600000;
	public static final double SHIFT_DOUBLE = 3600000.0;

	public static double parseDouble(int fixedInt) {
		return fixedInt / SHIFT_DOUBLE;
	}

	public static int parseFixedPoint(String str) {
		
		String[] param = str.split("\\.");
		int ret = Integer.parseInt(param[0]) * SHIFT;
		if (param.length == 2) {
			int mul = SHIFT;
			int div = 1;
			int size = param[1].length() > 5 ? 5 : param[1].length();
			for (int i = 0; i < size; i++) {
				mul /= 10;
			}
			if (param[1].length() > 5) {
				for (int i = param[1].length() - 6; i >= 0; i--) {
					div *= 10;
				}
			}
			ret += Integer.parseInt(param[1]) * mul / div;
		}

		int val = (int) (Double.parseDouble(str) * SHIFT + 0.5);
		assert(val + 1 >= ret || val <= ret + 1) : str + ": "+ val + " <=> "+ ret;
		
		return ret;
	}
	
}
