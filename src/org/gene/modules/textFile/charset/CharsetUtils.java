package org.gene.modules.textFile.charset;

import java.io.UnsupportedEncodingException;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.gene.modules.validation.Check;


public class CharsetUtils
{
	public static final String DEFAULT_CHARSET = "ISO-8859-1"; //8859_1

	public static String detectCharset(String str) throws UnsupportedEncodingException
	{
		String charsetName = null;
		if(str!=null && !"".equals(str))
		{
			CharsetDetector detector = new CharsetDetector();
			detector.setText(str.getBytes(DEFAULT_CHARSET));
			CharsetMatch match = detector.detect();
			charsetName = match.getName();
		}
		return charsetName;
	}

	public static String correct(String str) throws UnsupportedEncodingException
	{
		String result = null;
		if(str!=null && !"".equals(str))
		{
			String charsetDetected = detectCharset(str);
			if(!DEFAULT_CHARSET.equals(charsetDetected))
			{
				result = convertCharset(str, DEFAULT_CHARSET, charsetDetected);
			}
		}

		return result;
	}

	public static String convertCharset(String str, String beforeCharset, String afterCharset) throws UnsupportedEncodingException
	{
		String result = str;
		if(Check.isNotBlank(str, beforeCharset, afterCharset) && !beforeCharset.equalsIgnoreCase(afterCharset))
		{
			result = new String(str.getBytes(beforeCharset),afterCharset);
		}

		return result;
	}

	public static String convertCharset(String str, String targetCharset) throws UnsupportedEncodingException
	{
		return convertCharset(str, DEFAULT_CHARSET, targetCharset);
	}


	public static void main(String[] args) throws UnsupportedEncodingException
	{
		System.out.println(detectCharset("할렐루야"));
		System.out.println(correct("할렐루야"));
	}
}