package org.gene.textFile.charset;

import java.util.HashMap;

public enum Charset
{
	ISO_8859_1(
			"ISO-8859-1",
			new ByteRange[]{},
			new String[]{}
	),

	UTF_8(
			"UTF-8",
			new ByteRange[]{
					new ByteRange("00", "7F"),
					new ByteRange("E0A080", "EFBFBF"),
					new ByteRange("C280", "DFBF"),
					new ByteRange("F0908080", "F48083BF")
			},
			new String[]{
					"\uFEFF"  //UTF-8 BOM
			}
	),

	EUC_KR(
				"EUC-KR",
				new ByteRange[]{
						new ByteRange("00", "7F"),
						new ByteRange("A1A1", "FDFE")
				},
				new String[]{}
	);

	private static HashMap<String, Charset> instanceRegistry;
	private String displayName;
	private ByteRange[] byteRanges;
	private String[] lettersToBeIgnored;

	private Charset(String displayName, ByteRange[] byteRanges, String[] lettersToBeIgnored)
	{
		this.init(displayName, byteRanges, lettersToBeIgnored);
	}

	private void init(String displayName, ByteRange[] byteRanges, String[] lettersToBeIgnored)
	{
		if(instanceRegistry==null)
		{
			instanceRegistry = new HashMap<String, Charset>();
		}
		instanceRegistry.put(displayName, this);
		this.displayName = displayName;
		this.byteRanges = byteRanges;
		this.lettersToBeIgnored = lettersToBeIgnored;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public ByteRange[] getByteRanges()
	{
		return this.byteRanges;
	}

	public String[] getLettersToBeIgnored()
	{
		return this.lettersToBeIgnored;
	}

	public static synchronized Charset getCharset(String charsetCode)
	{
		return instanceRegistry.get(charsetCode);
	}

	public int[] getNumOfBytesSupported()
	{
		int[] numOfBytesSupported = new int[this.byteRanges.length];
		for(int i=0; i<this.byteRanges.length; ++i)
		{
			numOfBytesSupported[i] = this.byteRanges[i].getNumOfBytes();
		}

		return numOfBytesSupported;
	}

	public boolean inRange(String hexString)
	{
		byte[] byteArray = HexStringBinay.h2b(hexString);
		return this.inRange(byteArray);
	}

	public boolean inRange(byte[] byteArray)
	{
		int numOfByte = byteArray.length;
		boolean inRange = false;
		for(int i=0; i<this.byteRanges.length; ++i)
		{
			if(this.byteRanges[i].getNumOfBytes() == numOfByte)
			{
				if((compareBytes(this.byteRanges[i].getStartingByte(), byteArray)<=0)&&(compareBytes(this.byteRanges[i].getEndingByte(), byteArray)>=0))
				{
					inRange = true;
					break;
				}
			}
		}
		return inRange;
	}


	private static final int[] bitMask =  new int[]{0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
	private static synchronized Integer compareBytes(byte[] bytes1, byte[] bytes2)
	{
		Integer result = null;
		if(bytes1!=null && bytes2!=null)
		{
			int range = bytes1.length >= bytes2.length ? bytes1.length-1 : bytes2.length-1;
			for(int i=range; i>=0; --i)
			{
				for(int j=7; j>=0; --j)
				{
					int num1 = 0;
					int num2 = 0;
					if(i<=bytes1.length-1)
					{
						num1 = (int)(bytes1[i] & bitMask[j]);
					}
					if(i<=bytes2.length-1)
					{
						num2 = (int)(bytes2[i] & bitMask[j]);
					}

					if(num1>num2)
					{
						result = 1;
						break;
					}
					else if(num1<num2)
					{
						result = -1;
						break;
					}
					else
					{
						result = 0;
					}
				}
				if(result!=null)
				{
					break;
				}
			}
		}

		return result;
	}

	public static void main(String[] args)
	{
		System.out.println(Charset.UTF_8.inRange("EFBF80"));
		System.out.println(compareBytes(new byte[]{(byte)0x81}, new byte[]{(byte)0x81, (byte)0x81}));
	}
}
