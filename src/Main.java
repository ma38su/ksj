

import java.io.File;

import map.ksj.BusStop;
import database.KsjDataManager;

public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(
				".data"+File.separatorChar+"org",
				".data"+File.separatorChar+"csv",
				".data"+File.separatorChar+"serialize"
		);

		BusStop[][] stops = mgr.getBusStops();

		for (int code = 1; code <= 47; ++code) {
			
			// N03
			// mgr.getAdministrativeAreaArray(code);

			// N07
			// mgr.getBusRouteArray(code);

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
