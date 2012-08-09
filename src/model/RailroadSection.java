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
			if (tag.equals("ksj:loc")) {
				this.curve = (Curve) obj;
			}
		} else if (obj instanceof String) {
			String string = (String) obj;
			if (tag.equals("ksj:lin")) {
				this.line = string;
			} else if (tag.equals("ksj:opc")) {
				this.company = string;
			}
		}
	}
	
}
