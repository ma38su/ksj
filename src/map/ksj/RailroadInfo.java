package map.ksj;

import java.io.Serializable;

public class RailroadInfo implements Serializable {

	private String line;
	private String company;
	private int railwayType;
	private int instituteType;
	
	public RailroadInfo() {
	}

	public RailroadInfo(int railwayType, int instituteType, String line, String company) {
		this.railwayType = railwayType;
		this.instituteType = instituteType;
		this.line = line;
		this.company = company;
	}


	public String getLine() {
		return this.line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	
	public String getCompany() {
		return this.company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}

	public int getRailwayType() {
		return this.railwayType;
	}
	
	public void setRailwayType(int railwayType) {
		this.railwayType = railwayType;
	}
	
	public int getInstituteType() {
		return this.instituteType;
	}
	
	public void setInstituteType(int institudeType) {
		this.instituteType = institudeType;
	}

	@Override
	public int hashCode() {
		return line.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof RailroadInfo) {
			RailroadInfo info = (RailroadInfo) obj;
			ret = this.line.equals(info.line) && this.company.equals(info.company) &&
					this.railwayType == info.railwayType && this.instituteType == info.instituteType;
		}
		return ret;
	}

}
