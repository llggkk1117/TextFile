package org.gene.modules.textFile.test;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile("src/com/glorious/textFile/test/test-ko.txt", "rwd");

		// byte array in file -------- ISO-8859-1 (system defaul charset)�� ���ڵ� --------> String
		String line = file.readLine();

		// byte array in file <-------- ISO-8859-1 (system defaul charset)�� ���ڵ� -------- String
		// �� ������� ���� ���Ͽ� �ִ� byte array�� ���Ѵ�.
		byte[] byteArray = line.getBytes("ISO-8859-1");

		// byte array in file -------- EUC-KR �� ���ڵ� --------> String
		line = new String(byteArray, "EUC-KR");

		System.out.println(line);
		file.close();
	}
}
