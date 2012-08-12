package map.ksj;

import java.io.Serializable;

public class AreaInfo implements Serializable {
	
	/**
	 * 都道府県名
	 */
	private String prn;
	
	private String sun;
	private String con;
	private String cn2;
	
	/**
	 * 行政界コード
	 */
	private int aac;

	public AreaInfo() {
	}
	
	public AreaInfo(int aac, String prn, String sun, String con, String cn2) {
		this.aac = aac;
		this.prn = prn;
		this.sun = sun;
		this.con = con;
		this.cn2 = cn2;
	}
	
	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public String getSun() {
		return sun;
	}

	public void setSun(String sun) {
		this.sun = sun;
	}

	public String getCon() {
		return con;
	}

	public void setCon(String con) {
		this.con = con;
	}

	public String getCn2() {
		return cn2;
	}

	public void setCn2(String cn2) {
		this.cn2 = cn2;
	}

	public int getAac() {
		return aac;
	}

	public void setAac(int aac) {
		this.aac = aac;
	}

	@Override
	public int hashCode() {
		return this.aac;
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof AreaInfo) {
			AreaInfo info = (AreaInfo) obj;
			ret = this.aac == info.aac && 
					((this.prn == null && info.prn == null) || this.prn.equals(info.prn)) && 
					((this.sun == null && info.sun == null) || this.sun.equals(info.sun)) &&
					((this.con == null && info.con == null) || this.con.equals(info.con)) &&
					((this.cn2 == null && info.cn2 == null) || this.cn2.equals(info.cn2));
//			assert(((this.aac == info.aac) && ret) || ((this.aac != info.aac) && !ret)) : this.toString() +" <=> "+ info.toString();
		}
		return ret;
	}

	@Override
	public String toString() {
		return this.prn +" / "+ this.sun +" / "+ this.con +" / "+ this.cn2;
	}
}
