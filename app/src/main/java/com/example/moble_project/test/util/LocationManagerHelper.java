package com.example.moble_project.test.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log; // 로그를 사용하기 위해 추가
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moble_project.test.DTO.UserInfo;

public class LocationManagerHelper {
    private LocationManager locationManager;
    private Context context;
    private Activity activity;
    private static final int PERMISSIONS_REQUEST_LOCATION = 99;
    private boolean isWithinTargetRange = false; // 목표 범위 내에 있는지 확인하는 플래그

    // 생성자에서 Context와 Activity를 받습니다.
    public LocationManagerHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void checkLocationAndGoToActivity(Class<?> activityToStart, Location targetLocation, UserInfo userInfo) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        } else {
            requestLocationUpdates(activityToStart, targetLocation, userInfo);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates(null, null,null); // 필요에 따라 매개변수 수정
            } else {
                Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestLocationUpdates(Class<?> activityToStart, Location targetLocation, UserInfo userInfo) {
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // 현재 위치의 로그를 출력
                Log.d("LocationUpdate", "현재 위도: " + location.getLatitude());
                Log.d("LocationUpdate", "현재 경도: " + location.getLongitude());

                double distance = location.distanceTo(targetLocation); // 목표까지의 거리 계산
                Log.d("LocationUpdate", "목표까지의 거리: " + distance + "미터");

                // 이미 목표 범위 내에 있고 메시지를 표시했다면, 더 이상 진행하지 않음
                if (isWithinTargetRange) {
                    return;
                }

                if(distance < 100) { // 목표 위치와의 거리가 100미터 이내인 경우
                    Toast.makeText(context, "환영합니다!", Toast.LENGTH_LONG).show();
                    isWithinTargetRange = true; // 사용자가 목표 범위 내에 있음을 표시

                    if(activityToStart != null) {
                        Intent intent = new Intent(context, activityToStart);

                        intent.putExtra("user_info_no",userInfo.getNo());
                        intent.putExtra("user_info_name",userInfo.getName());
                        intent.putExtra("user_info_email",userInfo.getEmail());
                        intent.putExtra("user_info_grade",userInfo.getGrade());
                        intent.putExtra("user_info_state",userInfo.getState());
                        intent.putExtra("user_info_url",userInfo.getUrl());
                        intent.putExtra("user_info_phone",userInfo.getPhone());
                        intent.putExtra("user_info_clickBtn",userInfo.getClickBtn());
                        intent.putExtra("user_info_reason",userInfo.getReason());

                        context.startActivity(intent);
                    }

                    // 목표 범위에 도달했으므로 위치 업데이트 중지
                    locationManager.removeUpdates(this);
                } else {
                    Toast.makeText(context, "목표 위치와 너무 멀리 떨어져 있습니다.", Toast.LENGTH_LONG).show();
                }
            }

            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
                Toast.makeText(context, "GPS를 켜주세요.", Toast.LENGTH_LONG).show();
            }

            public void onProviderEnabled(String provider) { }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(context, "GPS 상태가 변경되었습니다. 위치 정보를 확인해주세요.", Toast.LENGTH_LONG).show();
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
