package map.ksj;

import java.io.Serializable;

public class RailroadSection implements Data, Serializable {

	private GmlCurve curve;
	
	private RailroadInfo info;

	public RailroadSection() {
		this.info = new RailroadInfo();
	}
	
	public RailroadSection(RailroadInfo info, GmlCurve curve) {
		this.info = info;
		this.curve = curve;
	}
	
	public RailroadInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(RailroadInfo info) {
		this.info = info;
	}
	
	public GmlCurve getCurve() {
		return this.curve;
	}

	public void setCurve(GmlCurve curve) {
		this.curve = curve;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			assert("ksj:loc".equals(tag));
			this.curve = (GmlCurve) obj;
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:lin".equals(tag)) {
				this.info.setLine(string);
			} else if ("ksj:opc".equals(tag)) {
				this.info.setCompany(string);
			} else if ("ksj:int".equals(tag)) {
				int instituteType = Integer.parseInt(string);
				this.info.setInstituteType(instituteType);
			} else if ("ksj:rac".equals(tag)) {
				int railwayType = "".equals(string) ? -1 : Integer.parseInt(string);
				this.info.setRailwayType(railwayType);
			}
		}
	}
}
