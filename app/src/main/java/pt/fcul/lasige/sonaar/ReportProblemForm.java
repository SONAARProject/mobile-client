package pt.fcul.lasige.sonaar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ReportProblemForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem_form);

        findViewById(R.id.bt_report_problem).setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), R.string.sent, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}