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
import map.ksj.BusRoute;
import map.ksj.BusRouteInfo;
import map.ksj.BusStop;
import map.ksj.Data;
import map.ksj.GmlCurve;
import map.ksj.GmlPoint;
import map.ksj.GmlPolygon;
import map.ksj.RailroadSection;
import map.ksj.Station;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import database.FixedPoint;

public class KsjHandler extends DefaultHandler {

	private static final String HEADER = "XMLDocument";

	public KsjHandler() {
		this.linkTarget.add("gml:curveMember");
		this.linkTarget.add("ksj:position");
		this.linkTarget.add("ksj:loc");
		this.linkTarget.add("ksj:srs");
		this.linkTarget.add("ksj:are");
		this.linkTarget.add("ksj:brt");

		this.classMap.put("gml:Curve", GmlCurve.class);
		this.classMap.put("gml:Point", GmlPoint.class);
		this.classMap.put("gml:Surface", GmlPolygon.class);
		this.classMap.put("ksj:BusStop", BusStop.class);
		this.classMap.put("ksj:busRouteInformation", BusRouteInfo.class);
		this.classMap.put("ksj:BusRoute", BusRoute.class);
		this.classMap.put("ksj:RailroadSection", RailroadSection.class);
		this.classMap.put("ksj:Station", Station.class);
		this.classMap.put("ksj:AdministrativeArea", AdministrativeArea.class);

		this.charactersTarget.add("gml:posList");
		this.charactersTarget.add("ksj:opc");
		this.charactersTarget.add("ksj:lin");
		this.charactersTarget.add("ksj:stn");
		this.charactersTarget.add("ksj:int");
		this.charactersTarget.add("ksj:rar");

		this.charactersTarget.add("gml:pos");
		this.charactersTarget.add("ksj:busStopName");
		this.charactersTarget.add("ksj:busType");
		this.charactersTarget.add("ksj:busOperationCompany");
		this.charactersTarget.add("ksj:busLineName");

		this.charactersTarget.add("ksj:bsc");
		this.charactersTarget.add("ksj:bln");
		this.charactersTarget.add("ksj:boc");
		this.charactersTarget.add("ksj:rpd");
		this.charactersTarget.add("ksj:rps");
		this.charactersTarget.add("ksj:rph");
		
		this.charactersTarget.add("ksj:prn");
		this.charactersTarget.add("ksj:sun");
		this.charactersTarget.add("ksj:con");
		this.charactersTarget.add("ksj:cn2");
		this.charactersTarget.add("ksj:aac");
	}
	
	private final StringBuilder buf = new StringBuilder();
	private final LinkedList<String> tagStack = new LinkedList<String>();
	private final Set<String> charactersTarget = new HashSet<String>();
	private final LinkedList<Data> dataStack = new LinkedList<Data>();

	private final Set<String> linkTarget = new HashSet<String>();

	private final Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();

	private final Map<String, Data> dataMap = new HashMap<String, Data>();

	public Map<String, Data> getDataMap() {
		return this.dataMap;
	}
	
	public void addTargetCharacters(String target) {
		this.charactersTarget.add(target);
	}
	
	public void addTargetClass(String name, Class<?> c) {
		this.classMap.put(name, c);
	}
	
	@Override
	public void startDocument() {
		if (this.buf.length() > 0 || !this.tagStack.isEmpty()) {
			throw new IllegalStateException();
		}
		this.tagStack.add(HEADER);
	}

	@Override
	public void endDocument() {
		if (!HEADER.equals(this.tagStack.pop()) || !this.tagStack.isEmpty()) {
			throw new IllegalAccessError();
		}
		assert(checkData()) : this.getClass();
		
		this.postProcessing();
	}

	protected boolean checkData() {
		return true;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (this.buf.length() > 0) {
			throw new IllegalStateException();
		}

		Class<?> c = this.classMap.get(qName);
		if (c != null) {
			try {
				Data data = (Data) c.newInstance();
				String key = attributes.getValue("gml:id");
				if (key != null) {
					this.dataMap .put(key, data);
				}
				
				Data parent = this.dataStack.peek();
				if (parent == null) {
					this.dataStack.push(data);
				} else if (!data.getClass().equals(parent.getClass())) {
					parent.link(qName, data);
					this.dataStack.push(data);
				} else {
					throw new IllegalStateException();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		if (this.dataStack.size() > 0) {
			if (this.linkTarget.contains(qName)) {
				String href = attributes.getValue("xlink:href");
				assert("#".equals(href.substring(0, 1)));
				Data link = this.dataMap.remove(href.substring(1));
				assert(link != null) : href;
				Data data = this.dataStack.peek();
				data.link(qName, link);
			}
		}

		this.tagStack.push(qName);
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		this.fixCharacters();
		
		String last = this.tagStack.pop();
		assert(last.equals(qName)) : last + " : "+ qName;
		
		if (this.classMap.containsKey(qName)) {
			assert(this.dataStack.size() > 0);
			this.dataStack.pop();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		String tag = this.tagStack.getFirst();
		if (this.charactersTarget .contains(tag)) {
			this.buf.append(new String(ch, start, length));
		}
	}

	public void fixCharacters() {
		if (this.buf.length() > 0) {
			String tag = this.tagStack.peek();
			Data data = this.dataStack .peek();
			if ("gml:posList".equals(tag)) {
				String string = this.buf.toString().replaceFirst("^\\s+", "");
				String[] param = string.split("\\s+");
				List<Point> points = new ArrayList<Point>();
				for (int i = 0; i + 1 < param.length; i += 2) {
					int lat = FixedPoint.parseFixedPoint(param[i]);
					int lng = FixedPoint.parseFixedPoint(param[i + 1]);
					points.add(new Point(lng, lat));
				}
				data.link(tag, points.toArray(new Point[]{}));
			} else if ("gml:pos".equals(tag)) {
				String string = this.buf.toString().replaceFirst("^\\s+", "");
				String[] param = string.split("\\s+");
				assert(param.length == 2);
				int lat = FixedPoint.parseFixedPoint(param[0]);
				int lng = FixedPoint.parseFixedPoint(param[1]);
				data.link(tag, new Point(lng, lat));
			} else if (data != null) {
				data.link(tag, this.buf.toString());
			}
			this.buf.setLength(0);
		}
	}
	
	protected void postProcessing() {
	}
	
}
