package org.gene.modules.textFile.test;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.gene.modules.textFile.charset.CharsetUtils;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile("src/org/gene/modules/textFile/test/test.txt", "rwd");
		//RandomAccessFile file = new RandomAccessFile("src/org/gene/modules/textFile/test/Genesis.json", "rwd");

		// byte array in file -------- ISO-8859-1 (system defaul charset)�� ���ڵ� --------> String
		String line = file.readLine();
		System.out.println(line);

		String charSetName = CharsetUtils.detectCharset(line);
		System.out.println(charSetName);

		// byte array in file <-------- ISO-8859-1 (system defaul charset)�� ���ڵ� -------- String
		// �� ������� ���� ���Ͽ� �ִ� byte array�� ���Ѵ�.
		byte[] byteArray = line.getBytes("ISO-8859-1");

		// byte array in file -------- EUC-KR �� ���ڵ� --------> String
		line = new String(byteArray, charSetName);
		line = line.replace("\uFEFF", "");

		// line = new String(byteArray, "Big5");

		System.out.println(line);
		file.close();
	}
}
