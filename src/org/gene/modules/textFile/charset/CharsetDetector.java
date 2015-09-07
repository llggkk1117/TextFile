package org.gene.modules.textFile.charset;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

public class CharsetDetector
{
	public static void test(String[] argv) throws IOException
	{
		// Initalize the nsDetector() ;
		int lang = (argv.length == 2)? Integer.parseInt(argv[1]) : nsPSMDetector.ALL ;
		nsDetector det = new nsDetector(lang) ;

		// Set an observer...
		// The Notify() will be called when a matching charset is found.

		det.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset) {
				HtmlCharsetDetector.found = true ;
				System.out.println("CHARSET = " + charset);
			}
		});

		URL url = new URL(argv[0]);
		BufferedInputStream imp = new BufferedInputStream(url.openStream());

		byte[] buf = new byte[1024] ;
		int len;
		boolean done = false ;
		boolean isAscii = true ;

		while( (len=imp.read(buf,0,buf.length)) != -1) {

			// Check if the stream is only ascii.
			if (isAscii)
				isAscii = det.isAscii(buf,len);

			// DoIt if non-ascii and not done yet.
			if (!isAscii && !done)
				done = det.DoIt(buf,len, false);
		}
		det.DataEnd();

		if (isAscii) {
			System.out.println("CHARSET = ASCII");
			HtmlCharsetDetector.found = true ;
		}
	}

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
