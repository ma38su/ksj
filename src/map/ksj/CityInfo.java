package map.ksj;

/**
 * 行政区画の情報
 * 
 * @author fujiwara
 *
 */
public class CityInfo {
	
	/**
	 * 都道府県名
	 */
	public static final String[] PREF_NAME = new String[]{
			"",			//  0
			"北海道",	//  1
			"青森県",	//  2
			"岩手県",	//  3
			"宮城県",	//  4
			"秋田県",	//  5
			"山形県",	//  6
			"福島県",	//  7
			"茨城県",	//  8
			"栃木県",	//  9
			"群馬県",	// 10
			"埼玉県",	// 11
			"千葉県",	// 12
			"東京都",	// 13
			"神奈川県",	// 14
			"新潟県",	// 15
			"富山県",	// 16
			"石川県",	// 17
			"福井県",	// 18
			"山梨県",	// 19
			"長野県",	// 20
			"岐阜県",	// 21
			"静岡県",	// 22
			"愛知県",	// 23
			"三重県",	// 24
			"滋賀県",	// 25
			"京都府",	// 26
			"大阪府",	// 27
			"兵庫県",	// 28
			"奈良県",	// 29
			"和歌山県",	// 30
			"鳥取県",	// 31
			"島根県",	// 32
			"岡山県",	// 33
			"広島県",	// 34
			"山口県",	// 35
			"徳島県",	// 36
			"香川県",	// 37
			"愛媛県",	// 38
			"高知県",	// 39
			"福岡県",	// 40
			"佐賀県",	// 41
			"長崎県",	// 42
			"熊本県",	// 43
			"大分県",	// 44
			"宮崎県",	// 45
			"鹿児島県",	// 46
			"沖縄県"		// 47
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
