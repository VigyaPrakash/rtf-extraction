import java.io.BufferedInputStream;

import java.io.ByteArrayOutputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import lotus.domino.*;

public class JavaAgent extends AgentBase {
		Database db;
		public void NotesMain() {

		try {

			Session session = getSession();

			final String currentDirectory =  session.getEnvironmentString("Notes_TempDir", true); //For scheduling agent to run on server
			//final String currentDirectory =  session.getEnvironmentString("Directory", true); //For running agent locally
			db = session.getCurrentDatabase();
			final String serv=db.getServer();
			final View vwReport=db.getView("VwAttachments"); //Name of view that will have all documents whose RTF field needs to be extracted
			final String dbpath=(db.getFilePath()).replace("\\", "/");

			lotus.domino.Document fDoc = vwReport.getFirstDocument();
			lotus.domino.Document tempDoc=null;

			deleteTemporarilyCreatedFolder(currentDirectory); //It will delete the folder if it already exists else create a new folder

			final File folder = new File(currentDirectory, "Attachments of all documents"); //Name of the main folder that you want to create
			if(!folder.exists())
				folder.mkdir();

			int count=0;
			while (fDoc != null) {
				String fDocUnid="";
				try {
					fDocUnid = fDoc.getUniversalID();
				} catch (NotesException e1) {
					SlLog.addLog(e1.toString());
				}
				tempDoc = vwReport.getNextDocument(fDoc);
				File docKeyFolder = new File(folder, fDoc.getItemValueString("DocKey")); //Any unique key field of the document with which you want the subfolder to be created. This can be UNID too.
				docKeyFolder.mkdir();
				String docKeyFolderAbsolutePath = docKeyFolder.getAbsolutePath();

				Document doc=null;
				try {
					doc = Jsoup.connect("http://"+serv.substring(serv.indexOf("=")+1, serv.indexOf(("/")))+".<domain>/"+dbpath+"/0/"+fDoc.getUniversalID()+"/Attachment?openfield&charset=utf-8").get(); //URL of the RTF field of the document. Attachment is name of the RTF field. In <domain> put domain name of web url. Remove angular brackets after entering domain name.
				} catch (IOException e) {
					SlLog.addLog("Couldn't fetch doc = "+fDocUnid+" Error Msg - "+e.toString());
					fDoc = tempDoc;
					continue;
					
				}
				Elements images = 
						doc.select("img[src~=(?i)\\.(png|jpe?g|gif)|=(png|jpe?g|gif)]") ;
				Elements imgatags=doc.select("a");
				ArrayList imgNames=new ArrayList();


				for (int im=0;im<images.size();im++) {
					String imgSrc=images.get(im).attr("src");
					URL urlConn = new URL("http://"+serv.substring(serv.indexOf("=")+1, serv.indexOf(("/")))+".<domain>/"+imgSrc); //put domain of web url in <domain>
					InputStream in = new BufferedInputStream(urlConn.openStream());
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int n = 0;
					while (-1!=(n=in.read(buf)))
					{
						out.write(buf, 0, n);

					}
					out.close();

					in.close(); //new statement added

					byte[] response = out.toByteArray();
					FileOutputStream fos=null;
					int imgSrcIndexOfSlash=imgSrc.lastIndexOf("/");
					if(imgSrc.contains("="))
					{

						fos = new FileOutputStream(docKeyFolderAbsolutePath+imgSrc.substring(imgSrcIndexOfSlash,imgSrc.lastIndexOf("?")));
						imgNames.add(imgSrc.substring(imgSrcIndexOfSlash,imgSrc.lastIndexOf("?")));
					}
					else
					{
						fos = new FileOutputStream(docKeyFolderAbsolutePath+imgSrc.substring(imgSrcIndexOfSlash));
						imgNames.add(imgSrc.substring(imgSrcIndexOfSlash));
					}

					fos.write(response);
					fos.close();
					fos.flush();

				}


				DFiles.extractAllFiles(doc,docKeyFolderAbsolutePath,serv);

				DataOutputStream dos;
				String attachmentTitle=fDoc.getItemValueString("AttachmentTitle"); //The name with which word rtf document needs to be created.
				//The name with which word rtf needs to be created must not contain symbols: /,\,|,:,?,<,>," and tab space
				if (attachmentTitle.contains("\\"))
				{
					attachmentTitle=attachmentTitle.replaceAll("\\", "-");
				}
				if (attachmentTitle.contains("/"))
				{ 
					attachmentTitle=attachmentTitle.replaceAll("/", "-");
				}

				if (attachmentTitle.contains("|"))
				{
					attachmentTitle=attachmentTitle.replaceAll("|", "-");
				}

				if (attachmentTitle.contains(":"))
				{
					attachmentTitle=attachmentTitle.replaceAll(":", "-");
				}

				if (attachmentTitle.contains("?")) 
				{
					attachmentTitle=attachmentTitle.replaceAll("?", "-");
				}
				if (attachmentTitle.contains("<"))
				{
					attachmentTitle=attachmentTitle.replaceAll("<", "-");
				}

				if (attachmentTitle.contains(">"))
				{
					attachmentTitle=attachmentTitle.replaceAll(">", "-");
				}

				if (attachmentTitle.contains("\"")) 
				{
					attachmentTitle=attachmentTitle.replaceAll("\"", "-");
				}
				if (attachmentTitle.contains("	")) //Added on 1st July,2022 by Vigya
				{
					attachmentTitle=attachmentTitle.replaceAll("	", "-"); //Added on 1st July,2022 by Vigya
				}

				File _file=new File(docKeyFolderAbsolutePath+"\\"+attachmentTitle+".rtf");
				String generatedHTML=doc.html();
				dos = new DataOutputStream(new FileOutputStream(_file));
				for(int i=0;i<imgNames.size();i++)
				{
					String imsrc=images.get(i).attr("src");


					if(imsrc.contains("&"))
					{
						imsrc=imsrc.replace("&", "&amp;");
					}
					generatedHTML=generatedHTML.replace(imsrc, ((String)imgNames.get(i)).replace("/", ""));
				}


				for(int k=0;k<imgatags.size();k++)
				{
					String imghrefs=imgatags.get(k).attr("href");

					if(imghrefs.contains("&"))
					{
						imghrefs=imghrefs.replace("&", "&amp;");
					}

					if(!imghrefs.contains(".nsf") || imghrefs.contains("javascript") && imghrefs!="")
					{

						continue;

					}
					String currentimghref=imgatags.get(k).attr("title");
					generatedHTML=generatedHTML.replace(imghrefs, currentimghref);
				}



				dos.writeBytes(generatedHTML);
				dos.close();
				images.clear();
				imgatags.clear();
				imgNames.clear();
				generatedHTML="";


				count++;

				fDoc = tempDoc;  

			}

			ZipUtils.appZip(folder.getAbsolutePath()+".zip", folder.getAbsolutePath()); //Zipping all rtf files together

			lotus.domino.Document d = db.createDocument();
			d.replaceItemValue("Form", "ExportFromJSoup"); //Putting zipped folder in LN form. 
			RichTextItem ritem = d.createRichTextItem("Body");
			ritem.embedObject(EmbeddedObject.EMBED_ATTACHMENT,null, folder.getAbsolutePath()+".zip", folder.getName()+".zip");

			d.save();

			System.out.println("Zipped folder attached to LN document successfully");
			
			File f=new File(folder.getAbsolutePath()+".zip");
			f.delete();	//Deleting zipped folder from temporariliy created folder under Directory/Notes_TempDir

			deleteTemporarilyCreatedFolder(currentDirectory);  //Deleting temporariliy created folder under Directory/Notes_TempDir
			
			
		} catch(Exception e) {
			SlLog.addLog(e.toString());
		}

		SlLog.logEntry(db,"Attachment");
	}

	private void deleteTemporarilyCreatedFolder(String tempdir)
	{
		File folder=new File(tempdir+"\\Attachments of all documents");
		if(folder.exists())
		{
			String[]entries = folder.list();
			for(int m=0;m<entries.length;m++)
			{
				String s=entries[m];
				File unidFolder = new File(folder.getPath(),s);
				if(unidFolder.exists())
				{
					String[] entriesOfSubfolder = unidFolder.list();
					for(int n=0;n<entriesOfSubfolder.length;n++)
					{
						String s2=entriesOfSubfolder[n];
						File currentFile = new File(unidFolder.getPath(),s2);
						currentFile.delete();
					}
					unidFolder.delete();
				}

			}
			folder.delete();
		}
		else{
			System.out.println("Folder doesn't exist!");
		}
	}

}
