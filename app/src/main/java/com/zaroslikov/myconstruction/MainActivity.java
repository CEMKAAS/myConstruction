package com.zaroslikov.myconstruction;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.appbar.MaterialToolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;
import com.zaroslikov.myconstruction.databinding.ActivityMainBinding;
import com.zaroslikov.myconstruction.db.MyDatabaseHelper;
import com.zaroslikov.myconstruction.project.MenuProjectFragment;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyDatabaseHelper myDB;
    private ExtendedFloatingActionButton fab;
    private BannerAdView mBannerAdView;//Реклама от Яндекса
    private InterstitialAd mInterstitialAd;
    private MaterialToolbar appBar;
    private int position = 0;

    public static int projectNumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDB = new MyDatabaseHelper(this);
        if (savedInstanceState == null) {  //при повороте приложение не брасывается

        }
        fab = findViewById(R.id.extended_fab);
        fab.setVisibility(View.GONE);

        appBar = findViewById(R.id.topAppBar);
        // AppBar

        appBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
//                case R.id.menu:
//                    replaceFragment(new InFragment());
//                    appBar.setTitle("Информация");
//                    fab.hide();
//                    fab.setVisibility(View.GONE);
//                    break;
//
//                case R.id.deleteAll:
//                    beginIncubator();
//                    break;
            }
            return true;
        });

        set();
        binding.navView.setVisibility(View.GONE);
        binding.navView.setOnNavigationItemSelectedListener(item -> {
            position = item.getItemId();

            if(position == R.id.warehouse_button){
                replaceFragment(new WarehouseFragment());
                appBar.setTitle("Мой Склад");
                fab.hide();
                fab.setVisibility(View.GONE);
            } else if (position==R.id.add_button){
                replaceFragment(new AddFragment());
            } else if (position == R.id.writeOff_button){
                replaceFragment(new WriteOffFragment());
            } else if (position == R.id.finance_button) {
                replaceFragment(new FinanceFragment());
            }

            return true;
        });


//        убираем ботом навигацию и фабкнопку при вызове клавиатуры
        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        Log.d(TAG, "onVisibilityChanged: Keyboard visibility changed");
                        if (isOpen) {
                            Log.d(TAG, "onVisibilityChanged: Keyboard is open");
                            binding.navView.setVisibility(View.GONE);
//                            fab.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onVisibilityChanged: NavBar got Invisible");
                        } else {
                            Log.d(TAG, "onVisibilityChanged: Keyboard is closed");
                            binding.navView.setVisibility(View.VISIBLE);
//                            fab.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onVisibilityChanged: NavBar got Visible");
                        }
                    }
                });



        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("visible_fragment");
                if (fragment instanceof WarehouseFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 0;
                }
                if (fragment instanceof MenuProjectFragment) {
                    binding.navView.setVisibility(View.GONE);
                }
                if (fragment instanceof FinanceFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 1;
                }
                if (fragment instanceof AddFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 2;
                }
                if (fragment instanceof WriteOffFragment) {
                    binding.navView.setVisibility(View.VISIBLE);
                    position = 3;
                }
                binding.navView.getMenu().getItem(position).setChecked(true);
            }
        }
        );

        //Реклама от яндекса

        mBannerAdView = (BannerAdView) findViewById(R.id.banner_ad_view);

        mBannerAdView.setAdUnitId("R-M-2536883-3"); //Вставляется свой айди от яндекса
        mBannerAdView.setAdSize(AdSize.stickySize(320));//Размер банера
        final AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAdView.loadAd(adRequest);
        //рекламав
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("R-M-2536883-2");

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setInterstitialAdEventListener(new InterstitialAdEventListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {

            }

            @Override
            public void onAdShown() {

            }

            @Override
            public void onAdDismissed() {

            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onLeftApplication() {

            }

            @Override
            public void onReturnedToApplication() {

            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {

            }
        });

//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void set() {
        Cursor cursor = myDB.readProject();

        if (cursor.getCount() == 0) {
            replaceFragment(new AddProjectFragment());
        } else if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            setProjectNumer(cursor.getInt(0));

            replaceFragment(new WarehouseFragment());
        } else {
            replaceFragment(new MenuProjectFragment());
        }
        cursor.close();
    }

    //Переходим на фрагмент
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.conteiner, fragment, "visible_fragment")
                .addToBackStack(null)
                .commit();
    }


    //Сворачивание клавиатуры при нажатие на любую часть экрана
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }



    public void beginIncubator() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Удаляем все ?");
        builder.setMessage("Вы уверены, что хотите удалить все проекты, включая архивные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                myDB.deleteAllData();
                replaceFragment(new WarehouseFragment());
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void setProjectNumer(int projectNumer){
        this.projectNumer = projectNumer;
    }
    public int getProjectNumer(){
        return projectNumer;
    }
}