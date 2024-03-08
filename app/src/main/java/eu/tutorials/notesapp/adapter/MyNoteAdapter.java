package eu.tutorials.notesapp.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import eu.tutorials.notesapp.R;
import eu.tutorials.notesapp.entities.MyNoteEntity;
import eu.tutorials.notesapp.entities.ShoppingListEntity;
import eu.tutorials.notesapp.listeners.MyNoteListeners;

public class MyNoteAdapter extends RecyclerView.Adapter<MyNoteAdapter.ViewHolder> {

    Context context;
    ArrayList<MyNoteEntity> myNoteEntityArrayList;

    MyNoteListeners myNoteListeners;

    ArrayList<MyNoteEntity> notesSearch = new ArrayList<>();
    Timer timer;
    ArrayList<MyNoteEntity> searchResults = new ArrayList<>(); // Danh sách kết quả tìm kiếm


    public MyNoteAdapter(Context context, ArrayList<MyNoteEntity> myNoteEntityArrayList, MyNoteListeners myNoteListeners) {
        this.context = context;
        this.myNoteEntityArrayList = myNoteEntityArrayList;
        this.myNoteListeners = myNoteListeners;
        this.notesSearch = myNoteEntityArrayList; // Tạo một bản sao của danh sách myNoteEntityArrayList
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int itemPosition = position; // Tạo biến final itemPosition và gán giá trị position vào đó
        MyNoteEntity myNoteEntity = myNoteEntityArrayList.get(position);
        holder.bind(myNoteEntity);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myNoteListeners.myNoteClick(myNoteEntityArrayList.get(itemPosition), itemPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myNoteEntityArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img_item;
        TextView txt_title_item, txt_note_item, txt_date_time_item;
        LinearLayout layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_item = itemView.findViewById(R.id.img_item);
            txt_title_item = itemView.findViewById(R.id.txt_title_item);
            txt_note_item = itemView.findViewById(R.id.txt_note_item);
            txt_date_time_item = itemView.findViewById(R.id.txt_date_time_item);
            layout = itemView.findViewById(R.id.layout_note);
        }

        public void bind(MyNoteEntity myNoteEntity) {
            txt_title_item.setText(myNoteEntity.getTitle());
            txt_note_item.setText(myNoteEntity.getNoteText());
            txt_date_time_item.setText(myNoteEntity.getDateTime());
            Glide.with(context).load(myNoteEntity.getImagePath()).into(img_item);

                // Giúp thay đổi màu nền của một drawable.
            GradientDrawable gradientDrawable = (GradientDrawable) layout.getBackground();
            if (myNoteEntity.getNoteText() != null) {
                // Nếu myNoteEntity có nội dung thì lấy giá trị màu kiểu String và ép về kiểu color.
                gradientDrawable.setColor(Color.parseColor(myNoteEntity.getColor()));
            } else {
                // Ngược lại thì dùng màu đỏ.
                gradientDrawable.setColor(Color.parseColor("#F44336"));
            }
            if (myNoteEntity.getImagePath() != null) {
                img_item.setImageBitmap(BitmapFactory.decodeFile(myNoteEntity.getImagePath()));
                img_item.setVisibility(View.VISIBLE);
            } else {
                img_item.setVisibility(View.GONE);
            }
        }
    }

    public void searchNotes(final String searchNotes) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("DDD", notesSearch.toString());
                ArrayList<MyNoteEntity> temp = new ArrayList<>();
                temp.clear();
                if (searchNotes.trim().isEmpty()) {
                    // Nếu chuỗi tìm kiếm rỗng, thì danh sách tạm thời sẽ giống danh sách gốc
                    temp.addAll(notesSearch);
                    Log.e("DDDD", temp.toString());

                } else {
                    // Chuyển đổi chuỗi tìm kiếm sang chữ thường để tìm kiếm không phân biệt hoa thường
                    String lowercaseSearch = searchNotes.toLowerCase();

                    // Duyệt qua danh sách gốc và tìm kiếm các mục phù hợp
                    for (MyNoteEntity note : notesSearch) {
                        if (note.getTitle().toLowerCase().contains(lowercaseSearch) || note.getNoteText().toLowerCase().contains(lowercaseSearch)) {
                            temp.add(note);
                        }
                    }
                }

                // Chạy trên luồng giao diện để cập nhật danh sách hiển thị
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Gán lại danh sách hiển thị với danh sách tạm thời
                        myNoteEntityArrayList.clear();
                        myNoteEntityArrayList.addAll(temp);
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

}
