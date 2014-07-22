package at.hgz.picturetrainer.snd;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public final class SoundUtil {

	private static SoundUtil instance;

	private Context context;

	private SoundUtil(Context context) {
		this.context = context;
	}

	public static SoundUtil getInstance(Context context) {
		if (instance == null) {
			instance = new SoundUtil(context);
		}
		return instance;
	}

	public void play(int soundId) {
		MediaPlayer mp = MediaPlayer.create(context, soundId);
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});
		mp.start();
	}

}
