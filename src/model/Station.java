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
			if (tag.equals("ksj:lin")) {
				this.line = string;
			} else if (tag.equals("ksj:opc")) {
				this.company = string;
			} else if (tag.equals("ksj:stn")) {
				this.name = string;
			} else if (tag.equals("ksj:int")) {
				this.instituteType = Integer.parseInt(string);
			} else if (tag.equals("ksj:rar")) {
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
