# shopcartAnimProject
仿京东底部添加到购物车效果
前言

美好的双休日快要结束了，昨天玩了一天，今天在家看了一整天的书，大体看了网络框架搭建的一个流程，自己也试了下，有些小问题，还在研究中。用过京东APP的都知道在商品详情页底部有个添加到购物车的按钮，一点击就会将商品以抛物线的形式放入购物车中，今天给大家带来的就是类似购物车的效果。

购物车 http://img.blog.csdn.net/20160327163248081

原理讲解

将上面效果拆分成两部分的话，就是抛物线+图片缩放，抛物线关键点就是获取Y坐标，这里直接套用网上的公式：

    /**
     * 这里是根据三个坐标点计算出来的抛物线方程
     * 
     * @param x
     * @return
     */
    private float getY(float x) {
        return a * x * x + 4 * x;
    }

整体的抛物线和缩放动画使用PropertyValuesHolder，达到同步播放的效果。

通过PropertyValuesHolder.ofKeyframe(“translationY”, keyframes);实现商品的向左平移的效果，这里面的keyframes是一个Keyframe对象的数组，看如下的整体取值：

Keyframe[] keyframes = new Keyframe[(int) count];
        final float keyStep = 1f / (float) count;
        float f = (float) (start_location[0] - end_location[0]) / count;
        float key = keyStep;
        for (int i = 0; i < count; ++i) {
            keyframes[i] = Keyframe.ofFloat(key, -i * f);
            key += keyStep;
        }

count是动画在执行过程中的步数，这里面拆分成300份，view在屏幕上向左平移时值为负数，因此Keyframe.ofFloat(key, -i * f)，这里面的f公式：

float f = (float) (start_location[0] - end_location[0]) / count;

sart_location[0]是获取的添加购物车按钮的X坐标，end_location[0]是购物车小图标的X坐标，相减得到按钮到购物车的距离，将距离进行平分，分成count份，因此通过for循环-i * f，进行向左的移动。

Y轴的平移，会发现先是向上平移（负数），平移到一半后，向下平移（正数），这样构成一个平滑的抛物线，通过上面的Y坐标的公式得出：

    key = keyStep;
    for (int i = 0; i < count; ++i) {
        keyframes[i] = Keyframe.ofFloat(key, -getY(i + 1));
        key += keyStep;
    }
    PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframes);

这时得到View进行抛物线移动时的X和Y坐标，接着会看到View的缩放，缩放代码如下：

PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",
                1f, 0.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",
                1f, 0.5f);

最后通过：

mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mShopView,
                    pvhX, pvhY, scaleX, scaleY).setDuration(1500);

获取ObjectAnimator对象，进行start()就可动画的播放。

如何应用到项目中

这里我创建了一个AnimUtils类，使用如下：

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

代码很简单，AnimUtils的初始化，接着调用AnimUtils的addShopCart方法进行动画播放完毕的监听。

以下是xml的布局：

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/holo_blue_bright" >

    <LinearLayout
        android:layout_width="fill_parent"
        android:layout_height="60dp"
        android:layout_alignParentBottom="true"
        android:background="@android:color/white"
        android:orientation="horizontal" >

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center"
            android:orientation="vertical" >


        </LinearLayout>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center"
            android:orientation="vertical" >
        </LinearLayout>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center"
            android:orientation="vertical" >
            <ImageView
                android:id="@+id/iv_shop"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/shopping_cart" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="购物车"
                android:textColor="@android:color/black"
                android:textSize="12sp" />
        </LinearLayout>

        <LinearLayout
            android:id="@+id/ll_btn_add"
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="2"
            android:background="@android:color/holo_red_light"
            android:gravity="center"
            android:orientation="vertical" >

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="加入购物车"
                android:textColor="@android:color/white"
                android:textSize="18sp" />
        </LinearLayout>
    </LinearLayout>

</RelativeLayout>

当然有些人会说，你这购物车图片在按钮旁边，那我如果在底部最左边呢？放心，一样可以支持，我们将xml改为如下：

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/holo_blue_bright" >

    <LinearLayout
        android:layout_width="fill_parent"
        android:layout_height="60dp"
        android:layout_alignParentBottom="true"
        android:background="@android:color/white"
        android:orientation="horizontal" >

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center"
            android:orientation="vertical" >

            <ImageView
                android:id="@+id/iv_shop"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/shopping_cart" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="购物车"
                android:textColor="@android:color/black"
                android:textSize="12sp" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center"
            android:orientation="vertical" >
        </LinearLayout>

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="1"
            android:gravity="center"
            android:orientation="vertical" >
        </LinearLayout>

        <LinearLayout
            android:id="@+id/ll_btn_add"
            android:layout_width="0dp"
            android:layout_height="60dp"
            android:layout_gravity="center_vertical"
            android:layout_weight="2"
            android:background="@android:color/holo_red_light"
            android:gravity="center"
            android:orientation="vertical" >

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="加入购物车"
                android:textColor="@android:color/white"
                android:textSize="18sp" />
        </LinearLayout>
    </LinearLayout>

</RelativeLayout>

运行程序如下：

http://img.blog.csdn.net/20160327165756770

购物车

怎么样，是不是很方便。
