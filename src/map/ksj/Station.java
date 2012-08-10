package map.ksj;

import java.io.Serializable;

public class Station implements Data, Serializable {

	private String line;
	private String company;
	private String name;
	private int railwayType;
	private int instituteType;
	private GmlCurve curve;
	
	public String getLine() {
		return this.line;
	}
	
	public String getCompany() {
		return this.company;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getRailwayType() {
		return this.railwayType;
	}
	
	public int getInstitudeType() {
		return this.instituteType;
	}
	
	public GmlCurve getCurve() {
		return this.curve;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:lin".equals(tag)) {
				this.line = string;
			} else if ("ksj:opc".equals(tag)) {
				this.company = string;
			} else if ("ksj:stn".equals(tag)) {
				this.name = string;
			} else if ("ksj:int".equals(tag)) {
				this.instituteType = Integer.parseInt(string);
			} else if ("ksj:rar".equals(tag)) {
				this.railwayType = Integer.parseInt(string);
			}
		} else if (obj instanceof RailroadSection) {
			RailroadSection section = (RailroadSection) obj;
			if (this.curve == null) {
				this.curve = section.getCurve();
			} else {
				assert(this.curve.equals(section.getCurve()));
				this.company = section.getCompany();
				this.line = section.getLine();
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
