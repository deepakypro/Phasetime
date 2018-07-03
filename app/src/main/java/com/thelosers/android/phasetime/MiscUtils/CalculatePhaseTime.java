package com.thelosers.android.phasetime.MiscUtils;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.random;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalculatePhaseTime {


 public double getPhaseTime(int day,int year,int month,double longitude,double latitude,Direction direction){
   double N1 = Math.floor(275 * month / 9);
   double N2 = Math.floor((month + 9) / 12);
   double N3 = (1 + Math.floor((year - 4 * Math.floor(year / 4) + 2) / 3));
   double N = N1 - (N2 * N3) + day - 30;



   double lngHour = longitude / 15.0;

   double t;

   if (direction == Direction.Sunrise)
     t = N + ((6.0 - lngHour) / 24.0);
   else
     t = N + ((18.0 - lngHour) / 24.0);

   // mean anomaly (M)
   double M = (0.9856 * t) - 3.289;

   // true longitude (L)
   double L = M + (1.916 * Math.sin(Deg2Rad(M))) + (0.020 * Math.sin(2 * Deg2Rad(M))) + 282.634;

   L= FixValue(L,0,360);
//
   // right asc (RA)
   double RA = Rad2Deg(Math.atan(0.91764 * Math.tan(Deg2Rad(L))));
   RA = FixValue(RA, 0, 360);

   // adjust quadrant of RA
   double Lquadrant = (Math.floor(L / 90.0)) * 90.0;
   double RAquadrant = (Math.floor(RA / 90.0)) * 90.0;
   RA = RA + (Lquadrant - RAquadrant);

   RA = RA / 15.0;

   // sin cos DEC (sinDec / cosDec)
   double sinDec = 0.39782 * Math.sin(Deg2Rad(L));
   double cosDec = Math.cos(Math.asin(sinDec));

   // local hour angle (cosH)
   double cosH = (Math.cos(Deg2Rad((double)90833/ 1000.0f)) - (sinDec * Math.sin(Deg2Rad(latitude)))) / (cosDec * Math.cos(Deg2Rad(latitude)));

   // local hour (H)
   double H;

   if (direction == Direction.Sunrise)
     H = 360.0 - Rad2Deg(Math.acos(cosH));
   else
     H = Rad2Deg(Math.acos(cosH));

   H = H / 15.0;

   // time (T)
   double T = H + RA - (0.06571 * t) - 6.622;

   // universal time (T)
   double UT = T - lngHour;

   UT += 5.30;  // local UTC offset

//        if (daylightChanges != null)
//            if ((date > daylightChanges.Start) && (date < daylightChanges.End))
//                UT += (double)daylightChanges.Delta.Ticks / 36000000000;

   return FixValue(UT, 0, 24);

 }


  private static double Deg2Rad(double angle)
  {
    return Math.PI * angle / 180.0;
  }

  private static double Rad2Deg(double angle)
  {
    return 180.0 * angle / Math.PI;
  }

  private static double FixValue(double value, double min, double max)
  {
    while (value < min)
      value += (max - min);

    while (value >= max)
      value -= (max - min);

    return value;
  }
 public enum Direction{
    Sunrise,
    Sunset
  }
}
