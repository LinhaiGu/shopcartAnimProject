package com.example.addshowanimproject;

import com.example.addshowanimproject.animutil.AnimUtils;
import com.example.addshowanimproject.inter.IAddShopListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	/**
	 * 购物车小图标
	 */
	private ImageView iv_shop;
	
	/**
	 * 添加到购物车按钮
	 */
	private LinearLayout ll_btn_add;
	
	private AnimUtils mAnimUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initEvent();
	}

	/**
	 * View的初始化
	 */
	private void initViews(){
		iv_shop=(ImageView) findViewById(R.id.iv_shop);
		ll_btn_add=(LinearLayout) findViewById(R.id.ll_btn_add);
	}
	
	/**
	 * 注册事件
	 */
	private void initEvent(){
		final ImageView imageView=new ImageView(this);
		imageView.setLayoutParams(new LayoutParams(30, 60));
		imageView.setImageResource(R.drawable.biancheng);
		ll_btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("TAG","onClick");
				if(mAnimUtils==null){
					mAnimUtils=new AnimUtils(MainActivity.this, ll_btn_add, iv_shop, imageView);
				}
				mAnimUtils.addShopCart(new IAddShopListener() {
					
					@Override
					public void addSucess() {
						Toast.makeText(MainActivity.this, "添加了一个商品", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
	}
	
	
	
}
