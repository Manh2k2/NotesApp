package eu.tutorials.notesapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.ReminderEntity;

public class AddNewReminderActivity extends AppCompatActivity {

    EditText edt_reminder_title, edt_reminder_text;
    TextView txt_date_time_reminder, txt_save_reminder;
    View view_reminder;
    ImageView img_back;
    String selected_reminder_color;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_reminder);
        initView();
        bottomSheet();
        saveReminder();
    }

    private void saveReminder() {
        selected_reminder_color = "#F44336";
        // Get current date and time
        txt_date_time_reminder.setText(
                new SimpleDateFormat("EEEE, dd, MMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        txt_save_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_reminder_title.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddNewReminderActivity.this, "Title can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                ReminderEntity reminderEntity = new ReminderEntity();
                reminderEntity.setTitle(edt_reminder_title.getText().toString().trim());
                reminderEntity.setDateTime(txt_date_time_reminder.getText().toString());
                reminderEntity.setColor(selected_reminder_color);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        MyNoteDatabase.getMyNoteDatabase(getApplicationContext())
                                .notesDAO()
                                .insertReminder(reminderEntity);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }
                });
                executor.shutdown(); // Add this line to properly shut down the executor
            }
        });
    }


    private void setViewColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) view_reminder.getBackground();
        gradientDrawable.setColor(Color.parseColor(selected_reminder_color));
    }

    private void bottomSheet() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout layout = findViewById(R.id.layout_dialog_button_sheet_reminder);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(layout);
        layout.findViewById(R.id.txt_bottom_click_reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        ImageView img_color_1 = layout.findViewById(R.id.img_color_1);
        ImageView img_color_2 = layout.findViewById(R.id.img_color_2);
        ImageView img_color_3 = layout.findViewById(R.id.img_color_3);
        ImageView img_color_4 = layout.findViewById(R.id.img_color_4);
        //Color 1
        layout.findViewById(R.id.view_color_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_reminder_color = "#F44336";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                setViewColor();
            }
        });
        //Color 2
        layout.findViewById(R.id.view_color_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_reminder_color = "#FFFB7B";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                setViewColor();
            }
        });
        //Color 3
        layout.findViewById(R.id.view_color_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_reminder_color = "#ADFF7B";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                setViewColor();
            }
        });
        //Color 4
        layout.findViewById(R.id.view_color_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_reminder_color = "#96FFEA";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                setViewColor();

            }
        });
    }

    private void initView() {
        edt_reminder_title = findViewById(R.id.edt_reminder_title);
        edt_reminder_text = findViewById(R.id.edt_reminder_text);
        txt_date_time_reminder = findViewById(R.id.txt_date_time_reminder);
        txt_save_reminder = findViewById(R.id.txt_save_reminder);
        view_reminder = findViewById(R.id.view_reminder);
        img_back = findViewById(R.id.img_back);

    }
}