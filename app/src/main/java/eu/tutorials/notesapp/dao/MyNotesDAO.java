package eu.tutorials.notesapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import eu.tutorials.notesapp.entities.MyNoteEntity;
import eu.tutorials.notesapp.entities.ReminderEntity;
import eu.tutorials.notesapp.entities.ShoppingListEntity;

@Dao
public interface MyNotesDAO {

    //ORDER BY id DESC: Sắp xếp kết quả theo trường "id" theo thứ tự giảm dần (DESC).
    @Query("SELECT * FROM note ORDER BY id DESC")
    List<MyNoteEntity> getAllNotes();

    //onConflict = OnConflictStrategy.REPLACE xác định rằng nếu có xung đột khi chèn dữ liệu vào cơ sở dữ liệu
    // (ví dụ: dòng đã tồn tại với cùng khóa chính), dữ liệu cũ sẽ bị thay thế bằng dữ liệu mới.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(MyNoteEntity myNoteEntity);

    @Delete
    void deleteNote(MyNoteEntity myNoteEntity);


    //------------------------------------------------Reminder------------------------------------------------

    //ORDER BY id DESC: Sắp xếp kết quả theo trường "id" theo thứ tự giảm dần (DESC).
    @Query("SELECT * FROM reminder ORDER BY id DESC")
    List<ReminderEntity> getAllReminder();

    //onConflict = OnConflictStrategy.REPLACE xác định rằng nếu có xung đột khi chèn dữ liệu vào cơ sở dữ liệu
    // (ví dụ: dòng đã tồn tại với cùng khóa chính), dữ liệu cũ sẽ bị thay thế bằng dữ liệu mới.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReminder(ReminderEntity reminderEntity);

    @Delete
    void deleteReminder(ReminderEntity reminderEntity);

    //------------------------------------------------SHOPPING LIST------------------------------------------------

    //ORDER BY id DESC: Sắp xếp kết quả theo trường "id" theo thứ tự giảm dần (DESC).
    @Query("SELECT * FROM shopping_list ORDER BY id DESC")
    List<ShoppingListEntity> getAllShoppingList();

    //onConflict = OnConflictStrategy.REPLACE xác định rằng nếu có xung đột khi chèn dữ liệu vào cơ sở dữ liệu
    // (ví dụ: dòng đã tồn tại với cùng khóa chính), dữ liệu cũ sẽ bị thay thế bằng dữ liệu mới.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShoppingList(ShoppingListEntity shoppingListEntity);

    @Delete
    void deleteShoppingList(ShoppingListEntity shoppingListEntity);
}
