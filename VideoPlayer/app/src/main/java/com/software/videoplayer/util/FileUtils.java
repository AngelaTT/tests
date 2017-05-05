package com.software.videoplayer.util;

import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.software.videoplayer.data.model.Folder;
import com.software.videoplayer.data.model.Song;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文件操作工具包
 * 
 * @author tracyZhang
 * 
 */
public class FileUtils {
	static String tag = "FileUtils";

//	/**
//	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
//	 * 
//	 * @param context
//	 * @param msg
//	 */
//	public static void write(Context context, String fileName, String content) {
//		if (content == null)
//			content = "";
//
//		try {
//			FileOutputStream fos = context.openFileOutput(fileName,
//					Context.MODE_PRIVATE);
//			fos.write(content.getBytes());
//
//			fos.close();
//		} catch (Exception e) {
//			Log.e(tag, Log.getStackTraceString(e));
//		}
//	}

//	/**
//	 * 读取文本文件
//	 * 
//	 * @param context
//	 * @param fileName
//	 * @return
//	 */
//	public static String read(Context context, String fileName) {
//		try {
//			FileInputStream in = context.openFileInput(fileName);
//			return readInStream(in);
//		} catch (Exception e) {
//			Log.e(tag, Log.getStackTraceString(e));
//		}
//		return "";
//	}

//	private static String readInStream(FileInputStream inStream) {
//		try {
//			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//			byte[] buffer = new byte[512];
//			int length = -1;
//			while ((length = inStream.read(buffer)) != -1) {
//				outStream.write(buffer, 0, length);
//			}
//
//			outStream.close();
//			inStream.close();
//			return outStream.toString();
//		} catch (IOException e) {
//			Log.i("FileTest", e.getMessage());
//		}
//		return null;
//	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

//	/**
//	 * 向手机写图片
//	 * 
//	 * @param buffer
//	 * @param folder
//	 * @param fileName
//	 * @return
//	 */
//	public static boolean writeFile(byte[] buffer, String folder,
//			String fileName) {
//		boolean writeSucc = false;
//
//		boolean sdCardExist = Environment.getExternalStorageState().equals(
//				android.os.Environment.MEDIA_MOUNTED);
//
//		String folderPath = "";
//		if (sdCardExist) {
//			folderPath = Environment.getExternalStorageDirectory()
//					+ File.separator + folder + File.separator;
//		} else {
//			writeSucc = false;
//		}
//
//		File fileDir = new File(folderPath);
//		if (!fileDir.exists()) {
//			fileDir.mkdirs();
//		}
//
//		File file = new File(folderPath + fileName);
//		FileOutputStream out = null;
//		try {
//			out = new FileOutputStream(file);
//			out.write(buffer);
//			writeSucc = true;
//		} catch (Exception e) {
//			Log.e(tag, Log.getStackTraceString(e));
//		} finally {
//			try {
//				out.close();
//			} catch (IOException e) {
//				Log.e(tag, Log.getStackTraceString(e));
//			}
//		}
//
//		return writeSucc;
//	}

//	/**
//	 * 根据文件绝对路径获取文件名
//	 * 
//	 * @param filePath
//	 * @return
//	 */
//	public static String getFileName(String filePath) {
//		if (TextUtils.isEmpty(filePath))
//			return "";
//		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
//	}

//	/**
//	 * 根据文件的绝对路径获取文件名但不包含扩展名
//	 * 
//	 * @param filePath
//	 * @return
//	 */
//	public static String getFileNameNoFormat(String filePath) {
//		if (TextUtils.isEmpty(filePath)) {
//			return "";
//		}
//		int point = filePath.lastIndexOf('.');
//		return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
//				point);
//	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (TextUtils.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 *
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSizeStr(long size) {
		if (size <= 0)
			return "0.0B";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

//	/**
//	 * 转换文件大小
//	 * 
//	 * @param fileS
//	 * @return B/KB/MB/GB
//	 */
//	public static String formatFileSize(long fileS) {
//		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
//		String fileSizeString = "";
//		if (fileS < 1024) {
//			fileSizeString = df.format((double) fileS) + "B";
//		} else if (fileS < 1048576) {
//			fileSizeString = df.format((double) fileS / 1024) + "KB";
//		} else if (fileS < 1073741824) {
//			fileSizeString = df.format((double) fileS / 1048576) + "MB";
//		} else {
//			fileSizeString = df.format((double) fileS / 1073741824) + "G";
//		}
//		return fileSizeString;
//	}

//	/**
//	 * 获取目录文件大小
//	 * 
//	 * @param dir
//	 * @return
//	 */
//	public static long getDirSize(File dir) {
//		if (dir == null) {
//			return 0;
//		}
//		if (!dir.isDirectory()) {
//			return 0;
//		}
//		long dirSize = 0;
//		File[] files = dir.listFiles();
//		for (File file : files) {
//			if (file.isFile()) {
//				dirSize += file.length();
//			} else if (file.isDirectory()) {
//				dirSize += file.length();
//				dirSize += getDirSize(file); // 递归调用继续统计
//			}
//		}
//		return dirSize;
//	}

//	/**
//	 * 获取目录文件个数
//	 * 
//	 * @param f
//	 * @return
//	 */
//	public long getFileList(File dir) {
//		long count = 0;
//		File[] files = dir.listFiles();
//		count = files.length;
//		for (File file : files) {
//			if (file.isDirectory()) {
//				count = count + getFileList(file);// 递归
//				count--;
//			}
//		}
//		return count;
//	}

//	public static byte[] toBytes(InputStream in) throws IOException {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		int ch;
//		while ((ch = in.read()) != -1) {
//			out.write(ch);
//		}
//		byte buffer[] = out.toByteArray();
//		out.close();
//		return buffer;
//	}

//	/**
//	 * 检查文件是否存在
//	 * 
//	 * @param name
//	 * @return
//	 */
//	public static boolean checkFileExists(String name) {
//		boolean status;
//		if (!name.equals("")) {
//			File path = Environment.getExternalStorageDirectory();
//			File newPath = new File(path.toString() + name);
//			status = newPath.exists();
//		} else {
//			status = false;
//		}
//		return status;
//	}

//	/**
//	 * 检查路径是否存在
//	 * 
//	 * @param path
//	 * @return
//	 */
//	public static boolean checkFilePathExists(String path) {
//		return new File(path).exists();
//	}

//	/**
//	 * 计算SD卡的剩余空间
//	 * 
//	 * @return 返回-1，说明没有安装sd卡
//	 */
//	public static long getFreeDiskSpace() {
//		String status = Environment.getExternalStorageState();
//		long freeSpace = 0;
//		if (status.equals(Environment.MEDIA_MOUNTED)) {
//			try {
//				File path = Environment.getExternalStorageDirectory();
//				StatFs stat = new StatFs(path.getPath());
//				long blockSize = stat.getBlockSize();
//				long availableBlocks = stat.getAvailableBlocks();
//				freeSpace = availableBlocks * blockSize / 1024;
//			} catch (Exception e) {
//				Log.e(tag, Log.getStackTraceString(e));
//			}
//		} else {
//			return -1;
//		}
//		return (freeSpace);
//	}

//	/**
//	 * 新建目录
//	 * 
//	 * @param directoryName
//	 * @return
//	 */
//	public static boolean createDirectory(String directoryName) {
//		boolean status;
//		if (!directoryName.equals("")) {
//			File path = Environment.getExternalStorageDirectory();
//			File newPath = new File(path.toString() + directoryName);
//			status = newPath.mkdir();
//			status = true;
//		} else
//			status = false;
//		return status;
//	}

//	/**
//	 * 检查是否安装SD卡
//	 * 
//	 * @return
//	 */
//	public static boolean checkSaveLocationExists() {
//		String sDCardStatus = Environment.getExternalStorageState();
//		boolean status;
//		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
//			status = true;
//		} else
//			status = false;
//		return status;
//	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 *
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/"
								+ listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					status = true;
				} catch (Exception e) {
					Log.e(tag, Log.getStackTraceString(e));
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 *
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {
			File newPath = new File(fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
//					Log.i("DirectoryManager deleteFile", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
//					sLog.e(tag, Log.getStackTraceString(e));
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}
	/**
	 * 复制单个文件
	 * @param oldPath String 原文件路径 如：c:/fqf.txt
	 * @param newPath String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024*5];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();

		}
	}
	public static void cutFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024*5];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}finally {
			delete(oldPath);
		}
	}

	/**
	 * 复制整个文件夹内容
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
			File a=new File(oldPath);
			String[] file=a.list();
			File temp=null;
			for (int i = 0; i < file.length; i++) {
				if(oldPath.endsWith(File.separator)){
					temp=new File(oldPath+file[i]);
				}
				else{
					temp=new File(oldPath+File.separator+file[i]);
				}

				if(temp.isFile()){
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" +
							(temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ( (len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if(temp.isDirectory()){//如果是子文件夹
					copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();

		}

	}

//	/**
//	 * 删除空目录
//	 * 
//	 * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
//	 * 
//	 * @return
//	 */
//	public static int deleteBlankPath(String path) {
//		File f = new File(path);
//		if (!f.canWrite()) {
//			return 1;
//		}
//		if (f.list() != null && f.list().length > 0) {
//			return 2;
//		}
//		if (f.delete()) {
//			return 0;
//		}
//		return 3;
//	}

	/**
	 * 重命名
	 *
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean reNamePath(String oldName, String newName) {
		File f = new File(oldName);
		return f.renameTo(new File(newName));
	}

//	/**
//	 * 删除文件
//	 * 
//	 * @param filePath
//	 */
//	public static boolean deleteFileWithPath(String filePath) {
//		SecurityManager checker = new SecurityManager();
//		File f = new File(filePath);
//		checker.checkDelete(filePath);
//		if (f.isFile()) {
//			Log.i("DirectoryManager deleteFile", filePath);
//			f.delete();
//			return true;
//		}
//		return false;
//	}

//	/**
//	 * 获取SD卡的根目录，末尾带\
//	 * 
//	 * @return
//	 */
//	public static String getSDRoot() {
//		return Environment.getExternalStorageDirectory().getAbsolutePath()
//				+ File.separator;
//	}

	/**
	 * 列出root目录下所有子目录
	 * @return 绝对路径
	 */
	public static List<String> listPath(String root) {
		List<String> allDir = new ArrayList<String>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory()) {
					allDir.add(f.getAbsolutePath());
				}
			}
		}
		return allDir;
	}
	
	public static List<File> getChild(String root) {
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		if (path.isDirectory()) 
			return  Arrays.asList(path.listFiles());
		else
			return null;
			
	}

	/**
	 * 获取下载文件
	 * @return 文件
	 *             异常信息
	 */
	public static File getDownloadDir() throws Exception {
		File downloadFile = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			downloadFile = new File(Constants.DOWNLOAD_PATH);
			if (!downloadFile.exists()) {
				downloadFile.mkdirs();
			}
		}
		if (downloadFile == null) {
			throw new Exception("can not make dir");
		}
		return downloadFile;
	}
	
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}
	
	public static boolean isDir(String filePath) {
		File file = new File(filePath);
		return file.exists() && file.isDirectory();
	}
	
	//获取后缀
	public static String getExspansion(String fileName){
		if(TextUtils.isEmpty(fileName))
			return null;
		int index = fileName.lastIndexOf(".");
		if(-1==index || index==(fileName.length()-1))
			return null;
		return fileName.substring(index);
	}

	public static void prepareFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void delete(String filePath) {
		if (filePath == null) {
			return;
		}
		try {
			File file = new File(filePath);
			if (file == null || !file.exists()) {
				return;
			}
			if (file.isDirectory()) {
				deleteDirRecursive(file);
			} else {
				file.delete();
			}
		} catch (Exception e) {
			Log.e(tag, e.toString());
		}
	}

	/*
	 * 递归删除目录
	 */
	public static void deleteDirRecursive(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile()) {
				f.delete();
			} else {
				deleteDirRecursive(f);
			}
		}
		dir.delete();
	}

	/**
	 * 判断SD卡是否已经准备好
	 * 
	 * @return 是否有SDCARD
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 可扩展卡路径
	 * @return
	 */
	public static String getExtSdCardPath(){
		File file = new File("/mnt/external_sd/");
		if(file.exists()){
			return file.getAbsolutePath();
		}else{
			file = new File("/mnt/extSdCard/");
			if(file.exists())
				return file.getAbsolutePath();
		}
		return null;
	}
	private static final String UNKNOWN = "unknown";

	/**
	 * http://stackoverflow.com/a/5599842/2290191
	 *
	 * @param size Original file size in byte
	 * @return Readable file size in formats
	 */
	public static String readableFileSize(long size) {
		if (size <= 0) return "0";
		final String[] units = new String[]{"b", "kb", "M", "G", "T"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static boolean isMusic(File file) {
		final String REGEX = "(.*/)*.+\\.(mp3|m4a|ogg|wav|aac)$";
		return file.getName().matches(REGEX);
	}

	public static boolean isLyric(File file) {
		return file.getName().toLowerCase().endsWith(".lrc");
	}

	public static List<Song> musicFiles(File dir) {
		List<Song> songs = new ArrayList<>();
		if (dir != null && dir.isDirectory()) {
			final File[] files = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File item) {
					return item.isFile() && isMusic(item);
				}
			});
			for (File file : files) {
				Song song = fileToMusic(file);
				if (song != null) {
					songs.add(song);
				}
			}
		}
		if (songs.size() > 1) {
			Collections.sort(songs, new Comparator<Song>() {
				@Override
				public int compare(Song left, Song right) {
					return left.getTitle().compareTo(right.getTitle());
				}
			});
		}
		return songs;
	}

	public static Song fileToMusic(File file) {
		if (file.length() == 0) return null;

		MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
		metadataRetriever.setDataSource(file.getAbsolutePath());

		final int duration;

		String keyDuration = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// ensure the duration is a digit, otherwise return null song
		if (keyDuration == null || !keyDuration.matches("\\d+")) return null;
		duration = Integer.parseInt(keyDuration);

		final String title = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
		final String displayName = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_TITLE, file.getName());
		final String artist = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ARTIST, UNKNOWN);
		final String album = extractMetadata(metadataRetriever, MediaMetadataRetriever.METADATA_KEY_ALBUM, UNKNOWN);

		final Song song = new Song();
		song.setTitle(title);
		song.setDisplayName(displayName);
		song.setArtist(artist);
		song.setPath(file.getAbsolutePath());
		song.setAlbum(album);
		song.setDuration(duration);
		song.setSize((int) file.length());
		return song;
	}

	public static Folder folderFromDir(File dir) {
		Folder folder = new Folder(dir.getName(), dir.getAbsolutePath());
		List<Song> songs = musicFiles(dir);
		folder.setSongs(songs);
		folder.setNumOfSongs(songs.size());
		return folder;
	}

	private static String extractMetadata(MediaMetadataRetriever retriever, int key, String defaultValue) {
		String value = retriever.extractMetadata(key);
		if (TextUtils.isEmpty(value)) {
			value = defaultValue;
		}
		return value;
	}
}