package com.wetrain.client.locationservice;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.wetrain.client.Constants;

public class LocationService implements LocationListener {

	private Context mContext;

	private LocationManager locationManager;

	public Location cLocation;

	private String locProvider;


	public LocationService(Context context) {
		this.mContext = context;
		locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		locProvider = null;

	}

	public void startLocationUpdate() {
		try {
			setLocationProvider();

			if (locProvider != null) {
				try {
					locationManager.requestLocationUpdates(locProvider, 0, 1, this);
				} catch (SecurityException e) {
					Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void setLocationProvider(){
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){			
			locProvider = LocationManager.NETWORK_PROVIDER;		
			
		}else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){			
			locProvider = LocationManager.GPS_PROVIDER;	
			
		}
	}
	
	public boolean checkLocationServiceEnabled(){
		try {
			if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){			
				locProvider = LocationManager.NETWORK_PROVIDER;		
				return true;
				
			}else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){			
				locProvider = LocationManager.GPS_PROVIDER;					
				return true;
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void onLocationChanged(Location location) {		
		this.cLocation = location;
	}

	public void onProviderDisabled(String provider) {		
		this.locProvider = null;
		
	}
	

	public void onProviderEnabled(String provider){
		
		this.locProvider = provider;
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	public Location getCurrentLocation(){

		if(Constants.MOCK_LOCATION_ENABLED){
			return getPhiladelphiaLocation();
		}
		
		Location location = cLocation;
		
		if(location == null){
			
			if(locProvider == null){
				setLocationProvider();
			}
			
			if(locProvider != null){				
				try {
					location = locationManager.getLastKnownLocation(locProvider);
				} catch (SecurityException e) {
					Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
				}
			}
		}


		if(location == null){
			location = getPhiladelphiaLocation();
		}
		
		return location;		
	}
	
	public void pauseLocationUpdates(){

		try {
			locationManager.removeUpdates(this);
		} catch (SecurityException e) {
			Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
		}
	}

	public void stopLocationUpdate(){
		try {
			locationManager.removeUpdates(this);
		} catch (SecurityException e) {
			Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
		}
		locationManager = null;
	}
	
	
	
	
	public double getCurrentLatitude(){
		Location loc = getCurrentLocation();
		if (loc != null) {							
			return loc.getLatitude();
		}
		
		return getPhiladelphiaLocation().getLatitude();
	}
	
	public double getCurrentLongitude(){
		Location loc = getCurrentLocation();
		if (loc != null) {							
			return loc.getLongitude();
		}
		
		return getPhiladelphiaLocation().getLongitude();
	}

	private static Location getPhiladelphiaLocation(){
		Location phildelphiaLoc = new Location("phildelphia");
		phildelphiaLoc.setLatitude(Constants.PHILADELPHIA_LAT);
		phildelphiaLoc.setLongitude(Constants.PHILADELPHIA_LON);
		return phildelphiaLoc;
	}
	
}
