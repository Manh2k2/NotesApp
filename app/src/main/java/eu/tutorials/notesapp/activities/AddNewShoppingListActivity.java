package eu.tutorials.notesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.ShoppingListEntity;

public class AddNewShoppingListActivity extends AppCompatActivity {
    TextView txt_date_time_shopping_list, txt_save_sl;
    EditText edt_shopping_list_title, edt_input_sl;
    ImageView img_back;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_shopping_list);
        initView();
        saveShoppingListNote();
    }

    private void saveShoppingListNote() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txt_date_time_shopping_list.setText(
                new SimpleDateFormat("EEEE, dd, MMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
        txt_save_sl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_shopping_list_title.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddNewShoppingListActivity.this, "Title can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_input_sl.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddNewShoppingListActivity.this, "Note text can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ShoppingListEntity shoppingListEntity = new ShoppingListEntity();
                shoppingListEntity.setTitle(edt_shopping_list_title.getText().toString());
                shoppingListEntity.setNoteText(edt_input_sl.getText().toString());
                shoppingListEntity.setDateTime(txt_date_time_shopping_list.getText().toString());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        MyNoteDatabase.getMyNoteDatabase(getApplicationContext())
                                .notesDAO()
                                .insertShoppingList(shoppingListEntity);
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

    private void initView() {
        txt_save_sl = findViewById(R.id.txt_save_sl);
        txt_date_time_shopping_list = findViewById(R.id.txt_date_time_shopping_list);
        edt_input_sl = findViewById(R.id.edt_input_sl);
        edt_shopping_list_title = findViewById(R.id.edt_shopping_list_title);
        img_back = findViewById(R.id.img_back);
    }
}