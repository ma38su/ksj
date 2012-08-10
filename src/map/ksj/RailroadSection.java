package map.ksj;

public class RailroadSection implements Data {

	private String line;
	private String company;
	private GmlCurve curve;

	public GmlCurve getCurve() {
		return this.curve;
	}
	
	public String getLine() {
		return this.line;
	}
	
	public String getCompany() {
		return this.company;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			assert("ksj:loc".equals(tag));
			this.curve = (GmlCurve) obj;
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:lin".equals(tag)) {
				this.line = string;
			} else if ("ksj:opc".equals(tag)) {
				this.company = string;
			}
		}
	}
	
}
