package leo.com.lazyloadfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import leo.com.lazyloadfragment.adapter.TabsPagerAdapter;
import leo.com.lazyloadfragment.fragment.BaseFragment;
import leo.com.lazyloadfragment.fragment.OneFragment;
import leo.com.lazyloadfragment.fragment.SecondFragment;
import leo.com.lazyloadfragment.fragment.ThirdFragment;
import leo.com.lazyloadfragment.weight.PagerSlidingTabStrip;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.pts_tab)
    PagerSlidingTabStrip ptsTab;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViewPager();
    }

    private void initViewPager() {
        String titles[] = getResources().getStringArray(R.array.title);
        List<BaseFragment> fragments =new ArrayList<BaseFragment>();

        fragments.add(OneFragment.newInstance("", ""));
        fragments.add(SecondFragment.newInstance("", ""));
        fragments.add(ThirdFragment.newInstance("", ""));

        viewPager.setOffscreenPageLimit(1);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(tabsPagerAdapter);
        ptsTab.setViewPager(viewPager);
        viewPager.setCurrentItem(0);
        getSupportFragmentManager().beginTransaction().commit();
    }
}
