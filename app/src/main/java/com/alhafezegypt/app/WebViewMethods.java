package com.alhafezegypt.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.webkit.JavascriptInterface;

public class WebViewMethods {

	Context mContext;

	/** Instantiate the interface and set the context */
	WebViewMethods(Context c) {
		mContext = c;
		mainactivity = (MainActivity) c;
	}

	/** Show a toast from the web page */
	MainActivity mainactivity;

	//android
	@JavascriptInterface
	public void calltel(String tel) {

		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + tel));
		if (ActivityCompat.checkSelfPermission(mainactivity, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		mainactivity.startActivity(callIntent);

	}


	
	@JavascriptInterface
	public void refreshwebview() {

		mainactivity.wv.loadUrl(mainactivity.site);
	


	}



}
