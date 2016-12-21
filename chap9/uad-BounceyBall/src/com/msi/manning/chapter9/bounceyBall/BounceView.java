package com.msi.manning.chapter9.bounceyBall;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BounceView extends View {

    protected Drawable mySprite;
    protected Point mySpritePos = new Point(0, 0);

    protected enum HorizontalDirection {
        LEFT, RIGHT
    }

    protected enum VerticalDirection {
        UP, DOWN
    }

    protected HorizontalDirection myXDirection = HorizontalDirection.RIGHT;
    protected VerticalDirection myYDirection = VerticalDirection.UP;

    public BounceView(Context context) {
        super(context);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.android));
        this.mySprite = getResources().getDrawable(R.drawable.world);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        this.mySprite.setBounds(this.mySpritePos.x, this.mySpritePos.y, this.mySpritePos.x + 50,
            this.mySpritePos.y + 50);

        if (this.mySpritePos.x >= getWidth() - this.mySprite.getBounds().width()) {
            this.myXDirection = HorizontalDirection.LEFT;
        } else if (this.mySpritePos.x <= 0) {
            this.myXDirection = HorizontalDirection.RIGHT;
        }

        if (this.mySpritePos.y >= getHeight() - this.mySprite.getBounds().height()) {
            this.myYDirection = VerticalDirection.UP;
        } else if (this.mySpritePos.y <= 0) {
            this.myYDirection = VerticalDirection.DOWN;
        }

        if (this.myXDirection == HorizontalDirection.RIGHT) {
            this.mySpritePos.x += 10;
        } else {
            this.mySpritePos.x -= 10;
        }

        if (this.myYDirection == VerticalDirection.DOWN) {
            this.mySpritePos.y += 10;
        } else {
            this.mySpritePos.y -= 10;
        }

        this.mySprite.draw(canvas);
    }
}
