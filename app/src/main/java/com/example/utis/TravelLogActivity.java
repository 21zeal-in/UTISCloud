package com.example.utis;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TravelLogActivity extends AppCompatActivity {

    private TextView travelLogTextView;
    private Button selectDateButton;
    private DatabaseReference travelLogRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_log);

        travelLogTextView = findViewById(R.id.travelLogTextView);
        selectDateButton = findViewById(R.id.selectDateButton);

        try {
            travelLogRef = FirebaseDatabase.getInstance("https://busbuddy-18eb3-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("travel_logs");
        } catch (Exception e) {
            Log.e("TravelLogActivity", "Firebase Init Error: " + e.getMessage());
        }

        // Set up the DatePickerDialog for date selection
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                    displayTravelLogForDate(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void displayTravelLogForDate(String date) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (date.compareTo(todayDate) > 0) {
            travelLogTextView.setText("YET TO TRAVEL");
            return;
        }

        if (travelLogRef == null) {
            travelLogTextView.setText("No travel data available for this date.");
            return;
        }

        travelLogTextView.setText("Loading travel log...");

        travelLogRef.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String log = snapshot.getValue(String.class);
                    if (log != null && !log.isEmpty()) {
                        travelLogTextView.setText(log);
                        return;
                    }
                }
                travelLogTextView.setText("No travel data available for this date.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                travelLogTextView.setText("No travel data available for this date.");
            }
        });
    }
}
