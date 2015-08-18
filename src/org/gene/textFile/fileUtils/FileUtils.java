package org.gene.textFile.fileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FileUtils
{
	private static final String[] invalidCharRegexForDirectoryName = new String[]{"\\\\", "\\/", "\\?", "\\%", "\\*", "\\:", "\\|", "\\\"", "\\t", "\\<", "\\>"};
	private static String directoryNameRegex;
	static
	{
		directoryNameRegex = "[^";
		for(int i=0; i<invalidCharRegexForDirectoryName.length; ++i)
		{
			directoryNameRegex += invalidCharRegexForDirectoryName[i];
		}
		directoryNameRegex += "]+"; //"[^\\\\\\/\\?\\%\\*\\:\\|\\\"\\t\\<\\>]+"
	}
	private static final String windowsDriveRegex = "[a-zA-Z]\\:";
	private static final String windowsSplitRegex = "\\\\";
	private static final String unixSplitRegex = "\\/";
	private static final String windowsStyleRegex = "("+windowsDriveRegex+windowsSplitRegex+")?("+directoryNameRegex+"("+windowsSplitRegex+")?)+";
	private static final String unixStyleRegex = "("+windowsDriveRegex+unixSplitRegex+")?("+directoryNameRegex+"("+unixSplitRegex+")?)+";



	public static List<String> getFilePathList(String dirPath)
	{
		return getFilePathList(dirPath, null, true, true);
	}

	public static List<String> getFilePathList(String dirPath, boolean ignoreCase, boolean subDirSearch)
	{
		return getFilePathList(dirPath, null, ignoreCase, subDirSearch);
	}

	public static List<String> getFilePathList(String dirPath, String fileNameRegex, boolean ignoreCase, boolean subDirSearch)
	{
		if(dirPath==null || "".equals(dirPath)){throw new IllegalArgumentException();}

		String[] subDirNames = dirPath.split("/");
		for(int i=0; i<subDirNames.length; ++i)
		{
			for(int j=0; j<invalidCharRegexForDirectoryName.length; ++j)
			{
				if(subDirNames[i].contains(invalidCharRegexForDirectoryName[j])){throw new IllegalArgumentException();}
			}
		}

		List<String> result = new ArrayList<String>();
		Vector<File> buffer = new Vector<File>();
		Vector<File> temp =  new Vector<File>();

		boolean startFromRoot = "".equals(subDirNames[0]);
		File[] initialFileList = startFromRoot ? (new File("/")).listFiles() : (new File(".")).listFiles();
		buffer.addAll(Arrays.asList(initialFileList));

		for(int i=0; i<subDirNames.length; ++i)
		{
			String regex = ((startFromRoot && i==0) ? ".+" : subDirNames[i]);
			if(ignoreCase)
			{
				regex = "(?i:"+regex+")";
			}

			for(int j=0; j<buffer.size(); ++j)
			{
				File currentFile = buffer.get(j);
				if(buffer.get(j).getName().matches(regex))
				{
					if(currentFile.isDirectory())
					{
						temp.addAll(Arrays.asList(currentFile.listFiles()));
					}
					else if(i==subDirNames.length-1)
					{
						temp.add(currentFile);
					}
				}
			}
			buffer.clear();
			buffer.addAll(temp);
			temp.clear();
		}


		for(int i=0; i<buffer.size(); ++i)
		{
			if(buffer.get(i).isDirectory())
			{
				if(subDirSearch)
				{
					File dirFile = buffer.get(i);
					buffer.addAll(Arrays.asList(dirFile.listFiles()));
				}
				buffer.remove(i);
				i--;
			}
		}


		for(int i=0; i<buffer.size(); ++i)
		{
			if((fileNameRegex==null) || ((fileNameRegex!=null)&&(buffer.get(i).getName().matches(fileNameRegex))))
			{
				result.add(getFullPath(buffer.get(i)));
			}
		}

		return result;
	}


	public static String getFullPath(File file)
	{
		if(file==null){throw new IllegalArgumentException();}

		String fullPath = null;
		try
		{
			fullPath = file.getCanonicalPath();
		}
		catch (Throwable t){throw new IllegalArgumentException();}

		fullPath = convertToUnixStyle(fullPath);

		return fullPath;
	}


	public static boolean isWindowsStyle(String path)
	{
		if(path==null){throw new IllegalArgumentException();}
		return path.matches(windowsStyleRegex);
	}

	public static boolean isUnixStyle(String path)
	{
		if(path==null){throw new IllegalArgumentException();}
		return path.matches(unixStyleRegex);
	}


	public static String convertToUnixStyle(String path)
	{
		if(path==null){throw new IllegalArgumentException();}

		boolean windowsStyle = isWindowsStyle(path);
		boolean unixStyle = isUnixStyle(path);

		if(!windowsStyle && !unixStyle){throw new IllegalArgumentException();}

		String resultPath = path;
		if(windowsStyle)
		{
			resultPath = resultPath.replaceAll(windowsSplitRegex, "/");
		}
		return resultPath;
	}





	public static void mkdir(String path)
	{
		if(path==null || "".equals(path)){throw new IllegalArgumentException();}
		if(!path.matches(unixStyleRegex)){throw new IllegalArgumentException();}

		if(!(new File(path)).exists())
		{
			String[] subDirs = path.split("/");
			String currentDir = "";
			for(int i=0; i<subDirs.length; ++i)
			{
				currentDir += subDirs[i]+"/";
				if(!"/".equals(currentDir))
				{
					File temp = new File(currentDir);
					if(!temp.exists())
					{
						temp.mkdir();
					}
				}
			}
		}
	}


	public static boolean exists(String path)
	{
		if(path==null){throw new IllegalArgumentException();}
		if(!isWindowsStyle(path) && !isUnixStyle(path)){throw new IllegalArgumentException();}
		return (new File(path)).exists();
	}


	public static boolean isDirectory(String path)
	{
		if(path==null){throw new IllegalArgumentException();}
		if(!isWindowsStyle(path) && !isUnixStyle(path)){throw new IllegalArgumentException();}
		return (new File(path)).isDirectory();
	}



	public static void main(String[] args)
	{
		System.out.println(convertToUnixStyle("a\\a\\a오메bb _b"));
		List<String> list = getFilePathList("src/com/glorious/bible/korean/", false, false);
		for(int i=0; i<list.size(); ++i)
		{
			System.out.println(list.get(i));
		}

//		File f = new File("E:\\temp");
//		System.out.println(f.exists());
	}
}
