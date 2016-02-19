package com.dailystudio.app.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.mozilla.universalchardet.UniversalDetector;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.dailystudio.development.Logger;
import com.dailystudio.utils.ResourcesUtils;

public class FileUtils {

	private final static String NO_MEDIA_TAG_FILE = ".nomedia";
	
	private static final int DOWNLOAD_CONNECTION_TIMEOUT = (3 * 1000);
	private static final int DOWNLOAD_READ_TIMEOUT = (20 * 1000);
	
	public static final long SIZE_KB = 1024;
	public static final long SIZE_MB = (1024 * SIZE_KB);
	public static final long SIZE_GB = (1024 * SIZE_MB);
	public static final long SIZE_TB = (1024 * SIZE_GB);

	public static boolean checkOrCreateNoMediaDirectory(String directory) {
		if (directory == null) {
			return false;
		}
		
		File dir = new File(directory);
	
		return checkOrCreateNoMediaDirectory(dir);
	}
	
	public static boolean checkOrCreateNoMediaDirectory(File directory) {
		return checkOrCreateDirectory(directory, true);
	}
	
	public static boolean checkOrCreateDirectory(String directory) {
		if (directory == null) {
			return false;
		}
		
		File dir = new File(directory);
	
		return checkOrCreateDirectory(dir);
	}
	
	public static boolean checkOrCreateDirectory(File directory) {
		return checkOrCreateDirectory(directory, false);
	}
	
	public static boolean checkOrCreateDirectory(File directory, boolean nomedia) {
		if (directory == null) {
			return false;
		}
		
		if (directory.exists()) {
			if (directory.isDirectory()) {
				return true;
			} else {
				Logger.warnning("%s is NOT a directory", directory);
			}
		}
		
		final boolean success = directory.mkdirs();
		if (success == false) {
			return false;
		}
		
		if (!nomedia) {
			return success;
		}
		
		return checkOrCreateNoMediaTagInDirectory(directory);
	}

	public static boolean checkOrCreateNoMediaTagInDirectory(String directory) {
		if (directory == null) {
			return false;
		}
		
		File dir = new File(directory);
	
		return checkOrCreateNoMediaTagInDirectory(dir);
	}
	
	public static boolean checkOrCreateNoMediaTagInDirectory(File dir) {
		if (dir == null) {
			return false;
		}
		
		File tagFile = new File(dir, NO_MEDIA_TAG_FILE);
		if (tagFile.exists()) {
			return true;
		}
		
		boolean success = false;
		try {
			success = tagFile.createNewFile();
		} catch (IOException e) {
			Logger.warnning("could not create tag[%s] in dir[%s]: %s",
					NO_MEDIA_TAG_FILE,
					dir.getAbsoluteFile(),
					e.toString());
			
			success = false;
		}
		
		return success;
	}
	
	public static boolean isFileExisted(String filename) {
		if (filename == null) {
			return false;
		}
		
		File dstFile = new File(filename);
		if (dstFile.exists()) {
			return true;
		}
		
		return false;
	}

	public static String getDirectory(String filename) {
		if (filename == null) {
			return null;
		}
		
		File dstFile = new File(filename);
		
		return dstFile.getParent();
	}
	
	public static boolean checkOrCreateFile(String filename) {
		if (filename == null) {
			return false;
		}
		
		File file = new File(filename);
		
		return checkOrCreateFile(file);
	}
	
