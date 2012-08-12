package map.ksj;

public class BusCollection {

	private final int code;
	private final BusStop[] stops;
	private final BusRoute[] routes;
	
	public BusCollection(int code, BusStop[] stops, BusRoute[] routes) {
		assert(stops != null && routes != null);

		this.code = code;
		this.stops = stops;
		this.routes = routes;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public BusStop[] getBusStops() {
		return this.stops;
	}
	
	public BusRoute[] getBusRoute() {
		return this.routes;
	}
}
