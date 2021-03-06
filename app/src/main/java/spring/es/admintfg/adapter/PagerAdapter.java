package spring.es.admintfg.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import spring.es.admintfg.MyApplication;
import spring.es.admintfg.fragment.OrdersFragment;
import spring.es.admintfg.fragment.ProductsFragment;
import spring.es.admintfg.fragment.ProfileFragment;
import spring.es.admintfg.fragment.UsersFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ProductsFragment();
            case 1:
                return new OrdersFragment();
            case 2:
                if(MyApplication.getInstance().isAdmin())
                    return new UsersFragment();
                else
                    return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}