import java.io.File;

import map.KsjDataManager;

public class Main {
	public static void main(String[] args) {
		String dir = args.length > 0 ? args[0] : ".data";

		KsjDataManager mgr = new KsjDataManager(
				dir + File.separatorChar + "org",
				dir + File.separatorChar + "csv"
		);
		
		System.out.println(dir);
		
		mgr.getRailwayDataset();
		mgr.getBusDataset();
		mgr.getAreaDataset();
	}
}
