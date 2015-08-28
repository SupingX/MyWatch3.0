package com.mycj.mywatch.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class MyFileUtil {
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "laputa");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == 1) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "json_" + timeStamp + ".json");
		} else if (type == 2) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "xml_" + timeStamp + ".xml");
		} else {
			return null;
		}
		return mediaFile;
	}
}
