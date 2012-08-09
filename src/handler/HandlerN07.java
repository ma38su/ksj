package handler;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.BusRoute;
import model.Curve;
import model.Data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class HandlerN07 extends DefaultHandler {

	private static final String HEADER = "XMLDocument";
	
	private LinkedList<String> list;
	
	private Map<String, Class<?>> classMap;

	private Map<String, Data> dataMap;
	
	private Data data;

	private List<Curve> curveList;

	private StringBuilder buf;

	private HashSet<String> charactersTarget;

	public HandlerN07() throws ClassNotFoundException {

		this.list = new LinkedList<String>();

		this.dataMap = new HashMap<String, Data>();
		
		this.classMap = new HashMap<String, Class<?>>();
		this.classMap.put("gml:Curve", Curve.class);
		this.classMap.put("ksj:BusRoute", BusRoute.class);
		
		this.charactersTarget = new HashSet<String>();
		this.charactersTarget.add("gml:posList");
		this.charactersTarget.add("ksj:bsc");
		this.charactersTarget.add("ksj:bln");
		this.charactersTarget.add("ksj:boc");
		this.charactersTarget.add("ksj:rpd");
		this.charactersTarget.add("ksj:rps");
		this.charactersTarget.add("ksj:rph");

		this.buf = new StringBuilder();
	}
	
	public Curve[] getCurves() {
		return this.curveList.toArray(new Curve[]{});
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
			String tag = this.list.peekFirst();
			if ("gml:posList".equals(tag)) {
				String string = this.buf.toString().replaceFirst("^\\s+", "");
				String[] param = string.split("\\s+");
				List<Point> points = new ArrayList<Point>();
				for (int i = 0; i + 1 < param.length; i += 2) {
					int lat = parseFixInt(param[i]);
					int lng = parseFixInt(param[i + 1]);
					points.add(new Point(lat, lng));
				}
				this.data.send(tag, points.toArray(new Point[]{}));
			} else if (this.data != null) {
				this.data.send(tag, this.buf.toString());
			} else {
				System.out.println("buf: "+ this.buf.toString());
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
	public void endDocument() throws SAXException {
		if (!HEADER.equals(this.list.pop()) || !this.list.isEmpty()) {
			throw new IllegalAccessError();
		}
		
		this.curveList = new ArrayList<Curve>();

		Map<Curve, Integer> idxMap = new HashMap<Curve, Integer>();
		Map<Point2D, List<Curve>> pointMap = new HashMap<Point2D, List<Curve>>();

		for (Data data : this.dataMap.values()) {
			if (data instanceof Curve) {
				Curve curve = (Curve) data;
				
				if (!idxMap.containsKey(curve)) {
					idxMap.put(curve, idxMap.size());
					curveList.add(curve);
				}

				Point p1 = curve.getFirstPoint();
				List<Curve> curves1 = pointMap.get(p1);
				if (curves1 == null) {
					curves1 = new ArrayList<Curve>();
					pointMap.put(p1, curves1);
				}
				curves1.add(curve);
				
				Point p2 = curve.getLastPoint();
				List<Curve> curves2 = pointMap.get(p2);
				if (curves2 == null) {
					curves2 = new ArrayList<Curve>();
					pointMap.put(p2, curves2);
				}
				curves2.add(curve);
			}
		}

		for (List<Curve> curves : pointMap.values()) {
			if (curves.size() >= 2) {
				for (int i = 0; i < curves.size(); i++) {
					Curve c1 = curves.get(i);
					int idx1 = idxMap.get(c1);
					for (int j = i + 1; j < curves.size(); j++) {
						Curve c2 = curves.get(j);
						int idx2 = idxMap.get(c2);

						c1.addLink(idx2);
						c2.addLink(idx1);
					}
				}
			}
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
			if (this.classMap.containsKey(this.list.getFirst())) {
				if (qName.equals("ksj:brt")) {
					String href = attr.getValue("xlink:href");
					assert("#".equals(href.substring(0, 1)));
					Data data = this.dataMap.get(href.substring(1));
					assert(data != null) : href;
					this.data.send(qName, data);
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

}
