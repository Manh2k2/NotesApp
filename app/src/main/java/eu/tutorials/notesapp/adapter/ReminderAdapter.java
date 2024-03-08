package eu.tutorials.notesapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import eu.tutorials.notesapp.entities.ReminderEntity;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    Context context;
    ArrayList<ReminderEntity> reminderEntityArrayList;
    Handler handler = new Handler();

    public ReminderAdapter(Context context, ArrayList<ReminderEntity> reminderEntityArrayList) {
        this.context = context;
        this.reminderEntityArrayList = reminderEntityArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReminderEntity reminderEntity = reminderEntityArrayList.get(position);
        holder.bind(reminderEntity);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        reminderEntityArrayList.remove(position);
                        MyNoteDatabase.getMyNoteDatabase(v.getContext())
                                .notesDAO()
                                .deleteReminder(reminderEntity);
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
        return reminderEntityArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title_item_reminder, txt_date_time_item_reminder;
        View view_item_reminder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title_item_reminder = itemView.findViewById(R.id.txt_title_item_reminder);
            txt_date_time_item_reminder = itemView.findViewById(R.id.txt_date_time_item_reminder);
            view_item_reminder = itemView.findViewById(R.id.view_item_reminder);

        }

        public void bind(ReminderEntity reminderEntity) {
            txt_title_item_reminder.setText(reminderEntity.getTitle());
            txt_date_time_item_reminder.setText(reminderEntity.getDateTime());
            // Giúp thay đổi màu nền của một drawable.
            GradientDrawable gradientDrawable = (GradientDrawable) view_item_reminder.getBackground();
            if (reminderEntity.getTitle() != null) {
                // Nếu myNoteEntity có nội dung thì lấy giá trị màu kiểu String và ép về kiểu color.
                gradientDrawable.setColor(Color.parseColor(reminderEntity.getColor()));
            } else {
                // Ngược lại thì dùng màu đỏ.
                gradientDrawable.setColor(Color.parseColor("#F44336"));
            }
        }
    }
}
