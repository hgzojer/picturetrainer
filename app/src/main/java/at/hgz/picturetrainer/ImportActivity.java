package at.hgz.picturetrainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.hgz.picturetrainer.db.Dictionary;
import at.hgz.picturetrainer.img.PictureUtil;
import at.hgz.picturetrainer.zip.ZipUtil;

public class ImportActivity extends ListActivity {

	private static class FileRow {
		public File file;
		public byte[] picture;
		public String dictionary;
	}
	
	private State state;

	private List<FileRow> list = new ArrayList<FileRow>();
	private FileArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import);

		Intent intent = getIntent();
		state = TrainingApplication.getState(intent.getIntExtra(State.STATE_ID, -1));

		if (state.getCurrentDirectory() == null) {
			File dir = getSDCardDir(this);
			state.setCurrentDirectory(dir);
		}
		TextView currentPath = (TextView) findViewById(R.id.currentPath);
		currentPath.setText("" + state.getCurrentDirectory());
		loadFiles();
		adapter = new FileArrayAdapter(this, R.layout.import_item, list);
		setListAdapter(adapter);
	}

	public static File getSDCardDir(Context context) {
		// return context.getExternalFilesDir(null);
		File[] dirs = context.getExternalFilesDirs(null);
		return dirs[dirs.length - 1];
	}

	private void loadFiles() {
		File dir = state.getCurrentDirectory();
		list.clear();
		File[] files = dir.listFiles(new FilenameFilter() {
			private Pattern p = Pattern.compile("^.*\\.pt$");
			@Override
			public boolean accept(File dir, String filename) {
				return p.matcher(filename.toLowerCase(Locale.US)).matches();
			}

		});
		if (files == null) {
			files = new File[0];
		}
	    PictureUtil util = PictureUtil.getInstance(ImportActivity.this);
		for (File file : files) {
			FileRow fileRow = new FileRow();
			fileRow.file = file;
			try {
				InputStream in = new FileInputStream(file);
				byte[] dictionaryBytes = IOUtils.toByteArray(in);
				Dictionary dictionary = ZipUtil.getInstance().unmarshall(dictionaryBytes).getDictionary();
				fileRow.dictionary = dictionary.getName();
				fileRow.picture = dictionary.getPicture();
			} catch (Exception ex) {
				fileRow.dictionary = "(X_X)";
				fileRow.picture = util.getResourcePicture("error_picture");
			}
			list.add(fileRow);
		}
	}

	private void deleteFile(final FileRow fileRow) {

		Resources resources = getApplicationContext().getResources();
		String confirmDeleteDictionaryTitle = resources.getString(R.string.confirmDeleteDictionaryTitle);
		String confirmDeleteDictionaryText = resources.getString(R.string.confirmDeleteDictionaryText);
		
		new AlertDialog.Builder(this)
		.setTitle(confirmDeleteDictionaryTitle)
		.setMessage(confirmDeleteDictionaryText)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
				if (fileRow.file.delete()) {
					Resources resources = getApplicationContext().getResources();
					String text = resources.getString(R.string.deletingDictionary);
					Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
					toast.show();
					adapter.remove(fileRow);
				}
		    }})
		.setNegativeButton(android.R.string.no, null).show();
	}
	
	private class FileArrayAdapter extends ArrayAdapter<FileRow> {

		public FileArrayAdapter(Context context, int resource,
				List<FileRow> objects) {
			super(context, resource, objects);
		}

		private class ViewHolder {
			public ImageButton buttonDelete;
			public View listItem;
			public TextView listItemName;
			public ImageView listItemPicture;
			public TextView listItemDictionary;
			public FileRow fileRow;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			FileRow fileRow = getItem(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.import_item, parent, false);
				final ViewHolder vh = new ViewHolder();
				vh.buttonDelete = (ImageButton) convertView.findViewById(R.id.buttonDelete);
				vh.listItem = (View) convertView.findViewById(R.id.listItem);
				vh.listItemName = (TextView) convertView.findViewById(R.id.listItemName);
				vh.listItemPicture = (ImageView) convertView.findViewById(R.id.listItemPicture);
				vh.listItemDictionary = (TextView) convertView.findViewById(R.id.listItemDictionary);
				
				vh.buttonDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteFile(vh.fileRow);
					}
				});
				OnClickListener selectFileListener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent resultIntent = new Intent();
						resultIntent.setData(Uri.fromFile(vh.fileRow.file));
						setResult(Activity.RESULT_OK, resultIntent);
						ImportActivity.this.finish();
					}
				};
				vh.listItem.setOnClickListener(selectFileListener);
				vh.listItemName.setOnClickListener(selectFileListener);
				vh.listItemPicture.setOnClickListener(selectFileListener);
				vh.listItemDictionary.setOnClickListener(selectFileListener);
				convertView.setTag(vh);
			}

			ViewHolder vh = (ViewHolder) convertView.getTag();
			vh.fileRow = fileRow;
			vh.listItemName.setText(fileRow.file.getName());
		    PictureUtil util = PictureUtil.getInstance(ImportActivity.this);
		    Drawable drawable = util.getDrawable(fileRow.picture);
			vh.listItemPicture.setImageDrawable(drawable);
			vh.listItemDictionary.setText(String.format("↔ %s", fileRow.dictionary));

			return convertView;
		}

	}
}
