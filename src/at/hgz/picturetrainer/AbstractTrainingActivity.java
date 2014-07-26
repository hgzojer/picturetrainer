package at.hgz.picturetrainer;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import at.hgz.picturetrainer.img.PictureUtil;
import at.hgz.picturetrainer.set.TrainingSet;
import at.hgz.picturetrainer.snd.SoundUtil;

public abstract class AbstractTrainingActivity extends Activity {
	
	protected State state;
	
	protected AbstractTrainingActivity() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.training_menu, menu);
	    return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (TrainingApplication.getState().isPlaySound()) {
			menu.findItem(R.id.toggleSound).setIcon(R.drawable.ic_menu_sound);
		} else {
			menu.findItem(R.id.toggleSound).setIcon(R.drawable.ic_menu_mute);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.toggleSound:
			boolean playSound = !state.isPlaySound();
			state.setPlaySound(playSound);
			TrainingApplication.getState().setConfigChanged(true);
			if (TrainingApplication.getState().isPlaySound()) {
				item.setIcon(R.drawable.ic_menu_sound);
			} else {
				item.setIcon(R.drawable.ic_menu_mute);
			}
			return true;
		}
		return false;
	}

	protected void onCreate2() {
		state = TrainingApplication.getState();
        //Intent intent = getIntent();
        //state.setDictionaryId(intent.getIntExtra("dictionaryId", state.getDictionaryId()));
    	if (state.isNeedInit()) {
            loadVocable();
            state.setNeedInit(false);
    	} else {
        	updateDisplay();
    	}
	}

	protected void evaluate(String word) {
		if (word != null && !word.trim().equals("")) {
    		if (word.equalsIgnoreCase(state.getVocable().getWord())) {
    			state.incRight();
    			state.decTodo();
    			if (state.getTodo() > 0) {
    				showRightToast();
    			} else {
    				showFinishedToast();
    			}
    		} else {
            	state.incWrong();
            	state.getList().add(state.getVocable());
            	showWrongToast();
    		}
    		loadVocable();
    	}
	}

	protected void evaluate(byte[] picture) {
		if (picture != null) {
    		if (picture == state.getVocable().getPicture()) {
    			state.incRight();
    			state.decTodo();
    			if (state.getTodo() > 0) {
    				showRightToast();
    			} else {
    				showFinishedToast();
    			}
    		} else {
            	state.incWrong();
            	state.getList().add(state.getVocable());
            	showWrongToast();
    		}
    		loadVocable();
    	}
	}

	protected void showRightToast() {
		if (state.isPlaySound()) {
			SoundUtil.getInstance(this).play(R.raw.sound_right);
		}
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_right_layout,
		                               (ViewGroup) findViewById(R.id.toast_right_layout_root));

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();		
	}

	protected void showWrongToast() {
		if (state.isPlaySound()) {
			SoundUtil.getInstance(this).play(R.raw.sound_wrong);
		}
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_wrong_layout,
		                               (ViewGroup) findViewById(R.id.toast_wrong_layout_root));
		
		if (state.getVocable().isFlipVocables()) {
			TextView text = (TextView) layout.findViewById(R.id.textWrongToastDetails);
			text.setVisibility(View.GONE);
			
			TextView picture = (TextView) layout.findViewById(R.id.textWrongToastPicture);
			Resources resources = getApplicationContext().getResources();
			picture.setText(resources.getString(R.string.wrongToastDetails, ""));
		    PictureUtil util = PictureUtil.getInstance(this);
		    Drawable drawable = util.getDrawable(state.getVocable().getPicture());
		    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			picture.setCompoundDrawables(null, null, drawable, null);
			picture.setVisibility(View.VISIBLE);
		} else {
			TextView text = (TextView) layout.findViewById(R.id.textWrongToastDetails);
			Resources resources = getApplicationContext().getResources();
			text.setText(resources.getString(R.string.wrongToastDetails, state.getVocable().getWord()));
			text.setVisibility(View.VISIBLE);
			
			TextView picture = (TextView) layout.findViewById(R.id.textWrongToastPicture);
			picture.setVisibility(View.GONE);
		}

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();		
	}
    
	protected void showFinishedToast() {
		if (state.isPlaySound()) {
			SoundUtil.getInstance(this).play(R.raw.sound_finished);
		}
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_finished_layout,
		                               (ViewGroup) findViewById(R.id.toast_finished_layout_root));

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();		
	}

    public void loadVocable() {
    	if (state.getList() == null || state.getList().isEmpty()) {
    		init();
    	}
		state.setVocable(state.getList().remove(0));
    	updateDisplay();
    }

	private void init() {
        state.setList(new TrainingSet(state.getVocables(), state.getDirection()).getList());
		state.setRight(0);
		state.setWrong(0);
		state.setTodo(state.getList().size());
	}

	protected abstract void updateDisplay();

	protected void updateDisplayStatistic() {
    	ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
    	progressBar.setMax(state.getRight() + state.getTodo());
    	progressBar.setProgress(state.getRight());
    	
		TextView outputRight = (TextView) findViewById(R.id.textViewRightCount);
		outputRight.setText("" + state.getRight());
		TextView outputWrong = (TextView) findViewById(R.id.textViewWrongCount);
		outputWrong.setText("" + state.getWrong());
		TextView outputTodo = (TextView) findViewById(R.id.textViewTodoCount);
		outputTodo.setText("" + state.getTodo());
	}
}
