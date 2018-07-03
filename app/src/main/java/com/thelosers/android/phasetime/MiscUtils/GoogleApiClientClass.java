package com.thelosers.android.phasetime.MiscUtils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GoogleApiClientClass implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

  private static final String TAG = GoogleApiClientClass.class.getSimpleName();
  private Context context;
  private ConnectionListener connectionListener;
  private Bundle connectionBundle;
  private static int UPDATE_INTERVAL = 200; // 10 sec
  private static int FATEST_INTERVAL = 100; // 5 sec
  private static int DISPLACEMENT = 3; // 10 meters
  private static GoogleApiClient mGoogleApiClient;
  private static LocationRequest mLocationRequest;


  public GoogleApiClientClass(Context context) {
    this.context = context;
    buildGoogleApiClient();
    CreateLocationRequest();
    connect();
  }

  public GoogleApiClient getGoogleApiClient() {
    return this.mGoogleApiClient;
  }

  public void setConnectionListener(ConnectionListener connectionListener) {
    this.connectionListener = connectionListener;
    if (this.connectionListener != null && isConnected()) {
      connectionListener.onConnected(connectionBundle, mGoogleApiClient);
    }
  }

  public LocationRequest CreateLocationRequest() {
    // if (mLocationRequest == null) {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    mLocationRequest.setFastestInterval(FATEST_INTERVAL);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    //}

    return mLocationRequest;
  }

  public void connect() {
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }
  }

  public void disconnect() {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  public boolean isConnected() {
    return mGoogleApiClient != null && mGoogleApiClient.isConnected();
  }

  private synchronized GoogleApiClient buildGoogleApiClient() {
    if(mGoogleApiClient==null) {
      mGoogleApiClient = new GoogleApiClient.Builder(context)
          .addConnectionCallbacks(this)

          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API).build();
    }
    return mGoogleApiClient;
  }

  @Override
  public void onConnected(Bundle bundle) {
    connectionBundle = bundle;
    startLocationUpdates(mGoogleApiClient);
    if (connectionListener != null) {

      connectionListener.onConnected(bundle, mGoogleApiClient);
      startLocationUpdates(mGoogleApiClient);
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.d(TAG, "onConnectionSuspended: googleApiClient.connect()");
    mGoogleApiClient.connect();
    if (connectionListener != null) {
      connectionListener.onConnectionSuspended(i);
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.d(TAG, "onConnectionFailed: connectionResult = " + connectionResult);
    if (connectionListener != null) {
      connectionListener.onConnectionFailed(connectionResult);
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    if (connectionListener != null) {
      connectionListener.onLocationChanged(location);
    }
  }

  @SuppressWarnings("deprecation")
  public void startLocationUpdates(GoogleApiClient mGoogleApiClient) {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);
    }
  }

  @SuppressWarnings("deprecation")
  public void stopLocationUpdates() {
    Log.d(TAG, "stopLocationUpdates");
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
      mGoogleApiClient.disconnect();
      mGoogleApiClient=null;
    }
  }

  public interface ConnectionListener {
    void onConnectionFailed(@NonNull ConnectionResult connectionResult);

    void onConnectionSuspended(int i);

    void onConnected(Bundle bundle,GoogleApiClient googleApiClient);

    void onLocationChanged(Location location);
  }
}

