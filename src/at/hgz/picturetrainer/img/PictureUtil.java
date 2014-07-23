package at.hgz.picturetrainer.img;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public final class PictureUtil {
	
	private static PictureUtil instance;

	private Context context;
	
	private PictureUtil(Context context) {
		this.context = context;
	}
	
	public static PictureUtil getInstance(Context context) {
		if (instance == null) {
			instance = new PictureUtil(context);
		}
		return instance;
	}
	
	public byte[] getDefaultPicture() {
		return getResourcePicture("default_picture");
	}
	
	public byte[] getResourcePicture(String filename) {
		try {
			Resources res = context.getResources();
			int id = res.getIdentifier(filename, "raw", context.getPackageName());
			InputStream in = res.openRawResource(id);
			byte[] picture = IOUtils.toByteArray(in);
			return picture;
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage(), e);
    	}
	}
	
	public File getGalleryFile(Uri uri) {
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);
		if (c == null) {
			return null;
		}
		c.moveToNext();
		String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
		c.close();
		return new File(path);
	}
	
	public File getCameraFile(Uri uri) {
		return new File(uri.getPath());
	}
	
	public byte[] getFileSystemPicture(File file) {
		try {
			InputStream in = new FileInputStream(file);
			byte[] picture = IOUtils.toByteArray(in);
			return compress(picture);
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage(), e);
    	}
	}
	
	public Drawable getDrawable(byte[] picture) {
		Resources res = context.getResources();
		return new BitmapDrawable(res, BitmapFactory.decodeByteArray(picture,
				0, picture.length));
	}
	
	public byte[] compress(byte[] picture) {
		try {
			Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			if (width <= 340 && height <= 200) {
				return picture;
			}
			float scaleX = 340.0f / width;
			float scaleY = 200.0f / height;
			if (scaleX < scaleY) {
				scaleY = scaleX;
			} else {
				scaleX = scaleY;
			}
			int newWidth = (int) (width * scaleX);
			int newHeight = (int) (height * scaleY);
			scaleX = ((float) newWidth) / width;
			scaleY = ((float) newHeight) / height;
			Matrix m = new Matrix();
			m.postScale(scaleX, scaleY);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			resizedBitmap.compress(CompressFormat.JPEG, 80, buf);
			byte[] compressed = buf.toByteArray();
			Log.e("compress", "size=" + compressed.length);
			return compressed;
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage(), e);
    	}
	}
	
}
