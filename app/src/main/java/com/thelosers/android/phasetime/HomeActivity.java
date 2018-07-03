package com.thelosers.android.phasetime;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.thelosers.android.phasetime.MiscUtils.CalculateMoonTime.AU;
import static com.thelosers.android.phasetime.MiscUtils.CalculateMoonTime.DEG_TO_RAD;
import static com.thelosers.android.phasetime.MiscUtils.CalculateMoonTime.RAD_TO_DEG;
import static com.thelosers.android.phasetime.MiscUtils.Utils.addDays;
import static com.thelosers.android.phasetime.MiscUtils.Utils.subtractDays;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocomplete.IntentBuilder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.thelosers.android.phasetime.BroadCastReceiver.NotificationBrodcastReceiver;
import com.thelosers.android.phasetime.MiscUtils.CalculateMoonTime;
import com.thelosers.android.phasetime.MiscUtils.CalculateMoonTime.TWILIGHT;
import com.thelosers.android.phasetime.MiscUtils.CalculatePhaseTime;
import com.thelosers.android.phasetime.MiscUtils.CalculatePhaseTime.Direction;
import com.thelosers.android.phasetime.MiscUtils.GoogleApiClientClass;
import com.thelosers.android.phasetime.MiscUtils.GoogleApiClientClass.ConnectionListener;
import com.thelosers.android.phasetime.Model.SavePhaseTime;
import io.realm.Realm;
import io.realm.Realm.Transaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {


  private Date mDate=new Date();
  private static final int PERMISSION_REQUEST_CODE = 200;
  @BindView(R.id.sliding_layout_top_relative)
  RelativeLayout mSlidingLayoutTopRelative;
  @BindView(R.id.sunset_textview)
  TextView mSunsetTextview;
  @BindView(R.id.sunrise_textview)
  TextView mSunriseTextview;
  @BindView(R.id.moon_rise_textview)
  TextView mMoonRiseTextview;
  @BindView(R.id.moon_set_textview)
  TextView mMoonSetTextview;
  private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
  private int dateCount = 0;
  private LatLng mCurrentLocation;
  private boolean isPlaceLocationGet = false;
  private GoogleApiClientClass mGoogleApiClientClass;
  private static final String TAG = HomeActivity.class.getSimpleName();
  @BindView(R.id.save_button)
  ImageButton mSaveButton;
  @BindView(R.id.sliding_up_button)
  ImageButton mSlidingUpButton;
  @BindView(R.id.today_date_textview)
  TextView mTodayDateTextview;
  @BindView(R.id.subtract_date_button)
  Button mSubtractDateButton;
  @BindView(R.id.reset_date_button)
  ImageButton mResetDateButton;
  @BindView(R.id.add_date_button)
  Button mAddDateButton;
  @BindView(R.id.listview_home)
  ListView mListviewHome;
  @BindView(R.id.dragView)
  LinearLayout mDragView;
  @BindView(R.id.sliding_layout)
  SlidingUpPanelLayout mSlidingLayout;
  @BindView(R.id.search_button)
  ImageButton mSearchButton;
  private GoogleMap mMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    ButterKnife.bind(this);
    Realm.init(getApplicationContext());
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map_view);
    mapFragment.getMapAsync(this);

    mTodayDateTextview.setText(convertStringToDate(getTodayDate()));

    if (!checkPermission()) {
      requestPermission();

    }

    check_location_on_off();

    mGoogleApiClientClass = new GoogleApiClientClass(getApplicationContext());
    mGoogleApiClientClass.setConnectionListener(new ConnectionListener() {
      @Override
      public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

      }

      @Override
      public void onConnectionSuspended(int i) {

      }

      @Override
      public void onConnected(Bundle bundle, GoogleApiClient googleApiClient) {

      }

      @Override
      public void onLocationChanged(Location location) {

        mSaveButton.setVisibility(View.VISIBLE);
        if (!isPlaceLocationGet) {

          if (location == null) {
            isPlaceLocationGet = false;
            return;
          }
          isPlaceLocationGet = true;
          Log.d(TAG, location.getLatitude() + " " + location.getLongitude());
          LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
          mCurrentLocation = mLatLng;
          addMarker(location.getLatitude(), location.getLongitude());
        }
      }
    });

    mSlidingLayout.addPanelSlideListener(new PanelSlideListener() {
      @Override
      public void onPanelSlide(View panel, float slideOffset) {
        Log.i("TAG", "onPanelSlide, offset " + slideOffset);
      }


      @Override
      public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {

        if (mSlidingLayout.getPanelState() == PanelState.EXPANDED) {
          mSlidingLayoutTopRelative.setVisibility(View.GONE);
          getUserPhaseList();

        } else if (mSlidingLayout.getPanelState() == PanelState.COLLAPSED) {

          mSlidingLayoutTopRelative.setVisibility(View.VISIBLE);

        }
      }
    });
    mSlidingLayout.setFadeOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        mSlidingLayout.setPanelState(PanelState.COLLAPSED);
      }
    });
  }

  private void addMarker(Double latitude, Double longitude) {
    mMap.clear();
    LatLng currentLocation = new LatLng(latitude, longitude);
    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));

    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
        currentLocation, 15);
    mMap.animateCamera(location);

    CalculateSunsetSunRise(convertStringToDate(getTodayDate()), latitude, longitude);


  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;


  }

  @OnClick({R.id.save_button, R.id.search_button, R.id.sliding_up_button, R.id.subtract_date_button,
      R.id.reset_date_button, R.id.add_date_button})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.save_button:
        Toast.makeText(getApplicationContext(),"Record Saved !!",Toast.LENGTH_SHORT).show();
        saveUserPhaseList();
        break;

      case R.id.sliding_up_button:
        if (mSlidingLayout.getPanelState() == PanelState.COLLAPSED) {
          mSlidingLayout.setPanelState(PanelState.EXPANDED);

        }
        break;
      case R.id.subtract_date_button:
        if(mCurrentLocation==null){
          Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
          return;
        }

        dateCount = dateCount + 1;
        mDate = subtractDays(mDate,1);
        setFormattedDate(mTodayDateTextview, mDate);
        CalculateSunsetSunRise(convertStringToDate(mDate), mCurrentLocation.latitude,
            mCurrentLocation.longitude);
        break;
      case R.id.reset_date_button:

        if(mCurrentLocation==null){
          Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
          return;
        }
        dateCount = 0;
        mDate = new Date();
        setFormattedDate(mTodayDateTextview, mDate);


        CalculateSunsetSunRise(convertStringToDate(mDate), mCurrentLocation.latitude,
            mCurrentLocation.longitude);
        break;
      case R.id.add_date_button:
        if(mCurrentLocation==null){
          Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
          return;
        }
