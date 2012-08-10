package map.ksj.handler;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.ksj.AdministrativeArea;
import map.ksj.Data;
import map.ksj.GmlCurve;
import map.ksj.GmlPolygon;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 国土数値情報JPGIS2.1(GML)形式の行政界(面)を読み込むための
 * DefaultHandlerの継承クラス
 * @author fujiwara
 */
public class HandlerN03 extends DefaultHandler {

	private static final String HEADER = "XMLDocument";
	
	private LinkedList<String> list;
	
	private Map<String, Class<?>> classMap;

	private Map<String, Data> dataMap;
	
	private Data data;

	private List<GmlCurve> curveList;

	private StringBuilder buf;

	private Set<String> charactersTarget;

	public HandlerN03() throws ClassNotFoundException {

		this.list = new LinkedList<String>();

		this.dataMap = new HashMap<String, Data>();
		
		this.classMap = new HashMap<String, Class<?>>();
		this.classMap.put("gml:Curve", GmlCurve.class);
		this.classMap.put("ksj:AdministrativeArea", AdministrativeArea.class);
		this.classMap.put("gml:Surface", GmlPolygon.class);

		this.charactersTarget = new HashSet<String>();
		this.charactersTarget.add("gml:posList");
		this.charactersTarget.add("ksj:prn");
		this.charactersTarget.add("ksj:sun");
		this.charactersTarget.add("ksj:con");
		this.charactersTarget.add("ksj:cn2");
		this.charactersTarget.add("ksj:aac");

		this.buf = new StringBuilder();
	}
	
	public GmlCurve[] getCurves() {
		return this.curveList.toArray(new GmlCurve[]{});
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String tag = this.list.getFirst();
		if (this.charactersTarget.contains(tag)) {
			this.buf.append(new String(ch, start, length));
		}
	}
	
	public void fixCharacters() {
		if (this.buf.length() > 0) {
			String tag = this.list.peek();
			if ("gml:posList".equals(tag)) {
				String string = this.buf.toString().replaceFirst("^\\s+", "");
				String[] param = string.split("\\s+");
				List<Point> points = new ArrayList<Point>();
				for (int i = 0; i + 1 < param.length; i += 2) {
					int lat = parseFixInt(param[i]);
					int lng = parseFixInt(param[i + 1]);
					points.add(new Point(lat, lng));
				}
				this.data.link(tag, points.toArray(new Point[]{}));
			} else if (this.data != null) {
				this.data.link(tag, this.buf.toString());
			}
			this.buf.setLength(0);
		}
	}

	private int parseFixInt(String str) {
		
		String[] param = str.split("\\.");
		int ret = Integer.parseInt(param[0]) * 3600000;
		if (param.length == 2) {
			int mul = 3600000;
			int div = 1;
			int size = param[1].length() > 5 ? 5 : param[1].length();
			for (int i = 0; i < size; i++) {
				mul /= 10;
			}
			if (param[1].length() > 5) {
				for (int i = param[1].length() - 6; i >= 0; i--) {
					div *= 10;
				}
			}
			ret += Integer.parseInt(param[1]) * mul / div;
		}

		int val = (int) (Double.parseDouble(str) * 3600000 + 0.5);
		assert(val + 1 >= ret || val <= ret + 1);
		
		return ret;
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		if (this.buf.length() > 0) {
			System.out.println("Start Element");
			this.fixCharacters();
		}

		
		Class<?> c = this.classMap.get(qName);
		if (c != null) {
			try {
				this.data = (Data) c.newInstance();
				String key = attr.getValue("gml:id");
				this.dataMap.put(key, this.data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (this.list.size() > 1) {
			if (this.classMap.containsKey(this.list.peek()) || this.data != null) {
				if (qName.equals("gml:curveMember") || qName.equals("ksj:are")) {
					String href = attr.getValue("xlink:href");
					assert("#".equals(href.substring(0, 1)));
					Data data = this.dataMap.remove(href.substring(1));
					assert(data != null) : href;
					this.data.link(qName, data);
				}
			}
		}

		this.list.push(qName);
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		this.fixCharacters();
		
		String last = this.list.pop();
		assert(last.equals(qName)) : last + " : "+ qName;
		
		if (this.classMap.containsKey(qName)) {
			this.data = null;
		}
	}
	
	@Override
	public void startDocument() throws SAXException {
		if (this.buf.length() > 0) {
			System.out.println("Start Document");
			this.fixCharacters();
		}
		if (!this.list.isEmpty()) {
			throw new IllegalAccessError();
		}
		this.list.add(HEADER);
	}
	

	@Override
	public void endDocument() throws SAXException {
		if (!HEADER.equals(this.list.pop()) || !this.list.isEmpty()) {
			throw new IllegalAccessError();
		}
		
		assert(checkData());
		
	}
	
	public AdministrativeArea[] getAdministrativeAreaList() {
		List<AdministrativeArea> list = new ArrayList<AdministrativeArea>();
		for (Data data : this.dataMap.values()) {
			list.add((AdministrativeArea) data);
		}
		assert(!list.isEmpty());
		return list.toArray(new AdministrativeArea[]{});
	}

	public boolean checkData() {
		boolean ret = true;
		for (Data data : this.dataMap.values()) {
			if (!(data instanceof AdministrativeArea)) {
				ret = false;
			}
		}
		return ret;
	}

}
