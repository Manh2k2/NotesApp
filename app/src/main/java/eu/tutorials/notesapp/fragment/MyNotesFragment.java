package eu.tutorials.notesapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import eu.tutorials.notesapp.adapter.MyNoteAdapter;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.MyNoteEntity;
import eu.tutorials.notesapp.listeners.MyNoteListeners;

public class MyNotesFragment extends Fragment implements MyNoteListeners {
    private static final int REQUEST_CODE_ADD_NEW_NOTE = 1;
    private static final int UPDATE_NOTE = 2;
    private int clickedPosition = -1;

    public static final int SHOW_NOTE = 3;

    EditText edt_search;
    private ImageView img_add_new_note;
    private RecyclerView recycler_view_note;
    private ArrayList<MyNoteEntity> myNoteEntityList = new ArrayList<>();
    private MyNoteAdapter myNoteAdapter;
    private Handler handler = new Handler();


    public MyNotesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_notes, container, false);

        img_add_new_note = view.findViewById(R.id.img_add_new_note);

        recycler_view_note = view.findViewById(R.id.recycler_view_note);
        edt_search = view.findViewById(R.id.edt_search);

        recycler_view_note.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        myNoteAdapter = new MyNoteAdapter(getContext(), myNoteEntityList, this);
        recycler_view_note.setAdapter(myNoteAdapter);

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getAllNotes(SHOW_NOTE, false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myNoteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (myNoteEntityList.size() != 0) {
                    myNoteAdapter.searchNotes(s.toString());
                    Toast.makeText(getContext(), s.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        addNewNote();
        getAllNotes(SHOW_NOTE, false);
        return view;
    }

    private void getAllNotes(int requestCode, boolean isNoteDeleted) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<MyNoteEntity> noteList = MyNoteDatabase.getMyNoteDatabase(getContext())
                    .notesDAO()
                    .getAllNotes();
            handler.post(() -> {
                if (requestCode == SHOW_NOTE) {
                    myNoteEntityList.clear();
                    myNoteEntityList.addAll(noteList);
                    myNoteAdapter.notifyDataSetChanged();
                } else if (requestCode == UPDATE_NOTE) {
                    if (clickedPosition >= 0 && clickedPosition < noteList.size()) {
                        myNoteEntityList.set(clickedPosition, noteList.get(clickedPosition));
                        myNoteAdapter.notifyItemChanged(clickedPosition);
                    }
                } else if (requestCode == REQUEST_CODE_ADD_NEW_NOTE) {
                    myNoteEntityList.clear();
                    myNoteEntityList.addAll(noteList);
                    myNoteAdapter.notifyDataSetChanged();
                }
                if (isNoteDeleted == true) {
                    myNoteEntityList.remove(clickedPosition);
                    myNoteAdapter.notifyItemRemoved(clickedPosition);
                    myNoteAdapter.notifyDataSetChanged();
                }
            });
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NEW_NOTE && resultCode == RESULT_OK) {
            getAllNotes(SHOW_NOTE, false);
        } else if (requestCode == UPDATE_NOTE && resultCode == RESULT_OK) {
            getAllNotes(UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
        }
    }

    private void addNewNote() {
        img_add_new_note.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), AddNewNotesActivity.class);
            startActivityForResult(i, REQUEST_CODE_ADD_NEW_NOTE);
        });
    }

    @Override
    public void myNoteClick(MyNoteEntity myNoteEntity, int position) {
        clickedPosition = position;
        Intent intent = new Intent(getContext(), AddNewNotesActivity.class);
        intent.putExtra("updateOrView", true);
        intent.putExtra("myNotes", myNoteEntity);
        startActivityForResult(intent, UPDATE_NOTE);
    }
}
