package com.wetrain.client.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.wetrain.client.R;

public class DetectSoftKeyPadLayout extends LinearLayout implements OnGlobalLayoutListener{
	
	private static final String TAG = "DetectSoftKeyPadLayout";
	
	private boolean listenKeyPad;
	
	public interface DetectSoftKeyPadListener{		
		public void setLayoutChangeListener();
		public void removeLayoutChangeListener();
		
		public void setKeyPadListener(KeyPadListener listener, boolean hideKeypadMaskAlways);

	}
	
	
	public interface KeyPadListener{
		public void softKeyPadShown();
		public void softKeyPadHidden();
		public void clickedOnMaskLayout();
		
	}
	
	
	private DetectSoftKeyPadListener layoutListener;	
	private View keyPadMaskLayout;
	
	public DetectSoftKeyPadLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		listenKeyPad = false;
	}
	

	public DetectSoftKeyPadLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		listenKeyPad = false;
	}

	public DetectSoftKeyPadLayout(Context context) {
		super(context);
		listenKeyPad = false;
	}

	public void initSoftKeyPadElements(DetectSoftKeyPadListener listener, View maskView){
		layoutListener = listener;
		
		keyPadMaskLayout = maskView;
		
		if(keyPadMaskLayout != null){

			keyPadMaskLayout.setOnTouchListener(new OnTouchListener() {				
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
					
					//Remove current focused view
					if(getFocusedChild()!= null){
						getFocusedChild().clearFocus();
					}
						
					if(keyPadListener != null) keyPadListener.clickedOnMaskLayout();
					
					
				
					return false;
				}
			});
		}
	}


	
	public void setkeyPadMaskLayout(View maskView){
		keyPadMaskLayout = maskView;
	}


	
	public void setLayoutChangeListener(){
		if(!listenKeyPad){
			listenKeyPad = true;
			this.getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void removeLayoutChangeListener(){
		listenKeyPad = false;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}else{
			this.getViewTreeObserver().removeGlobalOnLayoutListener(this);			
		}
	}
	private KeyPadListener keyPadListener;
	private boolean hideKeypadMaskAlways;
	public void setKeyPadListener(KeyPadListener listener, boolean hideKeypadMaskAlways){
		keyPadListener = listener;
		this.hideKeypadMaskAlways = hideKeypadMaskAlways;
		if(this.hideKeypadMaskAlways){
			keyPadMaskLayout.setVisibility(View.GONE);
		}
	}
	

	@Override
	public void onGlobalLayout() {
		if(listenKeyPad){
			if(layoutListener == null || keyPadMaskLayout == null) return;
			//Log.d(TAG, ">>>>>>Key Pad Shown<<<<<<");
			
			Rect rect = new Rect();
			// r will be populated with the coordinates of your view
			// that area still visible.
			
			(findViewById(R.id.keypad_detector_lyt)).getWindowVisibleDisplayFrame(rect);
			int heightDiff = (findViewById(R.id.keypad_detector_lyt))
					.getRootView().getHeight()
					- (rect.bottom - rect.top);
		
			if(keyPadMaskLayout != null) keyPadMaskLayout.setVisibility(View.GONE);
			if (heightDiff > 100) {
				if(keyPadListener != null) keyPadListener.softKeyPadShown();
				if(!hideKeypadMaskAlways) {
					if (keyPadMaskLayout != null) keyPadMaskLayout.setVisibility(View.VISIBLE);
				}
				
			}else{
				if(keyPadListener != null) keyPadListener.softKeyPadHidden();
				
			}
			
		}
	}
	
}