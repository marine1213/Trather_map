package thangtv.com.trather.ui.route;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.guna.libmultispinner.MultiSelectionSpinner;

import java.util.ArrayList;
import java.util.List;

import thangtv.com.trather.R;
import thangtv.com.trather.ui.helper.ViewHelper;

/**
 * Created by Nguyen on 10/15/2015.
 */

public class ViewRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_route_activity);

        MultiSelectionSpinner multiSelectionSpinner;

        String[] array = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.view_route_sp_loop_time);
        multiSelectionSpinner.setItems(array);
        multiSelectionSpinner.setSelection(new int[]{2, 6});

        Button sp_selectTime = (Button) findViewById(R.id.view_route_bt_select_date);
        sp_selectTime.setText("Choose a date");

        initViewPagerAndTabs();
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(ListRouteFragment.createInstance(20), getString(R.string.tab_1));
        pagerAdapter.addFragment(ListRouteFragment.createInstance(4), getString(R.string.tab_2));
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    public void onClickOnViewRoute(View v){
        switch (v.getId()){
            case R.id.view_route_bt_back:
                finish();
                break;
            case R.id.view_route_bt_select_date:
                ViewHelper.showDatePicker(this, null);
                break;
            case R.id.view_route_tv_select_time:
                ViewHelper.showTimePicker(this,null);
                break;
        }
    }
}
