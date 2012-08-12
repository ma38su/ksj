package database;

import java.awt.Point;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import map.ksj.Area;
import map.ksj.AreaCollection;
import map.ksj.AreaInfo;
import map.ksj.BusCollection;
import map.ksj.BusRoute;
import map.ksj.BusRouteInfo;
import map.ksj.BusStop;
import map.ksj.GmlCurve;
import map.ksj.GmlPolygon;
import map.ksj.RailroadInfo;
import map.ksj.RailroadSection;
import map.ksj.RailwayCollection;
import map.ksj.Station;
import map.ksj.handler.HandlerN02;
import map.ksj.handler.HandlerN03;
import map.ksj.handler.HandlerN07;
import map.ksj.handler.HandlerP11;

/**
 * 数値地図のデータ管理
 * 
 * @author ma38su
 */
public class KsjDataManager {

	/**
	 * 鉄道(線)のコード
	 */
	private static final int TYPE_RAILWAY = 2;

	/**
	 * 行政界(面)のコード
	 */
	private static final int TYPE_ADMINISTRATIVEAREA = 3;

	/**
	 * バスルート(線)のコード
	 */
	private static final int TYPE_BUS_ROUTE = 7;

	/**
	 * バス停留所(点)のコード
	 */
	private static final int TYPE_BUS_STOP = 11;

	/**
	 * ファイルの文字コード
	 */
	private static final String CHARSET = "MS932";

	/**
	 * 鉄道の駅データのファイル名
	 */
	private static final String CSV_RAIL = "all"+ File.separatorChar+"railway" + File.separatorChar + "rail.csv";

	/**
	 * 鉄道の情報データのファイル名
	 */
	private static final String CSV_RAIL_INFO = "all"+ File.separatorChar+"railway" + File.separatorChar + "rail_info.csv";

	/**
	 * 鉄道の曲線データのファイル名
	 */
	private static final String CSV_RAIL_CURVE = "all"+ File.separatorChar+"railway" + File.separatorChar + "rail_curve.csv";

	/**
	 * バス停のファイル名
	 */
	private static final String CSV_BUS_STOP_FORMAT = "bus" + File.separatorChar
			+ "bus_stop_%02d.csv";

	/**
	 * バスルート情報のファイル名
	 */
	private static final String CSV_BUS_ROUTE_INFO_FORMAT =
			"bus" + File.separatorChar + "bus_route_info_%02d.csv";

	/**
	 * バスルートのファイル名
	 */
	private static final String CSV_BUS_ROUTE_FORMAT = 
			"bus" + File.separatorChar + "bus_route_%02d.csv";

	/**
	 * バスルートの曲線データのファイル名
	 */
	private static final String CSV_BUS_ROUTE_CURVE_FORMAT = 
			"bus" + File.separatorChar + "bus_route_curve_%02d.csv";

	/**
	 * 行政界(面)のファイル名
	 */
	private static final String CSV_AREA_FORMAT = 
			"area" + File.separatorChar + "area_%02d.csv";

	/**
	 * 行政界(面)のポリゴンデータのファイル名
	 */
	private static final String CSV_AREA_INFO_FORMAT = 
			"area" + File.separatorChar + "area_info_%02d.csv";


	private static final String[] KSJ_URL_FORMAT_LIST = { null, // 0
			null, // 1
			"N02/N02-11/N02-11_GML.zip", // 2
			"N03/N03-11/N03-120331_%02d_GML.zip", // 3
			null, // 4
			null, // 5
			null, // 6
			"N07/N07-11/N07-11_%02d_GML.zip", // 7
			null, // 8
			null, // 9,
			null, // 10,
			"P11/P11-10/P11-10_%02d_GML.zip", // 11
	};

	private static final String[] KSJ_TYPE_FORMAT = { null, // 0
			null, // 1
			"N02", // 2
			"N03", // 3
			null, // 4
			null, // 5
			null, // 6
			"N07", // 7
			null, // 8
			null, // 9
			null, // 10
			"P11", // 11
	};

	private static final String KSJ_URL_BASE = "http://nlftp.mlit.go.jp/ksj/gml/data/";

	private final SAXParserFactory factory;

	/**
	 * オリジナルファイルの保存フォルダ
	 */
	private final String orgDir;

	/**
	 * CSVファイルの保存ディレクトリ
	 */
	private String csvDir;

	/**
	 * シリアライズファイルの保存ディレクトリ
	 */
	private String serializeDir;

	/**
	 * @param orgDir オリジナルファイルの格納ディレクトリ
	 * @param csvDir CSVファイルの格納ディレクトリ
	 * @param serializeDir シリアライズファイルの格納ディレクトリ
	 */
	public KsjDataManager(String orgDir, String csvDir, String serializeDir) {
		this.orgDir = orgDir;
		this.csvDir = csvDir;
		this.serializeDir = serializeDir;
		this.factory = SAXParserFactory.newInstance();
	}

