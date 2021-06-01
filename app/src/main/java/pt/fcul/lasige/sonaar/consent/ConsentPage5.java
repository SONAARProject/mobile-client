package pt.fcul.lasige.sonaar.consent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.fcul.lasige.sonaar.R;


public class ConsentPage5 extends Fragment {

    public ConsentPage5() {
        // Required empty public constructor
    }

    public static ConsentPage5 newInstance(String param1, String param2) {
        return new ConsentPage5();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consent_page5, container, false);
    }
}