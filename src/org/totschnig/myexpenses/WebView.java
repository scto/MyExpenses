/*   This file is part of My Expenses.
 *   My Expenses is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   My Expenses is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.totschnig.myexpenses;

import java.net.URL;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
//import android.util.Log;
//import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.net.Uri;
import android.os.Bundle;

/**
 * opens a WebView with the tutorial stored in the assets folder
 * @author Michael Totschnig
 *
 */
public class WebView extends Activity {
  protected FrameLayout webViewPlaceholder;
  protected android.webkit.WebView webView;
  static final int TUTORIAL_RELEASE_VERSION = 3;
  static final int CURRENT_NEWS_VERSION = 2;
  static final String HOST = "myexpenses.totschnig.org";
  private String startWith;
  private float zoomLevel;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.webview);
    setTitle(getString(R.string.app_name) + " " + getString(R.string.tutorial));
    Bundle extras = getIntent().getExtras();
    String[] supportedLangs = {"en","fr","de","it","es"};
    String lang =  Locale.getDefault().getLanguage();
    if (!java.util.Arrays.asList(supportedLangs).contains(lang)) {
      lang = "en";
    }
    switch (extras.getInt("start")) {
      case R.id.FAQ_COMMAND:
        startWith = "tutorial_r" +  TUTORIAL_RELEASE_VERSION + "/" + lang +  "/faq.html";
        setTitle(getString(R.string.app_name) + " " + getString(R.string.menu_faq));
        break;
      case R.id.CHANGES_COMMAND:
        startWith = "versionlist.html";
        setTitle(getString(R.string.app_name) + " " + getString(R.string.menu_changes));
        break;
      default:
        startWith = "tutorial_r" +  TUTORIAL_RELEASE_VERSION + "/" + lang +  "/introduction.html";
        setTitle(getString(R.string.app_name) + " " + getString(R.string.tutorial));
    }
    // Initialize the UI
    initUI();
  }
  protected void initUI()
  {
    // Retrieve UI elements
    webViewPlaceholder = ((FrameLayout)findViewById(R.id.webViewPlaceholder));

    // Initialize the WebView if necessary
    if (webView == null)
    {
      // Create the webview
      webView = new android.webkit.WebView(this);
      zoomLevel = webView.getScale();
      webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      WebSettings settings = webView.getSettings();
      settings.setSupportZoom(true);
      settings.setBuiltInZoomControls(true);
      webView.setScrollBarStyle(android.webkit.WebView.SCROLLBARS_OUTSIDE_OVERLAY);
      webView.setScrollbarFadingEnabled(true);
      settings.setLoadsImagesAutomatically(true);
      settings.setDefaultTextEncodingName("utf-8");
      //settings.setJavaScriptEnabled(true);
      /*    wv.setWebChromeClient(new WebChromeClient() {
      public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        Log.d("MyExpenses", message + " -- From line "
                             + lineNumber + " of "
                             + sourceID);
      }
    });*/
      // Load the URLs inside the WebView, not in the external web browser
      webView.setWebViewClient(new CustomWebViewClient());

      // Load a page
      webView.loadUrl("http://" + HOST + "/" + startWith);
    }

    // Attach the WebView to its placeholder
    webViewPlaceholder.addView(webView);
  }
  public class CustomWebViewClient extends WebViewClient {
    public boolean shouldOverrideUrlLoading (android.webkit.WebView view, String url) {
      //http://stackoverflow.com/questions/4907045/how-to-make-links-open-within-webview-or-open-by-default-browser-depending-on-do
      try {
        URL urlObj = new URL(url);
        if( TextUtils.equals(urlObj.getHost(),HOST) ) {
          zoomLevel = view.getScale();
          //Allow the WebView in your application to do its thing
          return false;
        } else {
          //Pass it to the system, doesn't match your domain
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(url));
          startActivity(intent);
          //Tell the WebView you took care of it.
          return true;
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return false;
    }
    public void onPageFinished (android.webkit.WebView view, String url) {
      view.setInitialScale((int) (zoomLevel *100));
    }
  }
  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    if (webView != null)
    {
      // Remove the WebView from the old placeholder
      webViewPlaceholder.removeView(webView);
    }

    super.onConfigurationChanged(newConfig);

    // Load the layout resource for the new configuration
    setContentView(R.layout.webview);

    // Reinitialize the UI
    initUI();
  }
  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    // Save the state of the WebView
    webView.saveState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);

    // Restore the state of the WebView
    webView.restoreState(savedInstanceState);
  }
}