	/**
	 * ファイルのコピーを行います。 入出力のストリームは閉じないので注意が必要です。
	 * 
	 * @param in 入力ストリーム
	 * @param out 出力ストリーム
	 * @throws IOException 入出力エラー
	 */
	private void copy(InputStream in, OutputStream out) throws IOException {
		final byte buf[] = new byte[1024];
		int size;
		while ((size = in.read(buf)) != -1) {
			out.write(buf, 0, size);
			out.flush();
		}
	}

	/**
	 * ファイルをダウンロードします。
	 * 
	 * @param url URL
	 * @param file ダウンロード先のファイル
	 * @return ダウンロードできればtrue
	 * @throws IOException 入出力エラー
	 */
	private boolean download(URL url, File file) {
		boolean ret = true;
		try {
			URLConnection connect = url.openConnection();
			InputStream in = connect.getInputStream();
			try {
				// ファイルのチェック（ファイルサイズの確認）
				int contentLength = connect.getContentLength();
				if (contentLength != file.length()) {
					if (!file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
					try {
						this.copy(in, out);
					} finally {
						out.close();
					}
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			ret = false;
		}
		return ret;
	}

	/**
	 * 圧縮ファイルを展開します。
	 * 
	 * @param zip 展開するファイル
	 * @param dir 展開するディレクトリ
	 * @param filter ファイルフィルター
	 * @return 展開したファイル配列
	 * @throws IOException 入出力エラー
	 */
	private List<File> extractZip(File zip, File dir, FileFilter filter) {
		List<File> extracted = new ArrayList<File>();
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zip));
			try {
				ZipEntry entry;
				while ((entry = in.getNextEntry()) != null) {
					String entryPath = entry.getName();
					/* 出力先ファイル */
					File outFile = new File(dir.getPath() + File.separatorChar + entryPath);
					if (filter == null || filter.accept(outFile)) {
						if (!outFile.exists() || entry.getSize() != outFile.length()) {
							/* entryPathにディレクトリを含む場合があるので */
							File dirParent = outFile.getParentFile();
							if (!dirParent.isDirectory() && !dirParent.mkdirs()) {
								throw new IOException("Failure of mkdirs: " + dirParent);
							}
							// ディレクトリはmkdirで作成する必要がある
							if (entryPath.endsWith(File.separator)) {
								if (!outFile.mkdirs()) {
									throw new IOException("Failure of mkdirs: " + outFile);
								}
							} else {
								FileOutputStream out = null;
								try {
									out = new FileOutputStream(outFile);
									this.copy(in, out);
								} finally {
									if (out != null) {
										out.close();
									}
								}
							}
						}
						extracted.add(outFile);
					}
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			extracted = null;
		}
		return extracted;
	}

	private File getFile(int type, int code) {
		return new File(this.orgDir
				+ File.separatorChar
				+ String.format("%02d" + File.separatorChar + KSJ_TYPE_FORMAT[type] + "-%02d.zip",
						code, code));
	}

	private boolean hasExtracted(File dir, FileFilter filter) {
		File[] files = dir.listFiles(filter);
		boolean ret = false;
		for (File file : files) {
			if (file.isFile() || (file.isDirectory() && hasExtracted(file, filter))) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public File[] getKsjFile(final int type) {
		File zip = new File(this.orgDir + File.separatorChar + KSJ_TYPE_FORMAT[type] + ".zip");
		File dir = zip.getParentFile();
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IllegalStateException();
		}
		File[] ret = null;
		FileFilter filter = new FileFilter() {
			String regexFile = KSJ_TYPE_FORMAT[type] + "-\\d+\\.xml";

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(regexFile) || pathname.getName().endsWith("GML");
			}
		};
		try {
			if (zip.exists() || !hasExtracted(dir, filter)) {
				/*
				 * 圧縮ファイルが残っている or ディレクトリが存在しない or ディレクトリ内のファイルが存在しない or
				 * ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE + KSJ_URL_FORMAT_LIST[type]);
				long t0 = System.currentTimeMillis();
				System.out.print("Download: " + url);
				if (!this.download(url, zip))
					return null;
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
			}
			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				System.out.print("Extract: " + zip);
				List<File> extracted = this.extractZip(zip, dir, filter);
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
				for (File file : extracted) {
					if (file.exists()) {
						File parent = file.getParentFile();
						if (!dir.equals(parent)) {
							for (File child : parent.listFiles()) {
								if (!child.renameTo(new File(dir, child.getName()))) {
									throw new IllegalSelectorException();
								}
							}
							if (!parent.delete())
								throw new IllegalStateException();
						}
					}
				}
				if (!zip.delete()) {
					throw new IllegalStateException();
				}
			}
			ret = dir.listFiles(filter);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			if (zip.exists() || !hasExtracted(dir, filter)) {
				/*
				 * 圧縮ファイルが残っている or ディレクトリが存在しない or ディレクトリ内のファイルが存在しない or
				 * ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE + KSJ_URL_FORMAT_LIST[type]);
				long t0 = System.currentTimeMillis();
				System.out.print("Download: " + url);
				if (!this.download(url, zip))
					return null;
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
			}
			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				System.out.print("Extract: " + zip);
				List<File> extracted = this.extractZip(zip, dir, filter);
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
				for (File file : extracted) {
					if (file.exists()) {
						File parent = file.getParentFile();
						if (!dir.equals(parent)) {
							for (File child : parent.listFiles()) {
								if (!child.renameTo(new File(dir, child.getName()))) {
									throw new IllegalSelectorException();
								}
							}
							if (!parent.delete())
								throw new IllegalStateException();
						}
					}
				}
				if (!zip.delete()) {
					throw new IllegalStateException();
				}
			}
			ret = dir.listFiles(filter);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 国土数値情報のファイルを取得します。 ファイルは、ウェブからダウンロード、展開します。
	 * 
	 * @param type ファイルの種類
	 * @param code 都道府県番号
	 * @return 国土数値情報のファイル
	 * @throws IOException 入出力エラー
	 */
	public File[] getKsjFile(final int type, final int code) {
		File zip = getFile(type, code);
		File dir = zip.getParentFile();
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IllegalStateException();
		}
		File[] ret = null;
		FileFilter filter = new FileFilter() {
			String regexFile = String.format(
					KSJ_TYPE_FORMAT[type] + "-(?:\\d+_)?%02d(?:.+)?\\.xml", code);

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(regexFile) || pathname.getName().endsWith("GML");
			}
		};
		try {
			if (zip.exists() || !hasExtracted(dir, filter)) {
				/*
				 * 圧縮ファイルが残っている or ディレクトリが存在しない or ディレクトリ内のファイルが存在しない or
				 * ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE + String.format(KSJ_URL_FORMAT_LIST[type], code));
				long t0 = System.currentTimeMillis();
				System.out.print("Download: " + url);
				if (!this.download(url, zip))
					return null;
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
			}
			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				System.out.print("Extract: " + zip);
				List<File> extracted = this.extractZip(zip, dir, filter);
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
				for (File file : extracted) {
					if (file.exists()) {
						File parent = file.getParentFile();
						if (!dir.equals(parent)) {
							for (File child : parent.listFiles()) {
								if (!child.renameTo(new File(dir, child.getName()))) {
									throw new IllegalSelectorException();
								}
							}
							if (!parent.delete())
								throw new IllegalStateException();
						}
					}
				}
				if (!zip.delete()) {
					throw new IllegalStateException();
				}
			}
			ret = dir.listFiles(filter);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private List<RailroadInfo> readRailroadInfoCSV() throws IOException {
		List<RailroadInfo> infos = new ArrayList<RailroadInfo>();
		File file = new File(this.csvDir + File.separatorChar + CSV_RAIL_INFO);
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), CHARSET));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					int railwayType = Integer.parseInt(param[0]);
					int instituteType = Integer.parseInt(param[1]);
					String ln = param[2];
					String company = param[3];
					infos.add(new RailroadInfo(railwayType, instituteType, ln, company));
				}
			} finally {
				in.close();
			}
		}
		return infos;	
	}

	private RailwayCollection readRailwayCSV(List<RailroadInfo> infos, List<GmlCurve> curves)
			throws IOException {

		RailwayCollection ret = null;
		List<Station> stations = new ArrayList<Station>();
		List<RailroadSection> sections = new ArrayList<RailroadSection>();
		
		File file = new File(this.csvDir + File.separatorChar + CSV_RAIL);
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), CHARSET));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					String name = param[0];
					int infoIdx = Integer.parseInt(param[1]);
					RailroadInfo info = infos.get(infoIdx);
					
					int curveIdx = Integer.parseInt(param[2]);
					GmlCurve curve = curves.get(curveIdx);
					
					if ("".equals(name)) {
						sections.add(new RailroadSection(info, curve));
					} else {
						stations.add(new Station(name, info, curve));
					}
				}
				if (stations.isEmpty() || sections.isEmpty()) {
					throw new IllegalStateException();
				}
				ret = new RailwayCollection(stations.toArray(new Station[stations.size()]),
						sections.toArray(new RailroadSection[sections.size()]));
			} finally {
				in.close();
			}
		}
		return ret;
	}

	private RailwayCollection readRailwayCollectionCSV() {
		RailwayCollection ret = null;
		try {
			File file = new File(this.csvDir + File.separatorChar + CSV_RAIL_CURVE);
			List<GmlCurve> curves = this.readCurveCSV(file);
			if (curves.isEmpty())
				return ret;
			
			List<RailroadInfo> infos = this.readRailroadInfoCSV();
			if (infos.isEmpty())
				return ret;
			
			ret = this.readRailwayCSV(infos, curves);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	private boolean writeRailwayStationCSV(RailwayCollection data,
			Map<RailroadInfo, Integer> infoMap, List<RailroadInfo> infoList,
			Map<GmlCurve, Integer> curveMap, List<GmlCurve> curveList) throws IOException {

		boolean ret = true;
		
		
		String path = this.csvDir + File.separatorChar + CSV_RAIL;
		
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".tmp");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (Station station : data.getStations()) {

					RailroadInfo info = station.getInfo();
					GmlCurve curve = station.getCurve();

					Integer infoIdx = infoMap.get(info);
					if (infoIdx == null) {
						infoIdx = infoList.size();
						infoMap.put(info, infoIdx);
						infoList.add(info);
					} else {
						info = infoList.get(infoIdx);
						station.setInfo(info);
					}

					Integer curveIdx = curveMap.get(info);
					if (curveIdx == null) {
						curveIdx = curveList.size();
						curveMap.put(curve, curveIdx);
						curveList.add(curve);
					} else {
						curve = curveList.get(curveIdx);
						station.setCurve(curve);
					}

					out.write(String.format("%s,%d,%d", station.getName(), infoIdx, curveIdx));
					out.newLine();
					out.flush();
				}
				
				for (RailroadSection section : data.getRailroadSection()) {

					RailroadInfo info = section.getInfo();
					GmlCurve curve = section.getCurve();

					Integer infoIdx = infoMap.get(info);
					if (infoIdx == null) {
						infoIdx = infoList.size();
						infoMap.put(info, infoIdx);
						infoList.add(info);
					} else {
						info = infoList.get(infoIdx);
						section.setInfo(info);
					}

					Integer curveIdx = curveMap.get(info);
					if (curveIdx == null) {
						curveIdx = curveList.size();
						curveMap.put(curve, curveIdx);
						curveList.add(curve);
					} else {
						curve = curveList.get(curveIdx);
						section.setCurve(curve);
					}

					out.write(String.format(",%d,%d", infoIdx, curveIdx));

					out.newLine();
					out.flush();
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(file)) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + file);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
		return ret;
	}

	private void writeRailroadInfoCSV(List<RailroadInfo> infos) throws IOException {
		String path = this.csvDir + File.separatorChar + CSV_RAIL_INFO;
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".csv");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (RailroadInfo info : infos) {
					out.write(String.format("%d,%d,%s,%s\n",
							info.getRailwayType(), info.getInstituteType(),
							info.getLine(), info.getCompany()));
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(new File(path))) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + path);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && !tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}
	
	public boolean writeRailwayCollectionCSV(RailwayCollection data) {
		boolean ret = true;

		List<RailroadInfo> infoList = new ArrayList<RailroadInfo>();
		Map<RailroadInfo, Integer> infoMap = new HashMap<RailroadInfo, Integer>();

		List<GmlCurve> curveList = new ArrayList<GmlCurve>();
		Map<GmlCurve, Integer> curveMap = new HashMap<GmlCurve, Integer>();
		
		String path = this.csvDir + File.separatorChar + CSV_RAIL_CURVE;
		try {
			// 1. Station
			writeRailwayStationCSV(data, infoMap, infoList, curveMap, curveList);

			// 2. Railroad Info
			writeRailroadInfoCSV(infoList);
			
			// 3. Curve
			writeCurveCSV(path, curveList);
		} catch (IOException e) {
			ret = false;
			e.printStackTrace();
		}

		return ret;
	}
	
	/**
	 * @return 鉄道のデータ配列
	 */
	public RailwayCollection getRailwayCollection() {
		RailwayCollection ret = this.readRailwayCollectionCSV();
		if (ret == null) {
			long t0 = System.currentTimeMillis();

			ret = this.readRailwayCollectionGML();

			System.out.printf("RAILWAY: %dms\n", (System.currentTimeMillis() - t0));

			this.writeRailwayCollectionCSV(ret);
		}
		return ret;
	}

	/**
	 * @return 行政界(面)のデータ配列
	 */
	private RailwayCollection readRailwayCollectionGML() {
		long t0 = System.currentTimeMillis();

		String name = "all" + File.separatorChar + "N02.obj";
		RailwayCollection ret = this.readSerializable(name, RailwayCollection.class);
		if (ret == null) {
			this.getKsjFile(TYPE_RAILWAY);
			try {
				SAXParser parser = factory.newSAXParser();
				File file = new File(this.orgDir + File.separatorChar + "N02-11.xml");
				HandlerN02 handler = new HandlerN02();
				parser.parse(file, handler);

				Station[] stations = handler.getStations();
				RailroadSection[] sections = handler.getRailroadSections();

				ret = new RailwayCollection(stations, sections);

				writeSerializable(name, ret);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.printf("N02 %02d: %dms\n", 2, (System.currentTimeMillis() - t0));

		return ret;
	}

	private List<AreaInfo> readAreaInfoCSV(int code) throws IOException {
		List<AreaInfo> ret = new ArrayList<AreaInfo>();
		File file = new File(this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_AREA_INFO_FORMAT, code));
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					String prn = param[0];
					String sun = param[1];
					String con = param[2];
					String cn2 = param[3];
					int aac = Integer.parseInt(param[4]);
					ret.add(new AreaInfo(aac, prn, sun, con, cn2));
				}
			} finally {
				in.close();
			}
		}
		return ret;
	}
	
	private List<Area> readAreaCSV(int code, List<AreaInfo> infos) throws IOException {

		List<Area> ret = new ArrayList<Area>();
		File file = new File(this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_AREA_FORMAT, code));
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), CHARSET));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					int idxInfo = Integer.parseInt(param[0]);
					AreaInfo info = infos.get(idxInfo);
					int n = Integer.parseInt(param[1]);
					int[] x = new int[n];
					int[] y = new int[n];
					for (int i = 0; i < n; i++) {
						x[i] = FixedPoint.parseFixedPoint(param[i * 2 + 2]);
						y[i] = FixedPoint.parseFixedPoint(param[i * 2 + 3]);
					}
					GmlPolygon polygon = new GmlPolygon(n, x, y);
					ret.add(new Area(info, polygon));
				}
			} finally {
				in.close();
			}
		}
		return ret;
	}
	
	private AreaCollection readAreaCollectionCSV(int code) {
		AreaCollection ret = null;

		try {
			long t0 = System.currentTimeMillis();

			List<AreaInfo> infos = readAreaInfoCSV(code);
			if (infos.isEmpty())
				return ret;

			List<Area> areas = readAreaCSV(code, infos);
			if (areas.isEmpty())
				return ret;
			
			ret = new AreaCollection(code, areas.toArray(new Area[areas.size()]));

			System.out.printf("AREA(%02d): %dms\n", code, (System.currentTimeMillis() - t0));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private void writeAreaCSV(AreaCollection data, Map<AreaInfo, Integer> infoMap, List<AreaInfo> infoList) throws IOException {

		int code = data.getCode();
		String path = this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_AREA_FORMAT, code);
		
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".tmp");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (Area area : data.getAreas()) {
					GmlPolygon polygon = area.getPolygon();
					int n = polygon.getArrayLength();
					int[] x = polygon.getArrayX();
					int[] y = polygon.getArrayY();
					AreaInfo info = area.getInfo();
					Integer idx = infoMap.get(info);
					if (idx == null) {
						idx = infoList.size();
						infoMap.put(info, idx);
						infoList.add(info);
					} else {
						info = infoList.get(idx);
						area.setInfo(info);
					}
					
					out.write(String.format("%d,%d", idx, n));
					for (int i = 0; i < n; i++) {
						out.write(String.format(",%f,%f", FixedPoint.parseDouble(x[i]),
								FixedPoint.parseDouble(y[i])));
					}
					out.newLine();
					out.flush();
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(file)) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + file);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}

	private void writeAreaInfoCSV(int code, List<AreaInfo> infoList) throws IOException {

		String path = this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_AREA_INFO_FORMAT, code);
		
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".tmp");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (int i = 0; i < infoList.size(); i++) {
					AreaInfo area = infoList.get(i);
					out.write(String.format("%s,%s,%s,%s,%d", area.getPrn(), area.getSun(), area.getCon(), area.getCn2(), area.getAac()));
					out.newLine();
					out.flush();
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(file)) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + file);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}

	
	private boolean writeAreaCollectionCSV(AreaCollection data) {

		boolean ret = false;
		int code = data.getCode();

		List<AreaInfo> infoList = new ArrayList<AreaInfo>();
		Map<AreaInfo, Integer> infoMap = new HashMap<AreaInfo, Integer>();
		
		try {
			// 1. Area
			this.writeAreaCSV(data, infoMap, infoList);
			
			// 2. Area Info
			this.writeAreaInfoCSV(code, infoList);

		} catch (IOException e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @param code 都道府県コード
	 * @return 行政界(面)のデータ配列
	 */
	public AreaCollection getAreaCollection(int code) {
		AreaCollection ret = this.readAreaCollectionCSV(code);
		if (ret == null) {
			long t0 = System.currentTimeMillis();
			
			ret = this.readAreaCollectionGML(code);
			this.writeAreaCollectionCSV(ret);
			System.out.printf("AREA(%02d): %dms\n", code, (System.currentTimeMillis() - t0));
		}
		return ret;
	}

	/**
	 * @return 行政界(面)のデータ配列
	 */
	public AreaCollection[] getAreaCollections() {
		AreaCollection[] ret = new AreaCollection[47];
		for (int i = 1; i <= 47; i++) {
			ret[i - 1] = this.getAreaCollection(i);
		}
		return ret;
	}

	/**
	 * @param code 都道府県コード
	 * @return 行政界(面)のデータ配列
	 */
	public AreaCollection readAreaCollectionGML(int code) {
		long t0 = System.currentTimeMillis();

		String name = String.format("%02d" + File.separatorChar + "N03-%02d.obj", code, code);
		Area[] areas = this.readSerializable(name, Area[].class);
		if (areas == null) {
			this.getKsjFile(TYPE_ADMINISTRATIVEAREA, code);
			try {
				SAXParser parser = this.factory.newSAXParser();
				File file = new File(this.orgDir + File.separatorChar
						+ String.format("%02d" + File.separatorChar + "N03-12_%02d_120331.xml",
								code, code));
				HandlerN03 handler = new HandlerN03();
				parser.parse(file, handler);

				areas = handler.getAreas();

				writeSerializable(name, areas);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		AreaCollection ret = new AreaCollection(code, areas);
		System.out.printf("N03 %02d: %dms\n", code, (System.currentTimeMillis() - t0));

		return ret;
	}

	private BusCollection readBusCollectionCSV(int code) {
		BusCollection ret = null;
		try {
			long t0 = System.currentTimeMillis();

			File file = new File(this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
					+ String.format(CSV_BUS_ROUTE_CURVE_FORMAT, code));

			List<GmlCurve> curves = readCurveCSV(file);
			if (curves.isEmpty())
				return ret;

			List<BusRouteInfo> infos = readBusRouteInfoCSV(code);
			if (infos.isEmpty())
				return ret;

			BusRoute[] routes = readBusRouteCSV(code, curves, infos);
			if (routes == null)
				return ret;

			BusStop[] stops = readBusStopCSV(code, infos);
			if (stops == null)
				return ret;

			ret = new BusCollection(code, stops, routes);

			System.out.printf("BUS(%02d): %dms\n", code, (System.currentTimeMillis() - t0));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private BusStop[] readBusStopCSV(int code, List<BusRouteInfo> infos) throws IOException {
		BusStop[] ret = null;
		File file = new File(this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_BUS_STOP_FORMAT, code));
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
					CHARSET));
			try {
				String line;
				List<BusStop> tmpStops = new ArrayList<BusStop>();
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					String name = param[0];
					int lat = FixedPoint.parseFixedPoint(param[1]);
					int lng = FixedPoint.parseFixedPoint(param[2]);
					BusRouteInfo[] a = new BusRouteInfo[param.length - 3];
					for (int i = 3; i < param.length; i++) {
						int idx = Integer.parseInt(param[i]);
						a[i - 3] = infos.get(idx);
					}
					BusStop stop = new BusStop(name, lng, lat, a);
					tmpStops.add(stop);
				}
				ret = tmpStops.toArray(new BusStop[] {});
			} finally {
				in.close();
			}
		}
		return ret;
	}

	private BusRoute[] readBusRouteCSV(int code, List<GmlCurve> curves, List<BusRouteInfo> infos)
			throws IOException {
		BusRoute[] routes = null;
		File file = new File(
				this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_BUS_ROUTE_FORMAT, code));
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
					CHARSET));
			try {
				String line;
				List<BusRoute> tmpRoutes = new ArrayList<BusRoute>();
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					int curveIdx = Integer.parseInt(param[0]);
					GmlCurve curve = curves.get(curveIdx);

					int infoIdx = Integer.parseInt(param[1]);
					BusRouteInfo info = infos.get(infoIdx);

					BusRoute route = new BusRoute(curve, info);
					tmpRoutes.add(route);
				}
				routes = tmpRoutes.toArray(new BusRoute[] {});
			} finally {
				in.close();
			}
		}
		return routes;
	}

	private List<BusRouteInfo> readBusRouteInfoCSV(int code) throws IOException {
		List<BusRouteInfo> infos = new ArrayList<BusRouteInfo>();
		File file = new File(this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_BUS_ROUTE_INFO_FORMAT, code));
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
					CHARSET));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					int type = Integer.parseInt(param[0]);
					String ln = param[1];
					String opc = param[2];
					infos.add(new BusRouteInfo(type, ln, opc));
				}
			} finally {
				in.close();
			}
		}
		return infos;
	}

	private List<GmlCurve> readCurveCSV(File file) throws IOException {
		List<GmlCurve> ret = new ArrayList<GmlCurve>();
		if (file.isFile()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file),
					CHARSET));
			try {
				String line;
				while ((line = in.readLine()) != null) {
					String[] param = line.split(",");
					int size = Integer.parseInt(param[0]);
					Point[] points = new Point[size];
					for (int i = 0; i < size; i++) {
						int lat = FixedPoint.parseFixedPoint(param[i * 2 + 1]);
						int lng = FixedPoint.parseFixedPoint(param[i * 2 + 2]);
						points[i] = new Point(lng, lat);
					}
					ret.add(new GmlCurve(points));
				}
			} finally {
				in.close();
			}
		}
		return ret;
	}

	/**
	 * @param code 都道府県コード
	 * @return バスルート(線)のデータ配列
	 */
	private BusStop[] readBusStopsGML(int code) {
		long t0 = System.currentTimeMillis();

		String name = String.format("%02d" + File.separatorChar + "P11-%02d.obj", code, code);
		BusStop[] ret = this.readSerializable(name, BusStop[].class);
		if (ret == null) {
			this.getKsjFile(TYPE_BUS_STOP, code);
			try {
				SAXParser parser = this.factory.newSAXParser();
				File file = new File(this.orgDir
						+ File.separatorChar
						+ String.format("%02d" + File.separatorChar + "P11-10_%02d-jgd-g.xml",
								code, code));
				HandlerP11 handler = new HandlerP11();
				parser.parse(file, handler);

				ret = handler.getBusStopArray();

				writeSerializable(name, ret);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.printf("P11 %02d: %dms\n", code, (System.currentTimeMillis() - t0));

		return ret;
	}

	/**
	 * @param code 都道府県コード
	 * @return バスルート(線)のデータ配列
	 */
	private BusRoute[] readBusRoutesGML(int code) {
		long t0 = System.currentTimeMillis();

		String name = String.format("%02d" + File.separatorChar + "N07-%02d.obj", code, code);
		BusRoute[] ret = this.readSerializable(name, BusRoute[].class);
		if (ret == null) {
			this.getKsjFile(TYPE_BUS_ROUTE, code);
			try {
				SAXParser parser = this.factory.newSAXParser();
				File file = new File(
						this.orgDir
								+ File.separatorChar
								+ String.format("%02d" + File.separatorChar + "N07-11_%02d.xml",
										code, code));
				HandlerN07 handler = new HandlerN07();
				parser.parse(file, handler);

				ret = handler.getBusRoutes();

				writeSerializable(name, ret);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.printf("N07 %02d: %dms\n", code, (System.currentTimeMillis() - t0));

		return ret;
	}

	private void writeBusStopCsv(BusCollection data, Map<BusRouteInfo, Integer> infoMap,
			List<BusRouteInfo> infoList) throws IOException {

		int code = data.getCode();
		BusStop[] stops = data.getBusStops();

		String path = this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar + String.format(CSV_BUS_STOP_FORMAT, code);
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".tmp");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (BusStop stop : stops) {
					out.write(String.format("%s,%f,%f", stop.getName(),
							FixedPoint.parseDouble(stop.getX()),
							FixedPoint.parseDouble(stop.getY())));
					BusRouteInfo[] infos = stop.getBusRouteInfos();
					for (int i = 0; i < infos.length; i++) {
						BusRouteInfo info = infos[i];
						Integer idx = infoMap.get(info);
						if (idx == null) {
							idx = infoList.size();
							infoMap.put(info, idx);
							infoList.add(info);
						} else {
							infos[i] = infoList.get(idx);
						}
						out.write(String.format(",%d", idx));
					}
					out.newLine();
					out.flush();
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(file)) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + file);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && !tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}

	private void writeBusRouteCsv(BusCollection collection, Map<BusRouteInfo, Integer> infoMap,
			List<BusRouteInfo> infoList, Map<GmlCurve, Integer> curveMap, List<GmlCurve> curveList) throws IOException {

		int code = collection.getCode();
		BusRoute[] routes = collection.getBusRoute();
		String path = this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_BUS_ROUTE_FORMAT, code);
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}

		File tmpFile = new File(path + ".tmp");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (BusRoute route : routes) {
					GmlCurve curve = route.getCurve();
					Integer curveIdx = curveMap.get(curve);
					if (curveIdx == null) {
						curveIdx = curveList.size();
						curveList.add(curve);
						curveMap.put(curve, curveIdx);
						assert (curveIdx != null);
						assert (curve.equals(curveList.get(curveIdx)));
						assert (curve.equals(curveList.get(curveMap.get(curve))));
					} else {
						curve = curveList.get(curveIdx);
						route.setCurve(curve);
						assert (curveIdx != null);
						assert (curve.equals(curveList.get(curveIdx)));
						assert (curve.equals(curveList.get(curveMap.get(curve))));
					}

					BusRouteInfo info = route.getInfo();
					Integer infoIdx = infoMap.get(info);
					if (infoIdx == null) {
						infoIdx = infoList.size();
						infoMap.put(info, infoIdx);
						infoList.add(info);
					} else {
						info = infoList.get(infoIdx);
						route.setInfo(info);
					}

					out.write(String.format("%d,%d,%f,%f,%f", curveIdx, infoIdx,
							route.getRateParDay(), route.getRatePerSaturday(),
							route.getRatePerHoliday()));
					out.newLine();
					out.flush();
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(new File(path))) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + path);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && !tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}

	private void writeBusRouteInfoCsv(int code, List<BusRouteInfo> infoList) throws IOException {
		String path = this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
				+ String.format(CSV_BUS_ROUTE_INFO_FORMAT, code);
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".csv");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (BusRouteInfo info : infoList) {
					out.write(String.format("%d,%s,%s\n", info.getType(), info.getLine(),
							info.getOperationCommunity()));
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(new File(path))) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + path);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && !tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}

	private void writeCurveCSV(String path, List<GmlCurve> curveList) throws IOException {
		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new IllegalStateException("Failure of delete: " + file);
		}
		File tmpFile = new File(path + ".tmp");
		try {
			if (!tmpFile.getParentFile().isDirectory() && !tmpFile.getParentFile().mkdirs()) {
				throw new IllegalStateException();
			}
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					tmpFile), CHARSET));
			try {
				for (GmlCurve curve : curveList) {
					int n = curve.getArrayLength();
					int[] x = curve.getArrayX();
					int[] y = curve.getArrayY();
					out.write(Integer.toString(n));
					for (int i = 0; i < n; i++) {
						out.write(String.format(",%f,%f", FixedPoint.parseDouble(x[i]),
								FixedPoint.parseDouble(y[i])));
					}
					out.newLine();
					out.flush();
				}
			} finally {
				out.close();
			}
			if (!tmpFile.renameTo(file)) {
				throw new IllegalStateException("Failure of rename: " + tmpFile + " => " + file);
			}
		} catch (IOException e) {
			if (tmpFile.isFile() && tmpFile.delete()) {
				throw new IllegalStateException("Failure of delete: " + tmpFile);
			}
			throw e;
		}
	}

	private boolean writeBusCollectionCsv(BusCollection collection) {
		
		boolean ret = true;

		int code = collection.getCode();

		List<BusRouteInfo> infoList = new ArrayList<BusRouteInfo>();
		Map<BusRouteInfo, Integer> infoMap = new HashMap<BusRouteInfo, Integer>();

		List<GmlCurve> curveList = new ArrayList<GmlCurve>();
		Map<GmlCurve, Integer> curveMap = new HashMap<GmlCurve, Integer>();

		try {
			// 1 Bus Stop
			writeBusStopCsv(collection, infoMap, infoList);

			// 2 Bus Route
			writeBusRouteCsv(collection, infoMap, infoList, curveMap, curveList);

			// 3 Bus Route Info
			writeBusRouteInfoCsv(code, infoList);

			// 4 GML Curve

			String path = this.csvDir + File.separatorChar + String.format("%02d", code) + File.separatorChar
					+ String.format(CSV_BUS_ROUTE_CURVE_FORMAT, code);

			this.writeCurveCSV(path, curveList);
			
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	
	/**
	 * @return バスのデータ(バスルートおよびバス停留所)
	 */
	public BusCollection[] getBusCollections() {
		BusCollection[] ret = new BusCollection[47];
		for (int i = 1; i <= 47; i++) {
			ret[i - 1] = getBusCollection(i);
		}
		return ret;
	}

	/**
	 * @param code 都道府県コード
	 * @return バスのデータ(バスルートおよびバス停留所)
	 */
	public BusCollection getBusCollection(int code) {
		BusCollection ret = this.readBusCollectionCSV(code);
		if (ret == null) {
			long t0 = System.currentTimeMillis();

			BusStop[] stops = readBusStopsGML(code);
			BusRoute[] routes = readBusRoutesGML(code);

			System.out.printf("BUS(%02d): %dms\n", code, (System.currentTimeMillis() - t0));

			ret = new BusCollection(code, stops, routes);
			this.writeBusCollectionCsv(ret);
		}
		return ret;
	}

	/**
	 * オブジェクトを直列化してファイルに保存します。 衝突を避けるため.tmpファイルに保存後、リネームします。
	 * 
	 * @param name 保存ファイル名
	 * @param obj シリアライズ可能なオブジェクト
	 * @return 保存の成否
	 */
	private boolean writeSerializable(String name, Object obj) {
		boolean ret = false;
		String path = this.serializeDir + File.separator + name;
		File file = new File(path + ".tmp");
		if (!file.getParentFile().isDirectory() && !file.getParentFile().mkdirs()) {
			return false;
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			try {
				out.writeObject(obj);
				out.flush();
			} finally {
				out.close();
			}
			if (!file.renameTo(new File(this.serializeDir + File.separator + name))) {
				if (!file.delete()) {
					throw new IllegalStateException("Failure of delete: " + file);
				}
				return ret;
			}
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
			if (file.isFile() && !file.delete()) {
				throw new IllegalStateException("Failure of delete: " + file);
			}
			File file2 = new File(this.serializeDir + File.separator + name);
			if (file2.isFile() && !file2.delete()) {
				throw new IllegalStateException("Failure of delete: " + file);
			}
		}
		return ret;
	}

	/**
	 * ファイルから直列化して保存されたオブジェクトを読み込みます。
	 * 
	 * @param name ファイル名
	 * @param c
	 * @return オブジェクト
	 */
	private <T> T readSerializable(String name, Class<T> c) {
		T ret = null;
		String path = this.serializeDir + File.separator + name;
		File file = new File(path);
		if (file.isFile()) {
			try {
				ObjectInputStream in = null;
				try {
					in = new ObjectInputStream(new FileInputStream(file));
					Object obj = in.readObject();
					ret = c.cast(obj);
				} finally {
					if (in != null) {
						in.close();
					}
				}
			} catch (Exception e) {
				System.err.println(e.getClass().getName() +": "+ e.getMessage());
				ret = null;
				if (file.isFile() && !file.delete()) {
					throw new IllegalStateException("Failure of delete: " + file);
				}
			}
		}
		return ret;
	}
}
