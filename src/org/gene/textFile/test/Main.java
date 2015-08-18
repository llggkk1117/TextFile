package org.gene.textFile.test;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		RandomAccessFile file = new RandomAccessFile("src/com/glorious/textFile/test/test-ko.txt", "rwd");

		// byte array in file -------- ISO-8859-1 (system defaul charset)로 디코딩 --------> String
		String line = file.readLine();

		// byte array in file <-------- ISO-8859-1 (system defaul charset)로 인코딩 -------- String
		// 이 방법으로 원래 파일에 있던 byte array를 구한다.
		byte[] byteArray = line.getBytes("ISO-8859-1");

		// byte array in file -------- EUC-KR 로 디코딩 --------> String
		line = new String(byteArray, "EUC-KR");

		System.out.println(line);
		file.close();
	}
}
