package model;

public class RailroadSection extends Data {

	private String line;
	private String company;
	private Curve curve;

	public Curve getCurve() {
		return this.curve;
	}
	
	public String getLine() {
		return this.line;
	}
	
	public String getCompany() {
		return this.company;
	}
	
	@Override
	public void send(String tag, Object obj) {
		if (obj instanceof Curve) {
			if ("ksj:loc".equals(tag)) {
				this.curve = (Curve) obj;
			}
		} else if (obj instanceof String) {
			String string = (String) obj;
			System.out.println(tag + ": "+ string);
			if ("ksj:lin".equals(tag)) {
				this.line = string;
			} else if ("ksj:opc".equals(tag)) {
				this.company = string;
			}
		}
	}
	
}
