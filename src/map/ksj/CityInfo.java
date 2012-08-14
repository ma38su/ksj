package map.ksj;

import java.io.Serializable;

/**
 * 行政区画の情報
 * 
 * @author fujiwara
 *
 */
public class CityInfo implements Serializable {
	
	/**
	 * 都道府県名
	 */
	public static final String[] PREF_NAME = new String[]{
			"",
			"北海道",
			"青森県",
			"岩手県",
			"宮城県",
			"秋田県",
			"山形県",
			"福島県",
			"茨城県",
			"栃木県",
			"群馬県",
			"埼玉県",
			"千葉県",
			"東京都",
			"神奈川県",
			"新潟県",
			"富山県",
			"石川県",
			"福井県",
			"山梨県",
			"長野県",
			"岐阜県",
			"静岡県",
			"愛知県",
			"三重県",
			"滋賀県",
			"京都府",
			"大阪府",
			"兵庫県",
			"奈良県",
			"和歌山県",
			"鳥取県",
			"島根県",
			"岡山県",
			"広島県",
			"山口県",
			"徳島県",
			"香川県",
			"愛媛県",
			"高知県",
			"福岡県",
			"佐賀県",
			"長崎県",
			"熊本県",
			"大分県",
			"宮崎県",
			"鹿児島県",
			"沖縄県"
		};

	/**
	 * 都道府県コード
	 */
	private int code;
	
	private String sun;
	private String con;
	private String cn2;
	
	/**
	 * 行政区画コード
	 */
	private int aac;

	public CityInfo() {
	}
	
	public CityInfo(int code, int aac, String sun, String con, String cn2) {
		this.code = code;
		this.aac = aac;
		this.sun = sun;
		this.con = con;
		this.cn2 = cn2;
	}
	
	public String getPrefectureName() {
		return PREF_NAME[this.code];
	}
	
	public int getCode() {
		return this.code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getSun() {
		return sun;
	}

	public void setPrn(String prn) {
		for (int i = 0; i < PREF_NAME.length; i++) {
			if (prn.equals(PREF_NAME[i])) {
				this.code = i;
				return;
			}
		}
		throw new IllegalArgumentException(prn);
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
		if (obj instanceof CityInfo) {
			CityInfo info = (CityInfo) obj;
			ret = this.aac == info.aac && this.code == info.code && 
					((this.sun == null && info.sun == null) || this.sun.equals(info.sun)) &&
					((this.con == null && info.con == null) || this.con.equals(info.con)) &&
					((this.cn2 == null && info.cn2 == null) || this.cn2.equals(info.cn2));
//			assert(((this.aac == info.aac) && ret) || ((this.aac != info.aac) && !ret)) : this.toString() +" <=> "+ info.toString();
		}
		return ret;
	}

	@Override
	public String toString() {
		return String.format("%s(%d) / %s / %s / %s : %05d (%02d)", 
				PREF_NAME[this.code], this.sun, this.con, this.cn2, this.aac, this.code);
	}
}
