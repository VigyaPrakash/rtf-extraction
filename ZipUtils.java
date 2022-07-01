import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private ArrayList fileList;
       static String OUTPUT_ZIP_FILE="";
       static String SOURCE_FOLDER =""; // SourceFolder path

    public ZipUtils() {
        fileList = new ArrayList();
        
    }

    public static void appZip(String outputZipFile, String sourceFolder) {
        ZipUtils appZip = new ZipUtils();
        OUTPUT_ZIP_FILE=outputZipFile;
        System.out.println("OUTPUT_ZIP_FILE= "+OUTPUT_ZIP_FILE);
        SOURCE_FOLDER = sourceFolder;
        System.out.println("SOURCE_FOLDER= "+SOURCE_FOLDER);
        appZip.generateFileList(new File(sourceFolder));
        appZip.zipIt(outputZipFile);
    }

    public void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(SOURCE_FOLDER).getName();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;

            for(int i=0;i<this.fileList.size();i++)
            {
            	String file=(String) fileList.get(i);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                        
                    }
                } finally {
                    in.close();
                }
            }

            zos.closeEntry();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateFileList(File node) {
        // add file only
        if (node.isFile()) {
            fileList.add(generateZipEntry(node.toString()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for(int k=0;k<subNote.length;k++)
            {
            	String filename=subNote[k];
                generateFileList(new File(node, filename));
            }
        }
    }

    private String generateZipEntry(String file) {
        return file.substring(SOURCE_FOLDER.length() + 1, file.length());
    }
}
