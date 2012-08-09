package model;


public class Station extends Data {

	private String line;
	private String company;
	private String name;
	private int railwayType;
	private int instituteType;
	private Data data;
	
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
	
	@Override
	public void send(String tag, Object obj) {
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
			if (this.data == null) {
				this.data = section;
			} else if (this.data instanceof Curve){
				assert(this.data.equals(section.getCurve()));
				this.data = section;
			} else {
				throw new IllegalArgumentException();
			}
		} else if (obj instanceof Curve) {
			Curve curve = (Curve) obj;
			if (this.data == null) {
				this.data = curve;
			} else if (this.data instanceof RailroadSection) {
				RailroadSection section = (RailroadSection) this.data;
				assert(curve.equals(section.getCurve()));
			} else {
				System.out.println(this.data.getClass().getName());
				throw new IllegalArgumentException();
			}
		}
	}

}
