package org.gene.modules.textFile.charset.lab;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;


public class Lab
{
	public static void test1() throws UnsupportedEncodingException
	{
		System.out.println("?".getBytes("UTF-8")[0]==0x003F);
	}

	public static void test2() throws UnsupportedEncodingException
	{
		String str = "a할!";
		System.out.println("Encoded by UTF-8: "+str.getBytes("UTF-8").length+" bytes");
		System.out.println("Encoded by Big5: "+str.getBytes("Big5").length+" bytes");
		System.out.println("Encoded by ISO-8859-1: "+str.getBytes("ISO-8859-1").length+" bytes");
		System.out.println("Encoded by EUC-KR: "+str.getBytes("EUC-KR").length+" bytes");
	}

	public static void test3() throws UnsupportedEncodingException
	{
		String str = "a할!";

		byte[] strBytes = str.getBytes("UTF-8");
		System.out.println((new String(strBytes, "UTF-8")));

		strBytes = str.getBytes("EUC-KR");
		System.out.println((new String(strBytes, "EUC-KR")));

		strBytes = str.getBytes("Big5");
		System.out.println((new String(strBytes, "Big5")));

		strBytes = str.getBytes("ISO-8859-1");
		System.out.println((new String(strBytes, "ISO-8859-1")));
	}

	public static void test4() throws UnsupportedEncodingException, CharacterCodingException
	{
		String str = "a할!";

		byte[] strBytes = str.getBytes("UTF-8");
		CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT);
		System.out.println(decoder.decode(ByteBuffer.wrap(strBytes)));

		byte[] temp = new byte[2];
		System.arraycopy(strBytes, 3, temp, 0, 2);
		System.out.println(decoder.decode(ByteBuffer.wrap(temp)));
	}

	public static void main(String[] args) throws UnsupportedEncodingException, CharacterCodingException
	{
		test4();
	}
}
