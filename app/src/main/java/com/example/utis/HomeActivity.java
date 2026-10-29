package com.example.utis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
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

    private ImageView notificationImageView, aboutUsImageView, cimage, dimage;
    private Spinner departureSpinner, arrivalSpinner;
    private Button timingButton, scheduleButton;
    private TextView timelyTextView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ImageView menuButton;

    private DatabaseReference mDatabaseRef;
    private final List<RouteSchedule> scheduleList = new ArrayList<>();
    private ArrayAdapter<String> departureAdapter;
    private ArrayAdapter<String> arrivalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        notificationImageView = findViewById(R.id.notification);
        aboutUsImageView = findViewById(R.id.AboutUs);
        departureSpinner = findViewById(R.id.departure);
        arrivalSpinner = findViewById(R.id.arrival);
        timingButton = findViewById(R.id.timing);
        scheduleButton = findViewById(R.id.schedule);
        cimage = findViewById(R.id.chatImageView);
        dimage = findViewById(R.id.imageView3);
        drawerLayout = findViewById(R.id.optimise);
        navigationView = findViewById(R.id.nav_view);
        menuButton = findViewById(R.id.menu);
        ImageView chatbotImageView = findViewById(R.id.chatbot);
        ImageView mapImageView = findViewById(R.id.map);
        ImageView tempImageView = findViewById(R.id.temp);
        ImageView viewTravelLogButton = findViewById(R.id.viewTravelLogButton);

        Menu menu = navigationView.getMenu();
        MenuItem appInfoItem = menu.findItem(R.id.nav_app_info);
        if (appInfoItem != null && appInfoItem.getActionView() != null) {
            appInfoItem.getActionView().setBackgroundColor(Color.parseColor("#518AFF"));
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        tempImageView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, TempStopActivity.class)));
        viewTravelLogButton.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, TravelLogActivity.class)));

        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(navigationView);
            return true;
        });

        chatbotImageView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ChatbotActivity.class)));
        mapImageView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MapActivity.class)));

        cimage.setOnClickListener(v -> showCustomContactDialog("8610122637", "bounishselfuse@gmail.com", R.layout.custom_dialog));
        dimage.setOnClickListener(v -> showCustomContactDialog("9445014448", "commercial@tnstc.org", R.layout.custom_dialog_report));

        View timeLayout = getLayoutInflater().inflate(R.layout.activity_time, null);
        timelyTextView = timeLayout.findViewById(R.id.timely);

        notificationImageView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, Admin.class)));
        aboutUsImageView.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AboutUs.class)));

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

            mDatabaseRef.addValueEventListener(new ValueEventListener() {
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

    private void showCustomContactDialog(String phone, String email, int layoutRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(layoutRes, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button btnWhatsApp = dialogView.findViewById(R.id.btn_whatsapp);
        Button btnGmail = dialogView.findViewById(R.id.btn_gmail);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnWhatsApp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + phone));
            startActivity(intent);
            alertDialog.dismiss();
        });

        btnGmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            startActivity(emailIntent);
            alertDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
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
