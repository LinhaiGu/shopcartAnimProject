package com.example.addshowanimproject.animutil;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.example.addshowanimproject.inter.IAddShopListener;

/**
 * View 的工具类
 * 
 * @author 顾林海
 * 
 */
public class AnimUtils {

	private Context mContext;

	/**
	 * 添加物品按钮
	 */
	private View mButtonView;

	/**
	 * 购物车小图标
	 */
	private View mShopCartView;

	/**
	 * 所购买商品的图标
	 */
	private View mShopView;

	private LinearLayout animLayout;

	float a = -1f / 75f;

	private ObjectAnimator mObjectAnimator;

	/**
	 * 
	 * @param context
	 * @param btnView
	 *            添加物品按钮
	 * @param shopCartView
	 *            购物车小图标（你要添加的购物车容器）
	 * @param result
	 *            你购买商品的图标
	 */
	public AnimUtils(Context context, View btnView, View shopCartView,
			View result) {
		this.mContext = context;
		this.mButtonView = btnView;
		this.mShopCartView = shopCartView;
		this.mShopView = result;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		ViewGroup rootView = (ViewGroup) ((Activity) mContext).getWindow()
				.getDecorView();
		animLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		animLayout.setLayoutParams(lp);
		animLayout.setId(Integer.MAX_VALUE);
		animLayout.setBackgroundResource(android.R.color.transparent);
		rootView.addView(animLayout);
		animLayout.addView(mShopView);
	}

	/**
	 * 这里是根据三个坐标点{（0,0），（300,0），（150,300）}计算出来的抛物线方程
	 * 
	 * @param x
	 * @return
	 */
	private float getY(float x) {
		return a * x * x + 4 * x;
	}

	public void addShopCart(final IAddShopListener listener) {
		int[] start_location = new int[2];
		mButtonView.getLocationInWindow(start_location);

		addViewToAnimLayout(animLayout, mShopView, start_location);

		int[] end_location = new int[2];
		mShopCartView.getLocationInWindow(end_location);

		float count = 300;
		Keyframe[] keyframes = new Keyframe[(int) count];
		final float keyStep = 1f / (float) count;
		float f = (float) (start_location[0] - end_location[0]) / count;
		float key = keyStep;
		for (int i = 0; i < count; ++i) {
			keyframes[i] = Keyframe.ofFloat(key, -i * f);
			key += keyStep;
		}

		PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe(
				"translationX", keyframes);
		key = keyStep;

		for (int i = 0; i < count; ++i) {
			keyframes[i] = Keyframe.ofFloat(key, -getY(i + 1));
			key += keyStep;
		}

		PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe(
				"translationY", keyframes);
		PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",
				1f, 0.5f);
		PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",
				1f, 0.5f);

		if (mObjectAnimator == null) {

			mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mShopView,
					pvhX, pvhY, scaleX, scaleY).setDuration(1500);
			mObjectAnimator.setInterpolator(new AccelerateInterpolator());
			mObjectAnimator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					mShopView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					// TODO Auto-generated method stub
					mShopView.setVisibility(View.GONE);
					listener.addSucess();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub

				}
			});

		}
		if (!mObjectAnimator.isStarted()) {

			mObjectAnimator.start();
		}
	}

	private View addViewToAnimLayout(final ViewGroup vg, final View view,
			int[] location) {
		int x = location[0];
		int y = location[1];
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = x;
		lp.topMargin = y;
		view.setLayoutParams(lp);
		return view;
	}

}
