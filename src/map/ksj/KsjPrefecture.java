package map.ksj;

public class KsjPrefecture {
	
	private int code;
	private Area[] area;
	private BusCollection bus;

	public KsjPrefecture(int code, Area[] area, BusCollection bus) {
		this.code = code;
		this.area = area;
		this.bus = bus;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public Area[] getAreas() {
		return this.area;
	}
	
	public BusCollection getBusCollection() {
		return this.bus;
	}
}
