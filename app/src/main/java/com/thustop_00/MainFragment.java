package com.thustop_00;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.thustop_00.databinding.FragmentMainBinding;
import com.thustop_00.model.Route;
import com.thustop_00.model.Stop;
import com.thustop_00.model.Via;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends FragmentBase implements MainRecyclerAdapter.OnListItemSelectedInterface, MainActivity.onBackPressedListener {
    FragmentMainBinding binding;
    private final static String TAG = "MainFragment";
    boolean toggle;
    boolean[] tog_local = {false, false, false};

    private GpsTracker gpsTracker;
    private ArrayList<Route> test_route_list;
    double latitude;
    double longitude;
    String address;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    long timeBackPressed = 0;


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater);
        binding.setMainfrag(this);

        checkRunTimePermission();

        binding.vPause.setVisibility(View.GONE);
        binding.layoutLocal.setVisibility(View.GONE);
        colorText(binding.tvLocal1, R.string.tv_local1_color, getResources().getColor(R.color.Primary));
        toggle = false;
        //Activity 기본 세팅
        _listener.setToolbarStyle(_listener.GREEN_HAMBURGER, null);

        _listener.setOnBackPressedListener(this);
        _listener.lockDrawer(false);

        //Test Routes
        Route route1 = new Route("A15", "수원", 123, 52.34f, 35, 13, 45, "운행중", 99000);
        Stop stop1_0 = new Stop("수원역 1번 출구", 0, "경기도 수원시 팔달구 매산동 103", 37.266260f, 127.001412f);
        Via via1_0 = new Via(0, stop1_0, "07:30");
        Stop stop1_1 = new Stop("이춘택 병원", 1, "경기도 수원시 팔달구 교동 매산로 138", 37.272110f, 127.015525f);
        Via via1_1 = new Via(1, stop1_1, "07:45");
        Stop stop1_2 = new Stop("성빈센트 병원", 2, "경기도 수원시 팔달구 지동 중부대로 93", 37.277585f, 127.028323f);
        Via via1_2 = new Via(2, stop1_2, "08:00");
        Stop stop1_3 = new Stop("정자역 1번 출구", 3, "성남시 정자동", 37.366200f, 127.108386f);
        Via via1_3 = new Via(3, stop1_3, "08:45");
        Stop stop1_4 = new Stop("정자역 5번 출구", 4, "성남시 정자동", 37.368213f, 127.108262f);
        Via via1_4 = new Via(4, stop1_4, "08:50");
        Stop stop1_5 = new Stop("두산위브파빌리온 오피스텔", 5, "경기도 성남시 분당구 정자동 7", 37.371521f, 127.108274f);
        Via via1_5 = new Via(5, stop1_5, "09:00");
        route1.boarding_stops.add(via1_0);
        route1.boarding_stops.add(via1_1);
        route1.boarding_stops.add(via1_2);
        route1.alighting_stops.add(via1_3);
        route1.alighting_stops.add(via1_4);
        route1.alighting_stops.add(via1_5);
        test_route_list = new ArrayList<Route>();
        test_route_list.add(route1);
        route1.status = "모집중";
        test_route_list.add(route1);

        //Recycler view 호출 및 어댑터와 연결, 데이터 할당
        RecyclerView mainRecycler = binding.rvRoutes;
        MainRecyclerAdapter mainAdapter = new MainRecyclerAdapter(getContext(), test_route_list, this);
        mainRecycler.setAdapter(mainAdapter);
