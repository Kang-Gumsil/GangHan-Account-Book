package dankook.kanghyeyoung.capstone_2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.pedro.library.AutoPermissions;
import org.opencv.android.OpenCVLoader;

import static dankook.kanghyeyoung.capstone_2.AccountBookDB.databaseOpen;
import static dankook.kanghyeyoung.capstone_2.AccountBookDB.delete;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_FOR_INPUT = 100;
    private static final int REQUEST_CODE_FOR_PERMISSION=101;

    FragmentManager mFragmentManager;
    TabLayout mTabLayout;
    ViewPager mPager;
    PagerAdapter mPagerAdapter;

    /* openCV 연결 확인 */
    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "openCV connection success");
        } else {
            Log.d(TAG, "openCV connection fail");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 내부DB 생성 */
        databaseOpen(this);

        delete(10);
        /* FragmentManger 얻기 */
        mFragmentManager = getSupportFragmentManager();
        Log.d(TAG, "getFragmentManager");

        /* 페이저에 adapter, onPageChangeListener 등록 */
        mPagerAdapter = new PagerAdapter(mFragmentManager, 1);
        mPager = findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);
        Log.d(TAG, "set adapter, onPageChangeListener on pager");

        /* TabLayout 참조 및 onTabSelectedListener 등록 */
        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
                mPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        Log.d(TAG, "set onTagSelectedListener on tabLayout");

        /* pager와 tabLayout 연동 */
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        Log.d(TAG, "set onPageChangeListener on pager");

        /* 위험 권한 승인 */
        AutoPermissions.Companion.loadAllPermissions(this, REQUEST_CODE_FOR_PERMISSION);
        Log.d(TAG, "get permissions");

        /* 인터넷 연결 확인 및 처리*/
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        cm.registerNetworkCallback(builder.build(), new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {}

            // 인터넷이 연결되지 않았을 경우 안내 메시지 띄우고 어플 종료
            @Override
            public void onLost(@NonNull Network network) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("네트워크 상태를 확인해주세요")
                        .setMessage("인터넷에 연결할 수 없습니다. 네트워크 연결을 확인해주세요")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        moveTaskToBack(true); // 태스크를 백그라운드로 이동
                                        finishAndRemoveTask();		  // 액티비티 종료 및 태스크 리스트에서 지우기
                                        android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
                                    }
                                })
                        .create()
                        .show();
            }
        });
        Log.d(TAG, "Internet Connection Verified");
    }

    /* SummaryView의 조작으로 year/month 변경되었을 경우 각 fragment에 알림 */
    public void updateSelectedDate(int year, int month) {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            ((MainFragment) fragment).updateSelectedDate(year, month);
        }
    }

    /* 내역이 입력/수정/삭제 되었을 경우, 각 fragment에 알림 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FOR_INPUT && resultCode == RESULT_OK) {
            for(Fragment fragment : mFragmentManager.getFragments()) {
                ((MainFragment) fragment).updateSelectedDate();
            }
        }
    }
}