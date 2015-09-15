package org.gene.modules.textFile.charset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.gene.modules.validation.Check;


public class CharsetUtils
{
	public static final String SYSTEM_CHARSET = "ISO-8859-1";
	private static final String[] DEFAULT_PRIORITY= new String[]{"EUC-KR", "UTF-8"};
	private static final int MAX_BUFFER_SIZE = 4096;

	public static String detectFileEncoding(File file) throws IOException
	{
		String argError = (file==null ? "is null" : !file.exists() ? "does not exist" : null);
		if (argError!=null)
		{
			throw new IllegalArgumentException("file "+argError);
		}

		long fileSize = file.length();
		int bufferSize = fileSize > MAX_BUFFER_SIZE ? MAX_BUFFER_SIZE : (int) fileSize;
		byte[] buffer = new byte[bufferSize];

		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(buffer);
		fileInputStream.close();

		return detectEncoding(buffer);
	}

	public static String detectFileEncoding(String path) throws IOException
	{
		String argError = (path==null ? "null" : "".equals(path) ? "empty" : null);
		if (argError!=null)
		{
			throw new IllegalArgumentException("path of file is "+argError);
		}

		return detectFileEncoding(new File(path));
	}

	/**
	 * Detect encoding of given data.
	 * @param data (byte[]) byte array to detect encoding
	 * @param guesses (String[]) Encodings most likely be in order. Encoding in highest possibility comes first.
	 * @throws IllegalArgumentException
	 * @return Encoding name detected
	 */
	public static String detectEncoding(byte[] data, String... guesses)
	{
		String argError = (data==null ? "null" : data.length==0 ? "empty" : null);
		if (argError!=null)
		{
			throw new IllegalArgumentException("Given byte array is "+argError+", could not detect encoding.");
		}

		CharsetDetector detector = new CharsetDetector();
		detector.setText(data);
		CharsetMatch[] cm = detector.detectAll();

		String charset = null;
		if(cm!=null && cm.length>0)
		{
			if(guesses!=null && guesses.length>0)
			{
				for(int i=0; i<cm.length; ++i)
				{
					String candidate = cm[i].getName().replace("-","");
					for(int j=0; j<guesses.length; ++j)
					{
						String guess = guesses[j].toUpperCase().replace("-", "").replace("_", "");
						if(candidate.contains(guess))
						{
							charset = cm[i].getName();
							break;
						}
					}
				}
			}
			else
			{
				charset = cm[0].getName();
			}
		}

		return charset;
	}

	public static String detectEncoding(byte[] data)
	{
		return detectEncoding(data, DEFAULT_PRIORITY);
	}

	public static String detectEncoding(String str) throws UnsupportedEncodingException
	{
		String charset = null;
		if(str!=null && !"".equals(str))
		{
			charset = detectEncoding(str.getBytes(SYSTEM_CHARSET));
		}
		return charset;
	}

	public static String fixEncoding(String str) throws UnsupportedEncodingException
	{
		String result = str;
		if(str!=null && !"".equals(str))
		{
			String charset = detectEncoding(str);
			if(!SYSTEM_CHARSET.equals(charset))
			{
				result = changeEncoding(str, charset);
			}
		}

		return result;
	}

	public static String changeEncoding(String str, String oldCharset, String newCharset) throws UnsupportedEncodingException
	{
		String result = str;
		if(Check.isNotBlank(str, oldCharset, newCharset) && !oldCharset.equalsIgnoreCase(newCharset))
		{
			result = new String(str.getBytes(oldCharset),newCharset);
		}

		return result;
	}

	public static String changeEncoding(String str, String newCharset) throws UnsupportedEncodingException
	{
		return changeEncoding(str, SYSTEM_CHARSET, newCharset);
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] normalize(Object[] args, Class<T> clazz)
	{
		T[] result = null;
		if(args!=null)
		{
			List<Object> list = new ArrayList<Object>();
			list.addAll(Arrays.asList(args));

			for(int i=0; i<list.size(); ++i)
			{
				if(list.get(i) instanceof Object[])
				{
					Object element = list.remove(i);
					list.addAll(i, Arrays.asList((Object[]) element));
					i--;
				}
			}

			result = (T[]) Array.newInstance(clazz, list.size());
			for(int i=0; i<result.length; ++i)
			{
				result[i] =clazz.cast(list.get(i));
			}
		}

		return result;
	}


	public static void t1(String[] args) throws IOException
	{
//		System.out.println(detectEncoding("할렐루야"));
//		System.out.println(correct("할렐루야"));

		RandomAccessFile file = new RandomAccessFile("src/org/gene/modules/textFile/test/utf8_sample.txt", "rwd");
		byte[] b = new byte[(int)file.length()];
		file.read(b);
		String charset = detectEncoding(b, new String[]{"euc_kr", "utf8"});
		String line = new String(b, charset);
		System.out.println(line);

		Object[] result = normalize(new Object[]{new Object[]{"a", "b"}, "c", "d"}, String.class);
		for(int i=0; i<result.length; ++i)
		{
			System.out.println(result[i]);
		}
	}

	public static void t2(String[] args) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile("src/org/gene/modules/textFile/test/euc_kr_sample.txt", "rwd");
		String str = file.readLine();
		System.out.println(str);
		System.out.println(detectEncoding(str));
		System.out.println(fixEncoding(str));
	}

	public static void t3(String[] args) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile("src/org/gene/modules/textFile/test/utf8_sample.txt", "rwd");
		byte[] b = new byte[(int)file.length()];
		file.read(b);
		String charset = detectEncoding(b, new String[]{"euc_kr", "utf8"});
		System.out.println(charset);
	}

	public static void t0(String[] args) throws IOException
	{
		String[] result = normalize(new Object[]{new Object[]{"a", "b"}, "c", "d"}, String.class);
		for(int i=0; i<result.length; ++i)
		{
			System.out.println(result[i]);
		}
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println(detectFileEncoding("src/org/gene/modules/textFile/test/euc_kr_sample.txt"));
		System.out.println("\uF0908080");
	}
}