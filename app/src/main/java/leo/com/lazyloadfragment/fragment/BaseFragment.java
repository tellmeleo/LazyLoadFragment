package leo.com.lazyloadfragment.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Administrator on 2016/3/28.
 */
public abstract class BaseFragment extends Fragment {


    private static final String TAG = BaseFragment.class.getSimpleName();
    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        Log.d(TAG, "setUserVisibleHint: "+"执行了setUserVisibleHint"+"isVisibleToUser=="+isVisibleToUser);
        prepareFetchData();
    }

    @Override
    public boolean getUserVisibleHint() {
        return super.getUserVisibleHint();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: "+"执行了onCreate方法");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: "+"执行了onActivityCreate方法");
        isViewInitiated = true;
        prepareFetchData();
    }

    //获取数据的方法
    public abstract void fetchData();



    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    /**
     * 这里就是预留了强制刷新～如果需要强制刷新～可以先调用prepareFetchData，传true
     * @param forceUpdate
     * @return
     */
    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            fetchData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }
}
