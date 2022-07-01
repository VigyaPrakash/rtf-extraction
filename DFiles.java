import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import org.jsoup.select.Elements;

public class DFiles { 

	public static void extractAllFiles(org.jsoup.nodes.Document doc, String docKeyFolderAbsolutePath, String serv) {
		Elements filesAttached = 
				doc.select("a[href~=(?i)\\.(pdf|zip|doc|docx|mht|msg|xls|jpg|jpeg|png|gif|ppt|pptx)|=(pdf|zip|doc|docx|mht|msg|xls|jpg|jpeg|png|gif|ppt|pptx)]");
try{
		

			for (int f=0;f<filesAttached.size();f++) {
				String fileSrc=filesAttached.get(f).attr("href");
				String fileTitle=filesAttached.get(f).attr("title");
				URL urlConn;
				if(fileSrc.contains("http"))
				{
					continue;
				}
				else
				{
				try {
					urlConn = new URL("http://"+serv.substring(serv.indexOf("=")+1, serv.indexOf(("/")))+".henkelgroup.net/"+fileSrc);
				
				} catch (Exception e) {
					serv.indexOf(("/")))+".<domain>/"+fileSrc); //In <domain> put domain name of web url.
					SlLog.addLog("Error in urlConn = "+"http://"+serv.substring(serv.indexOf("=")+1, serv.indexOf(("/")))+".<domain>/"+fileSrc+" Error Msg - "+e.toString()); //In <domain> put domain name of web url.
					continue;
				}
				ReadableByteChannel readableByteChannel = Channels.newChannel(urlConn.openStream());
				FileOutputStream fileOutputStream = new FileOutputStream(docKeyFolderAbsolutePath+"\\"+fileTitle);
				FileChannel fileChannel = fileOutputStream.getChannel();
				fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
				fileOutputStream.close();
				fileChannel.close();
				}
			}
}
catch(Exception e)
{
	SlLog.addLog(e.toString());
}
		
	}
}