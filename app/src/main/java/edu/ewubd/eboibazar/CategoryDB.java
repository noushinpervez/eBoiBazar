package edu.ewubd.eboibazar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class CategoryDB extends SQLiteOpenHelper {

    public CategoryDB(Context context) {
        super(context, "CategoryDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE categories (" + "id TEXT PRIMARY KEY," + "category TEXT" + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addCategory(String key, Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT id FROM categories WHERE id=?", new String[]{key});

        if (cur.getCount() > 0) {
            cur.close();
            db.close();
            return;
        }

        ContentValues cols = new ContentValues();
        cols.put("id", key);
        cols.put("category", category.getCategory());

        db.insert("categories", null, cols);
        db.close();
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        String q = "SELECT * FROM categories";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = null;

        try {
            cur = db.rawQuery(q, null);
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex("id"));
                String category = cur.getString(cur.getColumnIndex("category"));
                Category categoryObj = new Category(id, category);
                categories.add(categoryObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cur != null) cur.close();
        db.close();

        return categories;
    }
}