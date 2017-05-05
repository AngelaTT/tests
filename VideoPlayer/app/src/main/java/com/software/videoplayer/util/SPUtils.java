package com.software.videoplayer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * SharedPrefrences的工具类
 * @author Administrator
 *
 */
public class SPUtils {

	/**
	 * 万能的put方法
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void put(Context context, String key, Object value) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		//instanceof 用于判断数据类型的
		if (value instanceof String) {
			edit.putString(key, (String) value);
		} else if (value instanceof Integer) {
			edit.putInt(key, (int) value);
		} else if (value instanceof Boolean) {
			edit.putBoolean(key, (boolean) value);
		}
		edit.commit();
	}
	
	/**
	 * 获取字符串
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}
	/**
	 * 获取整数
	 * @param context
	 * @param key
	 * @return
	 */
	public static int getInt(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getInt(key, 0);
	}
	/**
	 * 获取Boolean
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getBoolean(key, false);
	}

	public static String SceneList2String(ArrayList SceneList)
			throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(SceneList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String SceneListString = new String(Base64.encode(
				byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return SceneListString;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList String2SceneList(String SceneListString)
			throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
				Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				mobileBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		ArrayList SceneList = (ArrayList) objectInputStream
				.readObject();
		objectInputStream.close();
		return SceneList;
	}
}
