package map.ksj;

public class RailwayCollections {

	private final RailroadSection[] sections;
	private final Station[] stations;
	
	public RailwayCollections(Station[] stations, RailroadSection[] sections) {
		this.stations = stations;
		this.sections = sections;
	}
	
	public Station[] getStations() {
		return this.stations;
	}
	
	public RailroadSection[] getRailroadSection() {
		return this.sections;
	}
	
}
