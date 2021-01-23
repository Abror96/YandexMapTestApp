package com.resttab.testproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.resttab.testproject.R;
import com.resttab.testproject.databinding.ActivityMainBinding;
import com.resttab.testproject.ui.fragments.MapFragment;
import com.resttab.testproject.ui.fragments.MyAddressesFragment;
import com.resttab.testproject.utils.interfaces.OnAddressAddListener;

public class MainActivity extends AppCompatActivity implements OnAddressAddListener {

    private ActivityMainBinding binding;
    private Fragment myAddressesFragment;
    private Fragment mapFragment;
    private Fragment activeFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setRoundedCornersToBnv();

        MapFragment.setControlBadgetsListener(this);
        myAddressesFragment = new MyAddressesFragment();
        mapFragment = new MapFragment();
        activeFragment = myAddressesFragment;
        fm = getSupportFragmentManager();

        initMainNav();
    }

    private void initMainNav() {
        fm.beginTransaction().add(R.id.main_container, mapFragment, "map").hide(mapFragment).commit();
        fm.beginTransaction().add(R.id.main_container, myAddressesFragment, "my_addresses").commit();

        binding.mainNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_my_addresses:
                    fm.beginTransaction().hide(activeFragment).show(myAddressesFragment).commit();
                    activeFragment = myAddressesFragment;
                    binding.mainNav.removeBadge(R.id.nav_my_addresses);
                    return true;
                case R.id.nav_map:
                    fm.beginTransaction().hide(activeFragment).show(mapFragment).commit();
                    activeFragment = mapFragment;
                    return true;
            }
            return false;
        });
    }

    private void setRoundedCornersToBnv() {
        float radius = getResources().getDimension(R.dimen.bnv_radius);
        MaterialShapeDrawable bnvBackground = (MaterialShapeDrawable) binding.mainNav.getBackground();
        bnvBackground.setShapeAppearanceModel(new ShapeAppearanceModel().toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build());
    }

    @Override
    public void onAddressAdded() {
        int num = binding.mainNav.getOrCreateBadge(R.id.nav_my_addresses).getNumber();
        binding.mainNav.getOrCreateBadge(R.id.nav_my_addresses).setNumber(++num);
    }
}