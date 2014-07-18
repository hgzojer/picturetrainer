package at.hgz.picturetrainer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TrainingActivity extends AbstractTrainingActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
    	EditText inputWord = (EditText) findViewById(R.id.editTextWordLanguage2);
    	inputWord.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                	check();
                }    
                return false;
            }
        });
    	onCreate2();
    }
    
	public void onClick(View view) {
    	check();
    }

	private void check() {
		EditText inputWord = (EditText) findViewById(R.id.editTextWordLanguage2);
    	String word = inputWord.getText().toString();
		inputWord.setText("");
    	evaluate(word);
	}

	@Override
	protected void updateDisplay() {
		TextView outputPicture = (TextView) findViewById(R.id.textViewPictureLanguage1);
		outputPicture.setText(state.getVocable().getPicture());
		TextView textLanguage1 = (TextView) findViewById(R.id.textViewLanguage1);
		textLanguage1.setText(state.getVocable().getLanguage1());
		TextView textLanguage2 = (TextView) findViewById(R.id.textViewLanguage2);
		textLanguage2.setText(state.getVocable().getLanguage2());
		
		updateDisplayStatistic();
	}

}
