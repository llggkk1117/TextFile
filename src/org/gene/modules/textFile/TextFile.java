package org.gene.modules.textFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.gene.modules.textFile.charset.Charset;
import org.gene.modules.textFile.fileUtils.FileUtils;


public final class TextFile
{
	private static HashMap<String, TextFile> textFileRegistry;
	private static Semaphore semaphore;
	private static final Charset SYSTEM_DEFAULT_CHARSET = Charset.ISO_8859_1;
	static
	{
		textFileRegistry = new HashMap<String, TextFile>();
		semaphore = new Semaphore();
	}
	private String fileName;
	private RandomAccessFile file;
	private long readOffset;
	private long writeOffset;
	private Charset fileCharset;


	public TextFile(String fileName) throws InterruptedException, IOException
	{
		this(fileName, SYSTEM_DEFAULT_CHARSET);
	}

	public TextFile(String fileName, String charsetCode) throws InterruptedException, IOException
	{
		this(fileName, Charset.getCharset(charsetCode));
	}

	public TextFile(String fileName, Charset charset) throws InterruptedException, IOException
	{
		if(fileName==null || "".equals(fileName)){throw new IllegalArgumentException();}

		TextFile.semaphore.acquire();

		if(TextFile.textFileRegistry.get(fileName) != null){throw new IllegalArgumentException("Instance for "+fileName+" file already exists.");}

		int lastSlashIndex = fileName.lastIndexOf("/");
		if(lastSlashIndex>=0)
		{
			String dir = fileName.substring(0, fileName.lastIndexOf("/"));
			FileUtils.mkdir(dir);
		}

		this.file = new RandomAccessFile(fileName, "rwd");
		this.fileName = fileName;
		this.readOffset = 0;
		this.writeOffset = this.file.length();
		this.fileCharset = charset;
		if(this.fileCharset == null)
		{
			this.fileCharset = SYSTEM_DEFAULT_CHARSET;
		}

		TextFile.textFileRegistry.put(fileName, this);

		TextFile.semaphore.release();
	}




	protected void finalize() throws Throwable
	{
		try
		{
			close();
		}
		finally
		{
			super.finalize();
		}
	}




	public synchronized void close() throws IOException
	{
		if(this.file != null)
		{
			this.file.close();
			this.file = null;
		}
		this.readOffset = 0;
		this.writeOffset = 0;
		this.fileCharset = null;

		TextFile.textFileRegistry.remove(this.fileName);
	}


	public synchronized String readChar() throws IOException
	{
		String result = null;

		if(this.file!=null && this.readOffset<this.getFileSize())
		{
			if(this.fileCharset == SYSTEM_DEFAULT_CHARSET)
			{
				this.file.seek(this.readOffset);
				byte[] buf = new byte[1];
				this.file.read(buf);
				result = new String(buf);
				this.readOffset = this.file.getFilePointer();
			}
			else
			{
				int[] numOfBytesSupported = this.fileCharset.getNumOfBytesSupported();
				for(int i=0; i<numOfBytesSupported.length; ++i)
				{
					this.file.seek(this.readOffset);
					byte[] buf = new byte[numOfBytesSupported[i]];
					this.file.read(buf);
					if(this.fileCharset.inRange(buf))
					{
						result = new String(buf, this.fileCharset.getDisplayName());
						this.readOffset = this.file.getFilePointer();
						break;
					}
				}
				if(result == null)
				{
					this.readOffset = this.readOffset+1;
					result="";
				}
			}
		}
		return result;
	}




