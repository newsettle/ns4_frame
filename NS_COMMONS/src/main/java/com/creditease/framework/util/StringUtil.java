package com.creditease.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtil {

	public static String[] PROVINCE_ZH = { "北京", "天津", "河北", "山西", "内蒙古", "辽宁",
			"吉林", "黑龙江", "上海", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北",
			"湖南", "广东", "广西", "海南", "重庆", "四川", "贵州", "云南", "西藏", "陕西", "甘肃",
			"青海", "宁夏", "新疆" };
	public static String[] PROVINCE_EN = { "beijing", "tianjin", "hebei",
			"shanxi", "neimenggu", "liaoning", "jilin", "heilongjiang",
			"shanghai", "jiangsu", "zhejiang", "anhui", "fujian", "jiangxi",
			"shandong", "henan", "hubei", "hunan", "guangdong", "guangxi",
			"hainan", "chongqing", "sichuan", "guizhou", "yunnan", "xizang",
			"shanxi1", "gansu", "qinghai", "ningxia", "xinjiang" };

	/**
	 * 给整数增加逗号，方便阅读 比如1234567763 -> 1,234,567,763
	 * 
	 * @param l 
	 * @return
	 */
	public static String addcommas(long l) {
		String s = "";
		String number = "" + l;
		char chars[] = number.toCharArray();
		int k = 0;
		for (int i = chars.length - 1; i >= 0; i--) {
			s = chars[i] + s;
			k++;
			if (k % 3 == 0 && i > 0) {
				s = "," + s;
			}
		}
		return s;
	}

	/**
	 * 给整数增加逗号，方便阅读 比如1234567763 -> 1,234,567,763
	 * 
	 * @param l
	 * @return
	 */
	public static String addcommas(int l) {
		return addcommas((long) l);
	}

	public static int MAX_SDFMAP_SIZE = 1024;
	private static Hashtable sdfMap = new Hashtable();

	/**
	 * 格式化日期显示
	 * 
	 * @param d
	 * @param format
	 * @return
	 */
	public static String formatDate(java.util.Date d, String format) {
		if (format == null || format.trim().length() == 0) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat f = (SimpleDateFormat) sdfMap.get(format);
		if (f == null) {
			try {
				f = new SimpleDateFormat(format);
			} catch (Exception e) {
				return d.toString();
			}
			sdfMap.put(format, f);
			if (sdfMap.size() > MAX_SDFMAP_SIZE) {
				sdfMap.clear();
			}
		}
		return f.format(d);
	}

	/**
	 * 解析格式化的日期，如果格式错误，返回null.
	 * 
	 * @param formatedDate
	 *            格式化后的日期
	 * @param format
	 *            格式
	 * @return
	 */
	public static Date parseDate(String formatedDate, String format) {
		if (format == null || format.trim().length() == 0) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat f = (SimpleDateFormat) sdfMap.get(format);
		if (f == null) {
			try {
				f = new SimpleDateFormat(format);
			} catch (Exception e) {
				return null;
			}
			sdfMap.put(format, f);
			if (sdfMap.size() > MAX_SDFMAP_SIZE) {
				sdfMap.clear();
			}
		}
		try {
			return f.parse(formatedDate);
		} catch (Exception e) {
			return null;
		}
	}

	public static int parseInt(String str, int defaultValue) {

		int ret = 0;
		try {
			ret = Integer.parseInt(str);
		} catch (Exception e) {
			ret = defaultValue;
		}

		return ret;
	}

	public static float parseFloat(String str, float defaultValue) {

		float ret = 0;
		try {
			ret = Float.parseFloat(str);
		} catch (Exception e) {
			ret = defaultValue;
		}

		return ret;
	}

	/**
	 * Replaces all instances of oldString with newString in line.
	 * 
	 * @param line
	 *            the String to search to perform replacements on
	 * @param oldString
	 *            the String that should be replaced by newString
	 * @param newString
	 *            the String that will replace all instances of oldString
	 * @return a String will all instances of oldString replaced by newString
	 */
	public static final String replace(String line, String oldString,
			String newString) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}
		return line;
	}

	/**
	 * Used by the hash method.
	 */
	private static MessageDigest digest = null;

	/**
	 * Hashes a String using the Md5 algorithm and returns the result as a
	 * String of hexadecimal numbers. This method is synchronized to avoid
	 * excessive MessageDigest object creation. If calling this method becomes a
	 * bottleneck in your code, you may wish to maintain a pool of MessageDigest
	 * objects instead of using this method.
	 * <p>
	 * A hash is a one-way function -- that is, given an input, an output is
	 * easily computed. However, given the output, the input is almost
	 * impossible to compute. This is useful for passwords since we can store
	 * the hash and a hacker will then have a very hard time determining the
	 * original password.
	 * <p>
	 * In Jive, every time a user logs in, we simply take their plain text
	 * password, compute the hash, and compare the generated hash to the stored
	 * hash. Since it is almost impossible that two passwords will generate the
	 * same hash, we know if the user gave us the correct password or not. The
	 * only negative to this system is that password recovery is basically
	 * impossible. Therefore, a reset password method is used instead.
	 * 
	 * @param data
	 *            the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public synchronized static final String hash(String data) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. "
						+ "Jive will be unable to function normally.");
				nsae.printStackTrace();
			}
		}
		// Now, compute hash.
		digest.update(data.getBytes());
		return toHex(digest.digest());
	}
	

	public synchronized static final String hash(byte data[]) {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. "
						+ "Jive will be unable to function normally.");
				nsae.printStackTrace();
			}
		}
		// Now, compute hash.
		digest.update(data);
		return toHex(digest.digest());
	}
	

	public synchronized static final String hash(String data,String encoding) throws UnsupportedEncodingException {
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("Failed to load the MD5 MessageDigest. "
						+ "Jive will be unable to function normally.");
				nsae.printStackTrace();
			}
		}
		// Now, compute hash.
		if(encoding!=null&&encoding.length()>0){
			digest.update(data.getBytes(encoding));
		}else{
			digest.update(data.getBytes());
		}
		return toHex(digest.digest());
	}
	
	public static String getFileMD5(File file) {  
        if (!file.exists() || !file.isFile()) {  
            return null;  
        }  
        MessageDigest digest = null;  
        FileInputStream in = null;  
        byte buffer[] = new byte[1024];  
        int len;  
        try {  
            digest = MessageDigest.getInstance("MD5");  
            in = new FileInputStream(file);  
            while ((len = in.read(buffer, 0, 1024)) != -1) {  
                digest.update(buffer, 0, len);  
            }  
            in.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
        BigInteger bigInt = new BigInteger(1, digest.digest());  
        return bigInt.toString(16);  
    }  

	/**
	 * Turns an array of bytes into a String representing each byte as an
	 * unsigned hex number.
	 * <p>
	 * Method by Santeri Paavolainen, Helsinki Finland 1996<br>
	 * (c) Santeri Paavolainen, Helsinki Finland 1996<br>
	 * Distributed under LGPL.
	 * 
	 * @param hash
	 *            an rray of bytes to convert to a hex-string
	 * @return generated hex string
	 */
	public static final String toHex(byte hash[]) {
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}
		return buf.toString();
	}

	/**
	 * Pseudo-random number generator object for use with randomString(). The
	 * Random class is not considered to be cryptographically secure, so only
	 * use these random Strings for low to medium security applications.
	 */
	private static java.util.Random randGen = new java.util.Random();

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
			+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
	private static char[] numbers = ("0123456789").toCharArray();;

	// private static Object initLock = new Object(){};

	/**
	 * Returns a random String of numbers and letters of the specified length.
	 * The method uses the Random class that is built-in to Java which is
	 * suitable for low to medium grade security uses. This means that the
	 * output is only pseudo random, i.e., each number is mathematically
	 * generated so is not truly random.
	 * <p>
	 * 
	 * For every character in the returned String, there is an equal chance that
	 * it will be a letter or number. If a letter, there is an equal chance that
	 * it will be lower or upper case.
	 * <p>
	 * 
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * 
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static final String randomString(int length) {
		if (length < 1) {
			return "";
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	/**
	 * Returns a random String of numbers and letters of the specified length.
	 * The method uses the Random class that is built-in to Java which is
	 * suitable for low to medium grade security uses. This means that the
	 * output is only pseudo random, i.e., each number is mathematically
	 * generated so is not truly random.
	 * <p>
	 * 
	 * For every character in the returned String, there is an equal chance that
	 * it will be a letter or number. If a letter, there is an equal chance that
	 * it will be lower or upper case.
	 * <p>
	 * 
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * 
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static final String randomIntegerString(int length) {
		if (length < 1) {
			return "";
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbers[randGen.nextInt(9)];
		}
		return new String(randBuffer);
	}

	/**
	 * Escapes all necessary characters in the String so that it can be used in
	 * an XML doc.
	 * 
	 * @param string
	 *            the string to escape.
	 * @return the string with appropriate characters escaped.
	 */
	public static final String escapeForXML(String string) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (string == null || string.length() == 0) {
			return string;
		}
		char[] sArray = string.toCharArray();
		StringBuffer buf = new StringBuffer(sArray.length);
		char ch;
		for (int i = 0; i < sArray.length; i++) {
			ch = sArray[i];
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else if (ch == '"') {
				buf.append("&quot;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
	 * &lt;table&gt;, etc) and converts the '&lt'' and '&gt;' characters to
	 * their HTML escape sequences.
	 * 
	 * @param input
	 *            the text to be converted.
	 * @return the input string with the characters '&lt;' and '&gt;' replaced
	 *         with their HTML escape sequences.
	 */
	public static final String escapeForHTML(String input) {
		// Check if the string is null or zero length -- if so, return
		// what was sent in.
		if (input == null || input.length() == 0) {
			return input;
		}
		// Use a StringBuffer in lieu of String concatenation -- it is
		// much more efficient this way.
		StringBuffer buf = new StringBuffer(input.length());
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '&') {
				buf.append("&amp;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	public static boolean arrayContains(int[] arr, int num) {
		for (int i = 0; i < arr.length; i++) {
			if (i == num) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据字符拆分字符串，此方法要比字符串的split方法效率高，当大量字符串需要拆分 的时候，建议使用此方法。
	 * 
	 * 比如： dsjflads@@dfafasfjlaks@dfdsf@@@dff@@dfdfdf@ 用字符@拆分，结果是：
	 * String[]{"dsjflads","dfafasfjlaks","dfdsf","dff","dfdfdf"}
	 * 
	 * @param content
	 * @param separator
	 * @return
	 */
	public static String[] split(String content, char separator) {
		ArrayList<String> al = new ArrayList<String>();
		char ch[] = content.toCharArray();
		int k = 0;
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == separator) {
				if (k < i) {
					al.add(new String(ch, k, i - k));
				}
				k = i + 1;
			}
		}
		if (k < ch.length) {
			al.add(new String(ch, k, ch.length - k));
		}
		String ret[] = new String[al.size()];
		for (int i = 0; i < al.size(); i++) {
			ret[i] = al.get(i);
		}
		return ret;
	}

	/**
	 * 
	 * @param strContent
	 *            content to parse
	 * @param strRegEx
	 *            reg pattern format
	 * @return a string which is group with the pattern matcher;
	 */
	public static String[] regDOTALLParseString(String strContent,
			String strRegEx) {
		Pattern myPattern;
		myPattern = Pattern.compile(strRegEx, Pattern.DOTALL);
		Matcher oMatcher = myPattern.matcher(strContent);
		if (!oMatcher.matches()) {
			return new String[0];

		}
		int iLength = oMatcher.groupCount();
		ArrayList al = new ArrayList();

		for (int i = 0; i <= iLength; i++) {
			try {
				al.add(oMatcher.group(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return (String[]) al.toArray(new String[0]);
	}

	/**
	 * 判断一个字符串是否为一个数的字符串. 比如 a12345 不是 12345 是 01230 是 .1234 是 123.12 是 1.2.3 不是
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNumberStr(String s) {
		if (s == null || s.trim().length() == 0)
			return false;
		s = s.trim();
		int count = 0;
		char ch[] = s.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (!Character.isDigit(ch[i])) {
				if (ch[i] != '.')
					return false;
				count++;
			}
		}
		if (count < 2)
			return true;
		return false;
	}

	/**
	 * 判断一个字符串是否为标准的标识符，所谓标准的标识符必须满足： 1. 以字母开头 2. 由数字，字母，"_"，组成。
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isValidIdentity(String s) {
		if (s == null || s.length() == 0)
			return false;
		char ch[] = s.toCharArray();
		if (!(ch[0] >= 'a' && ch[0] <= 'z') && !(ch[0] >= 'A' && ch[0] <= 'Z'))
			return false;
		for (int i = 1; i < ch.length; i++) {
			if (!(ch[i] >= 'a' && ch[i] <= 'z')
					&& !(ch[i] >= 'A' && ch[i] <= 'Z')
					&& !(ch[i] >= '0' && ch[i] <= '9') && !(ch[i] == '_')
					&& !(ch[i] == '-'))
				return false;
		}
		return true;
	}

	/**
	 * Java中的escape
	 * 
	 * @param src
	 * @return
	 */
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);

		for (i = 0; i < src.length(); i++) {

			j = src.charAt(i);

			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	/**
	 * Java中的unescape
	 * 
	 * @param src
	 * @return
	 */
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src
							.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src
							.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

	/**
	 * 获得异常堆栈的string
	 * 
	 * @param e
	 * @return
	 */
	public static String getStackTrace(Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append(e.toString() + "\n");
		StackTraceElement[] stacks = e.getStackTrace();
		return getStackTrace(stacks);
	}

	public static String getStackTrace(StackTraceElement stacks[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < stacks.length; i++) {
			sb.append("\t" + stacks[i].toString() + "\n");
		}
		return sb.toString();
	}

	public static String clearLetters(String str) {
		return str.replaceAll("[a-zA-Z]", "");
	}

	private static final char[] HEXDUMP_TABLE = new char[256 * 4];

	static {
		final char[] DIGITS = "0123456789abcdef".toCharArray();
		for (int i = 0; i < 256; i++) {
			HEXDUMP_TABLE[(i << 1) + 0] = DIGITS[i >>> 4 & 0x0F];
			HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i >>> 0 & 0x0F];
		}
	}

	public static short getUnsignedByte(byte index) {
		return (short) (index & 0xFF);
	}

	/**
	 * Returns a <a href="http://en.wikipedia.org/wiki/Hex_dump">hex dump</a>
	 * of the specified buffer's sub-region.
	 */
	public static String hexDump(byte bytes[], int fromIndex, int length) {
		if (length < 0) {
			throw new IllegalArgumentException("length: " + length);
		}
		if (length == 0) {
			return "";
		}

		int endIndex = fromIndex + length;
		char[] buf = new char[length << 1];

		int srcIdx = fromIndex;
		int dstIdx = 0;
		for (; srcIdx < endIndex; srcIdx++, dstIdx += 2) {
			System.arraycopy(HEXDUMP_TABLE,
					getUnsignedByte(bytes[srcIdx]) << 1, buf, dstIdx, 2);
		}

		return new String(buf);
	}

	public static String arrayToString(String[] array, String token) {
		if (array == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i != array.length - 1)
				sb.append(token);
		}
		return sb.toString();
	}
	
	public static String arrayToString(long[] array, String token) {
		if (array == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i != array.length - 1)
				sb.append(token);
		}
		return sb.toString();
	}

	public static String arrayToString(int[] array, String token) {
		if (array == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i != array.length - 1)
				sb.append(token);
		}
		return sb.toString();
	}

	public static String arrayToString(short[] array, String token) {
		if (array == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i != array.length - 1)
				sb.append(token);
		}
		return sb.toString();
	}

	public static String arrayToString(float[] array, String token) {
		if (array == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i != array.length - 1)
				sb.append(token);
		}
		return sb.toString();
	}

	public static String native2ascii(String content) {
		String tmp;
		StringBuffer sb = new StringBuffer();
		char c;
		int i, j;
		for (i = 0; i < content.length(); i++) {
			c = content.charAt(i);
			if (c > 255) {
				sb.append("\\\\u");
				j = (c >>> 8);
				tmp = Integer.toHexString(j);
				if (tmp.length() == 1)
					sb.append("0");
				sb.append(tmp);
				j = (c & 0xFF);
				tmp = Integer.toHexString(j);
				if (tmp.length() == 1)
					sb.append("0");
				sb.append(tmp);
			} else {
				sb.append(c);
			}

		}
		return (new String(sb));
	}
	
	public static String stackTraceToString(StackTraceElement[] stacks) {
		StringBuffer sb = new StringBuffer();
        for(int i=0;i<stacks.length; i++) {
        	sb.append("\t" + stacks[i].toString()+"\n");
        }
        return sb.toString();
	}
	
	public static String encode(String s, String srcEncoding, String dstEncoding) {
		try {
			byte[] bytes = s.getBytes(srcEncoding);
			return new String(bytes, dstEncoding);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String urlEncode(String url) {
		String paStr = url;
		if(url.indexOf("?") != -1) {
			paStr = url.split("\\?")[1];
		} else {
			return url;
		}
		String params[] = paStr.split("&");
		StringBuffer sb = new StringBuffer();
		for(String p : params) {
			try {
				String param = p.split("=")[0];
				String value = p.split("=")[1];
				sb.append(param);
				sb.append("=");
				sb.append(java.net.URLEncoder.encode(value));
				sb.append("&");
			} catch(Exception e) {
				System.err.print(url);
				e.printStackTrace();
			}
		}
		String str = sb.toString();
		if(str.endsWith("&")) {
			str = str.substring(0, str.length()-1);
		}
		if(url.indexOf("?") != -1) {
			url = url.split("\\?")[0]+"?"+str;
		}
		return url;
	}
	
	public static String urlDecode(String content, String encoding) {
		try {
			return java.net.URLDecoder.decode(content, encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
	public static String mapToString(Map map) {
		Object keys[] = map.keySet().toArray(new Object[0]);
		StringBuffer sb = new StringBuffer();
		for(Object k : keys) {
			String keystr = String.valueOf(k);
			Object value = map.get(k);
			if(value != null) {
				String vstr = String.valueOf(value);
				sb.append(keystr + "#@#" + vstr + "@#@");
			}
		}
		String str = sb.toString();
		if(str.length() > 2) {
			str = str.substring(0, str.length()-3);
		}
		return str;
	}
	
	public static Map stringToMap(String mstr) {
		String str[] = mstr.split("@#@");
		HashMap<String, String> map = new HashMap<String,String>();
		for(String s : str) {
			String ss[] = s.split("#@#");
			if(ss.length == 2) {
				map.put(ss[0], ss[1]);
			}
		}
		return map;
	}
	
	public static String propertiesToString(Properties map) {
		Object keys[] = map.keySet().toArray(new Object[0]);
		StringBuffer sb = new StringBuffer();
		for(Object k : keys) {
			String keystr = String.valueOf(k);
			Object value = map.get(k);
			if(value != null) {
				String vstr = String.valueOf(value);
				sb.append(keystr + "#@#" + vstr + "@#@");
			}
		}
		String str = sb.toString();
		if(str.length() > 2) {
			str = str.substring(0, str.length()-3);
		}
		return str;
	}
	
	public static Properties stringToProperties(String mstr) {
		String str[] = mstr.split("@#@");
		Properties map = new Properties();
		for(String s : str) {
			String ss[] = s.split("#@#");
			if(ss.length == 2) {
				map.put(ss[0], ss[1]);
			}
		}
		return map;
	}
 
	public static Properties loadProperties(String file) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}
	
	public static String ascii2native(String content) {
		char cs[] = new char[content.length()];
		for(int i=0; i<cs.length; i++) {
			cs[i] = content.charAt(i);
		}
		return loadConvert(cs, 0, cs.length, new char[cs.length*2]);
	}
	
	/*
     * Converts unicodes to encoded &#92;uxxxx and escapes
     * special characters with a preceding slash
     */
	private static String loadConvert (char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
	        newLen = Integer.MAX_VALUE;
	    } 
	    convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf; 
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];   
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
		    for (int i=0; i<4; i++) {
		        aChar = in[off++];  
		        switch (aChar) {
		          case '0': case '1': case '2': case '3': case '4':
		          case '5': case '6': case '7': case '8': case '9':
		             value = (value << 4) + aChar - '0';
			     break;
			  case 'a': case 'b': case 'c':
                          case 'd': case 'e': case 'f':
			     value = (value << 4) + 10 + aChar - 'a';
			     break;
			  case 'A': case 'B': case 'C':
                          case 'D': case 'E': case 'F':
			     value = (value << 4) + 10 + aChar - 'A';
			     break;
			  default:
                              throw new IllegalArgumentException(
                                           "Malformed \\uxxxx encoding.");
                        }
                     }
                    out[outLen++] = (char)value;
                } else {
                    if (aChar == 't') aChar = '\t'; 
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f'; 
                    out[outLen++] = aChar;
                }
            } else {
	        out[outLen++] = (char)aChar;
            }
        }
        return new String (out, 0, outLen);
    }
	
	public static String formatNum(int value, int num) {
		String s = String.valueOf(value);
		while(s.length() < num) {
			s = "0" + s;
		}
		return s;
	}
	
	public static String formatNum(long value, int num) {
		String s = String.valueOf(value);
		while(s.length() < num) {
			s = "0" + s;
		}
		return s;
	}
	
	/**
	 * 连接两个字符串成一个指定长度的串，中间不足的以0补位:
	 * 							12，5564332两个串合成长度为10的串: 1205564322
	 * @param str1
	 * @param str2
	 * @param length
	 * @return
	 */
	public static String concactAsNumer(String str1, String str2, int length) {
		int wlen = length  - str1.length() - str2.length();
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<wlen; i++) {
			sb.append("0");
		}
		String newstr = str1 +  sb.toString() + str2;
		return newstr;
	}
	
	/**
     * 对字符串加密,加密算法使用MD5,SHA-1,SHA-256,默认使用SHA-256
     * 
     * @param strSrc
     *            要加密的字符串
     * @param encName
     *            加密类型
     * @return
	 * @throws NoSuchAlgorithmException 
     */
    public static String encrypt(String strSrc, String encName) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        if (encName == null || encName.equals("")) {
            encName = "SHA-256";
        }
        md = MessageDigest.getInstance(encName);
        md.update(bt);
        strDes = toHex(md.digest()); // to HexString
        return strDes;
    }
    
    
    /**
     * 得到一个字符串的ascii码连接的串
     * @param content
     * @return
     */
    public static String getAsciiCode(String content) {
    	StringBuffer sb = new StringBuffer();
    	int len = content.length();
    	for(int i=0; i<len; i++) {
    		char c = content.charAt(i);
    		int asciiCode = c;
    		sb.append(String.valueOf(asciiCode));
    	}
    	return sb.toString();
    }
    
    /**
     * 得到一个字符串的ascii码个位组成的串
     * @param content
     * @return
     */
    public static String getAsciiCode4Low(String content) {
    	StringBuffer sb = new StringBuffer();
    	int len = content.length();
    	for(int i=0; i<len; i++) {
    		char c = content.charAt(i);
    		int asciiCode = c;
    		sb.append(String.valueOf(asciiCode%10));
    	}
    	return sb.toString();
    }

    
	public static boolean isEmpty(String s) {
		if (s == null || s.trim().length() == 0)
			return true;
		else 
		{
			return false;
		}
	}
	
	public static void main(String args[]) {
		String file = "E:/卧虎藏龙/服务器语言包20141029.xls";
		long now = System.currentTimeMillis();
		String md5 = StringUtil.getFileMD5(new File(file));
		System.out.println("["+md5+"] ["+(System.currentTimeMillis()-now)+"ms]");
	}
}