/*        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mainRecycler);*/

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    /**
     * 노선 추가 버튼 눌렀을 때.
     */
    @Override
    public void onItemSelected(View v, int position, int ticket_position) {
        if (position == 0) {
            if (ticket_position == -1) {
                _listener.addFragment(AddRouteMapFragment.newInstance(null, null));
            } else {
                Toast.makeText(getContext(), ticket_position + "번째 티켓 눌림", Toast.LENGTH_SHORT).show();
            }
        } else if (position >= 2) {
            _listener.addFragment(BoardingApplicationDetailFragment.newInstance(test_route_list.get(position - 2)));
        }
    }


    public void onSelLocalClick(View view) {
        if (!toggle) {
            binding.vPause.setVisibility(View.VISIBLE);
            binding.layoutLocal.setVisibility(View.VISIBLE);
            toggle = true;
        } else {
            binding.vPause.setVisibility(View.GONE);
            binding.layoutLocal.setVisibility(View.GONE);
            toggle = false;
        }
    }

    public void onTownRequestClick(View view) {
        _listener.addFragment(RequestTownServiceFragment.newInstance());
    }


    // when select local, change selected button's background resource
    public void onLocal1Click(View view) {
        if (tog_local[1] || tog_local[2]) {
            if (tog_local[1]) {
                tog_local[1] = false;
                binding.btLocal2.setBackgroundResource(R.drawable.button_local);
            } else {
                tog_local[2] = false;
                binding.btLocal3.setBackgroundResource(R.drawable.button_local);
            }
        }

        tog_local[0] = !tog_local[0];
        if (tog_local[0]) {
            binding.btLocal1.setBackgroundResource(R.drawable.button_local_sel);
            binding.tvSelLocal.setText(R.string.bt_local1);
        } else {
            binding.btLocal1.setBackgroundResource(R.drawable.button_local);
            binding.tvSelLocal.setText(R.string.tvSelLocal);
        }
    }

    public void onLocal2Click(View view) {
        if (tog_local[0] || tog_local[2]) {
            if (tog_local[0]) {
                tog_local[0] = false;
                binding.btLocal1.setBackgroundResource(R.drawable.button_local);
            } else {
                tog_local[2] = false;
                binding.btLocal3.setBackgroundResource(R.drawable.button_local);
            }
        }

        tog_local[1] = !tog_local[1];
        if (tog_local[1]) {
            binding.btLocal2.setBackgroundResource(R.drawable.button_local_sel);
            binding.tvSelLocal.setText(R.string.bt_local2);
        } else {
            binding.btLocal2.setBackgroundResource(R.drawable.button_local);
            binding.tvSelLocal.setText(R.string.tvSelLocal);
        }
    }

    public void onLocal3Click(View view) {
        if (tog_local[0] || tog_local[1]) {
            if (tog_local[0]) {
                tog_local[0] = false;
                binding.btLocal1.setBackgroundResource(R.drawable.button_local);
            } else {
                tog_local[1] = false;
                binding.btLocal2.setBackgroundResource(R.drawable.button_local);
            }
        }

        tog_local[2] = !tog_local[2];
        if (tog_local[2]) {
            binding.btLocal3.setBackgroundResource(R.drawable.button_local_sel);
            binding.tvSelLocal.setText(R.string.bt_local3);
        } else {
            binding.btLocal3.setBackgroundResource(R.drawable.button_local);
            binding.tvSelLocal.setText(R.string.tvSelLocal);
        }

    }


    public void onGPSClick(View view) {
        gpsTracker = new GpsTracker(getActivity());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        address = getCurrentAddress(latitude, longitude);
        binding.tvSelLocal.setText(address);
        Toast.makeText(getActivity(), "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();
    }

    // method to convert location to address
    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            // Last value is maxResults
            addresses = geocoder.getFromLocation(latitude, longitude, 100);
        } catch (IOException ioException) {
            // Network problem
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_SHORT).show();
            _listener.showLocationServiceSettingDialog();
            return "지오코더 서비스 사용불가";

        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show();
            _listener.showLocationServiceSettingDialog();
            return "잘못된 GPS 좌표표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            _listener.showLocationServiceSettingDialog();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAdminArea() + " " + address.getLocality() + " " + address.getSubLocality();
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면 3.  위치 값을 가져올 수 있음
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            Log.d(TAG, "checkRunTimePermission: 위치 권환 허용 되어 있음");
            //위치 서비스 활성화 되어 있는지 체크
            if (!_listener.checkLocationServicesStatus()) {
                _listener.showLocationServiceSettingDialog();
            } else {
                _listener.setGPSLocationServiceStatus(true);
            }
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                //Toast.makeText(getActivity(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
            //+ onRequestPermissionResult 에서 허용할 시 위치 설정이 켜져있는지 체크 후 다이얼로그를 띄움
        }
    }


    @Override
    public void onBack() {
        if (timeBackPressed == 0) {
            Toast.makeText(getContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            timeBackPressed = System.currentTimeMillis();
        } else {
            int ms = (int) (System.currentTimeMillis() - timeBackPressed);
            if (ms > 2000) {
                timeBackPressed = 0;
                onBack();
            } else {
                _listener.finishActivity();
            }
        }
    }







//메인 액티비티로 옮겨야 동작하는거 확인 추후 삭제 예정
/*    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        Log.d(TAG,"권한 결과 확인 함수 호출");
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }

        }
    }*/

//동작 안하는거 확인 삭제 예정
/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE: //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
            default:
                Log.d(TAG, "onActivityResult: 작동하기는 했음");
        }
    }*/

//이거도 동작하는 지 모르겠음
/*    // Activate GPS
    private void showLocationServiceSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }*/


}