	public synchronized String readCharBack() throws IOException
	{
		String result = null;

		if(this.file!=null && this.readOffset>0)
		{
			if(this.fileCharset == SYSTEM_DEFAULT_CHARSET)
			{
				long tempOffset = this.readOffset-1;
				if(tempOffset >= 0)
				{
					this.file.seek(tempOffset);
					byte[] buf = new byte[1];
					this.file.read(buf);
					result = new String(buf);
					this.readOffset = tempOffset;
				}
			}
			else
			{
				int[] numOfBytesSupported = this.fileCharset.getNumOfBytesSupported();
				for(int i=0; i<numOfBytesSupported.length; ++i)
				{
					int numOfBytes = numOfBytesSupported[i];
					long tempOffset = this.readOffset-numOfBytes;
					if(tempOffset >= 0)
					{
						this.file.seek(tempOffset);
						byte[] buf = new byte[numOfBytes];
						this.file.read(buf);
						if(this.fileCharset.inRange(buf))
						{
							result = new String(buf, this.fileCharset.getDisplayName());
							this.readOffset = tempOffset;
							break;
						}
					}
				}
				if(result == null)
				{
					this.readOffset = this.readOffset-1;
					result = "";
				}
			}
		}

		return result;
	}





	public synchronized String readLine() throws IOException
	{
		String line = null;

		if(this.file!=null && this.readOffset<this.getFileSize())
		{
			this.file.seek(this.readOffset);
			line = this.file.readLine();
			if(line!=null && !"".equals(line) && this.fileCharset != SYSTEM_DEFAULT_CHARSET)
			{
				line = encode(line, SYSTEM_DEFAULT_CHARSET.getDisplayName(), this.fileCharset.getDisplayName());
				String[] lettersToBeIgnored = this.fileCharset.getLettersToBeIgnored();
				for(int i=0; i<lettersToBeIgnored.length; ++i)
				{
					line = line.replace(lettersToBeIgnored[i], "");
				}
			}
			this.readOffset = this.file.getFilePointer();
		}

		return line;
	}





	public synchronized String readLineBack() throws IOException
	{
		String result = null;
		if(this.file!=null && this.readOffset > 0)
		{
			String previousLetter = this.readCharBack();
			if("\n".equals(previousLetter))
			{
				previousLetter = this.readCharBack();
				if("\n".equals(previousLetter))
				{
					this.readChar();
				}
			}
			else if("\r".equals(previousLetter))
			{
				previousLetter = this.readCharBack();
				if("\r".equals(previousLetter))
				{
					this.readChar();
				}
			}

			do
			{
				previousLetter = this.readCharBack();
			}
			while(previousLetter!=null && !"\n".equals(previousLetter) && !"\r".equals(previousLetter));

			if(previousLetter!=null)
			{
				this.readChar();
			}

			long finalOffSet = this.readOffset;
			result = this.readLine();
			this.readOffset = finalOffSet;
		}

		return result;
	}






	public static synchronized String encode(String content, String currentCharSet, String newCharSet) throws UnsupportedEncodingException
	{
		String encodedContent = null;
		if(content!=null && !"".equals(content))
		{
			encodedContent = new String(content.getBytes(currentCharSet), newCharSet);
		}

		return encodedContent;
	}





	public synchronized String[] readAll() throws IOException
	{
		Vector<String> lines_temp = new Vector<String>();

		if(this.file != null)
		{
			long currentOffset = this.file.getFilePointer();
			this.file.seek(0);
			String line = null;
			while((line = file.readLine()) != null)
			{
				lines_temp.add(line);
			}
			this.file.seek(currentOffset);
		}

		String[] lines = new String[lines_temp.size()];
		for(int i=0; i<lines.length; ++i)
		{
			lines[i] = lines_temp.elementAt(i);
		}

		return lines;
	}



	private synchronized long adjustOffset(long offset) throws IOException
	{
		long validOffset = offset;
		if(validOffset<0)
		{
			validOffset = 0;
		}
		else
		{
			long fileSize = this.getFileSize();
			if(validOffset > fileSize)
			{
				validOffset = fileSize;
			}
		}

		return validOffset;
	}


	public synchronized void setReadOffset(long readOffset) throws IOException
	{
		this.readOffset = this.adjustOffset(readOffset);
	}


	public synchronized long getReadOffset()
	{
		return this.readOffset;
	}

	public synchronized void setWriteOffset(long writeOffset) throws IOException
	{
		this.writeOffset = this.adjustOffset(writeOffset);
	}




	public synchronized long getWriteOffset()
	{
		return this.writeOffset;
	}



	public synchronized long getFileSize() throws IOException
	{
		return this.file.length();
	}






