package com.wallet.bitsyte.bitsite_wallet;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private AHBottomNavigation bottomNavigation;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private DemoFragment currentFragment;
    private AHBottomNavigationViewPager viewPager;
    private DemoViewPagerAdapter adapter;
    private Handler handler = new Handler();
    private int[] tabColors;
    private boolean useMenuResource = true;
    private AHBottomNavigationAdapter navigationAdapter;
    private ZXingScannerView mScannerView;
    boolean ScanActive = false;
    private LinearLayout cameraQr;
    private Button CloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
        setContentView(R.layout.activity_home);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);
        cameraQr = (LinearLayout) findViewById(R.id.cameraQr);
        CloseButton = (Button) findViewById(R.id.CloseButton);

        cameraQr.setVisibility(LinearLayout.GONE);
        CloseButton.setVisibility(LinearLayout.GONE);
        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraQr.setVisibility(LinearLayout.GONE);
                CloseButton.setVisibility(LinearLayout.GONE);
                mScannerView.removeAllViews();
                mScannerView.stopCamera();
                ScanActive = false;
            }
        });

            AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_home_black_24px, R.color.color_tab_1);
            AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_aspect_ratio_black_24px, R.color.color_tab_2);
            AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_send_black_24px, R.color.color_tab_3);
            AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_settings_black_24px, R.color.color_tab_3);
            viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

            bottomNavigationItems.add(item1);
            bottomNavigationItems.add(item2);
            bottomNavigationItems.add(item3);
            bottomNavigationItems.add(item4);

            bottomNavigation.addItems(bottomNavigationItems);

        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (currentFragment == null) {
                    currentFragment = adapter.getCurrentFragment();
                }

                if (wasSelected) {
                    currentFragment.refresh();
                    return true;
                }

                if (currentFragment != null) {
                    currentFragment.willBeHidden();
                }

                viewPager.setCurrentItem(position, false);

                if (currentFragment == null) {
                    return true;
                }

                currentFragment = adapter.getCurrentFragment();
                currentFragment.willBeDisplayed();

                if (position == 1) {
                    bottomNavigation.setNotification("", 1);

                } else {

                }

                return true;
            }
        });

        viewPager.setOffscreenPageLimit(4);
        adapter = new DemoViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        currentFragment = adapter.getCurrentFragment();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Setting custom colors for notification
                AHNotification notification = new AHNotification.Builder()
                        .setText(":)")
                        .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.color_notification_back))
                        .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_notification_text))
                        .build();
                bottomNavigation.setNotification(notification, 1);
                Snackbar.make(bottomNavigation, "Snackbar with bottom navigation",
                        Snackbar.LENGTH_SHORT).show();

            }
        }, 3000);

        AHBottomNavigation.TitleState state = false ? AHBottomNavigation.TitleState.ALWAYS_HIDE : AHBottomNavigation.TitleState.ALWAYS_SHOW;
        bottomNavigation.setTitleState(state);

    }

    public void QrScanner(View view){
        cameraQr.setVisibility(LinearLayout.VISIBLE);
        CloseButton.setVisibility(LinearLayout.VISIBLE);
        ScanActive = true;

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        //setContentView(mScannerView);
        cameraQr.addView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onBackPressed(){
        if(ScanActive){
            cameraQr.setVisibility(LinearLayout.GONE);
            CloseButton.setVisibility(LinearLayout.GONE);
            mScannerView.removeAllViews();
            mScannerView.stopCamera();
            ScanActive = false;
        }else {
            super.onBackPressed();
        }

    }



    /**
     * Update the bottom navigation colored param
     */
    public void updateBottomNavigationColor(boolean isColored) {
        bottomNavigation.setColored(isColored);
    }

    /**
     * Return if the bottom navigation is colored
     */
    public boolean isBottomNavigationColored() {
        return bottomNavigation.isColored();
    }

    /**
     * Add or remove items of the bottom navigation
     */
    public void updateBottomNavigationItems(boolean addItems) {

        if (useMenuResource) {
            if (addItems) {
                navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_5);
                navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
                bottomNavigation.setNotification("1", 3);
            } else {
                navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
                navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
            }

        } else {
            if (addItems) {
                AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.tab_4),
                        ContextCompat.getDrawable(this, R.drawable.ic_maps_local_bar),
                        ContextCompat.getColor(this, R.color.color_tab_4));
                AHBottomNavigationItem item5 = new AHBottomNavigationItem(getString(R.string.tab_5),
                        ContextCompat.getDrawable(this, R.drawable.ic_maps_place),
                        ContextCompat.getColor(this, R.color.color_tab_5));

                bottomNavigation.addItem(item4);
                bottomNavigation.addItem(item5);
                bottomNavigation.setNotification("1", 3);
            } else {
                bottomNavigation.removeAllItems();
                bottomNavigation.addItems(bottomNavigationItems);
            }
        }
    }

    public void showOrHideBottomNavigation(boolean show) {
        if (show) {
            bottomNavigation.restoreBottomNavigation(true);
        } else {
            bottomNavigation.hideBottomNavigation(true);
        }
    }

    /**
     * Show or hide selected item background
     */
    public void updateSelectedBackgroundVisibility(boolean isVisible) {
        bottomNavigation.setSelectedBackgroundVisible(isVisible);
    }

    /**
     * Show or hide selected item background
     */
    public void setForceTitleHide(boolean forceTitleHide) {
        AHBottomNavigation.TitleState state = forceTitleHide ? AHBottomNavigation.TitleState.ALWAYS_HIDE : AHBottomNavigation.TitleState.ALWAYS_SHOW;
        bottomNavigation.setTitleState(state);
    }

    /**
     * Reload activity
     */
    public void reload() {
        startActivity(new Intent(this, DemoActivity.class));
        finish();
    }

    /**
     * Return the number of items in the bottom navigation
     */
    public int getBottomNavigationNbItems() {
        return bottomNavigation.getItemsCount();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }
}
