package com.example.utis;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private Spinner departureSpinner, arrivalSpinner;
    private Button timingButton, scheduleButton;

    private DatabaseReference mDatabaseRef;
    private final List<RouteSchedule> scheduleList = new ArrayList<>();
    private ArrayAdapter<String> departureAdapter;
    private ArrayAdapter<String> arrivalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Exit button logic on back pressed
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Exit App")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            finishAffinity(); // Closes the app and clears it from background
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        departureSpinner = findViewById(R.id.departure);
        arrivalSpinner = findViewById(R.id.arrival);
        timingButton = findViewById(R.id.timing);
        scheduleButton = findViewById(R.id.schedule);

        timingButton.setOnClickListener(v -> showNearestTime());
        scheduleButton.setOnClickListener(v -> schedule());

        // Hardcoded Departure Zone options as requested
        departureAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"KATPADI", "VELLORE"});
        departureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departureSpinner.setAdapter(departureAdapter);

        // Arrival Zone adapter populated ONLY from Firebase
        arrivalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        arrivalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrivalSpinner.setAdapter(arrivalAdapter);

        // Connect to Firebase Realtime Database
        try {
            mDatabaseRef = FirebaseDatabase.getInstance("https://busbuddy-18eb3-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("BusBuddy"); // Using the correct root node name from the screenshot

            mDatabaseRef.keepSynced(true); // Keeps data synced in background optimally
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("FirebaseAPI", "API Call (Read): onDataChange triggered.");
                    scheduleList.clear();
                    Set<String> uniqueDestinations = new HashSet<>();

                    if (snapshot.exists()) {
                        Log.d("FirebaseAPI", "API Call (Read): Data received: " + snapshot.getChildrenCount() + " items");
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            RouteSchedule item = ds.getValue(RouteSchedule.class);
                            if (item != null) {
                                scheduleList.add(item);
                                if (item.getDestination() != null && !item.getDestination().trim().isEmpty()) {
                                    uniqueDestinations.add(item.getDestination().trim());
                                }
                            }
                        }
                    } else {
                        Log.d("FirebaseAPI", "API Call (Read): Snapshot does not exist (empty database).");
                    }

                    List<String> sortedDestinations = new ArrayList<>(uniqueDestinations);
                    Collections.sort(sortedDestinations);

                    arrivalAdapter.clear();
                    arrivalAdapter.addAll(sortedDestinations);
                    arrivalAdapter.notifyDataSetChanged();
                    Log.d("FirebaseAPI", "Arrival Dropdown updated with " + sortedDestinations.size() + " destinations.");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseAPI", "API Call (Read) FAILED / Denied: " + error.getMessage() + " | Details: " + error.getDetails());
                }
            });
        } catch (Exception e) {
            Log.e("FirebaseInit", "Firebase Database Init Error: " + e.getMessage());
        }
    }

    private void showNearestTime() {
        if (departureSpinner.getSelectedItem() == null || arrivalSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select departure and arrival zones", Toast.LENGTH_SHORT).show();
            return;
        }

        String source = departureSpinner.getSelectedItem().toString();
        String destination = arrivalSpinner.getSelectedItem().toString();
        String currentTime = getCurrentTime();
        RouteSchedule nearest = null;

        for (RouteSchedule item : scheduleList) {
            boolean matchSource = item.getSource() != null && item.getSource().equalsIgnoreCase(source);
            boolean matchDest = item.getDestination() != null && item.getDestination().equalsIgnoreCase(destination);

            if (matchSource && matchDest) {
                if (item.getSourceTime() != null && item.getSourceTime().compareTo(currentTime) > 0) {
                    if (nearest == null || item.getSourceTime().compareTo(nearest.getSourceTime()) < 0) {
                        nearest = item;
                    }
                }
            }
        }

        if (nearest != null) {
            String nearestSourceTime = "\n" +
                    "ROUTE: " + nearest.getRoute() + "\n" +
                    "SOURCE: " + nearest.getSource() + "\n" +
                    "SOURCE TIME: " + nearest.getSourceTime() + "\n" +
                    "DESTINATION: " + nearest.getDestination() + "\n" +
                    "DESTINATION TIME: " + nearest.getDestinationTime();

            Intent intent = new Intent(HomeActivity.this, TimeActivity.class);
            intent.putExtra("message", nearestSourceTime);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No direct route buses found for " + source + " -> " + destination, Toast.LENGTH_SHORT).show();
        }
    }

    private void schedule() {
        if (departureSpinner.getSelectedItem() == null || arrivalSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select departure and arrival zones", Toast.LENGTH_SHORT).show();
            return;
        }

        String source = departureSpinner.getSelectedItem().toString();
        String destination = arrivalSpinner.getSelectedItem().toString();
        List<RouteSchedule> matched = new ArrayList<>();

        for (RouteSchedule item : scheduleList) {
            boolean matchSource = item.getSource() != null && item.getSource().equalsIgnoreCase(source);
            boolean matchDest = item.getDestination() != null && item.getDestination().equalsIgnoreCase(destination);

            if (matchSource && matchDest) {
                matched.add(item);
            }
        }

        Collections.sort(matched, (o1, o2) -> {
            if (o1.getSourceTime() == null) return -1;
            if (o2.getSourceTime() == null) return 1;
            return o1.getSourceTime().compareTo(o2.getSourceTime());
        });

        List<String> timingsList = new ArrayList<>();
        for (RouteSchedule item : matched) {
            String timing = "BUS NAME: " + item.getRoute() + "\n" +
                    "SOURCE TIME: " + item.getSourceTime() + "\n" +
                    "DESTINATION TIME: " + item.getDestinationTime();
            timingsList.add(timing);
        }

        if (!timingsList.isEmpty()) {
            Intent intent = new Intent(HomeActivity.this, Schedule.class);
            intent.putStringArrayListExtra("timingsList", (ArrayList<String>) timingsList);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No schedule found for " + source + " -> " + destination, Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
