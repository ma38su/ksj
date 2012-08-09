package model;

public class BusRoute extends Data {

	private Curve curve;

	private int bsc;
	private String city;
	private double rpd;
	private double rps;
	private double rph;
	private String line;

	public Curve getCurve() {
		return this.curve;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public String getLine() {
		return this.line;
	}
	
	@Override
	public void send(String tag, Object obj) {
		if (obj instanceof Curve) {
			if (tag.equals("ksj:brt")) {
				this.curve = (Curve) obj;
			}
		} else if (obj instanceof String) {
			String string = (String) obj;
			if (tag.equals("ksj:bsc")) {
				this.bsc = Integer.parseInt(string);
			} else if (tag.equals("ksj:boc")) {
				this.city = string;
			} else if (tag.equals("ksj:bln")) {
				this.line = string;
			} else if (tag.equals("ksj:rpd")) {
				this.rpd = Double.parseDouble(string);
			} else if (tag.equals("ksj:rps")) {
				this.rps = Double.parseDouble(string);
			} else if (tag.equals("ksj:rph")) {
				this.rph = Double.parseDouble(string);
			}
		}
	}
	
}
