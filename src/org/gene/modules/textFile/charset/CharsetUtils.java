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

			System.out.println("--->"+charsetName);
		}
		return charsetName;
	}

	public static String adjust(String str) throws UnsupportedEncodingException
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


	public static void main(String[] args) throws UnsupportedEncodingException
	{
		System.out.println(detectCharset("¾î¸Ó³ª"));
	}
}