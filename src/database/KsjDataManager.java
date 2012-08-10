package database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import map.ksj.AdministrativeArea;
import map.ksj.BusRoute;
import map.ksj.BusStop;
import map.ksj.RailroadSection;
import map.ksj.RailwayCollections;
import map.ksj.Station;
import map.ksj.handler.HandlerN02;
import map.ksj.handler.HandlerN03;
import map.ksj.handler.HandlerN07;
import map.ksj.handler.HandlerP11;

/**
 * 数値地図のデータ管理
 * @author ma38su
 */
public class KsjDataManager {

	private static final String[] KSJ_URL_FORMAT_LIST = {
		null, // 0
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
	
	private static final String[] KSJ_TYPE_FORMAT = {
		null, // 0
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
	 * 保存フォルダ
	 */
	private final String baseDir;

	/**
	 * シリアライズデータの保存ディレクトリ
	 */
	private String serializeDir;

	/**
	 * @param baseDir データ格納ディレクトリ
	 * @param serializeDir シリアライズデータの格納ディレクトリ
	 */
	public KsjDataManager(String baseDir, String serializeDir) {
		this.baseDir = baseDir;
		this.serializeDir = serializeDir;
		this.factory = SAXParserFactory.newInstance();
	}
	

	/**
	 * ファイルのコピーを行います。
	 * 入出力のストリームは閉じないので注意が必要です。
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
					File outFile = new File(dir.getPath() +File.separatorChar+ entryPath);
					if (filter == null || filter.accept(outFile)) {
						if (!outFile.exists() || entry.getSize() != outFile.length()) {
							/* entryPathにディレクトリを含む場合があるので */
							File dirParent = outFile.getParentFile();
							if(!dirParent.isDirectory() && !dirParent.mkdirs()) {
								throw new IOException("Failure of mkdirs: "+ dirParent);
							}
							// ディレクトリはmkdirで作成する必要がある
							if (entryPath.endsWith(File.separator)) {
								if (!outFile.mkdirs()) {
									throw new IOException("Failure of mkdirs: "+ outFile);
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
		return new File(this.baseDir +File.separatorChar+ String.format("%02d" +File.separatorChar+ KSJ_TYPE_FORMAT[type] +"-%02d.zip", code, code));
	}
	
	private static boolean hasExtracted(File dir, FileFilter filter) {
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
		File zip = new File(this.baseDir +File.separatorChar+ KSJ_TYPE_FORMAT[type] +".zip");
		File dir = zip.getParentFile();
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IllegalStateException();
		}
		File[] ret = null;
		FileFilter filter = new FileFilter() {
			String regexFile = KSJ_TYPE_FORMAT[type] +"-\\d+\\.xml";
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(regexFile) || pathname.getName().endsWith("GML");
			}
		};
		try {
			if (zip.exists() || !hasExtracted(dir, filter)) {
				/* 圧縮ファイルが残っている
				 * or ディレクトリが存在しない
				 * or ディレクトリ内のファイルが存在しない
				 * or ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE + KSJ_URL_FORMAT_LIST[type]);
				long t0 = System.currentTimeMillis();
				System.out.print("Download: "+ url);
				if (!this.download(url, zip)) return null;
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
			}
			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				System.out.print("Extract: "+ zip);
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
							if (!parent.delete()) throw new IllegalStateException();
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
				/* 圧縮ファイルが残っている
				 * or ディレクトリが存在しない
				 * or ディレクトリ内のファイルが存在しない
				 * or ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE + KSJ_URL_FORMAT_LIST[type]);
				long t0 = System.currentTimeMillis();
				System.out.print("Download: "+ url);
				if (!this.download(url, zip)) return null;
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
			}
			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				System.out.print("Extract: "+ zip);
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
							if (!parent.delete()) throw new IllegalStateException();
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
	 * 国土数値情報のファイルを取得します。
	 * ファイルは、ウェブからダウンロード、展開します。
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
			String regexFile = String.format(KSJ_TYPE_FORMAT[type] +"-(?:\\d+_)?%02d(?:.+)?\\.xml", code);
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(regexFile) || pathname.getName().endsWith("GML");
			}
		};
		try {
			if (zip.exists() || !hasExtracted(dir, filter)) {
				/* 圧縮ファイルが残っている
				 * or ディレクトリが存在しない
				 * or ディレクトリ内のファイルが存在しない
				 * or ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE + String.format(KSJ_URL_FORMAT_LIST[type], code));
				long t0 = System.currentTimeMillis();
				System.out.print("Download: "+ url);
				if (!this.download(url, zip)) return null;
				System.out.printf(" %dms\n", (System.currentTimeMillis() - t0));
			}
			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				System.out.print("Extract: "+ zip);
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
							if (!parent.delete()) throw new IllegalStateException();
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
	 * @param code 都道府県コード
	 * @return 行政界(面)のデータ配列
	 */
	public RailwayCollections getRailway() {
		long t0 = System.currentTimeMillis();

		String name = "N02.obj";
		RailwayCollections ret = this.readSerializable(name, RailwayCollections.class);
		if (ret == null) {
			this.getKsjFile(TYPE_RAILWAY);
			try {
				SAXParser parser = factory.newSAXParser();
				File file = new File(".data/N02-11.xml");
				HandlerN02 handler = new HandlerN02();
				parser.parse(file, handler);
				
				Station[] stations = handler.getStations();
				RailroadSection[] sections = handler.getRailroadSections();
				
				ret = new RailwayCollections(stations, sections);

				writeSerializable(name, ret);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.printf("N02 %d: %dms\n", 2, (System.currentTimeMillis() - t0));
		
		return ret;
	}
	
	/**
	 * @param code 都道府県コード
	 * @return 行政界(面)のデータ配列
	 */
	public AdministrativeArea[] getAdministrativeAreaArray(int code) {
		long t0 = System.currentTimeMillis();

		String name = String.format("N03-%02d.obj", code);
		AdministrativeArea[] ret = this.readSerializable(name, AdministrativeArea[].class);
		if (ret == null) {
			this.getKsjFile(TYPE_ADMINISTRATIVEAREA, code);
			try {
				SAXParser parser = this.factory.newSAXParser();
				File file = new File(".data" +File.separatorChar+ String.format("%02d" +File.separatorChar+ "N03-12_%02d_120331.xml", code, code));
				HandlerN03 handler = new HandlerN03();
				parser.parse(file, handler);
				
				ret = handler.getAdministrativeAreaList();

				writeSerializable(name, ret);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.printf("N03 %d: %dms\n", code, (System.currentTimeMillis() - t0));
		
		return ret;
	}

	/**
	 * @param code 都道府県コード
	 * @return バスルート(線)のデータ配列
	 */
	public BusRoute[] getBusRouteArray(int code) {
		long t0 = System.currentTimeMillis();

		String name = String.format("N07-%02d.obj", code);
		BusRoute[] ret = this.readSerializable(name, BusRoute[].class);
		if (ret == null) {
			this.getKsjFile(TYPE_BUS_ROUTE, code);
			try {
				SAXParser parser = this.factory.newSAXParser();
				File file = new File(".data" +File.separatorChar+ String.format("%02d" +File.separatorChar+ "N07-11_%02d.xml", code, code));
				HandlerN07 handler = new HandlerN07();
				parser.parse(file, handler);
				
				ret = handler.getBusRouteArray();

				writeSerializable(name, ret);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.printf("N07 %d: %dms\n", code, (System.currentTimeMillis() - t0));

		return ret;
	}

	/**
	 * @param code 都道府県コード
	 * @return バス停(線)のデータ配列
	 */
	public BusStop[] getBusStopArray(int code) {
		long t0 = System.currentTimeMillis();

		String name = String.format("P11-%02d.obj", code);
		BusStop[] ret = this.readSerializable(name, BusStop[].class);
		if (ret == null) {
			this.getKsjFile(TYPE_BUS_STOP, code);
			try {
				SAXParser parser = factory.newSAXParser();
				File file = new File(".data" +File.separatorChar+ String.format("%02d" +File.separatorChar+ "P11-10_%02d-jgd-g.xml", code, code));
				HandlerP11 handler = new HandlerP11();
				parser.parse(file, handler);
				
				ret = handler.getBusStopArray();
				
				writeSerializable(name, ret);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.printf("P11 %d: %dms\n", code, (System.currentTimeMillis() - t0));
		
		return ret;
	}
	
	/**
	 * オブジェクトを直列化してファイルに保存します。
	 * 衝突を避けるため.tmpファイルに保存後、リネームします。
	 * @param name 保存ファイル名
	 * @param obj シリアライズ可能なオブジェクト
	 * @return 保存の成否
	 */
	public boolean writeSerializable(String name, Object obj) {
		boolean ret = false;
		String path = this.serializeDir +File.separator + name;
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
			if (!file.renameTo(new File(this.serializeDir +File.separator + name))) {
				if (!file.delete()) {
					throw new IllegalStateException("Failure of delete: "+ file);
				}
				return ret;
			}
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
			if (file.isFile() && !file.delete()) {
				throw new IllegalStateException("Failure of delete: "+ file);
			}
			File file2 = new File(this.serializeDir +File.separator + name);
			if (file2.isFile() && !file2.delete()) {
				throw new IllegalStateException("Failure of delete: "+ file);
			}
		}
		return ret;
	}

	/**
	 * ファイルから直列化して保存されたオブジェクトを読み込みます。
	 * @param name ファイル名
	 * @param c 
	 * @return オブジェクト
	 */
	private <T> T readSerializable(String name, Class<T> c) {
		T ret = null;
		String path = this.serializeDir +File.separator + name;
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
				e.printStackTrace();
				ret = null;
				if (file.isFile() && !file.delete()) {
					throw new IllegalStateException("Failure of delete: "+ file);
				}
			}
		}
		return ret;
	}
}
