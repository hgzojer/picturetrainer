package at.hgz.picturetrainer;

import android.app.Application;

public class TrainingApplication extends Application {
	
	private static State state;

	public static State getState() {
		return state;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		state = new State();
	}
}