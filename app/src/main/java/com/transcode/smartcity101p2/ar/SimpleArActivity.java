package com.transcode.smartcity101p2.ar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.transcode.smartcity101p2.GifImageActivity;
import com.transcode.smartcity101p2.R;
import com.transcode.smartcity101p2.TravelPlaceDetailActivity;
import com.transcode.smartcity101p2.VideoPlayerActivity;
import com.transcode.smartcity101p2.api.ApiRequest;
import com.transcode.smartcity101p2.model.CommonResponse;
import com.transcode.smartcity101p2.model.Const;
import com.transcode.smartcity101p2.model.LoginResponse;
import com.transcode.smartcity101p2.model.SCannerRequest;
import com.transcode.smartcity101p2.view.CustomAppBarLayout2;
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Activity is (almost) the least amount of code required to use the
 * basic functionality for Image-/Instant- and Object Tracking.
 * <p>
 * This Activity needs Manifest.permission.CAMERA permissions because the
 * ArchitectView will try to start the camera.
 */
public class SimpleArActivity extends AppCompatActivity implements ArchitectJavaScriptInterfaceListener {

    public static final String INTENT_EXTRAS_KEY_SAMPLE = "sampleData";

    private static final String TAG = SimpleArActivity.class.getSimpleName();

    /**
     * Root directory of the sample AR-Experiences in the assets dir.
     */
    private static final String SAMPLES_ROOT = "samples/";

    /**
     * The ArchitectView is the core of the AR functionality, it is the main
     * interface to the Wikitude SDK.
     * The ArchitectView has its own lifecycle which is very similar to the
     * Activity lifecycle.
     * To ensure that the ArchitectView is functioning properly the following
     * methods have to be called:
     * - onCreate(ArchitectStartupConfiguration)
     * - onPostCreate()
     * - onResume()
     * - onPause()
     * - onDestroy()
     * Those methods are preferably called in the corresponding Activity lifecycle callbacks.
     */
    protected ArchitectView architectView;

    /**
     * The path to the AR-Experience. This is usually the path to its index.html.
     */
    private String arExperience;
    private String place_id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Used to enabled remote debugging of the ArExperience with google chrome https://developers.google.com/web/tools/chrome-devtools/remote-debugging
        WebView.setWebContentsDebuggingEnabled(true);

        /*
         * The following code is used to run different configurations of the SimpleArActivity,
         * it is not required to use the ArchitectView but is used to simplify the Sample App.
         *
         * Because of this the Activity has to be started with correct intent extras.
         * e.g.:
         *  SampleData sampleData = new SampleData.Builder("SAMPLE_NAME", "PATH_TO_AR_EXPERIENCE")
         *              .arFeatures(ArchitectStartupConfiguration.Features.ImageTracking)
         *              .cameraFocusMode(CameraSettings.CameraFocusMode.CONTINUOUS)
         *              .cameraPosition(CameraSettings.CameraPosition.BACK)
         *              .cameraResolution(CameraSettings.CameraResolution.HD_1280x720)
         *              .camera2Enabled(false)
         *              .build();
         *
         * Intent intent = new Intent(this, SimpleArActivity.class);
         * intent.putExtra(UrlLauncherStorageActivity.URL_LAUNCHER_SAMPLE_CATEGORY, category);
         * startActivity(intent);
         */
        final Intent intent = getIntent();
        if (!intent.hasExtra(INTENT_EXTRAS_KEY_SAMPLE)) {
            throw new IllegalStateException(getClass().getSimpleName() +
                    " can not be created without valid SampleData as intent extra for key " + INTENT_EXTRAS_KEY_SAMPLE + ".");
        }

        final SampleData sampleData = (SampleData) intent.getSerializableExtra(INTENT_EXTRAS_KEY_SAMPLE);
        arExperience = sampleData.getPath();

        /*
         * The ArchitectStartupConfiguration is required to call architectView.onCreate.
         * It controls the startup of the ArchitectView which includes camera settings,
         * the required device features to run the ArchitectView and the LicenseKey which
         * has to be set to enable an AR-Experience.
         */
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration(); // Creates a config with its default values.
        config.setLicenseKey(getString(R.string.wikitude_license_key)); // Has to be set, to get a trial license key visit http://www.wikitude.com/developer/licenses.
        config.setCameraPosition(sampleData.getCameraPosition());       // The default camera is the first camera available for the system.
        config.setCameraResolution(sampleData.getCameraResolution());   // The default resolution is 640x480.
        config.setCameraFocusMode(sampleData.getCameraFocusMode());     // The default focus mode is continuous focusing.
        config.setCamera2Enabled(sampleData.isCamera2Enabled());        // The camera2 api is disabled by default (old camera api is used).

        architectView = new ArchitectView(this);
        architectView.onCreate(config); // create ArchitectView with configuration

        setContentView(R.layout.activity_ar);
        FrameLayout content = findViewById(R.id.content);
        content.addView(architectView);

        CustomAppBarLayout2 appBar = (CustomAppBarLayout2) findViewById(R.id.appbar);
        appBar.setTitle("");
        View close = appBar.findViewById(R.id.leftBt);
        close.setOnClickListener(v -> finish());

        architectView.addArchitectJavaScriptInterfaceListener(this);