	public synchronized void write(String message) throws IOException
	{
		if(this.file!=null && message!=null && !"".equals(message))
		{
			this.file.seek(this.writeOffset);
			if(this.fileCharset != SYSTEM_DEFAULT_CHARSET)
			{
				message = encode(message, this.fileCharset.getDisplayName(), SYSTEM_DEFAULT_CHARSET.getDisplayName());
			}
			this.file.writeBytes(message);
			this.writeOffset = this.file.getFilePointer();
		}
	}



	public synchronized void writeLine(String delimeter, String... elements) throws IOException
	{
		if(this.file!=null && elements != null)
		{
			for(int i=0; i<elements.length; ++i)
			{
				this.write(elements[i]);
				if(i < (elements.length-1))
				{
					this.write(delimeter);
				}
			}
			this.write("\r\n");
		}
	}




	public synchronized void writeLine(String message) throws IOException
	{
		if(this.file!=null && message!=null)
		{
			this.write(message+"\r\n");
		}
	}


	public synchronized void writeLine() throws IOException
	{
		if(this.file!=null)
		{
			this.write("\r\n");
		}
	}


	public synchronized void writeLines(String[] messages) throws IOException
	{
		if(this.file!=null && messages!=null)
		{
			for(int i=0; i<messages.length; ++i)
			{
				this.writeLine(messages[i]);
			}
		}
	}




	public synchronized void clear() throws IOException
	{
		if(this.fileName != null)
		{
			FileWriter remover = new FileWriter(fileName, false);
			remover.write("");
			remover.flush();
			remover.close();
			remover = null;
			this.readOffset = 0;
			this.writeOffset = 0;
		}
	}




	public synchronized boolean delete() throws IOException
	{
		this.close();

		boolean result = false;
		if(this.fileName != null)
		{
			File currentFile = new File(this.fileName);
			result = currentFile.delete();
		}

		return result;
	}




