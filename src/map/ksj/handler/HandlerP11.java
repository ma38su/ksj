package map.ksj.handler;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.ksj.BusRouteInfomation;
import map.ksj.BusStop;
import map.ksj.Data;
import map.ksj.GmlPoint;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 国土数値情報JPGIS2.1(GML)形式のバス停留所(点)を読み込むための
 * DefaultHandlerの継承クラス
 * @author fujiwara
 */
public class HandlerP11 extends DefaultHandler {

	private static final String HEADER = "XMLDocument";
	
	private LinkedList<String> list;
	
	private Map<String, Class<?>> classMap;

	private Map<String, Data> dataMap;
	
	private LinkedList<Data> dataStack;

	private StringBuilder buf;

	private Set<String> charactersTarget;

	public HandlerP11() throws ClassNotFoundException {

		this.list = new LinkedList<String>();
		
		this.dataStack = new LinkedList<Data>();

		this.dataMap = new HashMap<String, Data>();
		
		this.classMap = new HashMap<String, Class<?>>();
		this.classMap.put("gml:Point", GmlPoint.class);
		this.classMap.put("ksj:busRouteInformation", BusRouteInfomation.class);
		this.classMap.put("ksj:BusStop", BusStop.class);

		this.charactersTarget = new HashSet<String>();
		this.charactersTarget.add("gml:pos");
		this.charactersTarget.add("ksj:busStopName");
		this.charactersTarget.add("ksj:busType");
		this.charactersTarget.add("ksj:busOperationCompany");
		this.charactersTarget.add("ksj:busLineName");
		this.buf = new StringBuilder();
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
			Data data = this.dataStack.peek();
			if ("gml:pos".equals(tag)) {
				String string = this.buf.toString().replaceFirst("^\\s+", "");
				String[] param = string.split("\\s+");
				assert(param.length == 2);
				int lat = parseFixInt(param[0]);
				int lng = parseFixInt(param[1]);
				data.link(tag, new Point(lat, lng));
			} else if (data != null) {
				data.link(tag, this.buf.toString());
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
				Data data = (Data) c.newInstance();
				String key = attr.getValue("gml:id");
				if (key != null) {
					this.dataMap.put(key, data);
				}
				
				Data parent = this.dataStack.peek();
				if (parent == null) {
					this.dataStack.push(data);
				} else if (!data.getClass().equals(parent.getClass())) {
					parent.link(qName, data);
					this.dataStack.push(data);
				} else {
					System.out.println("AAA");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		if (this.list.size() > 1) {
			if (this.classMap.containsKey(this.list.getFirst())) {
				if (qName.equals("ksj:position")) {
					String href = attr.getValue("xlink:href");
					assert("#".equals(href.substring(0, 1)));
					Data link = this.dataMap.remove(href.substring(1));
					assert(link != null) : href;
					Data data = this.dataStack.peek();
					data.link(qName, link);
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
			assert(this.dataStack.size() > 0);
			this.dataStack.pop();
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
	
	public boolean checkData() {
		boolean ret = true;
		for (Map.Entry<String, Data> entry : this.dataMap.entrySet()) {
			Data data = entry.getValue();
			if (!(data instanceof BusStop)) {
				System.out.println("class: "+ data.getClass() + " - "+ entry.getKey());
				ret = false;
				break;
			}
		}
		return ret;
	}

	public BusStop[] getBusStopArray() {
		List<BusStop> ret = new ArrayList<BusStop>();

		for (Data data : this.dataMap.values()) {
			assert(data instanceof BusStop) : data.getClass();
			ret.add((BusStop) data);
		}
		
		return ret.toArray(new BusStop[]{});
	}
}
