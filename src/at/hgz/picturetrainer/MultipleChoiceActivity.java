package at.hgz.picturetrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import at.hgz.picturetrainer.img.PictureUtil;
import at.hgz.picturetrainer.set.TrainingElem;
import at.hgz.picturetrainer.snd.SoundUtil;

public class MultipleChoiceActivity extends AbstractTrainingActivity {
	
	private Drawable defaultDrawableRight;
	
	private Drawable defaultDrawableRightCount;
	
	private Drawable defaultDrawableWrong;
	
	private Drawable defaultDrawableWrongCount;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_choice);

		TextView textViewRight = (TextView) findViewById(R.id.textViewRight);
		defaultDrawableRight = textViewRight.getBackground();
		TextView textViewRightCount = (TextView) findViewById(R.id.textViewRightCount);
		defaultDrawableRightCount = textViewRightCount.getBackground();
		TextView textViewWrong = (TextView) findViewById(R.id.textViewWrong);
		defaultDrawableWrong = textViewWrong.getBackground();
		TextView textViewWrongCount = (TextView) findViewById(R.id.textViewWrongCount);
		defaultDrawableWrongCount = textViewWrongCount.getBackground();
		
    	onCreate2();
    }
    
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.buttonChoice1:
		case R.id.buttonChoice2:
		case R.id.buttonChoice3:
			String word = ((Button) view).getText().toString();
	    	evaluate(word);
	    	break;
		case R.id.imageButtonChoice1:
		case R.id.imageButtonChoice2:
		case R.id.imageButtonChoice3:
			byte[] picture = (byte[]) ((ImageButton) view).getTag();
			evaluate(picture);
	    	break;
		}
    }


	@Override
	protected void updateDisplay() {
		TrainingElem vocable = state.getVocable();
		TrainingElem[] alternatives = vocable.getAlternatives();
		List<TrainingElem> choices = new ArrayList<TrainingElem>(3);
		choices.add(vocable);
		choices.add(alternatives[0]);
		choices.add(alternatives[1]);
		Collections.shuffle(choices);
		
    	PictureUtil util = PictureUtil.getInstance(this);
		if (vocable.isFlipVocables()) {
			ImageView outputPicture = (ImageView) findViewById(R.id.imageViewPicture);
			outputPicture.setVisibility(View.GONE);

			Button buttonChoice1 = (Button) findViewById(R.id.buttonChoice1);
			buttonChoice1.setVisibility(View.GONE);
			
			Button buttonChoice2 = (Button) findViewById(R.id.buttonChoice2);
			buttonChoice2.setVisibility(View.GONE);
			
			Button buttonChoice3 = (Button) findViewById(R.id.buttonChoice3);
			buttonChoice3.setVisibility(View.GONE);
			
			TextView outputWord = (TextView) findViewById(R.id.textViewWord);
			outputWord.setText(vocable.getWord());
			outputWord.setVisibility(View.VISIBLE);
			
			ImageButton imageButtonChoice1 = (ImageButton) findViewById(R.id.imageButtonChoice1);
        	Drawable drawable1 = util.getDrawable(choices.get(0).getPicture());
        	imageButtonChoice1.setTag(choices.get(0).getPicture());
        	imageButtonChoice1.setImageDrawable(drawable1);
        	imageButtonChoice1.setVisibility(View.VISIBLE);
			
			ImageButton imageButtonChoice2 = (ImageButton) findViewById(R.id.imageButtonChoice2);
        	Drawable drawable2 = util.getDrawable(choices.get(1).getPicture());
        	imageButtonChoice2.setTag(choices.get(1).getPicture());
        	imageButtonChoice2.setImageDrawable(drawable2);
        	imageButtonChoice2.setVisibility(View.VISIBLE);
			
			ImageButton imageButtonChoice3 = (ImageButton) findViewById(R.id.imageButtonChoice3);
        	Drawable drawable3 = util.getDrawable(choices.get(2).getPicture());
        	imageButtonChoice3.setTag(choices.get(2).getPicture());
        	imageButtonChoice3.setImageDrawable(drawable3);
        	imageButtonChoice3.setVisibility(View.VISIBLE);
			
		} else {
			ImageView outputPicture = (ImageView) findViewById(R.id.imageViewPicture);
        	Drawable drawable = util.getDrawable(vocable.getPicture());
			outputPicture.setImageDrawable(drawable);
			outputPicture.setVisibility(View.VISIBLE);

			Button buttonChoice1 = (Button) findViewById(R.id.buttonChoice1);
			buttonChoice1.setText(choices.get(0).getWord());
			buttonChoice1.setVisibility(View.VISIBLE);
			
			Button buttonChoice2 = (Button) findViewById(R.id.buttonChoice2);
			buttonChoice2.setText(choices.get(1).getWord());
			buttonChoice2.setVisibility(View.VISIBLE);
			
			Button buttonChoice3 = (Button) findViewById(R.id.buttonChoice3);
			buttonChoice3.setText(choices.get(2).getWord());
			buttonChoice3.setVisibility(View.VISIBLE);
			
			TextView outputWord = (TextView) findViewById(R.id.textViewWord);
			outputWord.setVisibility(View.GONE);
			
			ImageButton imageButtonChoice1 = (ImageButton) findViewById(R.id.imageButtonChoice1);
			imageButtonChoice1.setVisibility(View.GONE);
			
			ImageButton imageButtonChoice2 = (ImageButton) findViewById(R.id.imageButtonChoice2);
			imageButtonChoice2.setVisibility(View.GONE);
			
			ImageButton imageButtonChoice3 = (ImageButton) findViewById(R.id.imageButtonChoice3);
			imageButtonChoice3.setVisibility(View.GONE);
			
		}
		
		updateDisplayStatistic();
	}

	@Override
	protected void showRightToast() {
		if (state.isPlaySound()) {
			SoundUtil.getInstance(this).play(R.raw.sound_right);
		}
		TextView textViewStatistic = (TextView) findViewById(R.id.textViewStatistic);
		textViewStatistic.setBackgroundColor(0x8000FF00);
		TextView textViewRight = (TextView) findViewById(R.id.textViewRight);
		textViewRight.setBackgroundColor(0x8000FF00);
		TextView textViewRightCount = (TextView) findViewById(R.id.textViewRightCount);
		textViewRightCount.setBackgroundColor(0x8000FF00);
		TextView textViewWrong = (TextView) findViewById(R.id.textViewWrong);
		textViewWrong.setBackgroundDrawable(defaultDrawableWrong);
		TextView textViewWrongCount = (TextView) findViewById(R.id.textViewWrongCount);
		textViewWrongCount.setBackgroundDrawable(defaultDrawableWrongCount);
	}

	@Override
	protected void showWrongToast() {
		if (state.isPlaySound()) {
			SoundUtil.getInstance(this).play(R.raw.sound_wrong);
		}
		TextView textViewStatistic = (TextView) findViewById(R.id.textViewStatistic);
		textViewStatistic.setBackgroundColor(0x80FF0000);
		TextView textViewRight = (TextView) findViewById(R.id.textViewRight);
		textViewRight.setBackgroundDrawable(defaultDrawableRight);
		TextView textViewRightCount = (TextView) findViewById(R.id.textViewRightCount);
		textViewRightCount.setBackgroundDrawable(defaultDrawableRightCount);
		TextView textViewWrong = (TextView) findViewById(R.id.textViewWrong);
		textViewWrong.setBackgroundColor(0x80FF0000);
		TextView textViewWrongCount = (TextView) findViewById(R.id.textViewWrongCount);
		textViewWrongCount.setBackgroundColor(0x80FF0000);
	}

	@Override
	protected void showFinishedToast() {
		TextView textViewStatistic = (TextView) findViewById(R.id.textViewStatistic);
		textViewStatistic.setBackgroundColor(0x800000FF);
		TextView textViewRight = (TextView) findViewById(R.id.textViewRight);
		textViewRight.setBackgroundDrawable(defaultDrawableRight);
		TextView textViewRightCount = (TextView) findViewById(R.id.textViewRightCount);
		textViewRightCount.setBackgroundDrawable(defaultDrawableRightCount);
		TextView textViewWrong = (TextView) findViewById(R.id.textViewWrong);
		textViewWrong.setBackgroundDrawable(defaultDrawableWrong);
		TextView textViewWrongCount = (TextView) findViewById(R.id.textViewWrongCount);
		textViewWrongCount.setBackgroundDrawable(defaultDrawableWrongCount);
		
		super.showFinishedToast();
	}

}
