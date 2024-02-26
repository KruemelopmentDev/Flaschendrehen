package org.kruemelopment.de.flaschendrehen

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, Database_Name, null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table $Table_Name (ID INTEGER PRIMARY KEY AUTOINCREMENT, Aufgabe TEXT,Art TEXT)")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $Table_Name")
    }

    fun insertData(aufgabe: String?, art: String?): Boolean {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Aufgabe", aufgabe)
        contentValues.put("Art", art)
        val result = database.insert(Table_Name, null, contentValues)
        database.close()
        return result != -1L
    }

    val allData: Cursor
        get() {
            val sqLiteDatabase = this.writableDatabase
            return sqLiteDatabase.rawQuery("Select * from $Table_Name", null)
        }

    fun deleteData(id: String?) {
        val db = this.writableDatabase
        db.delete(Table_Name, "ID=?", arrayOf(id))
    }

    fun updateData(id: String?, aufgabe: String?, art: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("Aufgabe", aufgabe)
        contentValues.put("Art", art)
        val result = db.update(Table_Name, contentValues, "ID=?", arrayOf(id))
        return result > 0
    }

    fun deletespecified(string: String) {
        val db = this.writableDatabase
        db.delete(Table_Name, "Art=?", arrayOf(string))
    }

    fun isEmpty(type: String): Boolean {
        var empty = true
        val db = readableDatabase
        val res = db.rawQuery("Select * from $Table_Name", null)
        if (res != null && res.count > 0) {
            while (res.moveToNext()) {
                if (res.getString(2) == type) {
                    empty = false
                }
            }
        }
        res.close()
        db.close()
        return empty
    }

    companion object {
        private const val Database_Name = "Aufgaben.db"
        private const val Table_Name = "default_table"
    }
}
