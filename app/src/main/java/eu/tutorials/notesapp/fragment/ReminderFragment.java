package eu.tutorials.notesapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.activities.AddNewNotesActivity;
import eu.tutorials.notesapp.activities.AddNewReminderActivity;
import eu.tutorials.notesapp.adapter.MyNoteAdapter;
import eu.tutorials.notesapp.adapter.ReminderAdapter;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.MyNoteEntity;
import eu.tutorials.notesapp.entities.ReminderEntity;


public class ReminderFragment extends Fragment {

    ImageView img_add_new_reminder;
    public static final int REQUEST_CODE_ADD_NEW_NOTE = 1;

    private RecyclerView recycler_view_reminder;
    private ArrayList<ReminderEntity> reminderEntityArrayList = new ArrayList<>();
    private ReminderAdapter reminderAdapter;
    private Handler handler = new Handler();

    public ReminderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        img_add_new_reminder = view.findViewById(R.id.img_add_new_reminder);
        recycler_view_reminder = view.findViewById(R.id.recycler_view_reminder);
        recycler_view_reminder.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        reminderAdapter= new ReminderAdapter(getContext(), reminderEntityArrayList);
        recycler_view_reminder.setAdapter(reminderAdapter);
        addNewReminder();
        getAllReminder();
        return view;
    }

    private void getAllReminder() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<ReminderEntity> reminderEntityList = MyNoteDatabase.getMyNoteDatabase(getContext())
                    .notesDAO()
                    .getAllReminder();
            handler.post(() -> {
                reminderEntityArrayList.clear();
                reminderEntityArrayList.addAll(reminderEntityList);
                reminderAdapter.notifyDataSetChanged();
            });
        });
    }

    private void addNewReminder() {
        img_add_new_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddNewReminderActivity.class);
                startActivityForResult(i, REQUEST_CODE_ADD_NEW_NOTE);

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NEW_NOTE && resultCode == RESULT_OK) {
            getAllReminder();
        }
    }
}