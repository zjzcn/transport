package transport.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
  
public class PackageUtils {  

	private static final String FILE_URL_PREFIX = "file:";
	private static final String URL_PROTOCOL_FILE = "file";
	private static final String URL_PROTOCOL_JAR = "jar";
	private static final String URL_PROTOCOL_ZIP = "zip";
//	private static final String URL_PROTOCOL_VFSZIP = "vfszip";
//	private static final String URL_PROTOCOL_VFS = "vfs";
	private static final String URL_PROTOCOL_WSJAR = "wsjar";
	private static final String JAR_URL_SEPARATOR = "!/";

    public static void main(String[] args) throws Exception {  
        String packageName = "org";  
//        getClassNameByJar1(packageName);
         List<String> classNames = getClassNames(packageName);
//        List<String> classNames = getClassNameByJar1(packageName);  
        if (classNames != null) {  
            for (String className : classNames) {  
                System.out.println(className);
            }  
        } 
    }  
  
  
    public static List<String> getClassNames(String packageName) throws IOException {  
        List<String> fileNames = null;  
        ClassLoader loader = Thread.currentThread().getContextClassLoader();  
        String packagePath = packageName.replace(".", "/");  
        Enumeration<URL> urls = loader.getResources(packagePath);  
        while (urls != null && urls.hasMoreElements()) {
        	URL url = urls.nextElement();
        	String urlPath = url.getPath();    
        	String up = url.getProtocol();  
        	if (URL_PROTOCOL_JAR.equals(up) || URL_PROTOCOL_ZIP.equals(up) || URL_PROTOCOL_WSJAR.equals(up)) {  
        		fileNames = getClassNameByJar(urlPath);  
        	} else if (URL_PROTOCOL_FILE.equals(up)) {  
        		fileNames = getClassNameByFile(urlPath, null);  
        	}
        }
        return fileNames;  
    }  
  
    private static List<String> getClassNameByFile(String filePath, List<String> className) {  
    	List<String> myClassName = new ArrayList<String>();  
    	File file = new File(filePath);  
    	File[] childFiles = file.listFiles();  
    	for (File childFile : childFiles) {  
    		if (childFile.isDirectory()) {  
    			myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName));  
    		} else {  
    			String childFilePath = childFile.getPath();  
    			if (childFilePath.endsWith(".class")) {  
    				childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));  
    				childFilePath = childFilePath.replace("\\", ".");  
    				myClassName.add(childFilePath);  
    			}  
    		}  
    	}  
  
        return myClassName;  
    }  
  
    private static List<String> getClassNameByJar(String urlPath) throws IOException {  
        List<String> classNames = new ArrayList<String>();  
		String jarFileUrl;
		String packagePath;
		JarFile jarFile;
		int separatorIndex = urlPath.indexOf(JAR_URL_SEPARATOR);
		if (separatorIndex != -1) {
			jarFileUrl = urlPath.substring(0, separatorIndex);
			packagePath = urlPath.substring(separatorIndex + JAR_URL_SEPARATOR.length());
		}
		else {
			jarFileUrl = urlPath;
			packagePath = "";
		}
		jarFileUrl = jarFileUrl.startsWith(FILE_URL_PREFIX) ? jarFileUrl.substring(FILE_URL_PREFIX.length()) : jarFileUrl;
		jarFile = new JarFile(jarFileUrl);
        try {  
            Enumeration<JarEntry> entrys = jarFile.entries();  
            while (entrys.hasMoreElements()) {  
                JarEntry jarEntry = entrys.nextElement();  
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                	if (entryName.startsWith(packagePath)) {
                		entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));  
                		classNames.add(entryName);
                	}  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            jarFile.close();
        }  
        return classNames; 
    }  
    
}  