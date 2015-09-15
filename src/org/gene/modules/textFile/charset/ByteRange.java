package org.gene.modules.textFile.charset;

public class ByteRange
{
	private int numOfBytes;
	private byte[] startingByte;
	private byte[] endingByte;

	public ByteRange(){}

	public ByteRange(String startingByte, String endingByte)
	{
		this.numOfBytes = startingByte.length()/2;
		this.startingByte = Hex2Byte.h2b(startingByte);
		this.endingByte = Hex2Byte.h2b(endingByte);
	}

	public int getNumOfBytes()
	{
		return this.numOfBytes;
	}

	public byte[] getStartingByte()
	{
		return this.startingByte;
	}

	public String getStartingHex()
	{
		return Hex2Byte.b2h(this.startingByte);
	}

	public byte[] getEndingByte()
	{
		return this.endingByte;
	}

	public String getEndingHex()
	{
		return Hex2Byte.b2h(this.endingByte);
	}
}