package pt.fcul.lasige.sonaar.consent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.fcul.lasige.sonaar.R;

public class ConsentPage3 extends Fragment {

    public ConsentPage3() {
        // Required empty public constructor
    }

    public static ConsentPage3 newInstance(String param1, String param2) {
        return new ConsentPage3();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consent_page3, container, false);
    }
}