package at.hgz.picturetrainer.img;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public final class ImageUtil {
	
	private static ImageUtil instance;

	private Context context;
	
	private ImageUtil(Context context) {
		this.context = context;
	}
	
	public static ImageUtil getInstance(Context context) {
		if (instance == null) {
			instance = new ImageUtil(context);
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
	
	public Drawable getDrawable(byte[] picture) {
		Resources res = context.getResources();
		return new BitmapDrawable(res, BitmapFactory.decodeByteArray(picture,
				0, picture.length));
	}
	
}
