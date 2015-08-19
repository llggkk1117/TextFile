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
		this.startingByte = HexStringBinay.h2b(startingByte);
		this.endingByte = HexStringBinay.h2b(endingByte);
	}

	public int getNumOfBytes()
	{
		return this.numOfBytes;
	}

	public byte[] getStartingByte()
	{
		return this.startingByte;
	}

	public String getStartingByteHexString()
	{
		return HexStringBinay.b2h(this.startingByte);
	}

	public byte[] getEndingByte()
	{
		return this.endingByte;
	}

	public String getEndingByteHexString()
	{
		return HexStringBinay.b2h(this.endingByte);
	}
}