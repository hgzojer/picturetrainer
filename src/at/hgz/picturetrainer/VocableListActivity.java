package at.hgz.picturetrainer;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import at.hgz.picturetrainer.db.Dictionary;
import at.hgz.picturetrainer.db.Vocable;
import at.hgz.picturetrainer.db.VocableOpenHelper;
import at.hgz.picturetrainer.img.PictureUtil;

public class VocableListActivity extends ListActivity {
	
	private VocableArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocable_list);

		State state = TrainingApplication.getState();
		
    	PictureUtil util = PictureUtil.getInstance(VocableListActivity.this);
    	Drawable drawable = util.getDrawable(state.getDictionary().getPicture());
		ImageView imageViewDictionaryPicture = (ImageView) findViewById(R.id.imageViewDictionaryPicture);
		imageViewDictionaryPicture.setImageDrawable(drawable);
		EditText editTextDictionaryName = (EditText) findViewById(R.id.editTextDictionaryName);
		editTextDictionaryName.setText(state.getDictionary().getName());

		adapter = new VocableArrayAdapter(this, R.layout.vocable_list_item, state.getVocables());
		setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.vocable_list_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.addVocable:
	        	PictureUtil util = PictureUtil.getInstance(this);
	        	byte[] image = util.getDefaultPicture();
	            adapter.add(new Vocable(-1, -1, image, ""));
	            setSelection(adapter.getCount() - 1);
	            return true;
	        case R.id.deleteDictionary:
	            deleteDictionary();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void deleteDictionary() {

		Resources resources = getApplicationContext().getResources();
		String confirmDeleteDictionaryTitle = resources.getString(R.string.confirmDeleteDictionaryTitle);
		String confirmDeleteDictionaryText = resources.getString(R.string.confirmDeleteDictionaryText);
		
		new AlertDialog.Builder(this)
		.setTitle(confirmDeleteDictionaryTitle)
		.setMessage(confirmDeleteDictionaryText)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
				deleteState();
				Intent returnIntent = new Intent();
				returnIntent.putExtra("result", "delete");
				setResult(RESULT_OK,returnIntent);
				finish();
		    }})
		.setNegativeButton(android.R.string.no, null).show();
	}

	@Override
	public void onBackPressed() {
		saveState();
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", "save");
		setResult(RESULT_OK,returnIntent);
		//finish();
		super.onBackPressed();
	}

	private void saveState() {
		State state = TrainingApplication.getState();
		Dictionary dictionary = state.getDictionary();
		
		EditText editTextDictionaryName = (EditText) findViewById(R.id.editTextDictionaryName);
		dictionary.setName(editTextDictionaryName.getText().toString());
		
		List<Vocable> vocables = state.getVocables();
		/*vocables.clear();
		for (int i = 0; i < adapter.getCount(); i++) {
			vocables.add(adapter.getItem(i));
		}*/
		
		Resources resources = getApplicationContext().getResources();
		String text = resources.getString(R.string.savingDictionary);
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
		
		VocableOpenHelper helper = VocableOpenHelper.getInstance(getApplicationContext());
		helper.persist(dictionary, vocables);
	}

	private void deleteState() {
		State state = TrainingApplication.getState();
		Dictionary dictionary = state.getDictionary();
		List<Vocable> vocables = state.getVocables();
		
		Resources resources = getApplicationContext().getResources();
		String text = resources.getString(R.string.deletingDictionary);
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
		
		VocableOpenHelper helper = VocableOpenHelper.getInstance(getApplicationContext());
		helper.remove(dictionary, vocables);
	}

	private class VocableArrayAdapter extends ArrayAdapter<Vocable> {
		
		public VocableArrayAdapter(Context context, int resource,
				List<Vocable> objects) {
			super(context, resource, objects);
		}
		
		private class ViewHolder {
			public ImageView listItemEditPicture;
			public EditText listItemEditWord;
			public View buttonDeleteVocable;
			public Vocable vocable;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final Vocable vocable = getItem(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.vocable_list_item, parent, false);
				final ViewHolder vh = new ViewHolder();
				vh.listItemEditPicture = (ImageView) convertView.findViewById(R.id.listItemEditPicture);
				vh.listItemEditWord = (EditText) convertView.findViewById(R.id.listItemEditWord);
				vh.buttonDeleteVocable = convertView.findViewById(R.id.buttonDeleteVocable);
				
				//TODO vh.listItemEditPicture.add(...);
				
				vh.listItemEditWord.addTextChangedListener(new TextWatcher() {
					@Override
					public void afterTextChanged(Editable arg0) {
						String word = vh.listItemEditWord.getText().toString();
						vh.vocable.setWord(word);
					}
					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					}
					@Override
					public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					}
				});
				
				vh.buttonDeleteVocable.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						VocableArrayAdapter.this.remove(vh.vocable);
					}
				});
				convertView.setTag(vh);
			}
			
			ViewHolder vh = (ViewHolder) convertView.getTag();
			vh.vocable = vocable;
        	PictureUtil util = PictureUtil.getInstance(VocableListActivity.this);
        	Drawable drawable = util.getDrawable(vocable.getPicture());
			vh.listItemEditPicture.setImageDrawable(drawable);
			vh.listItemEditWord.setText(vocable.getWord());

			return convertView;
		}

	}
}
