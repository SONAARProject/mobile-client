package pt.fcul.lasige.sonaar.overlay;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.fcul.lasige.sonaar.Controller;
import pt.fcul.lasige.sonaar.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class Overlay implements View.OnClickListener{
    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private WindowManager windowManager;
    private LayoutInflater li;
    private ViewGroup altTextList;
    private AccessibilityService service;

    private static Overlay instance;

    public static Overlay getInstance(){

        if (instance == null)
            instance = new Overlay();

        return instance;
    }

    public AccessibilityService getService(){
        return service;
    }

    public void setContext(AccessibilityService service) {
        this.li = (LayoutInflater) service.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.windowManager = (WindowManager) service.getSystemService(WINDOW_SERVICE);
        this.service = service;
    }

    public void showAltTextList(ArrayList<String> altText){
        altTextList = (ViewGroup) li.inflate(R.layout.alt_text_list, null);
        altTextList.findViewById(R.id.bt_close).setOnClickListener(v -> {
            hideAltTextList();
        });
        RecyclerView mRecyclerView = (RecyclerView) altTextList.findViewById(R.id.rv_alt_text_list);
        AltTextListAdapter mAdapter = new AltTextListAdapter(this, altText);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(service);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        params.gravity = Gravity.TOP | Gravity.END;
        addView(altTextList, params);
    }

    public void hideAltTextList(){
        if(altTextList != null)
            removeView(altTextList);
    }

    private void addView(View v, WindowManager.LayoutParams lp){
        windowManager.addView(v, lp);
    }

    private void removeView(View v){
        try{
            windowManager.removeView(v);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) service.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("altText", ((TextView) v).getText().toString());
        clipboard.setPrimaryClip(clip);
        Controller.getInstance().setSonaarAltText(((TextView) v).getText().toString());
        hideAltTextList();
    }
}