	public static boolean checkOrCreateFile(File file) {
		if (file == null) {
			return false;
		}
		
		if (file.exists()) {
			return true;
		}
		
		boolean success = false;
		try {
			success = file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		
		return success;
	}
	
	public static boolean deleteFiles(String path) {
		return deleteFiles(path, true);
	}

	public static boolean deleteFiles(String path, boolean includeFolder) {
		if (path == null) {
			return false;
		}
		
	    File file = new File(path);

	    boolean success = false;
	    if (file.exists()) {
	    	StringBuilder deleteCmd =
	    			new StringBuilder("rm -r ");
	    	
	    	deleteCmd.append(path);
	    	if (!includeFolder) {
	    		deleteCmd.append("/*");
	    	}
	        
	        Runtime runtime = Runtime.getRuntime();
	        try {
	        	Logger.debug("delete cmd: %s", 
	        			deleteCmd.toString());
	            runtime.exec(deleteCmd.toString()).waitFor();
	            
	            success = true;
	        } catch (IOException e) { 
	        	Logger.debug("delete failure: [%s]", e.toString());
	        	
	        	success = false;
	        } catch (InterruptedException e) {
	        	Logger.debug("delete failure: [%s]", e.toString());
	        	
	        	success = false;
			}
	    }
	    
	    return success;
	}
	
	public static boolean deleteFile(String fileName) {
		if (fileName == null) {
			return false;
		}
		
	    File file = new File(fileName);
	    
	    return deleteFile(file);
	}
	
	public static boolean deleteFile(File file) {
		if (file == null) {
			return false;
		}
		
	    boolean success = false;
	    if (file.exists()) {
	        success = file.delete();
	    }
	    
	    return success;
	}
	
	public static long getFolderSize(String dirName) {
		if (TextUtils.isEmpty(dirName)) {
			return 0l;
		}
		
		return getFolderSize(new File(dirName));
	}
	
	public static long getFolderSize(File dir) {
		if (dir == null || dir.exists() == false) {
			return 0l;
		}
		
		long size = 0l;

		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				size += file.length();
			} else {
				size += getFolderSize(file);
			}
		}

