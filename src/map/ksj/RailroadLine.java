package map.ksj;

public class RailroadLine {
	private RailroadInfo info;
	private GmlCurve[] curves;

	public RailroadLine(RailroadInfo info, GmlCurve[] curves) {
		this.info = info;
		this.curves = curves;
	}

	public RailroadInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(RailroadInfo info) {
		this.info = info;
	}
	
	public GmlCurve[] getCurves() {
		return this.curves;
	}
}
