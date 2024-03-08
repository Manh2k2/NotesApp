package eu.tutorials.notesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import eu.tutorials.notesapp.fragment.MyNotesFragment;
import eu.tutorials.notesapp.fragment.ReminderFragment;
import eu.tutorials.notesapp.fragment.ShoppingListFragment;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar bottom_navigation_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        bottomMenu();
    }

    private void bottomMenu() {
        bottom_navigation_bar.setItemSelected(R.id.home, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag, new MyNotesFragment()).commit();

        bottom_navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                if (i == R.id.home) {
                    fragment = new MyNotesFragment();
                } else if (i == R.id.reminder) {
                    fragment = new ReminderFragment();
                } else if (i == R.id.shopping_list) {
                    fragment = new ShoppingListFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view_tag, fragment).commit();
            }
        });
    }

    private void initViews() {
        bottom_navigation_bar = findViewById(R.id.bottom_navigation_bar);
    }
}