		return size;
	}

	public static long getFileLength(String file) {
		if (file == null) {
			return 0l;
		}
		
		File dstFile = new File(file);
		if (dstFile.exists()) {
			return 0l;
		}

		return dstFile.length();
	}
	
	public static String getFileContent(String file) throws IOException {
		if (file == null) {
			return null;
		}
		
		final String encoding = detectFileEncoding(file);
		Logger.debug("encoding = %s", encoding);

		return getFileContent(new FileInputStream(file), encoding);
	}
	
	public static String getFileContent(InputStream istream, String encoding) throws IOException {
		if (istream == null) {
			return null;
		}
		
		InputStreamReader ireader = null;
		if (encoding != null) {
			ireader = new InputStreamReader(istream, encoding);
		} else {
			ireader = new InputStreamReader(istream);
		}
		
		StringWriter writer = new StringWriter();
		
		char buffer[] = new char[2048];
		int n = 0;
		while((n = ireader.read(buffer)) != -1) {
			writer.write(buffer, 0, n);
		}
		
		writer.flush();
		istream.close();
		
		return writer.toString();
	}
	
	public static String getAssetFileContent(Context context, String file) throws IOException {
		if (context == null || file == null) {
			return null;
		}
		
		final AssetManager asstmgr = context.getAssets();
		if (asstmgr == null) {
			return null;
		}
		
		final String encoding = detectFileEncoding(asstmgr.open(file));
		Logger.debug("encoding = %s", encoding);

		InputStream istream = asstmgr.open(file);
		if (istream == null) {
			return null;
		}
		
		return getFileContent(istream, encoding);
	}
	
    public static String getRawFileContent(Context context, int rawId) throws IOException {
        if (context == null || rawId <= 0) {
            return null;
        }

        final Resources res = context.getResources();
        if (res == null) {
            return null;
        }

        InputStream istream = res.openRawResource(rawId);
        if (istream == null) {
            return null;
        }

        final String encoding = FileUtils.detectFileEncoding(istream);

        return FileUtils.getFileContent(res.openRawResource(rawId),
                encoding);
    }

	
	public static boolean copyRawFile(Context context, String rawFile, 
			String dstFile) throws IOException {
		if (context == null 
				|| rawFile == null
				|| dstFile == null) {
			return false;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return false;
		}
		
		final int resId = res.getIdentifier(rawFile, 
				"raw", context.getPackageName());
		if (resId <= 0) {
			return false;
		}
		
		InputStream istream = res.openRawResource(resId);
		if (istream == null) {
			return false;
		}		
		
		FileOutputStream ostream = 
				new FileOutputStream(dstFile);

		return ResourcesUtils.copyToFile(istream, ostream);
	}
	
	public static boolean copyAssetFile(Context context, 
			String assetFile, String dstFile) throws IOException {
		if (context == null 
				|| assetFile == null
				|| dstFile == null) {
			return false;
		}
		
		final AssetManager asstmgr = context.getAssets();
		if (asstmgr == null) {
			return false;
		}
		
		InputStream istream = asstmgr.open(assetFile);
		if (istream == null) {
			return false;
		}
		
		FileOutputStream ostream = 
				new FileOutputStream(dstFile);

		return ResourcesUtils.copyToFile(istream, ostream);
	}
	
	public static String detectFileEncoding(String filename) {
		if (filename == null) {
			return null;
		}
		
		File file = new File(filename);
		
		return detectFileEncoding(file);
	}
	
	public static String detectFileEncoding(File file) {
		if (file == null) {
			return null;
		}
		
	    FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Logger.warnning("get encodeing failure: %s", e.toString());
			fis = null;
		}
		
		return detectFileEncoding(fis);
	}
	
	public static String detectFileEncoding(InputStream istream) {
		if (istream == null) {
			return null;
		}
		
	    byte[] buf = new byte[4096];
	    
	    UniversalDetector detector = new UniversalDetector(null);

	    int nread;
	    try {
			while ((nread = istream.read(buf)) > 0 && !detector.isDone()) {
			  detector.handleData(buf, 0, nread);
			}
		} catch (IOException e) {
			Logger.warnning("get encodeing failure: %s", e.toString());
		}
		
	    detector.dataEnd();

	    String encoding = detector.getDetectedCharset();

	    detector.reset();
	    
	    try {
	    	istream.close();
	    }  catch (IOException e) {
			Logger.warnning("close stream failure: %s", e.toString());
		}
	    
	    return encoding;
	}
	
	public static String getFileExtension(String filename) {
		return getFileExtension(filename, "");
	}

    public static String getFileExtension(String filename, String defExt) {
    	if ((filename != null) && (filename.length() > 0)) {
    		int i = filename.lastIndexOf('.');

    		if ((i >-1) && (i < (filename.length() - 1))) {
    			return filename.substring(i + 1);
    		}
    	}
    	
    	return defExt;
    }
    
	public static void writeFileContent(String file, String fileContent) throws IOException {
		if (file == null || fileContent == null) {
			return;
		}
		
		StringReader reader = new StringReader(fileContent);
		FileWriter ostream = new FileWriter(file);
		
		char buffer[] = new char[2048];
		int n = 0;
		while((n = reader.read(buffer)) != -1) {
			ostream.write(buffer, 0, n);
		}
		
		ostream.flush();
		reader.close();
		
		return;
	}
	
	public static boolean downloadFile(String fileUrl, String dstFile) {
		if (fileUrl == null || dstFile == null) {
			return false;
		}
		
		if (FileUtils.checkOrCreateFile(dstFile) == false) {
			return false;
		}
		
		boolean ret = false;
		
		try {
			ret = downloadFile(fileUrl, new FileOutputStream(dstFile));
		} catch (Exception e) {
			Logger.debug("download file failure: %s", e.toString());

			ret = false;
		}
		
		return ret;
	}
	
	public static boolean downloadFile(String fileUrl, OutputStream os) {
		if (fileUrl == null || os == null) {
			return false;
		}
		
		InputStream is = null;

		boolean success = false;
		try {
			URL u = new URL(fileUrl);

			URLConnection connection = u.openConnection();
			
			/*
			 * XXX: we could not use u.openStream() here.
			 * 		the default connect/read timeout is infinite.
			 * 		we need to set a acceptable value
			 */
			connection.setConnectTimeout(DOWNLOAD_CONNECTION_TIMEOUT);
			connection.setReadTimeout(DOWNLOAD_READ_TIMEOUT);
			
			is = connection.getInputStream();
			
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);
			
			@SuppressWarnings("unused")
			int bytesReceived = 0;
			int bytesRead = 0;
			byte[] buffer = new byte[2048];

			while ((bytesRead = dis.read(buffer, 0, 2048)) > 0) {
				bytesReceived += bytesRead;
//				Logger.debug("bytes received = %d", bytesReceived);
				
				dos.write(buffer, 0, bytesRead);
				dos.flush();
			}
			
			success = true;
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			
			success = false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			
			success = false;
		}  catch (NullPointerException ne) {
			/*
			 * XXX: sometime, here will be thrown
			 * 		a NULL-pointer exception for address 
			 * 		resolving.
			 */
			ne.printStackTrace();
			
			success = false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				
				if (os != null) {
					os.close();
				}
			} catch (IOException ioe) {
			}
		}

		return success;
	}

}
