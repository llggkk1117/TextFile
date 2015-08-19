package org.gene.modules.textFile.charset;

public class HexStringBinay
{
	final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String byteArrayToHexString(byte[] bytes)
	{
		char[] hexChars = new char[bytes.length*2];
		for(int j=0; j<bytes.length; j++)
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4]; //http://www.geeksforgeeks.org/bitwise-shift-operators-in-java/
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s)
	{
	    int len = s.length();
	    byte[] data = new byte[len/2];
	    for (int i=0; i<len; i+=2)
	    {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

	public static String b2h(byte[] bytes)
	{
		return byteArrayToHexString(bytes);
	}

	public static byte[] h2b(String s)
	{
		return hexStringToByteArray(s);
	}

	public static void main(String[] args)
	{
		byte b = (byte) 0xFF;
		System.out.println(b2h(new byte[]{b}));
	}
}
