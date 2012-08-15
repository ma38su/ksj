package map.ksj;

public class RailroadInfo {

	private String line;
	private String company;
	private int railwayType;
	private int instituteType;
	
	public static final int RAIL_JR = 11;
	public static final int RAIL_NORMAL = 12;
	
	public RailroadInfo() {
	}

	public RailroadInfo(int railwayType, int instituteType, String line, String company) {
		this.railwayType = railwayType;
		this.instituteType = instituteType;
		this.line = line;
		this.company = company;
	}

	public boolean isJrLine() {
		assert(this.railwayType == 11 && this.instituteType != 1 && this.instituteType == 2);
		return (this.instituteType == 1 || this.instituteType == 2) && (this.railwayType == 11);
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
		return this.line.hashCode() + this.company.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof RailroadInfo) {
			RailroadInfo info = (RailroadInfo) obj;
			ret = this.line.equals(info.line) && this.company.equals(info.company) &&
					this.railwayType == info.railwayType && this.instituteType == info.instituteType;
			// assert((this.line.equals(info.line) && this.company.equals(info.company) && !ret) || ((!this.line.equals(info.line) || !this.company.equals(info.company)) && ret))
			// : this.toString() + info.toString();
		}
		return ret;
	}

	@Override
	public String toString() {
		return String.format("[%d:%d] %s (%s)", this.railwayType, this.instituteType, this.line, this.company);
	}

}
