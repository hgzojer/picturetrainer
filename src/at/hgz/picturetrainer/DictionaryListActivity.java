package at.hgz.picturetrainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.hgz.picturetrainer.db.Dictionary;
import at.hgz.picturetrainer.db.Vocable;
import at.hgz.picturetrainer.db.VocableOpenHelper;
import at.hgz.picturetrainer.img.PictureUtil;
import at.hgz.picturetrainer.set.TrainingSet;
import at.hgz.picturetrainer.zip.ZipUtil;
import at.hgz.picturetrainer.zip.ZipUtil.Entity;

public class DictionaryListActivity extends ListActivity {

	private static final int EDIT_ACTION = 1;
	private static final int CONFIG_ACTION = 2;
	private static final int IMPORT_ACTION = 3;
	private List<Dictionary> list = new ArrayList<Dictionary>();
	
	private DictionaryArrayAdapter adapter;
	
	private String directionSymbol = "↔";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary_list);

        loadConfig();

		loadDictionaryList();
	}

	@Override
	protected void onDestroy() {
        saveConfig();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
        saveConfig();
		super.onPause();
	}

	@Override
	protected void onStop() {
        saveConfig();
		super.onStop();
	}

	private void loadConfig() {
		SharedPreferences settings = DictionaryListActivity.this.getPreferences(MODE_PRIVATE);
        int direction = settings.getInt(ConfigActivity.WORD_DIRECTION, TrainingSet.DIRECTION_BIDIRECTIONAL);
        TrainingApplication.getState().setDirection(direction);
        boolean playSound = settings.getBoolean(ConfigActivity.PLAY_SOUND, true);
        TrainingApplication.getState().setPlaySound(playSound);
	}
	
	private void saveConfig() {
		if (TrainingApplication.getState().hasConfigChanged()) {
			SharedPreferences settings = this.getPreferences(MODE_PRIVATE);
	        SharedPreferences.Editor editor = settings.edit();
			editor.putInt(ConfigActivity.WORD_DIRECTION, TrainingApplication.getState().getDirection());
			editor.putBoolean(ConfigActivity.PLAY_SOUND, TrainingApplication.getState().isPlaySound());
			editor.commit();
			TrainingApplication.getState().setConfigChanged(false);
		}
	}

	private boolean isDictionarySelected() {
		return TrainingApplication.getState().getDictionary() != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dictionary_list_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem exportToExternalStorage = menu.findItem(R.id.exportToExternalStorage);
		exportToExternalStorage.setVisible(isDictionarySelected());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.addDictionary:
	        {
	        	PictureUtil util = PictureUtil.getInstance(DictionaryListActivity.this);
	        	byte[] image = util.getDefaultPicture();
	        	TrainingApplication.getState().setDictionary(new Dictionary(-1, image, ""));
	        	List<Vocable> vocables = new ArrayList<Vocable>(5);
	        	vocables.add(new Vocable(-1, -1, image, ""));
	        	vocables.add(new Vocable(-1, -1, image, ""));
	        	vocables.add(new Vocable(-1, -1, image, ""));
	        	vocables.add(new Vocable(-1, -1, image, ""));
	        	vocables.add(new Vocable(-1, -1, image, ""));
	        	TrainingApplication.getState().setVocables(vocables);
				Intent intent = new Intent(DictionaryListActivity.this, VocableListActivity.class);
				//intent.putExtra("dictionaryId", dictionaryId);
				DictionaryListActivity.this.startActivityForResult(intent, EDIT_ACTION);
	            return true;
	        }
	        case R.id.openConfig:
	        {
				Intent intent = new Intent(DictionaryListActivity.this, ConfigActivity.class);
				//intent.putExtra("dictionaryId", dictionaryId);
				DictionaryListActivity.this.startActivityForResult(intent, CONFIG_ACTION);
	            return true;
	        }
	        case R.id.exportToExternalStorage:
	        {
				exportDictionaryToExternalStorage();
	        	return true;
	        }
	        case R.id.importFromExternalStorage:
	        {
				Intent intent = new Intent(DictionaryListActivity.this, ImportActivity.class);
				DictionaryListActivity.this.startActivityForResult(intent, IMPORT_ACTION);
	        	return true;
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void exportDictionaryToExternalStorage() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			ZipUtil util = ZipUtil.getInstance();
			Dictionary dictionary = TrainingApplication.getState().getDictionary();
			List<Vocable> vocables = TrainingApplication.getState().getVocables();
			byte[] dictionaryBytes = util.marshall(dictionary, vocables);
			File storageDir = getExternalFilesDir(null);
			if (!storageDir.exists()) {
				if (!storageDir.mkdirs()) {
					Log.d("DictionaryListActivity", "failed to create directory");
				}
			}
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		    File file;
		    int i = 1;
		    do {
		    	file = new File(storageDir, "DICT_"+ timeStamp + (i > 1 ? "_" + i : "") + ".pt");
		    	i++;
		    } while (file.exists());
		    try {
			    OutputStream out = new FileOutputStream(file);
			    try {
			    	out.write(dictionaryBytes);
			    	out.flush();
			    } catch (IOException ex) {
			    	
			    } finally {
			    	if (out != null) {
			    		out.close();
			    	}
			    }
		    } catch (IOException ex) {
		    	throw new RuntimeException(ex.getMessage(), ex);
		    }
			String text = getResources().getString(R.string.exportedDictionary, file.getName());
		    Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		} else {
			String text = getResources().getString(R.string.errorExportingDictionary);
		    Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}
	}

	private void loadDictionaryList() {
		int direction = TrainingApplication.getState().getDirection();
		switch (direction) {
		case TrainingSet.DIRECTION_FORWARD:
			directionSymbol = "→";
			break;
		case TrainingSet.DIRECTION_BIDIRECTIONAL:
			directionSymbol = "↔";
			break;
		case TrainingSet.DIRECTION_BACKWARD:
			directionSymbol = "←";
			break;
		}

		VocableOpenHelper helper = VocableOpenHelper.getInstance(getApplicationContext());
		list.clear();
		for (Dictionary lib : helper.getDictionaries()) {
			list.add(lib);
		}

		adapter = new DictionaryArrayAdapter(this, R.layout.dictionary_list_item, list);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		loadDictionaryVocables(position);
		setSelection(position);
		adapter.notifyDataSetChanged();
	}

	private void loadDictionaryVocables(final int position) {
		VocableOpenHelper helper = VocableOpenHelper.getInstance(DictionaryListActivity.this);
		Dictionary dictionary = list.get(position);
		List<Vocable> vocables = helper.getVocables(dictionary.getId());
		TrainingApplication.getState().setDictionary(dictionary);
		TrainingApplication.getState().setVocables(vocables);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == EDIT_ACTION) {
			String result = "";
			if (resultCode == RESULT_OK) {
				result = data.getStringExtra("result");
				if ("save".equals(result)) {
					adapter.notifyDataSetChanged();
				}
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}
		
		if (requestCode == CONFIG_ACTION) {
			adapter.notifyDataSetChanged();
		}
		
		if (requestCode == IMPORT_ACTION) {
			if (resultCode == RESULT_OK) {
				importDictionaryFromExternalStorage(data.getData());
				loadDictionaryVocables(list.size() - 1);
				setSelection(list.size() - 1);
				adapter.notifyDataSetChanged();
			}
			if (resultCode == RESULT_CANCELED) {
			}
		}
	}

	private void importDictionaryFromExternalStorage(Uri importFile) {
		try {
			InputStream in = getContentResolver().openInputStream(importFile);
			byte[] dictionaryBytes = IOUtils.toByteArray(in);
			ZipUtil util = ZipUtil.getInstance();
			Entity entity = util.unmarshall(dictionaryBytes);
			Resources resources = getApplicationContext().getResources();
			String text = resources.getString(R.string.importingDictionary);
			Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
			toast.show();
			VocableOpenHelper helper = VocableOpenHelper.getInstance(getApplicationContext());
			helper.persist(entity.getDictionary(), entity.getVocables());
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private class DictionaryArrayAdapter extends ArrayAdapter<Dictionary> {

		public DictionaryArrayAdapter(Context context, int resource,
				List<Dictionary> objects) {
			super(context, resource, objects);
		}
		
		private class ViewHolder {
			public ImageView listItemPicture;
			public TextView listItemName;
			public Dictionary dictionary;
			public TextView listItemCount;
			public Button buttonEdit;
			public Button buttonTraining;
			public Button buttonMultipleChoice;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Dictionary dictionary = getItem(position);
			PictureUtil util = PictureUtil
					.getInstance(DictionaryListActivity.this);

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.dictionary_list_item, parent, false);
				final ViewHolder vh = new ViewHolder();
				vh.listItemPicture = (ImageView) convertView.findViewById(R.id.listItemPicture);
				vh.listItemName = (TextView) convertView.findViewById(R.id.listItemName);
				vh.listItemCount = (TextView) convertView.findViewById(R.id.listItemCount);
				vh.buttonEdit = (Button) convertView.findViewById(R.id.buttonEdit);
				vh.buttonTraining = (Button) convertView.findViewById(R.id.buttonTraining);
				vh.buttonMultipleChoice = (Button) convertView.findViewById(R.id.buttonMultipleChoice);
				vh.buttonEdit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(DictionaryListActivity.this, VocableListActivity.class);
						//intent.putExtra("dictionaryId", dictionaryId);
						DictionaryListActivity.this.startActivityForResult(intent, EDIT_ACTION);
					}
				});
				vh.buttonTraining.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(DictionaryListActivity.this, TrainingActivity.class);
						//intent.putExtra("dictionaryId", dictionaryId);
						DictionaryListActivity.this.startActivity(intent);
					}
				});
				vh.buttonMultipleChoice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(DictionaryListActivity.this, MultipleChoiceActivity.class);
						//intent.putExtra("dictionaryId", dictionaryId);
						DictionaryListActivity.this.startActivity(intent);
					}
				});

				convertView.setTag(vh);
			}

			ViewHolder vh = (ViewHolder) convertView.getTag();
			vh.dictionary = dictionary;
			Drawable drawable = util.getDrawable(dictionary.getPicture());
			vh.listItemPicture.setImageDrawable(drawable);
			vh.listItemName.setText(String.format(" %s %s", directionSymbol, dictionary.getName()));
			int visibility = View.GONE;
			int visibilityTraining = View.GONE;
			if (vh.dictionary == TrainingApplication.getState().getDictionary()) {
				visibility = View.VISIBLE;
				int count = TrainingApplication.getState().getVocables().size();
				Resources resources = getApplicationContext().getResources();
				vh.listItemCount.setText(resources.getString(R.string.count, count));
				if (count > 0) {
					visibilityTraining = View.VISIBLE;
				}
				vh.listItemCount.setText("");
			}
			vh.listItemCount.setVisibility(visibility);
			vh.buttonEdit.setVisibility(visibility);
			vh.buttonTraining.setVisibility(visibilityTraining);
			vh.buttonMultipleChoice.setVisibility(visibilityTraining);

			return convertView;
		}

	}
}
