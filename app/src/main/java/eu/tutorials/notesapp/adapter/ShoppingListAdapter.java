package eu.tutorials.notesapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.database.MyNoteDatabase;
import eu.tutorials.notesapp.entities.ShoppingListEntity;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    Context context;
    ArrayList<ShoppingListEntity> shoppingListEntityArrayList;
    Handler handler = new Handler();

    public ShoppingListAdapter(Context context, ArrayList<ShoppingListEntity> shoppingListEntityArrayList) {
        this.context = context;
        this.shoppingListEntityArrayList = shoppingListEntityArrayList;
    }

    @NonNull
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ViewHolder holder, int position) {
        ShoppingListEntity shoppingListEntity = shoppingListEntityArrayList.get(position);
        holder.bind(shoppingListEntity);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        shoppingListEntityArrayList.remove(position);

                        MyNoteDatabase.getMyNoteDatabase(v.getContext())
                                .notesDAO()
                                .deleteShoppingList(shoppingListEntity);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                executor.shutdown();
            }
        });

    }

    @Override
    public int getItemCount() {
        return shoppingListEntityArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_item_title, txt_item_text, txt_item_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_item_title = (TextView) itemView.findViewById(R.id.txt_item_title);
            txt_item_date = itemView.findViewById(R.id.txt_item_date);
            txt_item_text = itemView.findViewById(R.id.txt_item_text);
        }

        public void bind(ShoppingListEntity shoppingListEntity) {
            txt_item_title.setText(shoppingListEntity.getTitle());
            txt_item_date.setText(shoppingListEntity.getDateTime());
            txt_item_text.setText(shoppingListEntity.getNoteText());

        }
    }
}
