package map.ksj;

import java.awt.Graphics2D;

public interface RailroadSection {

	public RailroadInfo getInfo();
		
	public void setInfo(RailroadInfo info);
		
	public GmlCurve getCurve();

	public void setCurve(GmlCurve curve);

	public void draw(Graphics2D g);
	
}