//        dateCount = dateCount + 1;
        mDate = addDays(mDate,1);
        setFormattedDate(mTodayDateTextview, mDate);
        CalculateSunsetSunRise(convertStringToDate(mDate), mCurrentLocation.latitude,
            mCurrentLocation.longitude);

        break;

      case R.id.search_button:
        try {
          Intent intent =
              new IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                  .build(this);
          startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
          // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
          // TODO: Handle the error.
        }
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        mSaveButton.setVisibility(View.VISIBLE);
        Place place = PlaceAutocomplete.getPlace(this, data);
        Log.i(TAG, "Place: " + place.getLatLng().latitude + place.getLatLng().longitude);
        isPlaceLocationGet = true;
        if (place != null) {
          mCurrentLocation = place.getLatLng();
          addMarker(place.getLatLng().latitude, place.getLatLng().longitude);

        }
      } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
        Status status = PlaceAutocomplete.getStatus(this, data);
        // TODO: Handle the error.
        Log.i(TAG, status.getStatusMessage());

      } else if (resultCode == RESULT_CANCELED) {
        // The user canceled the operation.
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mGoogleApiClientClass.stopLocationUpdates();
  }


  private void setFormattedDate(TextView textView, Date date) {

    textView.setText(convertStringToDate(date));


  }

  public String convertStringToDate(Date indate) {
    String dateString = null;
    SimpleDateFormat sdfr = new SimpleDateFormat("dd-MM-yyyy");

    try {
      dateString = sdfr.format(indate);
    } catch (Exception ex) {
      System.out.println(ex);
    }
    return dateString;
  }

  private void saveUserPhaseList() {
    Realm mRealm = Realm.getDefaultInstance();

    mRealm.executeTransactionAsync(new Transaction() {
      @Override
      public void execute(Realm realm) {
        Number maxId = realm.where(SavePhaseTime.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        SavePhaseTime mSavePhaseTime = new SavePhaseTime(nextId, mCurrentLocation.latitude,
            mCurrentLocation.longitude, mSunsetTextview.getText().toString(),
            mSunriseTextview.getText().toString(), mMoonSetTextview.getText().toString(),
            mMoonRiseTextview.getText().toString());

        realm.copyToRealmOrUpdate(mSavePhaseTime);
      }
    });
  }

  private void getUserPhaseList() {
    Realm mRealm = Realm.getDefaultInstance();
    mRealm.executeTransaction(new Transaction() {
      @Override
      public void execute(Realm realm) {
        List<SavePhaseTime> mSavePhaseTime = realm.where(SavePhaseTime.class).findAll();

        List<String> mList = new ArrayList<>();

        for (SavePhaseTime mSavePhaseTime1 : mSavePhaseTime) {

          mList.add(
              mSavePhaseTime1.getId() + " " + "Location :-" + mSavePhaseTime1.getLatitude() + " "
                  + mSavePhaseTime1.getLongitude() + "\n" + "Sunset :- " + mSavePhaseTime1
                  .getSunset_time() + "\nSunrise :- " + mSavePhaseTime1.getSunrise_time()
                  + "\n MoonSet :- " + mSavePhaseTime1.getMoonset_time() + "\n MoonRise :- "
                  + mSavePhaseTime1.getMoonrise_time());
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
            R.layout.listview_savephase_time, mList);

        mListviewHome.setAdapter(adapter);
      }
    });
  }


  public void CalculateSunsetSunRise(String date, double latitude, double longitude) {

    if (date == null) {
      Log.d(TAG, "Empty Date");
      return;
    }
    if (latitude == 0) {
      Log.d(TAG, "Empty latitude");
      return;
    }
    if (longitude == 0) {
      Log.d(TAG, "Empty longitude");
      return;
    }
    int day = 0;
    int month = 0;
    int year = 0;
    StringTokenizer mStringTokenizer = new StringTokenizer(date, "-");
    while (mStringTokenizer.hasMoreTokens()) {

      day = Integer.parseInt(mStringTokenizer.nextToken());
      month = Integer.parseInt(mStringTokenizer.nextToken());
      year = Integer.parseInt(mStringTokenizer.nextToken());
    }

    //sunrise & sunset
    CalculatePhaseTime mCalculatePhaseTime = new CalculatePhaseTime();
    double sunRise = mCalculatePhaseTime
        .getPhaseTime(day, year, month, longitude, latitude, Direction.Sunrise);

    double sunset = mCalculatePhaseTime
        .getPhaseTime(day, year, month, longitude, latitude, Direction.Sunset);

    mSunriseTextview
        .setText(String.format(Locale.US, "%.2f", sunRise));

    mSunsetTextview.setText(String.format(Locale.US, "%.2f", sunset));

    //moonrise and moonset

    int h = 11, m = 8, s = 0;
    double obsLon = 76.9300787 * DEG_TO_RAD, obsLat = 28.5345483 * DEG_TO_RAD;
    CalculateMoonTime smc = null;
    try {
      smc = new CalculateMoonTime(year, month, day, h, m, s, obsLon, obsLat);
      smc.calcSunAndMoon();

      String moonSetTime = CalculateMoonTime.getDateAsString(smc.moonSet);

      String moonRiseTime = CalculateMoonTime.getDateAsString(smc.moonRise);

      mMoonRiseTextview.setText(String.format(Locale.US, "%.2f", getTimeInIst(moonRiseTime)));
      mMoonSetTextview.setText(String.format(Locale.US, "%.2f", getTimeInIst(moonSetTime)));

      int hour=0,minute=0;
      StringTokenizer mStringTokenizer1 = new StringTokenizer(mSunsetTextview.getText().toString(),".");
      while (mStringTokenizer1.hasMoreTokens()) {

        hour = Integer.parseInt(mStringTokenizer1.nextToken());
        minute = Integer.parseInt(mStringTokenizer1.nextToken());

      }
      setGoldenHourNotification(hour,minute);
    } catch (Exception e) {
      e.printStackTrace();
    }


  }

  public Date getTodayDate() {
    return new Date();
  }

  private void setGoldenHourNotification(int hour,int minute) {
    Calendar calendar = Calendar.getInstance();

    calendar.set(Calendar.HOUR_OF_DAY, hour-1);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.AM_PM, Calendar.PM);

    Intent myIntent = new Intent(HomeActivity.this, NotificationBrodcastReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, myIntent, 0);

    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
  }

  public boolean checkPermission() {
    int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

    return result == PackageManager.PERMISSION_GRANTED;
  }

  private void requestPermission() {

    ActivityCompat
        .requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, READ_EXTERNAL_STORAGE},
            PERMISSION_REQUEST_CODE);

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0) {

          boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

          if (locationAccepted) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
          }
          // Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
          else {

            //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

            if (VERSION.SDK_INT >= VERSION_CODES.M) {
              if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                showMessageOKCancel(
                    "We need location permission to show you the sunset and sunrise time !!",
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        if (VERSION.SDK_INT >= VERSION_CODES.M) {
                          requestPermissions(
                              new String[]{ACCESS_FINE_LOCATION},
                              PERMISSION_REQUEST_CODE);
                        }
                      }
                    });
                return;
              }
            }

          }
        }

        break;
    }
  }

  private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
    new Builder(HomeActivity.this)
        .setMessage(message)
        .setPositiveButton("OK", okListener)
        .setNegativeButton("Cancel", null)
        .create()
        .show();
  }

  public double getTimeInIst(String utcTime) {
    double utcTimeInDouble = Double.parseDouble(utcTime.replace(",", "."));
    return 5.30 + utcTimeInDouble;
  }


  public void check_location_on_off() {
    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    try {
      boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

      boolean network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

      if (!gps_enabled && !network_enabled) {

        final Dialog dialog2 = new Dialog(HomeActivity.this);
        dialog2.setContentView(R.layout.custom_dialog_home_location_on);
        dialog2.setCancelable(false);

        dialog2.show();
        TextView yes = (TextView) dialog2.findViewById(R.id.custom_location_yes);
        TextView no = (TextView) dialog2.findViewById(R.id.custom_location_no);

        yes.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {

            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);

            dialog2.dismiss();

          }
        });

        no.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog2.dismiss();

          }
        });


      }
    } catch (Exception ex) {
    }
  }

}
