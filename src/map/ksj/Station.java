package map.ksj;

import java.io.Serializable;

public class Station implements Data, Serializable {


	private RailroadInfo info;
	
	private String name;
	private GmlCurve curve;

	public Station() {
		this.info = new RailroadInfo();
	}
	
	public Station(String name, RailroadInfo info, GmlCurve curve) {
		this.name = name;
		this.info = info;
		this.curve = curve;
	}
	
	public RailroadInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(RailroadInfo info) {
		this.info = info;
	}
	
	public String getName() {
		return this.name;
	}
	
	public GmlCurve getCurve() {
		return this.curve;
	}
	
	public void setCurve(GmlCurve curve) {
		this.curve = curve;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:lin".equals(tag)) {
				this.info.setLine(string);
			} else if ("ksj:opc".equals(tag)) {
				this.info.setCompany(string);
			} else if ("ksj:stn".equals(tag)) {
				this.name = string;
			} else if ("ksj:int".equals(tag)) {
				int instituteType = Integer.parseInt(string);
				this.info.setInstituteType(instituteType);
			} else if ("ksj:rac".equals(tag)) {
				int railwayType = "".equals(string) ? -1 : Integer.parseInt(string);
				this.info.setRailwayType(railwayType);
			}
		} else if (obj instanceof RailroadSection) {
			RailroadSection section = (RailroadSection) obj;
			if (this.curve == null) {
				this.curve = section.getCurve();
			} else {
				assert(this.curve.equals(section.getCurve()));
				this.info = section.getInfo();
			}
		} else if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			if (this.curve == null) {
				this.curve = curve;
			} else {
				assert(this.curve.equals(curve));
			}
		}
	}

}
