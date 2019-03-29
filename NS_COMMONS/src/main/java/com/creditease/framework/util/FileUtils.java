package com.creditease.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;



public class FileUtils {
	
	public static final  String CLASSPATH_ALL_URL_PREFIX = "classpath*:";
	
	/**
	 * 把一个文件保存到另一个文件
	 * 
	 * @param srcfile
	 * @param dstfile
	 * @throws IOException
	 */
	public static void saveAs(String srcfile, String dstfile, boolean append) throws IOException {
		// read data from srcfile and write to dstfile;
		try {
			byte bytes[] = new byte[1024 * 1024];
			int size = 0;
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(srcfile));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dstfile, append));
			int n = 0;
			while ((n = in.read(bytes)) != -1) {
				size += n;
				out.write(bytes, 0, n);
				out.flush();
			}
			in.close();
			out.close();
			File file = new File(dstfile);
			long dstLen = 0;
			if (file.isFile()) {
				dstLen = file.length();
			}
			System.out.println("[trace_saveas] [" + srcfile + "] [len:" + size + "] [" + dstfile + "] [dstLen:" + dstLen + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveAsNIO(String srcfile, String dstfile) {
		try {
			FileInputStream fileInputStream = new FileInputStream(srcfile);
			FileOutputStream fileOutputStream = new FileOutputStream(dstfile);
			FileChannel inChannel = fileInputStream.getChannel();
			FileChannel outChannel = fileOutputStream.getChannel();

			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

			int size = 0;
			while (true) {
				int eof = inChannel.read(byteBuffer);
				if (eof == -1)
					break;
				byteBuffer.flip();
				outChannel.write(byteBuffer);
				byteBuffer.clear();
				size += eof;
			}
			inChannel.close();
			outChannel.close();
			File file = new File(dstfile);
			long dstLen = 0;
			if (file.isFile()) {
				dstLen = file.length();
			}
			// System.out.println("[trace_saveasNIO] ["+srcfile+"]
			// [copylen:"+size+"] [dstLen:"+dstLen+"]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存一个inputstream到目标文件中
	 * 
	 * @param input
	 * @param dstfile
	 * @return
	 * @throws IOException
	 */
	public static String save(InputStream input, String dstfile) throws IOException {
		try {
			byte bytes[] = new byte[1024];
			int size = 0;
			String encodeFileName = null;
			try {
				encodeFileName = new String(dstfile.getBytes("gb2312"), "iso-8859-1");
			} catch (Exception e) {
				e.printStackTrace();
			}
			BufferedInputStream in = new BufferedInputStream(input);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(encodeFileName, false));
			int n = 0;
			while ((n = in.read(bytes)) != -1) {
				size += n;
				out.write(bytes, 0, n);
				out.flush();
			}
			in.close();
			out.flush();
			out.close();
			return dstfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getContent(InputStream input) throws IOException {
		try {
			byte bytes[] = new byte[1024];
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int n = 0;
			while ((n = input.read(bytes)) != -1) {
				out.write(bytes, 0, n);
				out.flush();
			}
			input.close();
			out.close();
			return out.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查是否为folder，如不是则创建
	 * 
	 * @param file
	 */
	public static void chkFolder(String file) {
		String str[] = file.split("/");
		String path = "";
		if(file.indexOf(":") != -1) {
			//windows系统
			path = file.substring(0,2);
		}
		for (int i = 1; i < str.length; i++) {
			if (str[i].indexOf(".") == -1 || file.substring((path + "/" + str[i]).length()).indexOf("/") != -1) {
				path = path + "/" + str[i];
				File nowfile = new File(path);
				if (!nowfile.isDirectory()) {
					nowfile.mkdir();
				}
			}
		}
	}

	public static String readFile(String file) {
		try {
			byte data[] = readFileData(file);
			return new String(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readFileData(String file) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			FileInputStream in = new FileInputStream(file);
			byte buffer[] = new byte[512];
			int n = 0;
			while ((n = in.read(buffer)) != -1) {
				bout.write(buffer, 0, n);
			}
			in.close();
			byte data[] = bout.toByteArray();
			bout.close();
			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] readFileData2(String file) {
		ArrayList<Byte> blist = new ArrayList<Byte>();
		try {
			FileInputStream in = new FileInputStream(file);
			byte buffer[] = new byte[512];
			int n = 0;
			while ((n = in.read(buffer)) != -1) {
				for (int i = 0; i < n; i++) {
					blist.add(new Byte(buffer[i]));
				}
			}
			in.close();
			byte data[] = new byte[blist.size()];
			for (int i = 0; i < data.length; i++) {
				data[i] = blist.get(i);
			}
			return data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static void writeFile(String content, String file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(content);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFile(String content, String file, boolean append) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, append));
			out.write(content);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFile(String file, byte[] data) {
		FileUtils.chkFolder(file);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveUrl(String url, String dstFile) {
		try {
			URL ourl = new URL(url);
			URLConnection con = ourl.openConnection();
			InputStream in = con.getInputStream();
			byte buffer[] = new byte[512];
			int n = 0;
			int total = 0;
			FileOutputStream fout = new FileOutputStream(dstFile);
			while ((n = in.read(buffer)) != -1) {
				total += n;
				fout.write(buffer, 0, n);
			}
			in.close();
			fout.close();
			System.out.println("download complete.[" + total + " bytes]");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void copy(File srcFile, File dstFile, boolean overwrite) throws Exception {
		if (srcFile.isDirectory()) {
			chkFolder(dstFile.getPath());
			File files[] = srcFile.listFiles();
			for (File f : files) {
				String subSrcF = f.getPath().substring(srcFile.getPath().length());
				String dstF = dstFile.getPath() + subSrcF;
				copy(f, new File(dstF), overwrite);
			}
		} else {
			saveAs(srcFile.getPath(), dstFile.getPath(), !overwrite);
		}
	}

	/**
	 * 删除这个文件或文件夹
	 * 
	 * @param file
	 */
	public static void remove(File file) {
		if (file.isDirectory()) {
			File files[] = file.listFiles();
			if (files != null) {
				for (File f : files) {
					remove(f);
				}
			}
		}
		file.delete();
	}

	/**
	 * 清空这个目录下的文件
	 * 
	 * @param file
	 */
	public static void clear(File file) {
		if (file.isDirectory()) {
			File files[] = file.listFiles();
			if (files != null) {
				for (File f : files) {
					remove(f);
				}
			}
		}
	}

	public static void main(String args[]) {
//		String path = args[0];
//		String newpath = args[1];
//		try {
//			File file = new File(path);
//			StringBuffer sb = new StringBuffer();
//			if (file.isDirectory()) {
//				File files[] = file.listFiles();
//				for (File f : files) {
//					String filename = f.getName();
//					filename = filename.replace('.', 'd');
//					String nfilepath = System.getProperty("user.dir") + "/" + newpath + "/" + filename;
//					nfilepath = new String(nfilepath.getBytes("GBK"), "UTF-8");
//					File dir = new File(nfilepath);
//					dir.mkdirs();
//
//					sb.append(nfilepath + "\n");
//					try {
//						// saveAs(f.getPath(), nfilepath, false);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			} else {
//			}
//			FileOutputStream fous = new FileOutputStream(new File("zzz.txt"));
//			OutputStreamWriter fw = new OutputStreamWriter(fous, "UTF-8");
//			fw.write(sb.toString());
//			fw.close();
//			System.out.println("System encoding is:" + System.getProperty("file.encoding"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
	
	public static void removePrefixFolder(File folder, String prefix) {
		if(folder.isDirectory()) {
			File files[] = folder.listFiles();
			for(File f : files) {
				if(f.getName().startsWith(prefix)) {
					removeFolder(f);
				}
			}
		}
	}
	
	public static void removeFolder(File folder) {
		File files[] = folder.listFiles();
		for(File f : files) {
			f.delete();
		}
		folder.delete();
	}
	
	public static String findFile(String name, String dstFolder) {
		File dst = new File(dstFolder);
		String matched = null;
		if(dst.isDirectory()) {
			File fs[] = dst.listFiles();
			for(File f : fs) {
				if(f.isFile()) {
					String n = f.getName();
					n = n.substring(0, n.lastIndexOf("."));
					if(name.equals(n)) {
						matched = f.getPath();
						break;
					}
				} else {
					String n = findFile(name, f.getPath());
					if(n != null) {
						matched = n;
						break;
					}
				}
			}
		}
		return matched;
	}
	
	/**
	 * 递归获得本目录下的所有文件
	 * @param path
	 * @return
	 */
	public static List<String> listAllFiles(String path) {
		List<String> list = new ArrayList<String>();
		File f = new File(path);
		if(f.isDirectory()) {
			File fs[] = f.listFiles();
			for(File file : fs) {
				collectFiles(file.getPath(), list);
			}
		}
		return list;
	}
	
	public static void collectFiles(String path, List<String> collection) {
		File f = new File(path);
		if(f.isDirectory()) {
			File fs[] = f.listFiles();
			for(File file : fs) {
				collectFiles(file.getPath(), collection);
			}
		} else {
			collection.add(path);
		}
	}
	
	/**
	 * 获得磁盘的使用比例
	 * @param diskFilePath 磁盘上某一个目录
	 * @return
	 */
	public static double getDiskUsage(String diskFilePath) {
		File f = new File(diskFilePath);
		if(f.exists()) {
			long total = f.getTotalSpace();
			if(total > 0) {
				long free = f.getFreeSpace();
				long used = total - free;
				used = used < 0 ? 0 : used;
				double usage = new Double(used) / new Double(total); 
				return usage;
			}
		}
		return 0;
	}
	
	public static String convertToAbsolutePath(String resoucePath)
	{
		String path = resoucePath;
		
		File f = new File(resoucePath);
		
		if (f.isAbsolute()) 
		{
			return path;
		}
		
		if (path.startsWith("/")) {
			path = path.substring(1);
		}	
		return getDefaultClassLoader().getResource(path).getPath();
	}
	
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = FileUtils.class.getClassLoader();
		}
		return cl;
	}
	
}
