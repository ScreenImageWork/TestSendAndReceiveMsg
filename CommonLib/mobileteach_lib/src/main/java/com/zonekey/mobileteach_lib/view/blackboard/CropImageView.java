package com.zonekey.mobileteach_lib.view.blackboard;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by xu.wang
 * Date on 2017/5/23 18:01
 */

public class CropImageView extends AppCompatImageView {
    public CropImageView(Context context) {
        this(context,null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
    }
}
