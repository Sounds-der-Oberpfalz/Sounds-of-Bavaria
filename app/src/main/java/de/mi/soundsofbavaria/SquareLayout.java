package de.mi.soundsofbavaria;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {
  public SquareLayout(Context context) {
    super(context);
  }
  
  public SquareLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
    setMeasuredDimension(size, size);
  }
}
