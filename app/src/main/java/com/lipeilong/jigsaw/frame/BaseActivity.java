package com.lipeilong.jigsaw.frame;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lipeilong.jigsaw.log.JDLog;


/**
 * 封装的FragmentActivity，原则上工程内部的所有Activity都要继承这个类
 * 
 * 在此实现的功能包括: 友盟统计上报的功能控制 内存紧张时缓存图片的释放
 * 
 * @author ls
 * 
 */
public class BaseActivity extends FragmentActivity {

	protected static final String TAG = BaseActivity.class.getSimpleName();

	/**
	 * 记录当前最顶部的Activity
	 */
	private static BaseActivity sTopActivity = null;

	/**
	 * 获取顶部的Activity
	 */
	public static BaseActivity getTopActivity() {
		return sTopActivity;
	}

	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		JDLog.log(TAG, "onCreate：" + getClass().getSimpleName());

		sTopActivity = this; // 确保最新一个Activity创建后，TopActivity立马指向它，此时一些操作可以在onCreate函数执行

		// traceActivityOnCreate(getClass().getSimpleName());
	}

	/**
	 * onResume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		JDLog.log(TAG, "onResume：" + getClass().getSimpleName());


		sTopActivity = this; // 确保上一个Activity销毁后，TopActivity指到最上一个Activity
	}

	/**
	 * onPause
	 */
	@Override
	protected void onPause() {

		JDLog.log(TAG, "onPause：" + getClass().getSimpleName());
		super.onPause();
	}

	/**
	 * onDestroy
	 */
	@Override
	protected void onDestroy() {
		JDLog.log(TAG, "onDestroy：" + getClass().getSimpleName());
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		JDLog.log(TAG, "onStop：" + getClass().getSimpleName());
		super.onStop();
	}

	/**
	 * onLowMemory
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();

	}


}
