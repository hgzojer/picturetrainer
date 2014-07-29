package at.hgz.picturetrainer;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import at.hgz.picturetrainer.db.Vocable;
import at.hgz.picturetrainer.img.PictureUtil;
import at.hgz.picturetrainer.set.TrainingElem;

public class TrainingActivity extends AbstractTrainingActivity {
	
	private ImageButtonArrayAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
    	EditText inputWord = (EditText) findViewById(R.id.editTextWord);
    	inputWord.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                	checkWord();
                }    
                return false;
            }
        });
    	onCreate2();
    	ListView list = (ListView) findViewById(android.R.id.list);
		adapter = new ImageButtonArrayAdapter(this, R.layout.imagebutton_list_item, state.getVocables());
		list.setAdapter(adapter);
    }
    
	public void onClick(View view) {
    	checkWord();
    }

	private void checkWord() {
		EditText inputWord = (EditText) findViewById(R.id.editTextWord);
    	String word = inputWord.getText().toString();
		inputWord.setText("");
    	evaluate(word);
	}

	private void checkPicture(Vocable vocable) {
    	evaluate(vocable.getPicture());
	}

	@Override
	protected void updateDisplay() {
    	PictureUtil util = PictureUtil.getInstance(this);
		TrainingElem vocable = state.getVocable();
		if (vocable.isFlipVocables()) {
			
			ImageView imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
			imageViewPicture.setVisibility(View.GONE);
			
			TextView editTextWord = (TextView) findViewById(R.id.editTextWord);
			editTextWord.setVisibility(View.GONE);
			
			ImageButton buttonNext = (ImageButton) findViewById(R.id.buttonNext);
			buttonNext.setVisibility(View.GONE);
			
			TextView textViewWord = (TextView) findViewById(R.id.textViewWord);
			textViewWord.setText(vocable.getWord());
			textViewWord.setVisibility(View.VISIBLE);
			
			ListView list = (ListView) findViewById(android.R.id.list);
			// TODO
			list.setVisibility(View.VISIBLE);
			
		} else {
			
			ImageView imageViewPicture = (ImageView) findViewById(R.id.imageViewPicture);
        	Drawable drawable1 = util.getDrawable(vocable.getPicture());
        	imageViewPicture.setImageDrawable(drawable1);
			imageViewPicture.setVisibility(View.VISIBLE);
			
			TextView editTextWord = (TextView) findViewById(R.id.editTextWord);
			editTextWord.setVisibility(View.VISIBLE);
			
			ImageButton buttonNext = (ImageButton) findViewById(R.id.buttonNext);
			buttonNext.setVisibility(View.VISIBLE);
			
			TextView textViewWord = (TextView) findViewById(R.id.textViewWord);
			textViewWord.setVisibility(View.GONE);
			
			ListView list = (ListView) findViewById(android.R.id.list);
			list.setVisibility(View.GONE);
		}
		
		updateDisplayStatistic();
	}

	private class ImageButtonArrayAdapter extends ArrayAdapter<Vocable> {
		
		public ImageButtonArrayAdapter(Context context, int resource,
				List<Vocable> objects) {
			super(context, resource, objects);
		}
		
		private class ViewHolder {
			public ImageButton buttonSelectPicture;
			public Vocable vocable;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final Vocable vocable = getItem(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.imagebutton_list_item, parent, false);
				final ViewHolder vh = new ViewHolder();
				vh.buttonSelectPicture = (ImageButton) convertView.findViewById(R.id.buttonSelectPicture);
				
				vh.buttonSelectPicture.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						checkPicture(vh.vocable);
					}
				});
				convertView.setTag(vh);
			}
			
			ViewHolder vh = (ViewHolder) convertView.getTag();
			vh.vocable = vocable;
        	PictureUtil util = PictureUtil.getInstance(TrainingActivity.this);
        	Drawable drawable = util.getDrawable(vocable.getPicture());
			vh.buttonSelectPicture.setImageDrawable(drawable);

			return convertView;
		}

	}
}
