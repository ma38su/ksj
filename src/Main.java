

import java.io.File;

import database.KsjDataManager;

public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(
				".data"+File.separatorChar+"org",
				".data"+File.separatorChar+"csv",
				".data"+File.separatorChar+"serialize"
		);

		mgr.getBusCollections();

		for (int code = 1; code <= 47; ++code) {
			
			// N03
			// mgr.getAdministrativeAreaArray(code);


			// P11
			// BusStop[] stops = mgr.getBusStops(code);
		}

//		for (BusRouteInfomation info : infoSet) {
//			System.out.println(info);
//		}

		// N02
		mgr.getRailway();
	}
}