	public synchronized boolean isOpen()
	{
		if(this.file != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}




	//---static
	public static synchronized void refreshAll() throws IOException
	{
		String[] everyFileName = getEveryFileName();

		for(int i=0; i<everyFileName.length; ++i)
		{
			refreshInstance(everyFileName[i]);
		}
	}




	public static synchronized void refreshInstance(String fileName) throws IOException
	{
		TextFile textFile = textFileRegistry.get(fileName);

		if(textFile != null)
		{
			if(!TextFile.fileExists(fileName))
			{
				textFileRegistry.remove(fileName);
			}
			else if(!textFile.isOpen())
			{
				textFileRegistry.remove(fileName);
			}
		}
	}




	public static synchronized TextFile getInstance(String fileName) throws IOException, InterruptedException
	{
		return TextFile.getInstance(fileName, SYSTEM_DEFAULT_CHARSET);
	}

	public static synchronized TextFile getInstance(String fileName, String charSet) throws IOException, InterruptedException
	{
		return TextFile.getInstance(fileName, Charset.getCharset(charSet));
	}

	public static synchronized TextFile getInstance(String fileName, Charset charSet) throws IOException, InterruptedException
	{
		TextFile.refreshInstance(fileName);

		TextFile instance = TextFile.textFileRegistry.get(fileName);
		if(instance == null)
		{
			instance = new TextFile(fileName, charSet);
		}

		return instance;
	}




	public static Integer getNumOfInstance() throws IOException
	{
		return TextFile.textFileRegistry.size();
	}




	public static synchronized void closeInstance(String fileName) throws IOException
	{
		TextFile temp = TextFile.textFileRegistry.get(fileName);
		if(temp != null)
		{
			temp.close();
			temp  = null;
		}
	}




	public static synchronized void closeEveryInstance() throws IOException
	{
		String[] everyFileName = TextFile.getEveryFileName();
		for(int i=0; i<everyFileName.length; ++i)
		{
			TextFile.closeInstance(everyFileName[i]);
		}
		TextFile.textFileRegistry.clear();
	}




	public static synchronized String[] getEveryFileName() throws IOException
	{
		Set<String> fileNameSet = textFileRegistry.keySet();
		Object[] fileNameSet_ObjectArray = fileNameSet.toArray();
		String[] fileNameSet_StringArray = new String[fileNameSet_ObjectArray.length];
		for(int i=0; i<fileNameSet_StringArray.length; ++i)
		{
			fileNameSet_StringArray[i] = (String)fileNameSet_ObjectArray[i];
		}
		fileNameSet = null;
		fileNameSet_ObjectArray = null;

		return fileNameSet_StringArray;
	}




	public static synchronized boolean fileExists(String fileName)
	{
		boolean fileExists = false;
		if(fileName!=null && !"".equals(fileName))
		{
			fileExists = (new File(fileName)).exists();
		}

		return fileExists;
	}




	public static synchronized boolean instanceExists(String fileName)
	{
		return (textFileRegistry.get(fileName) != null);
	}


	public static synchronized boolean delete(String fileName) throws IOException
	{
		boolean result = false;
		if(fileExists(fileName))
		{
			TextFile fileInstance = TextFile.textFileRegistry.get(fileName);
			if(fileInstance != null)
			{
				fileInstance.close();
			}

			File tempFile = new File(fileName);
			result = tempFile.delete();
		}

		return result;
	}





//	public static List<String> getFilePathList(String dirPath) throws IOException
//	{
//		return getFilePathList(dirPath, null, true, true);
//	}
//
//	public static List<String> getFilePathList(String dirPath, boolean ignoreCase, boolean subDirSearch) throws IOException
//	{
//		return getFilePathList(dirPath, null, ignoreCase, subDirSearch);
//	}

//	public static List<String> getFilePathList(String dirPath, String fileNameRegex, boolean ignoreCase, boolean subDirSearch) throws IOException
//	{
//		if(dirPath==null || "".equals(dirPath)){throw new IllegalArgumentException();}
//		List<String> result = new ArrayList<String>();
//
//		Vector<File> buffer = new Vector<File>();
//		Vector<File> temp =  new Vector<File>();
//
//		String[] subDirNames = dirPath.split("/");
//		boolean startFromRoot = "".equals(subDirNames[0]);
//		File[] initialFileList = startFromRoot ? (new File("/")).listFiles() : (new File(".")).listFiles();
//		buffer.addAll(Arrays.asList(initialFileList));
//
//		for(int i=0; i<subDirNames.length; ++i)
//		{
//			String regex = ((startFromRoot && i==0) ? ".+" : subDirNames[i]);
//			if(ignoreCase)
//			{
//				regex = "(?i:"+regex+")";
//			}
//
//			for(int j=0; j<buffer.size(); ++j)
//			{
//				File currentFile = buffer.get(j);
//				if(buffer.get(j).getName().matches(regex))
//				{
//					if(currentFile.isDirectory())
//					{
//						temp.addAll(Arrays.asList(currentFile.listFiles()));
//					}
//					else if(i==subDirNames.length-1)
//					{
//						temp.add(currentFile);
//					}
//				}
//			}
//			buffer.clear();
//			buffer.addAll(temp);
//			temp.clear();
//		}
//
//
//		if(subDirSearch)
//		{
//			for(int i=0; i<buffer.size(); ++i)
//			{
//				if(buffer.get(i).isDirectory())
//				{
//					File dirFile = buffer.get(i);
//					buffer.remove(i);
//					buffer.addAll(Arrays.asList(dirFile.listFiles()));
//					i--;
//				}
//			}
//		}
//		else
//		{
//			for(int i=0; i<buffer.size(); ++i)
//			{
//				if(buffer.get(i).isDirectory())
//				{
//					buffer.remove(i);
//					i--;
//				}
//			}
//		}
//
//
//		for(int i=0; i<buffer.size(); ++i)
//		{
//			if(fileNameRegex!=null)
//			{
//				if(buffer.get(i).getName().matches(fileNameRegex))
//				{
//					result.add(getFullPath(buffer.get(i)));
//				}
//			}
//			else
//			{
//				result.add(getFullPath(buffer.get(i)));
//			}
//		}
//
//		return result;
//	}

//	private static String getFullPath(File file)
//	{
//		String fullPath = null;
//		try
//		{
//			fullPath = file.getCanonicalPath();
//			fullPath = fullPath.replaceFirst("\\w:\\\\", "/");
//			fullPath = fullPath.replace("\\", "/");
//		}
//		catch (Throwable t){}
//
//		return fullPath;
//	}


//	public static void mkdir(String path)
//	{
//		if(path==null || "".equals(path)){throw new IllegalArgumentException();}
//		if(!(new File(path)).exists())
//		{
//			String[] subDirs = path.split("/");
//			String currentDir = "";
//			for(int i=0; i<subDirs.length; ++i)
//			{
//				currentDir += subDirs[i]+"/";
//				if(!"/".equals(currentDir))
//				{
//					File temp = new File(currentDir);
//					if(!temp.exists())
//					{
//						temp.mkdir();
//					}
//				}
//			}
//		}
//	}



	public static void main(String[] args) throws IOException, InterruptedException
	{
//		TextFile s = TextFile.getInstance("resource/bible/Korean/개역개정/01창세기.txt", "UTF-8");
//		TextFile s = TextFile.getInstance("resource/bible/English/NIV/01-Genesis.txt", "UTF-8");
//		TextFile s = TextFile.getInstance("sample2.txt", "UTF-8");
//		System.out.println(s.readLine());
//		System.out.println(s.readLine());
//		System.out.println(s.readLine());
//		System.out.println(s.readLineBack());
//		System.out.println(s.readLineBack());
//		System.out.println(s.readLineBack());
//		System.out.println(s.readChar());
//		System.out.println(s.readChar());
//		System.out.println(s.readCharBack());
//		System.out.println(s.readCharBack());


//		s.close();



		//		s.clear();
		//		s.writeLine("~12할렐루야");
//		System.out.println("Line: "+s.readChar());
//		System.out.println("Line: "+s.readLine());
//		System.out.println("Line: "+s.readCharBack());
//		System.out.println("Line: "+s.readLineBack());
		//		System.out.println(bytesToHex("~".getBytes("EUC-KR")));

//		byte[] b = new byte[]{(byte) 0xe0, (byte) 0xab, (byte) 0x8c};
//		System.out.println(new String(b, "UTF-8"));
//
//		byte[] b = new byte[]{(byte) 0x0d};
//		System.out.print(new String(b, "UTF-8"));


//		byte[] b = new byte[]{(byte) 0x0a};
//		System.out.println("\r\n".equals((char)b[0]+""));

		//		s.writeLine(new String("할렐루야!!".getBytes("UTF-16LE"), "8859_1"));
		//		System.out.println("Line: "+s.readLine("8859_1", "UTF-16LE"));

		//		s.writeLine(new String("할렐루야!!".getBytes("EUC-KR"), "8859_1"));
		//		System.out.println("Line: "+s.readLine("8859_1", "EUC-KR"));

		//		s.writeLine(new String("aaaa".getBytes("8859_1"), "8859_1"));
		//		System.out.println("Line: "+s.readLine("8859_1", "8859_1"));

//		CharacterSetByteRange c = new UTF_8();
//		System.out.println(byteArrayToHexString(c.getByteRanges()[0].getStartingByte()));


		//		System.out.println(s.readLine());
		//		s.readLine();
		//		System.out.println(s.getReadOffset());
		//		s.setReadOffset(s.getFileSize());
		//		System.out.println("Line: "+s.readLineBack());
		//		System.out.println("Line: "+s.readLineBack());
		//		System.out.println("Line: "+s.readLineBack());

//		TextFile s = TextFile.getInstance("mytext.txt", "UTF-8");
//		System.out.println("Line: "+s.readLine());
//		System.out.println("Line: "+s.readLineBack());
//		System.out.println("Line: "+s.readLineBack());

//		String path = "/aaa/bbb/";
//		String[] a = path.split("/");
//		for(int i=0; i<a.length; ++i)
//		{
//			System.out.println(a[i]);
//		}
//		System.out.println("End");

//		TextFile s = TextFile.getInstance("aaa/bbb/ccc/ddd/mytext.txt", "UTF-8");
////		s.clear();
////		s.writeLine("으흐허허헝");
//		System.out.println("Line: "+s.readLine());
//		System.out.println("Line: "+s.readLine());
//		System.out.println("Line: "+s.readLineBack());
//		System.out.println("Line: "+s.readLineBack());
//		System.out.println("Line: "+s.readLineBack());
//		System.out.println("Line: "+s.readChar());
//		System.out.println("Line: "+s.readCharBack());
//		s.close();

//		TextFile f = new TextFile("src/com/glorious/textFile/test/test-ko.txt", "EUC-KR");
//		System.out.println(f.readChar());
//		System.out.println(f.readChar());
//		System.out.println(f.readChar());
//		System.out.println(f.readChar());
//		System.out.println(f.readCharBack());
//		System.out.println(f.readCharBack());
//		System.out.println(f.readCharBack());
//		System.out.println(f.readCharBack());

//		List<String> list = getFilePathList("resource/raw/", ".*\\.bdf", false, true);
//		for(int i=0; i<list.size(); ++i)
//		{
//			System.out.println(list.get(i));
//		}

		System.out.println(FileUtils.getFullPath(new File("src/com/glorious/bible/english/engesv/Acts.java")));
	}
}

// 윈도우 기본 charset: 8859_1
// 유니코드: UTF-16LE

// 한글: EUC-KR


//String word = "무궁화 꽃이 피었습니다.";
//System.out.println("utf-8 -> euc-kr        : " + new String(word.getBytes("utf-8"), "euc-kr"));
//System.out.println("utf-8 -> ksc5601       : " + new String(word.getBytes("utf-8"), "ksc5601"));
//System.out.println("utf-8 -> x-windows-949 : " + new String(word.getBytes("utf-8"), "x-windows-949"));
//System.out.println("utf-8 -> iso-8859-1    : " + new String(word.getBytes("utf-8"), "iso-8859-1"));
//System.out.println("iso-8859-1 -> euc-kr        : " + new String(word.getBytes("iso-8859-1"), "euc-kr"));
//System.out.println("iso-8859-1 -> ksc5601       : " + new String(word.getBytes("iso-8859-1"), "ksc5601"));
//System.out.println("iso-8859-1 -> x-windows-949 : " + new String(word.getBytes("iso-8859-1"), "x-windows-949"));
//System.out.println("iso-8859-1 -> utf-8         : " + new String(word.getBytes("iso-8859-1"), "utf-8"));
//System.out.println("euc-kr -> utf-8         : " + new String(word.getBytes("euc-kr"), "utf-8"));
//System.out.println("euc-kr -> ksc5601       : " + new String(word.getBytes("euc-kr"), "ksc5601"));
//System.out.println("euc-kr -> x-windows-949 : " + new String(word.getBytes("euc-kr"), "x-windows-949"));
//System.out.println("euc-kr -> iso-8859-1    : " + new String(word.getBytes("euc-kr"), "iso-8859-1"));
//System.out.println("ksc5601 -> euc-kr        : " + new String(word.getBytes("ksc5601"), "euc-kr"));
//System.out.println("ksc5601 -> utf-8         : " + new String(word.getBytes("ksc5601"), "utf-8"));
//System.out.println("ksc5601 -> x-windows-949 : " + new String(word.getBytes("ksc5601"), "x-windows-949"));
//System.out.println("ksc5601 -> iso-8859-1    : " + new String(word.getBytes("ksc5601"), "iso-8859-1"));
//System.out.println("x-windows-949 -> euc-kr     : " + new String(word.getBytes("x-windows-949"), "euc-kr"));
//System.out.println("x-windows-949 -> utf-8      : " + new String(word.getBytes("x-windows-949"), "utf-8"));
//System.out.println("x-windows-949 -> ksc5601    : " + new String(word.getBytes("x-windows-949"), "ksc5601"));
//System.out.println("x-windows-949 -> iso-8859-1 : " + new String(word.getBytes("x-windows-949"), "iso-8859-1"));