        findViewById(R.id.info).setOnClickListener(v -> {
            Intent intent2 = new Intent(this, TravelPlaceDetailActivity.class);
            if (place_id == "1") {
                intent2.putExtra("title", "หอโหวด");
            } else if (place_id == "2") {
                intent2.putExtra("title", "ศูนย์แสดงพันธุ์สัตว์น้ำ");
            } else if (place_id == "3") {
                intent2.putExtra("title", "วัดบูรพาภิราม");
            }
            intent2.putExtra("place_id", place_id);
            startActivity(intent2);
        });
        findViewById(R.id.gif).setOnClickListener(v -> {
            Intent intent2 = new Intent(SimpleArActivity.this, GifImageActivity.class);
            SimpleArActivity.this.startActivity(intent2);
        });

        findViewById(R.id.video).setOnClickListener(v -> {
            if (place_id == "1") {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:lgCA96dLFS4"));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/lgCA96dLFS4"));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            } else if (place_id == "2") {
                String name = "video101_aq.mp4";
                Intent videoIntent = new Intent(this, VideoPlayerActivity.class);
                videoIntent.putExtra("filename", name);
                startActivity(videoIntent);
            } else if (place_id == "3") {
                String name = "video101.mp4";
                Intent videoIntent = new Intent(this, VideoPlayerActivity.class);
                videoIntent.putExtra("filename", name);
                startActivity(videoIntent);
            }
        });

        findViewById(R.id.web_home).setOnClickListener(v -> {
            String url = "https://roietmunicipal.go.th/roiet/";
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(url));
            startActivity(webIntent);
        });

        findViewById(R.id.web_fb).setOnClickListener(v -> {
            String url = "https://m.facebook.com/prmuni101/";
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(url));
            startActivity(webIntent);
        });

        findViewById(R.id.map_h).setOnClickListener(v -> {
            String url = "http://159.192.123.250:8000/nextcloud/index.php/s/BnxAjnR58kwDdGf/preview";
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(url));
            startActivity(webIntent);
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        architectView.onPostCreate();

        try {
            /*
             * Loads the AR-Experience, it may be a relative path from assets,
             * an absolute path (file://) or a server url.
             *
             * To get notified once the AR-Experience is fully loaded,
             * an ArchitectWorldLoadedListener can be registered.
             */
            architectView.load(SAMPLES_ROOT + arExperience);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            architectView.callJavascript("World.getDataFromNative('file://" + path + "')");
//            architectView.callJavascript("WorldInit()");
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.error_loading_ar_experience), Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Exception while loading arExperience " + arExperience + ".", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause(); // Mandatory ArchitectView lifecycle call
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
         * Deletes all cached files of this instance of the ArchitectView.
         * This guarantees that internal storage for this instance of the ArchitectView
         * is cleaned and app-memory does not grow each session.
         *
         * This should be called before architectView.onDestroy
         */
        architectView.clearCache();
        architectView.onDestroy(); // Mandatory ArchitectView lifecycle call
    }

    boolean isOnScan = false;

    @Override
    public void onJSONObjectReceived(JSONObject jsonObject) {
        try {
            String action = jsonObject.getString("action");
            Runnable runnable = () -> {
                findViewById(R.id.menu_horwote).setVisibility(View.VISIBLE);
                findViewById(R.id.info).setVisibility(View.VISIBLE);
                findViewById(R.id.gif).setVisibility(View.GONE);
                findViewById(R.id.video).setVisibility(View.VISIBLE);
                findViewById(R.id.web_home).setVisibility(View.VISIBLE);
                findViewById(R.id.web_fb).setVisibility(View.VISIBLE);
                findViewById(R.id.map_h).setVisibility(View.VISIBLE);
            };
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(SimpleArActivity.this , action , Toast.LENGTH_LONG).show();
//                }
//            });
            switch (action) {
                case "horwote_click": {
//                    runOnUiThread(() -> {
//                        Intent intent = new Intent(SimpleArActivity.this, VideoPlayerActivity.class);
//                        SimpleArActivity.this.startActivity(intent);
//                    });
                }
                break;
                case "horwote_recognized": {
                    place_id = "1";
                    runOnUiThread(() -> {
                        findViewById(R.id.menu_horwote).setVisibility(View.VISIBLE);
                        findViewById(R.id.info).setVisibility(View.VISIBLE);
                        findViewById(R.id.gif).setVisibility(View.VISIBLE);
                        findViewById(R.id.video).setVisibility(View.VISIBLE);
                    });
                    onScan();
                }
                break;
                case "horwote_lost":
                case "aq_lost":
                case "wat_lost": {
                    isOnScan = false;
                    runOnUiThread(() -> findViewById(R.id.menu_horwote).setVisibility(View.GONE));
                }
                break;
                case "aq_recognized": {
                    place_id = "2";
                    runOnUiThread(runnable);
                    onScan();
                }
                break;
                case "wat_recognized": {
                    place_id = "3";
                    runOnUiThread(runnable);
                    onScan();
                }
                break;
                default: {
                    runOnUiThread(() -> Toast.makeText(this, action, Toast.LENGTH_LONG).show());
                }
            }
        } catch (JSONException e) {
        }
    }

    private void onScan() {
        if (isOnScan) {
            return;
        }

        isOnScan = true;

        Callback callbacks = new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Log.e("success", "success");
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Log.e("error", "error");
            }
        };

        LoginResponse loginResponse = Hawk.get(Const.KEY_LOGIN_DATA);
        String citizen_id = "";
        try {
            citizen_id = Objects.requireNonNull(loginResponse.getAuthority_info()).getCitizenId();
        } catch (Exception e) {
            citizen_id = "";
        }
        String city_id;
        try {
            city_id = Objects.requireNonNull(loginResponse.getAuthority_info()).getCityId();
        } catch (Exception e) {
            city_id = "";
        }
        ApiRequest.Companion.getINSTANCE().requestScanner(callbacks, new SCannerRequest(citizen_id.toString(), place_id, city_id.toString()));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
