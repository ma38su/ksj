
import database.KsjDataManager;

public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(".data");

		System.out.println("<Parse Ksj Data>");
		// N02
		mgr.getRailway();

		for (int code = 47; code > 0; --code) {
			
			// N03
			mgr.getAdministrativeAreaArray(code);

			// N07
			mgr.getBusRouteArray(code);

			// P11
			mgr.getBusStopArray(code);
		}
	}
}
