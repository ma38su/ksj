package database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

	/**
	 * 保存フォルダ
	 */
	private final String CACHE_DIR;

	/**
	 * コンストラクタ
	 * @param cacheDir データ格納ディレクトリ
	 * @param status ステータスバー
	 * @throws IOException 入出力エラー
	 */
	public KsjDataManager(String cacheDir) {
		this.CACHE_DIR = cacheDir;
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
		return new File(this.CACHE_DIR +File.separatorChar+ String.format("%02d" +File.separatorChar+ KSJ_TYPE_FORMAT[type] +"-%02d.zip", code, code));
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
		File zip = new File(this.CACHE_DIR +File.separatorChar+ KSJ_TYPE_FORMAT[type] +".zip");
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

}
