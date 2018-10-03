package com.dailystudio.app.utils;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
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
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.mozilla.universalchardet.UniversalDetector;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dailystudio.development.Logger;
import com.dailystudio.utils.ResourcesUtils;

public class FileUtils {

	private final static String SCHEME_CONTENT = "content";
	private final static String SCHEME_FILE = "file";
	private final static String COLUMN_DATA = "_data";

	private final static String MEDIA_DOC_AUTHORITY = "com.android.providers.media.documents";
	private final static String EXT_STORAGE_DOC_AUTHORITY = "com.android.externalstorage.documents";
	private final static String DOWNLOAD_DOC_AUTHORITY = "com.android.providers.downloads.documents";

	private final static String MEIDA_TYPE_VIDEO = "video";
	private final static String MEIDA_TYPE_AUDIO = "audio";
	private final static String MEIDA_TYPE_IMAGE = "image";

	private final static String NO_MEDIA_TAG_FILE = ".nomedia";
	
	public static final int DOWNLOAD_CONNECTION_TIMEOUT = (3 * 1000);
	public static final int DOWNLOAD_READ_TIMEOUT = (20 * 1000);
	
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
				Logger.warn("%s is NOT a directory", directory);
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
			Logger.warn("could not create tag[%s] in dir[%s]: %s",
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

	public static File[] listFiles(String dir) {
		return listFiles(dir, null);
	}

	public static File[] listFiles(String dir, String pattern) {
		return listFiles(new File(dir), pattern);
	}

	public static File[] listFiles(File dir) {
		return listFiles(dir, null);
	}

	public static File[] listFiles(File dir, String pattern) {
		if (dir == null || dir.exists() == false) {
			return null;
		}

		FileFilter ff = new RegexFilter(pattern);

		return dir.listFiles(ff);
	}

	public static File[] filterFiles(String dir, FileFilter ff) {
		return filterFiles(new File(dir), ff);
	}

	public static File[] filterFiles(File dir, FileFilter ff) {
		if (dir == null || dir.exists() == false) {
			return null;
		}

		return dir.listFiles(ff);
	}

	public static long getFileLastModified(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return 0l;
		}

		File file = new File(fileName);

		return file.lastModified();
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

	public static boolean isAssetFileExisted(Context context, String assetFile) {
		if (context == null
				|| TextUtils.isEmpty(assetFile)) {
			return false;
		}

		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		try {
			inputStream = assetManager.open(assetFile);
			if (null != inputStream) {
				return true;
			}
		} catch(IOException e) {
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
		}

		return false;
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
			Logger.warn("get encodeing failure: %s", e.toString());
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
			Logger.warn("get encodeing failure: %s", e.toString());
		}
		
	    detector.dataEnd();

	    String encoding = detector.getDetectedCharset();

	    detector.reset();
	    
	    try {
	    	istream.close();
	    }  catch (IOException e) {
			Logger.warn("close stream failure: %s", e.toString());
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

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPathFromUri(final Context context, final Uri uri) {
		final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >=  Build.VERSION_CODES.KITKAT;

		if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			} else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if (MEIDA_TYPE_IMAGE.equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if (MEIDA_TYPE_VIDEO.equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if (MEIDA_TYPE_AUDIO.equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		} else if (SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		} else if (SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = COLUMN_DATA;
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);

				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}


	public static boolean isExternalStorageDocument(Uri uri) {
		return EXT_STORAGE_DOC_AUTHORITY.equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return DOWNLOAD_DOC_AUTHORITY.equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return MEDIA_DOC_AUTHORITY.equals(uri.getAuthority());
	}

	public static void writeFileContent(String file, String fileContent) throws IOException {
		writeFileContent(file, fileContent, false);
	}

	public static void writeFileContent(String file, String fileContent, boolean append) throws IOException {
		if (file == null || fileContent == null) {
			return;
		}
		
		StringReader reader = new StringReader(fileContent);
		FileWriter ostream = new FileWriter(file, append);
		
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
		return downloadFile(fileUrl, dstFile,
				DOWNLOAD_CONNECTION_TIMEOUT, DOWNLOAD_READ_TIMEOUT);
	}

	public static boolean downloadFile(String fileUrl, String dstFile,
									   int connTimeout, int readTimeout) {
		if (fileUrl == null || dstFile == null) {
			return false;
		}
		
		if (FileUtils.checkOrCreateFile(dstFile) == false) {
			return false;
		}
		
		boolean ret = false;
		
		try {
			ret = downloadFile(fileUrl, new FileOutputStream(dstFile),
					connTimeout, readTimeout);
		} catch (Exception e) {
			Logger.debug("download file failure: %s", e.toString());

			ret = false;
		}
		
		return ret;
	}

	public static boolean downloadFile(String fileUrl, OutputStream os) {
		return downloadFile(fileUrl, os,
				DOWNLOAD_CONNECTION_TIMEOUT, DOWNLOAD_READ_TIMEOUT);
	}

	public static boolean downloadFile(String fileUrl, OutputStream os,
									   int connTimeout, int readTimeout) {
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
			connection.setConnectTimeout(connTimeout);
			connection.setReadTimeout(readTimeout);
			
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

	public static class RegexFilter implements FileFilter {

		private Pattern mFilePattern;

		private RegexFilter(String pattern) {
			if (!TextUtils.isEmpty(pattern)) {
				mFilePattern = Pattern.compile(pattern);
			}
		}

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}

			if (mFilePattern == null) {
				return true;
			}

			return mFilePattern.matcher(file.getName()).matches();
		}

	}

	public static boolean saveFile(byte[] bytes, String filename) {
		if (TextUtils.isEmpty(filename)) {
			return false;
		}

 		return saveFile(bytes, new File(filename));
	}

	public static boolean saveFile(byte[] bytes, File file) {
		if (bytes == null || file == null) {
			return false;
		}

		boolean success = false;
		try {
			FileOutputStream out = new FileOutputStream(file);

			out.write(bytes);

			out.flush();
			out.close();

			success = true;
		} catch (IOException e) {
			Logger.debug("save bytes failure: %s", e.toString());

			success = false;
		}

		return success;
	}

	public static String md5Dir(String dir) {
		return md5Dir(dir, false);
	}

	public static String md5Dir(String dir, boolean hiddenFies) {
		return md5Dir(new File(dir), hiddenFies);
	}

	public static String md5File(String file) {
		return md5File(new File(file));
	}

	public static String md5Dir(File dir, boolean hiddenFiles) {
		String md5 = "";

		if (dir == null
				|| !dir.exists()
				|| !dir.isDirectory()) {
			return md5;
		}

		File[] files = dir.listFiles();
		if (files == null) {
			return md5;
		}

        Arrays.sort(files);

		String childMd5;
		for (File file: files) {
		    if (!hiddenFiles && file.isHidden()) {
		        continue;
            }

			if (file.isDirectory()) {
				childMd5 = md5Dir(file, hiddenFiles);
			} else {
				childMd5 = md5File(file);
			}

			Logger.debug("[%s] of (%s, %s)",
                    childMd5,
                    file.isDirectory() ? "D" : "F",
                    file.getName());
			md5 += childMd5;
		}

		return md5HashOfString(md5);
	}

	public static String md5File(File file) {
		String md5 = "";

		if (file == null
				|| !file.exists()
				|| !file.isFile()) {
			return md5;
		}

		try {
			InputStream input = new FileInputStream(file);
			byte[] buffer  = new byte[1024];
			MessageDigest md5Hash = MessageDigest.getInstance("MD5");

			int numRead = 0;
			while (numRead != -1) {
				numRead = input.read(buffer);
				if (numRead > 0) {
					md5Hash.update(buffer, 0, numRead);
				}
			}

			input.close();

			byte [] md5Bytes = md5Hash.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            md5 = String.format("%32s", output).replace(' ', '0');
		} catch (Exception e) {
			Logger.error("md5 calculation failed on file[%s]: %s",
					file, e.toString());
		}

		return md5;
	}

	private static String md5HashOfString (String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}

		MessageDigest md5Hash;
		String md5 = "";

		try {
            md5Hash = MessageDigest.getInstance("MD5");
			md5Hash.reset();
			md5Hash.update(str.getBytes(Charset.forName("UTF8")));

			byte md5Bytes[] = md5Hash.digest();

            BigInteger bigInt = new BigInteger(1, md5Bytes);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            md5 = String.format("%32s", output).replace(' ', '0');
		} catch (Exception e) {
			Logger.error("md5 hash failed on string[%s]: %s",
					str, e.toString());
		}

		return md5;
	}

	public static boolean unzip(String zipFile, String destDir) {
		Logger.debug("unzip file [%s] to directory: [%s]",
				zipFile, destDir);
		if (TextUtils.isEmpty(zipFile)
				|| TextUtils.isEmpty(destDir)) {
			return false;
		}

		final long start = System.currentTimeMillis();

		checkOrCreateDir(destDir);

		int count = 0;
		boolean succeed = false;
		try {
			FileInputStream fin = new FileInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;

			StringBuilder builder = new StringBuilder();
			String destPath = null;
			while ((ze = zin.getNextEntry()) != null) {
				builder.setLength(0);
				builder.append(destDir);
				builder.append('/');
				builder.append(ze.getName());

				destPath = builder.toString();
/*
				Logger.debug("[%s] uncompressing [%s] to: [%s]",
						ze.getName(),
						(ze.isDirectory() ? "D" : "F"),
						destPath);
*/

				//create dir if required while unzipping
				if (ze.isDirectory()) {
					checkOrCreateDir(destPath);
				} else {
					FileOutputStream fOut =
							new FileOutputStream(destPath);
					BufferedOutputStream bufOut = new BufferedOutputStream(fOut);
					byte[] buffer = new byte[1024];
					int read = 0;
					while ((read = zin.read(buffer)) != -1) {
						bufOut.write(buffer, 0, read);
					}

					bufOut.close();
					zin.closeEntry();
					fOut.close();
				}

				count++;
			}

			zin.close();

			succeed = true;
		} catch (Exception e) {
			Logger.error("unzip file [%s] to [%s] failed: %s",
					zipFile,
					destDir,
					e.toString());

			succeed = false;
		}

		final long end = System.currentTimeMillis();
		Logger.debug("unzip %d files %s in %d millis.",
				count,
				succeed ? "succeed" : "failed",
				(end - start));

		return succeed;
	}

	private static void checkOrCreateDir(String dir) {
		if (TextUtils.isEmpty(dir)) {
			return;
		}

		checkOrCreateDir(new File(dir));
	}

	private static void checkOrCreateDir(File dir) {
		if (dir == null) {
			return;
		}

		if (dir.exists() == false) {
			dir.mkdir();
		}
	}

}
