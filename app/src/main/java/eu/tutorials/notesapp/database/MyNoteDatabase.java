package eu.tutorials.notesapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import eu.tutorials.notesapp.dao.MyNotesDAO;
import eu.tutorials.notesapp.entities.MyNoteEntity;
import eu.tutorials.notesapp.entities.ReminderEntity;
import eu.tutorials.notesapp.entities.ShoppingListEntity;

@Database(entities = {MyNoteEntity.class, ReminderEntity.class, ShoppingListEntity.class}, version = 1)
public abstract class MyNoteDatabase extends RoomDatabase {
    private static MyNoteDatabase myNoteDatabase;

    public static synchronized MyNoteDatabase getMyNoteDatabase(Context context) {
        if (myNoteDatabase == null) {
            myNoteDatabase = Room.databaseBuilder(
                    context,
                    MyNoteDatabase.class,
                    "note_db"
            ).build();
        }
        return myNoteDatabase;
    }

    public abstract MyNotesDAO notesDAO();
}
