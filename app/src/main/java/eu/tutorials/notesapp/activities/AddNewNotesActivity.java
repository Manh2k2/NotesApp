package eu.tutorials.notesapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.MyNoteEntity;

public class AddNewNotesActivity extends AppCompatActivity {

    private EditText edt_input_note_text, edt_note_title;
    private TextView txt_date_time, txt_save;
    private View view_indicator, view_indicator_2;
    private String selectedColor;
    private Handler handler = new Handler();
    ImageView addImg, img_back;
    String selectedImg;
    public static final int STORAGE_PERMISSIONS = 1;
    public static final int SELECT_IMG = 1;
    private MyNoteEntity alreadyAvailableNote;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_notes);
        initView();
        bottomSheet();
        saveNotes();
    }

    private void viewOrUpdateNotes() {
        if (getIntent().getBooleanExtra("updateOrView", false)) {
            alreadyAvailableNote = (MyNoteEntity) getIntent().getSerializableExtra("myNotes");
            edt_note_title.setText(alreadyAvailableNote.getTitle());
            edt_input_note_text.setText(alreadyAvailableNote.getNoteText());
            txt_date_time.setText(alreadyAvailableNote.getDateTime());
            bottomSheet();
            if (alreadyAvailableNote.getImagePath() != null) {
                addImg.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
                addImg.setVisibility(View.VISIBLE);
                findViewById(R.id.img_remove).setVisibility(View.VISIBLE);
                selectedImg = alreadyAvailableNote.getImagePath();
            }
        }


    }


    private void saveNotes() {
        selectedImg = "";
        selectedColor = "#F44336";
        viewOrUpdateNotes();
        // Get current date and time
        txt_date_time.setText(
                new SimpleDateFormat("EEEE, dd, MMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImg.setImageBitmap(null);
                addImg.setVisibility(View.GONE);
                findViewById(R.id.img_remove).setVisibility(View.VISIBLE);
                selectedImg = "";

            }
        });
        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_note_title.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddNewNotesActivity.this, "Title can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_input_note_text.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddNewNotesActivity.this, "Note text can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final MyNoteEntity myNoteEntity = new MyNoteEntity();
                if (alreadyAvailableNote != null) {
                    myNoteEntity.setId(alreadyAvailableNote.getId());
                }
                myNoteEntity.setTitle(edt_note_title.getText().toString().trim());
                myNoteEntity.setNoteText(edt_input_note_text.getText().toString().trim());
                myNoteEntity.setDateTime(txt_date_time.getText().toString());
                myNoteEntity.setColor(selectedColor);
                myNoteEntity.setImagePath(selectedImg);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        MyNoteDatabase.getMyNoteDatabase(getApplicationContext())
                                .notesDAO()
                                .insertNote(myNoteEntity);
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
            }
        });
    }

    private void setViewColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) view_indicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedColor));

        GradientDrawable gradientDrawable2 = (GradientDrawable) view_indicator_2.getBackground();
        gradientDrawable2.setColor(Color.parseColor(selectedColor));
    }

    private void bottomSheet() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayout layout = findViewById(R.id.layout_dialog_button_sheet);
        //////////////
        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null) {
            switch (alreadyAvailableNote.getColor()) {
                case "#F44336":
                    layout.findViewById(R.id.view_color_1).performClick();
                    break;
                case "#FFFB7B":
                    layout.findViewById(R.id.view_color_2).performClick();
                    break;
                case "#ADFF7B":
                    layout.findViewById(R.id.view_color_3).performClick();
                    break;
                case "#96FFEA":
                    layout.findViewById(R.id.view_color_4).performClick();
                    break;
                case "#969CFF":
                    layout.findViewById(R.id.view_color_5).performClick();
                    break;
                case "#FF96F5":
                    layout.findViewById(R.id.view_color_6).performClick();
                    break;
            }
        }

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(layout);
        layout.findViewById(R.id.txt_bottom_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });
        //Delete note
        if (alreadyAvailableNote != null) {
            layout.findViewById(R.id.remove_note).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.remove_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteDialog();
                }
            });
        }

        //////////////////////ADD IMAGE //////////////////
        ImageView img_color_1 = layout.findViewById(R.id.img_color_1);
        ImageView img_color_2 = layout.findViewById(R.id.img_color_2);
        ImageView img_color_3 = layout.findViewById(R.id.img_color_3);
        ImageView img_color_4 = layout.findViewById(R.id.img_color_4);
        ImageView img_color_5 = layout.findViewById(R.id.img_color_5);
        ImageView img_color_6 = layout.findViewById(R.id.img_color_6);

        //Color 1
        layout.findViewById(R.id.view_color_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#F44336";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_5);
                Glide.with(getApplicationContext()).load(0).into(img_color_6);
                setViewColor();

            }
        });
        //Color 2
        layout.findViewById(R.id.view_color_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#FFFB7B";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_5);
                Glide.with(getApplicationContext()).load(0).into(img_color_6);
                setViewColor();
            }
        });
        //Color 3
        layout.findViewById(R.id.view_color_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#ADFF7B";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_5);
                Glide.with(getApplicationContext()).load(0).into(img_color_6);
                setViewColor();

            }
        });
        //Color 4
        layout.findViewById(R.id.view_color_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#96FFEA";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_5);
                Glide.with(getApplicationContext()).load(0).into(img_color_6);
                setViewColor();

            }
        });
        //Color 5
        layout.findViewById(R.id.view_color_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#969CFF";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_5);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                Glide.with(getApplicationContext()).load(0).into(img_color_6);
                setViewColor();
            }
        });
        //Color 6
        layout.findViewById(R.id.view_color_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColor = "#FF96F5";
                Glide.with(getApplicationContext()).load(R.drawable.ic_done).into(img_color_6);
                Glide.with(getApplicationContext()).load(0).into(img_color_2);
                Glide.with(getApplicationContext()).load(0).into(img_color_3);
                Glide.with(getApplicationContext()).load(0).into(img_color_4);
                Glide.with(getApplicationContext()).load(0).into(img_color_5);
                Glide.with(getApplicationContext()).load(0).into(img_color_1);
                setViewColor();

            }
        });

        //---------------------------ADD IMAGES--------------------------------
        //Yêu cầu quyền truy cập bộ nhớ
        layout.findViewById(R.id.img_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);// Đặt trạng thái của bottom sheet là ẩn đi
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {//Kiểm tra xem quyền truy cập bộ nhớ được cấp hay chưa PERMISSION_GRANTED là được cấp)
                    ActivityCompat.requestPermissions(AddNewNotesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS);
                } else {
                    selectedYourImage();
                }
            }
        });

    }

    private void showDeleteDialog() {
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.delete_note_layout,
                    (ViewGroup) findViewById(R.id.layout_delete_container));
            builder.setView(view);
            alertDialog = builder.create();
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.txt_delete_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            MyNoteDatabase.getMyNoteDatabase(getApplicationContext())
                                    .notesDAO()
                                    .deleteNote(alreadyAvailableNote);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.putExtra("isNoteDeleted", true);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                        }
                    });
                    executor.shutdown();
                }
            });
            view.findViewById(R.id.txt_cancel_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    private void selectedYourImage() {// cho phép chọn 1 mục từ một danh sách giao diện
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//EXTERNAL_CONTENT_URI truy cập hình ảnh từ bộ nhớ ngoài
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_IMG);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSIONS && grantResults.length > 0) {//Kiểm tra xem có đúng key không
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//Nếu quyền truy cập bộ nhớ được cấp
                selectedYourImage();
            } else {
                Snackbar.make(AddNewNotesActivity.this.getCurrentFocus(), "Permission denied!", Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMG && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    addImg.setImageBitmap(bitmap);
                    addImg.setVisibility(View.VISIBLE);
                    selectedImg = getPath(selectedImageUri);
                    findViewById(R.id.img_remove).setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    Snackbar.make(AddNewNotesActivity.this.getCurrentFocus(), e.getMessage(), Snackbar.LENGTH_SHORT);
                }
            }
        }
    }

    private String getPath(Uri uri) {
        String filePath;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            filePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }


    private void initView() {
        edt_note_title = findViewById(R.id.edt_reminder_title);
        edt_input_note_text = findViewById(R.id.edt_input_note_text);
        txt_date_time = findViewById(R.id.txt_date_time_reminder);
        view_indicator = findViewById(R.id.view_indicator);
        view_indicator_2 = findViewById(R.id.view_indicator_2);
        txt_save = findViewById(R.id.txt_save);
        addImg = findViewById(R.id.img_notes);
        img_back = findViewById(R.id.img_back);
    }
}
