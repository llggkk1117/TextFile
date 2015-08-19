package org.gene.modules.textFile.charset;

import java.io.UnsupportedEncodingException;

import org.gene.modules.validation.Check;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;


public class CharsetUtils
{
	public static final String DEFAULT_CHARSET = "ISO-8859-1"; //8859_1

	public static String detectCharset(String str)
	{
		String charsetName = null;
		if(str!=null && !"".equals(str))
		{
			CharsetDetector detector = new CharsetDetector();
			detector.setText(str.getBytes());
			CharsetMatch match = detector.detect();
			charsetName = match.getName();

			System.out.println("--->"+charsetName);
		}
		return charsetName;
	}

	public static String adjust(String str)
	{
		String result = null;
		String charsetDetected = null;
		if(str!=null && !"".equals(str) && (charsetDetected=detectCharset(str))!=null && !DEFAULT_CHARSET.equals(charsetDetected))
		{
			try
			{
				result = new String(str.getBytes(DEFAULT_CHARSET),charsetDetected);
			}
			catch (Throwable t){}
		}

		if(result==null)
		{
			result = str;
		}

		return result;
	}


	public static String convertCharset(String str, String beforeCharset, String afterCharset) throws UnsupportedEncodingException
	{
		String result = null;
		if(Check.isNotBlank(str, beforeCharset, afterCharset) && !beforeCharset.equalsIgnoreCase(afterCharset))
		{
			result = new String(str.getBytes(beforeCharset),afterCharset);
		}
		else
		{
			result = str;
		}

		return result;
	}


	public static String convertCharset(String str, String afterCharset) throws UnsupportedEncodingException
	{
		return convertCharset(str, DEFAULT_CHARSET, afterCharset);
	}
}