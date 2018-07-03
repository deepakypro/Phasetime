package com.thelosers.android.phasetime.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SavePhaseTime extends RealmObject {

  @PrimaryKey
  private int id;
  double latitude;
  double longitude;
  String sunset_time;
  String sunrise_time;
  String moonset_time;
  String moonrise_time;

  public SavePhaseTime(){}

  public SavePhaseTime(int id, double latitude, double longitude, String sunset_time,
      String sunrise_time, String moonset_time, String moonrise_time) {
    this.id = id;
    this.latitude = latitude;
    this.longitude = longitude;
    this.sunset_time = sunset_time;
    this.sunrise_time = sunrise_time;
    this.moonset_time = moonset_time;
    this.moonrise_time = moonrise_time;
  }

  public String getMoonset_time() {
    return moonset_time;
  }

  public void setMoonset_time(String moonset_time) {
    this.moonset_time = moonset_time;
  }

  public String getMoonrise_time() {
    return moonrise_time;
  }

  public void setMoonrise_time(String moonrise_time) {
    this.moonrise_time = moonrise_time;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getSunset_time() {
    return sunset_time;
  }

  public void setSunset_time(String sunset_time) {
    this.sunset_time = sunset_time;
  }

  public String getSunrise_time() {
    return sunrise_time;
  }

  public void setSunrise_time(String sunrise_time) {
    this.sunrise_time = sunrise_time;
  }
}
