package map.ksj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RailwayDataset {

	private final RailroadLine[] otherLines;
	private final RailroadLine[] jrLines;
	private final Station[] stations;
	
	public RailwayDataset(Station[] stations, List<RailroadLine> lines) {
		this.stations = stations;
		List<RailroadLine> others = new ArrayList<RailroadLine>();
		List<RailroadLine> jrs = new ArrayList<RailroadLine>();
		for (RailroadLine line : lines) {
			if (line.getInfo().getRailwayType() == RailroadInfo.RAIL_JR) {
				jrs.add(line);
				System.out.println(line.getCurves().length);
				System.out.println(line.getInfo());
			} else {
				others.add(line);
			}
		}
		this.jrLines = jrs.toArray(new RailroadLine[jrs.size()]);
		this.otherLines = others.toArray(new RailroadLine[others.size()]);
	}
	
	public RailroadSection[] sections;
	public RailwayDataset(Station[] stations, RailroadSection[] sections) {
		this.stations = stations;
		
		this.sections = sections;

		Map<RailroadInfo, List<GmlCurve>> map = new HashMap<RailroadInfo, List<GmlCurve>>();
		for (RailroadSection section : sections) {
			RailroadInfo info = section.getInfo();
			List<GmlCurve> list = map.get(info);
			if (list == null) {
				list = new ArrayList<GmlCurve>();
				map.put(info, list);
			}
			list.add(section.getCurve());
		}

		List<RailroadLine> others = new ArrayList<RailroadLine>();
		List<RailroadLine> jrs = new ArrayList<RailroadLine>();
		for (Map.Entry<RailroadInfo, List<GmlCurve>> entry : map.entrySet()) {
			RailroadInfo info = entry.getKey();
			List<GmlCurve> list = entry.getValue();
			if (info.getRailwayType() == RailroadInfo.RAIL_JR) {
				RailroadLine line = new RailroadLine(info, GmlCurve.join(list));
				jrs.add(line);
			} else {
				RailroadLine line = new RailroadLine(info, list.toArray(new GmlCurve[list.size()]));
				others.add(line);
			}
		}
		this.jrLines = jrs.toArray(new RailroadLine[jrs.size()]);
		this.otherLines = others.toArray(new RailroadLine[others.size()]);
	}
	
	public Station[] getStations() {
		return this.stations;
	}
	
	public RailroadLine[] getOtherLines() {
		return this.otherLines;
	}
	
	public RailroadLine[] getJrLines() {
		return this.jrLines;
	}
	
}
