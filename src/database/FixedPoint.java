package database;

public class FixedPoint {

	public static int parseFixedPoint(String str) {
		
		String[] param = str.split("\\.");
		int ret = Integer.parseInt(param[0]) * 3600000;
		if (param.length == 2) {
			int mul = 3600000;
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

		int val = (int) (Double.parseDouble(str) * 3600000 + 0.5);
		assert(val + 1 >= ret || val <= ret + 1);
		
		return ret;
	}
	
}
