package at.hgz.picturetrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LicenseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);

		Intent intent = getIntent();
		String moduleName = intent.getStringExtra("moduleName");
		String licenseText = intent.getStringExtra("licenseText");
		
		TextView textViewName = (TextView) findViewById(R.id.textViewName);
		TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		
		textViewName.setText(moduleName);
		textViewDescription.setText(licenseText);
	}

}
