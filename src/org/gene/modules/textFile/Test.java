package org.gene.modules.textFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Scanner;

public class Test
{
	public static void fileRead1()
	{
		try{
			FileInputStream fis = new FileInputStream(new File("resource/bible/Korean/개역개정/01창세기.txt"));

			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");

			BufferedReader br = new BufferedReader(isr);

			while(true){
				String str = br.readLine();
				if(str==null) break;
				System.out.println(str);
			}

			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void fileRead2()
	{
		Scanner s = null;
		try{
			s = new Scanner(new File("sample2.txt"),"UTF-8");

			while(true){
				String str = s.nextLine();
				System.out.println(str);
			}

		}catch(Exception e){
			s.close();
		}
	}


	public static void fileRead3() throws IOException
	{
		RandomAccessFile file = new RandomAccessFile("sample2.txt", "rwd");
		String line = file.readLine();
		file.close();
//		line = new String(line.getBytes("ISO-8859-1"), "UTF-8");
		line = new String(line.getBytes("8859_1"), "UTF-8");

		System.out.println(line);
	}

	public static void fileWrite()
	{

		try {
			String srcText = new String("UTF-8 파일을 생성합니다.");

			File targetFile = new File("D:\\output.txt");
			targetFile.createNewFile();

			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), "UTF8"));

			output.write(srcText);
			output.close();
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}


	}


	public static void fileWrite2(){

		  try {
			File fileDir = new File("sample");

			Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileDir), "UTF8"));

			out.append("으하하").append("\r\n");
			out.flush();
			out.close();

		    }
		   catch (UnsupportedEncodingException e)
		   {
			System.out.println(e.getMessage());
		   }
		   catch (IOException e)
		   {
			System.out.println(e.getMessage());
		    }
		   catch (Exception e)
		   {
			System.out.println(e.getMessage());
		   }
		}

	public static void main(String[] args) throws IOException
	{

//		fileRead3();







	}

}
