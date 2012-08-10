package model;

public interface Data {
	
	/**
	 * GMLタグに対応したデータを紐づける
	 * @param tag GMLタグ
	 * @param obj 対応するオブジェクト
	 */
	public void link(String tag, Object obj);
}
