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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.activities.AddNewShoppingListActivity;
import eu.tutorials.notesapp.adapter.MyNoteAdapter;
import eu.tutorials.notesapp.adapter.ShoppingListAdapter;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.ReminderEntity;
import eu.tutorials.notesapp.entities.ShoppingListEntity;

public class ShoppingListFragment extends Fragment {

    ImageView img_add_new_shopping_list;
    public static final int REQUEST_CODE_ADD_NEW_NOTE = 1;

    RecyclerView rec_list;
    ArrayList<ShoppingListEntity> shoppingListEntityArrayList = new ArrayList<>();
    ShoppingListAdapter shoppingListAdapter;

    Handler handler = new Handler();

    public ShoppingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        img_add_new_shopping_list = view.findViewById(R.id.img_add_new_shopping_list);
        rec_list = view.findViewById(R.id.rec_list);
        addNewShoppingList();
        rec_list.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        shoppingListAdapter = new ShoppingListAdapter(getContext(), shoppingListEntityArrayList);
        rec_list.setNestedScrollingEnabled(true);
        rec_list.setAdapter(shoppingListAdapter);
        getAllShoppingList();
        return view;

    }
    private void getAllShoppingList() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<ShoppingListEntity> shoppingListEntities = MyNoteDatabase.getMyNoteDatabase(getContext())
                    .notesDAO()
                    .getAllShoppingList();
            handler.post(() -> {
                shoppingListEntityArrayList.clear();
                shoppingListEntityArrayList.addAll(shoppingListEntities);
                shoppingListAdapter.notifyDataSetChanged();
            });
        });
    }

    private void addNewShoppingList() {
        img_add_new_shopping_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddNewShoppingListActivity.class);
                startActivityForResult(i, REQUEST_CODE_ADD_NEW_NOTE);

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NEW_NOTE && resultCode == RESULT_OK) {
            getAllShoppingList();
        }
    }
}