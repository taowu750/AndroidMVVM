package com.wutaodsg.mvvm.component;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.wutaodsg.mvvm.R;

/**
 * 一个对单个 Fragment 进行托管的 Activity。
 * <p>
 * 这个 Activity 界面上只显示一个 Fragment，它只是这个 Fragment 的容器。
 */

public abstract class SingleFragmentContainerActivity<F extends Fragment> extends AppCompatActivity {

    private F mFragment;


    /**
     * 创建一个指定类型的，被这个 Activity 托管的 Fragment。
     *
     * @return 指定类型的 Fragment 对象
     */
    @NonNull
    protected abstract F newFragment();


    /**
     * 将 {@link #newFragment()} 创建的 Fragment 添加到界面中。
     * 子类覆盖这个方法需要注意不可以在这个方法中调用 {@link #getFragment()}
     * 来获取 Fragment，否则会抛出 {@link IllegalStateException} 异常。<br/>
     * 也不可以使用 {@link #setContentView(int)} 方法再次设置界面。
     *
     * @param savedInstanceState 同父类
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment_container);

        FragmentManager manager = getSupportFragmentManager();
        mFragment = (F) manager.findFragmentById(R.id.fragment_container);
        if (mFragment == null) {
            mFragment = newFragment();
            manager.beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }


    /**
     * 获取创建好的 Fragment 对象。
     * <p>
     * 该方法必须在 Fragment 和与它相关的 Activity 已经创建好之后调用，也就是
     * Activity 的 {@link #onCreate(Bundle)} 方法之后，否则会抛出 {@link IllegalStateException} 异常。
     *
     * @return Fragment 对象。
     * @throws IllegalStateException 如果该方法在 Activity 创建完成之前调用，抛出此异常
     */
    @NonNull
    public final F getFragment() {
        if (mFragment == null || mFragment.getActivity() == null) {
            throw new IllegalStateException(getClass().getName() + ": The Activity associated with \"" +
                    mFragment.getClass().getName() + "\" has not been created yet. " +
                    "You must call \"getFragment()\" after the \"onCreate()\" method of Activity.");
        }

        return mFragment;
    }
}
