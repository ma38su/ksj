package map.ksj;

import java.io.Serializable;

/**
 * 都道府県の行政区画(面)
 * @author fujiwara
 */
public class AreaCollection implements Serializable {

	private int code;
	private Area[] areas;

	public AreaCollection(int code, Area[] areas) {
		this.code = code;
		this.areas = areas;
	}

	public int getCode() {
		return this.code;
	}
	
	public Area[] getAreas() {
		return this.areas;
	}
	
}
