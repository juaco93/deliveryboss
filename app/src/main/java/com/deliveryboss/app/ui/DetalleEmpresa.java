package com.deliveryboss.app.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.deliveryboss.app.data.api.model.BodegasBody;
import com.deliveryboss.app.data.api.model.MessageEvent;
import com.google.gson.Gson;
import com.deliveryboss.app.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import toan.android.floatingactionmenu.FloatingActionsMenu;


public class DetalleEmpresa extends AppCompatActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    BodegasBody empresa;
    String codRubro;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    InfoMenuFragment fragmentMenu = new InfoMenuFragment();
    InfoEmpresaFragment fragmentInfoEmpresa = new InfoEmpresaFragment();
    InfoCalificacionesFragment fragmentCalificaciones = new InfoCalificacionesFragment();

    FloatingActionsMenu mSharedFab;

    private int[] tabIcons = {
            R.mipmap.ic_list,
            R.drawable.ic_info_white_36dp,
            R.drawable.ic_star
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_empresa);

        toolbar = (Toolbar) findViewById(R.id.toolbarMenu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedFab = findViewById(R.id.multiple_actions);

        Intent intentRecibido = getIntent();
        empresa = (new Gson()).fromJson((intentRecibido.getStringExtra("empresaJson")), BodegasBody.class);
        getSupportActionBar().setTitle(empresa.getNombre());

        codRubro = getRubro();



        //collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        //collapsing_container.setTitle(getResources().getString(R.string.title_activity_detalle_empresa));
        //collapsing_container.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3-1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //mSharedFab.hide(); // Hide animation
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        switch (viewPager.getCurrentItem()) {
                            case 0:
                                //fragmentCalificaciones.shareFab(null); // Remove FAB from fragment
                                fragmentInfoEmpresa.shareFab(null);
                                //fragmentMenu.shareFab(mSharedFab); // Share FAB to new displayed fragment
                                //mSharedFab.show();
                                EventBus.getDefault().post(new MessageEvent("11", "Tab en menu"));
                                break;
                            case 1:
                                fragmentMenu.shareFab(null); // Remove FAB from fragment
                                //fragmentCalificaciones.shareFab(null); // Share FAB to new displayed fragment
                                fragmentInfoEmpresa.shareFab(null);
                                EventBus.getDefault().post(new MessageEvent("12", "Tab NO en menu"));
                                break;
                            case 2:
                                fragmentMenu.shareFab(null); // Remove FAB from fragment
                                //fragmentCalificaciones.shareFab(null); // Share FAB to new displayed fragment
                                fragmentInfoEmpresa.shareFab(null);
                                EventBus.getDefault().post(new MessageEvent("12", "Tab NO en menu"));
                                break;
                            default:
                                fragmentMenu.shareFab(null); // Remove FAB from fragment
                                //fragmentCalificaciones.shareFab(null); // Share FAB to new displayed fragment
                                fragmentInfoEmpresa.shareFab(null);
                                EventBus.getDefault().post(new MessageEvent("12", "Tab NO en menu"));
                                break;
                        }
                        //mSharedFab.show(); // Show animation
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        setearColores();
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(fragmentMenu, "Menu");
        adapter.addFragment(fragmentInfoEmpresa, "Informacion");
        //adapter.addFragment(fragmentCalificaciones, "Calificacion");
        viewPager.setAdapter(adapter);

        fragmentMenu.shareFab(mSharedFab); // To init the FAB
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {





        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        //tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                //Intent intent = new Intent(this, PrincipalActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //intent.putExtra("rubro",codRubro);
                //startActivity(intent);
                EventBus.getDefault().post(new MessageEvent("13", "Presionado Back"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getRubro(){
        String rubro = empresa.getIdempresa_rubro();
        String codRubro = "";
        switch (rubro){
            case "Comida":
                codRubro = "1";
                break;
            case "Farmacia":
                codRubro = "2";
                break;
            case "Mercado":
                codRubro = "3";
                break;
            case "Taxi":
                codRubro = "4";
                break;
        }
        return codRubro;
    }

    public void setearColorToolbar(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public void setearColores(){
        if(codRubro!=null) {
            if (codRubro.equals("1")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorComida)));
                setearColorToolbar(getResources().getColor(R.color.colorComida));
                tabLayout.setBackgroundColor(getResources().getColor(R.color.colorComida));
            }
            if (codRubro.equals("2")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorFarmacia)));
                setearColorToolbar(getResources().getColor(R.color.colorFarmacia));
                tabLayout.setBackgroundColor(getResources().getColor(R.color.colorFarmacia));
            }
            if (codRubro.equals("3")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMercado)));
                setearColorToolbar(getResources().getColor(R.color.colorMercado));
                tabLayout.setBackgroundColor(getResources().getColor(R.color.colorMercado));
            }
            if (codRubro.equals("4")) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorTaxi)));
                setearColorToolbar(getResources().getColor(R.color.colorTaxi));
                tabLayout.setBackgroundColor(getResources().getColor(R.color.colorTaxi));
            }
        }
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new MessageEvent("13", "Presionado Back"));
        //Intent intent = new Intent(this, PrincipalActivity.class);
        //startActivity(intent);
    }


}
