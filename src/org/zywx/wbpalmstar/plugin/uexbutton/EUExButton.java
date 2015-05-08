package org.zywx.wbpalmstar.plugin.uexbutton;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class EUExButton extends EUExBase {
	public static final String onClickFunName = "uexButton.onClick";

	private HashMap<Integer, View> viewMap = new HashMap<Integer, View>();

	public EUExButton(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	public void open(String[] params) {
		if (params.length != 6) {
			return;
		}
		int opId = 0;
		int x = 0;
		int y = 0;
		int w = 0;
		int h = 0;
		String jsonString = null;
		try {
			opId = Integer.parseInt(params[0]);
			x = Integer.parseInt(params[1]);
			y = Integer.parseInt(params[2]);
			w = Integer.parseInt(params[3]);
			h = Integer.parseInt(params[4]);
			jsonString = params[5];
			
			if (viewMap.containsKey(opId)) {
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createButtonInUIThread(opId, x, y, w, h, jsonString);
		
	}
	
	private void createButtonInUIThread(final int opId, final int x, final int y, final int w, final int h, final String jsonString)
	{
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
//					'{"title":"AppCan","textSize":"15" "titleColor":"#111111","bgImage":"res://a1.png"}';
					
					String titleString = null;
					String titleColorString = null;
					String bgImagePathString = null;
					int textSize = 0;
					if (jsonObject.has("title")) {
						titleString = jsonObject.getString("title");
					}
					
					if (jsonObject.has("titleColor")) {
						titleColorString = jsonObject.getString("titleColor");
					}
					
					if (jsonObject.has("textSize")) {
						textSize = jsonObject.getInt("textSize");
					}
				
					bgImagePathString = jsonObject.getString("bgImage");
					
					Button btn = new Button(mContext);
					
					RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams (w, h); 
					btParams.leftMargin = x;
					btParams.topMargin = y;
					
					if (titleString != null) {
						btn.setText(titleString);
					}
					
					if (titleColorString != null) {
						btn.setTextColor(Color.parseColor(titleColorString));
					}
					
					if (textSize > 0) {
						btn.setTextSize(textSize);
					}
					
			       
					Bitmap defaultImage = ImageColorUtils.getImage(mContext, bgImagePathString);
					
					if (defaultImage != null) {
						btn.setBackgroundDrawable(ImageColorUtils.bgColorDrawableSelector(defaultImage,defaultImage));
					}
					
					
					addViewToCurrentWindow(btn, btParams);
					
					viewMap.put(opId, btn);
					
					MyButtonListener listener = new MyButtonListener();
					btn.setOnClickListener(listener);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public class MyButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			int opId = getKeyFromViewMaps(arg0);
			jsCallback(onClickFunName, opId, EUExCallback.F_C_INT, EUExCallback.F_C_SUCCESS);
		}
	}
	
	private int getKeyFromViewMaps(View btn) {
		for (Map.Entry<Integer,View> e : viewMap.entrySet()) {
            Integer key = e.getKey();
            View value2 = (View) e.getValue();
            if (value2 == btn)
            {
                return key;
            }
        }
		return -1;
	}
	
	
	public void close(String[] params) {
		if (params.length != 1) {
			return;
		}
		int opId = 0;
		try {
			opId = Integer.parseInt(params[0]);
			if (viewMap.containsKey(opId)) {
				closeButtonInUIThread(opId);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void closeButtonInUIThread(final int opId) {
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Button btnButton = (Button)viewMap.get(opId);
				removeViewFromCurrentWindow(btnButton);
				viewMap.remove(opId);
			}
		});
	}

	@Override
	protected boolean clean() {
		return false;
	}
}
