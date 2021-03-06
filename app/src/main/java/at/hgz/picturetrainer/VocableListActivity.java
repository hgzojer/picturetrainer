package at.hgz.picturetrainer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import at.hgz.picturetrainer.db.Dictionary;
import at.hgz.picturetrainer.db.Vocable;
import at.hgz.picturetrainer.db.VocableOpenHelper;
import at.hgz.picturetrainer.img.PictureUtil;

public class VocableListActivity extends ListActivity {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_VOCABLE = 200;
	private static final int SELECT_PICTURE = 300;
	private static final int SELECT_PICTURE_VOCABLE = 400;
	
	private State state;

	private VocableArrayAdapter adapter;

	String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vocable_list);
        
		Intent intent = getIntent();
		state = TrainingApplication.getState(intent.getIntExtra(State.STATE_ID, -1));
		
    	PictureUtil util = PictureUtil.getInstance(VocableListActivity.this);
    	Drawable drawable = util.getDrawable(state.getDictionary().getPicture());
		ImageView imageButtonDictionaryPicture = (ImageView) findViewById(R.id.imageButtonDictionaryPicture);
		imageButtonDictionaryPicture.setImageDrawable(drawable);
		EditText editTextDictionaryName = (EditText) findViewById(R.id.editTextDictionaryName);
		editTextDictionaryName.setText(state.getDictionary().getName());

		adapter = new VocableArrayAdapter(this, R.layout.vocable_list_item, state.getVocables());
		setListAdapter(adapter);
	}
	
	public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    	String text = getResources().getString(R.string.selectPicture);
        startActivityForResult(Intent.createChooser(intent, text), SELECT_PICTURE);
	}
	
	public void onClickCamera(View v) {
		/*
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    state.setImageUri(Uri.fromFile(getOutputMediaFile()));
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, state.getImageUri());
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	    */
		dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void dispatchTakePictureIntent(int requestCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = getOutputMediaFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Log.d("PictureTrainer", "Error occurred while creating the File");
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"at.hgz.picturetrainer.fileprovider",
						photoFile);
				state.setImageUri(photoURI);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, requestCode);
			}
		}
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	private File getOutputMediaFile() throws IOException {
		/*
		File mediaStorageDir;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		    mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
		              Environment.DIRECTORY_PICTURES), "PictureTrainer");
		    state.setImageSavedInternalStorage(false);
		} else {
		    mediaStorageDir = Environment.getDataDirectory();
		    state.setImageSavedInternalStorage(true);
		}


	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("PictureTrainer", "failed to create directory");
	            if (state.isImageSavedInternalStorage()) {
		            return null;
	            }
    		    mediaStorageDir = Environment.getDataDirectory();
    		    state.setImageSavedInternalStorage(true);
	        }
	    }

	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
        "IMG_"+ timeStamp + ".jpg");

	    if (state.isImageSavedInternalStorage()) {
	    	state.setImageInternalStorage(mediaFile);
	    }
	    return mediaFile;
	    */
		return createImageFile();
	}

	private File getStorageDir() {
		File mediaStorageDir;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Log.d("PictureTrainer", "external media mounted");
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES), "PictureTrainer");
			state.setImageSavedInternalStorage(false);
		} else {
			Log.d("PictureTrainer", "external media not mounted, using internal cache directory");
			mediaStorageDir = new File(getCacheDir(), Environment.DIRECTORY_PICTURES);
			state.setImageSavedInternalStorage(true);
		}


		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("PictureTrainer", "failed to create directory: " + mediaStorageDir);
				if (state.isImageSavedInternalStorage()) {
					return null;
				}

				mediaStorageDir = new File(getCacheDir(), Environment.DIRECTORY_PICTURES);
				if (!mediaStorageDir.exists()) {
					if (!mediaStorageDir.mkdirs()) {
						Log.d("PictureTrainer", "failed to create directory: " + mediaStorageDir);
						return null;
					}
				}
				state.setImageSavedInternalStorage(true);
			}
		}
		return mediaStorageDir;
		//return getExternalFilesDir(Environment.DIRECTORY_PICTURES);
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		String imageFileName = "IMG_" + timeStamp + "_";
		File storageDir = getStorageDir();
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = image.getAbsolutePath();
		if (state.isImageSavedInternalStorage()) {
			state.setImageInternalStorage(image.getAbsoluteFile());
		}
		return image;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
                PictureUtil util = PictureUtil.getInstance(this);
                if (data != null && data.getData() != null) {
                	state.setImageUri(data.getData());
                }
				byte[] picture = util.getFileSystemPicture(state.getImageUri());
	            if (state.isImageSavedInternalStorage() && state.getImageInternalStorage().exists()) {
	            	state.getImageInternalStorage().delete();
	            }
				state.getDictionary().setPicture(picture);
            	Drawable drawable = util.getDrawable(picture);
        		ImageView imageButtonDictionaryPicture = (ImageView) findViewById(R.id.imageButtonDictionaryPicture);
        		imageButtonDictionaryPicture.setImageDrawable(drawable);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        	String text = getResources().getString(R.string.errorSavingPicture);
	            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	        }
	    }
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_VOCABLE) {
	        if (resultCode == RESULT_OK) {
	            // Image captured and saved to fileUri specified in the Intent
                PictureUtil util = PictureUtil.getInstance(this);
                if (data != null && data.getData() != null) {
                	state.setImageUri(data.getData());
                }
				byte[] picture = util.getFileSystemPicture(state.getImageUri());
	            if (state.isImageSavedInternalStorage() && state.getImageInternalStorage().exists()) {
	            	state.getImageInternalStorage().delete();
	            }
	            state.getImageSaveVocable().setPicture(picture);
				adapter.notifyDataSetChanged();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        	String text = getResources().getString(R.string.errorSavingPicture);
	            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	        }
	    }
	    if (requestCode == SELECT_PICTURE) {
	        if (resultCode == RESULT_OK) {
				Uri selectedImageUri = data.getData();
                PictureUtil util = PictureUtil.getInstance(this);
				byte[] picture = util.getFileSystemPicture(selectedImageUri);
				state.getDictionary().setPicture(picture);
            	Drawable drawable = util.getDrawable(picture);
        		ImageView imageButtonDictionaryPicture = (ImageView) findViewById(R.id.imageButtonDictionaryPicture);
        		imageButtonDictionaryPicture.setImageDrawable(drawable);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        	String text = getResources().getString(R.string.errorSavingPicture);
	            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	        }
	    }
	    if (requestCode == SELECT_PICTURE_VOCABLE) {
	        if (resultCode == RESULT_OK) {
				Uri selectedImageUri = data.getData();
                PictureUtil util = PictureUtil.getInstance(this);
				byte[] picture = util.getFileSystemPicture(selectedImageUri);
				state.getImageSaveVocable().setPicture(picture);
				adapter.notifyDataSetChanged();
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        	String text = getResources().getString(R.string.errorSavingPicture);
	            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	        }
	    }
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
		String result = (state.getDictionary().getId() == -1 ? "add" : "save");
		saveState();
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", result);
		setResult(RESULT_OK,returnIntent);
		//finish();
		super.onBackPressed();
	}

	private void saveState() {
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
			public ImageButton listItemEditPicture;
			public ImageButton listItemCamera;
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
				vh.listItemEditPicture = (ImageButton) convertView.findViewById(R.id.listItemEditPicture);
				vh.listItemCamera = (ImageButton) convertView.findViewById(R.id.listItemCamera);
				vh.listItemEditWord = (EditText) convertView.findViewById(R.id.listItemEditWord);
				vh.buttonDeleteVocable = convertView.findViewById(R.id.buttonDeleteVocable);
				
				vh.listItemEditPicture.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						state.setImageSaveVocable(vh.vocable);
				        Intent intent = new Intent();
				        intent.setType("image/*");
				        intent.setAction(Intent.ACTION_GET_CONTENT);
				    	String text = getResources().getString(R.string.selectPicture);
				        startActivityForResult(Intent.createChooser(intent, text), SELECT_PICTURE_VOCABLE);
					}
				});
				
				vh.listItemCamera.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						state.setImageSaveVocable(vh.vocable);
						/*
					    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					    state.setImageUri(Uri.fromFile(getOutputMediaFile()));
					    intent.putExtra(MediaStore.EXTRA_OUTPUT, state.getImageUri());
					    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_VOCABLE);
					    */
						dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_VOCABLE);
					}
				});
				
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
