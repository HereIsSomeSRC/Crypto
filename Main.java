import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
	public static String lastm = "";
	public static int rndShit = randomshit();
	public static int rndShit2 = randomshit2();
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, UnsupportedEncodingException {
		Scanner sc = new Scanner(System.in, "UTF-8");
		System.setOut(new PrintStream(System.out, true, "UTF-8"));
		help();
		while (true) {
			if (!sc.hasNextLine()) {
				sleep();
				continue;
			}
			String s = sc.nextLine();
			args = s.split(" ");
			if (args.length < 3) {
				help();
				sleep();
				continue;
			}
			//--encrypt 03458 []
			//--decrypt 03458 []
			int key = Integer.parseInt(args[1]);
			int methodname = methodname();
			int it = linenumb();
			String data = "";
			for (int i = 2;i < args.length;i++) {
				data += args[i] + " ";
			}
			data = data.substring(0, data.length() - 1);
			if (args[0].equalsIgnoreCase("encrypt")) {
				byte[] crypt = encrypt(data, methodname, it, key).getBytes(StandardCharsets.UTF_16);
				String hex = toHex(crypt);
				System.out.println("------Encrypted------");
				System.out.println(hex);
				System.out.println("---------------------");
			}else if (args[0].equalsIgnoreCase("decrypt")) {
				byte[] crypted = hexStringToByteArray(data);
				String decrypted = decrypt(new String(crypted,StandardCharsets.UTF_16), methodname, it, key);
				System.out.println("------Decrypted------");
				System.out.println(decrypted);
				System.out.println("---------------------");
			}else {
				help();
			}
		}
	}
	public static String toHex(byte[] bytes) {
		final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	public static void help() {
		System.out.println("------------------------------------");
		System.out.println("encrypt <key> <string>");
		System.out.println("decypt <key> <data (format [FFFF]>");
		System.out.println("------------------------------------");
	}
	public static void sleep() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
	}
	public static byte[] parseData(String in) {
		in = in.replace("[", "").replace("]", "");
		String[] array = in.split(",");
		byte[] bytes = new byte[array.length];
		for (int i = 0;i < array.length;i++) {
			String b = array[i];
			b = b.replace(" ", "");
			byte by = Byte.parseByte(b);
			bytes[i] = by;
		}
		return bytes;
	}
	public static String encrypt(String xored,int methodname,int it,int key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for (int i = 0;i < it;i++) {
			xored = xor(xored, methodname + i + key);
		}
		return xored;
	}
	public static String decrypt(String xored,int methodname,int it,int key) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for (int i = it - 1;i >= 0;i--) {
			xored = xor(xored, methodname + i + key);
		}
		return xored;
	}
	public static String xor(String in,int key) {
		String out = "";
		for (int i = 0;i < in.length();i++) {
			//System.out.println(key);
			out += Character.toString((char) (((int)in.charAt(i)) ^ key));
			try {
				int methodname = methodname();
				int line = linenumb();
				key -= methodname;
				key += rndShit - rndShit2;
				if (out.length() >= line + methodname - (methodname % 2)) key *= line + methodname;
				int e = line + methodname;
				key += e;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				e.printStackTrace();
			}
		}
		return out;
	}
	public static int methodname() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Thread thread = Thread.currentThread();
		Class clazz = thread.getClass();
		Method m = clazz.getDeclaredMethods()[16];
		m.setAccessible(true);
		Object o = ((Object[]) m.invoke(thread, null))[3];
		clazz = o.getClass();
		Field f = clazz.getDeclaredFields()[1];
		f.setAccessible(true);
		String name = (String) f.get(o);
		int i = 0;
		for (int i1 = 0;i < name.length();i++) {
			i+= name.charAt(i1);
		}
		return i;
	}
	public static Object[] get(Class clazz,Object obj,int index) {
		try {
			return (Object[]) clazz.getDeclaredMethods()[index].invoke(obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static int linenumb() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Throwable ex = new Throwable();
		Class clazz = ex.getClass();
		Method getStackTraceElement = (Method) (get(Class.class,clazz,65)[15]);
		getStackTraceElement.setAccessible(true);
		Object stacktraceelement = getStackTraceElement.invoke(ex, 1);
		Field field = stacktraceelement.getClass().getDeclaredFields()[3];
		field.setAccessible(true);
		int linenumb = (int) field.get(stacktraceelement);
		return linenumb;
	}
	public static int linenumb_methodname() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Throwable ex = new Throwable();
		Class clazz = ex.getClass();
		Method getStackTraceElement = (Method) (get(Class.class,clazz,65)[15]);
		getStackTraceElement.setAccessible(true);
		Object stacktraceelement = getStackTraceElement.invoke(ex, 1);
		Field field = stacktraceelement.getClass().getDeclaredFields()[3];
		field.setAccessible(true);
		int linenumb = (int) field.get(stacktraceelement);
		//		System.out.println("Linenumb1 " + new Throwable().getStackTrace()[1].getLineNumber());
		//		System.out.println("Linenumb " + linenumb);
		linenumb += methodname();
		return linenumb;
	}
	public static int randomshit() {
		int x = 25;
		String in = new String(new byte[] {125, 124, 122, 107, 96, 105, 109});
		String out = "";
		for (int i = 0;i < in.length();i++) {
			char c = in.charAt(i);
			out += Character.toString((char) (c ^ x));
		}
		return out.length();
	}
	public static int randomshit2() {
		int x = 25;
		String in = new String(new byte[] {101, 110, 116, 115, 99, 104, 108, 117, 101, 115, 115, 101, 108, 110});
		String out = "";
		for (int i = 0;i < in.length();i++) {
			char c = in.charAt(i);
			out += Character.toString((char) (c ^ x));
		}
		int r = 0;
		for (int i = 0;i < out.length();i++) {
			char c = in.charAt(i);
			r += c;
		}
		return r;
	}
}
