package map.ksj;

import java.io.Serializable;

public class RailwayCollection implements Serializable {

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
