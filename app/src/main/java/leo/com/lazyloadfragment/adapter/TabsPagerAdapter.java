package leo.com.lazyloadfragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import leo.com.lazyloadfragment.fragment.BaseFragment;

/**
 * Created by leo on 2016/1/5.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragmentClassList;
    private String[] titleArray;

    public TabsPagerAdapter(FragmentManager fm,
                            List<BaseFragment> fragmentClassList, String[] titleArray) {
        super(fm);
        this.fragmentClassList = fragmentClassList;
        this.titleArray = titleArray;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        int count = fragmentClassList.size();
        if (position < count) {
            // try {
            return fragmentClassList.get(position);
            // } catch (InstantiationException e) {
            // Log.e("Fragment", "unable to create fragment:" + e.getMessage());
            // } catch (IllegalAccessException e) {
            // Log.e("Fragment", "unable to create fragment:" + e.getMessage());
            // }
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragmentClassList.size();
    }
}
