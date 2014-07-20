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
    	EditText inputWord = (EditText) findViewById(R.id.editTextWord);
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
		EditText inputWord = (EditText) findViewById(R.id.editTextWord);
    	String word = inputWord.getText().toString();
		inputWord.setText("");
    	evaluate(word);
	}

	@Override
	protected void updateDisplay() {
		TextView outputPicture = (TextView) findViewById(R.id.textViewWord);
		//TODO outputPicture.setText(state.getVocable().getPicture());
		
		updateDisplayStatistic();
	}

}
