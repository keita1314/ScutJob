package com.keita.scutjob.myview;
/*
 * 自定义ImageButton 能显示文字
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class MyImageButton extends ImageButton {
	private String text="";
	private int color=0;
	
	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public void setText(String text){
		this.text = text;
	}
	public void setColor(int color){
		this.color = color;
	}
	@Override 
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        Paint paint=new Paint();  
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25);
        paint.setColor(color);  
        canvas.drawText(text,55,45 , paint);  //绘制文字  
    }  

}
