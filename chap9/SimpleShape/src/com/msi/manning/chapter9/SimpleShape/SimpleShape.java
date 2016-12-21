package com.msi.manning.chapter9.SimpleShape;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.View;

public class SimpleShape extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(new SimpleView(this));
    }

    private static class SimpleView extends View {

        private ShapeDrawable mDrawable = new ShapeDrawable();

        public SimpleView(Context context) {
            super(context);
            setFocusable(true);
            this.mDrawable = new ShapeDrawable(new RectShape());
            this.mDrawable.getPaint().setColor(0xFFFF0000);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int x = 10;
            int y = 10;
            int width = 300;
            int height = 50;
            this.mDrawable.setBounds(x, y, x + width, y + height);
            this.mDrawable.draw(canvas);
            y += height + 5;

        }
    }
}
