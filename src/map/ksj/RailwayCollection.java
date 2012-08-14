package map.ksj;

public class RailwayCollection {

	private final RailroadSectionData[] sections;
	private final Station[] stations;
	
	public RailwayCollection(Station[] stations, RailroadSectionData[] sections) {
		this.stations = stations;
		this.sections = sections;
	}
	
	public Station[] getStations() {
		return this.stations;
	}
	
	public RailroadSectionData[] getRailroadSection() {
		return this.sections;
	}
}
