package com.me.hatem.a03_kt_note.Services

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.me.hatem.a03_kt_note.Model.Note
import com.me.hatem.a03_kt_note.Model.Tag
import com.me.hatem.a03_kt_note.Utilises.NOTE_TABLE

class DBManager(val context: Context, val tableName: String = NOTE_TABLE) {

   private val DB_Read  = SQLiteHelper().readableDatabase
   private val DB_Write = SQLiteHelper().writableDatabase

    private object FeedEntry  : BaseColumns {
        const val NOTES_TABLE_NAME      = "tblNotes"
        const val TAGS_TABLE_NAME       = "tblTags"
        const val COLUMN_NAME_ID        = "_id" // common
        const val COLUMN_NAME_TITLE     = "title" // common
        const val COLUMN_NAME_CONTENT   = "content" // only for notes table
        const val COLUMN_NAME_TAGS      = "tags" // only for notes table
        const val COLUMN_NAME_DATE      = "date" // only for notes table
        const val COLUMN_NAME_DESC      = "description" // only for tags table

    }
    private object SetupDB {
        const val DB_NAME               = "data"
        const val DB_VERSION            = 1
    }

    private object Query {
        val SQL_CREATE_NOTES_ENTRIES = "CREATE TABLE IF NOT EXISTS ${FeedEntry.NOTES_TABLE_NAME} " +
                "(${FeedEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${FeedEntry.COLUMN_NAME_TITLE} TEXT," +
                "${FeedEntry.COLUMN_NAME_CONTENT} TEXT,"+
                "${FeedEntry.COLUMN_NAME_DATE} TEXT," +
                "${FeedEntry.COLUMN_NAME_TAGS} TEXT);"
        const val SQL_DELETE_NOTES_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.NOTES_TABLE_NAME};"

        val SQL_CREATE_TAGS_ENTRIES = "CREATE TABLE IF NOT EXISTS ${FeedEntry.TAGS_TABLE_NAME} " +
                "(${FeedEntry.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${FeedEntry.COLUMN_NAME_TITLE} TEXT," +
                "${FeedEntry.COLUMN_NAME_DESC} TEXT)"
        const val SQL_DELETE_TAGS_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TAGS_TABLE_NAME}"
    }



    fun insertNote(note: Note): Long? {
        val values = ContentValues().apply {
            //put("_id", null)
            put(FeedEntry.COLUMN_NAME_TITLE, note.title)
            put(FeedEntry.COLUMN_NAME_CONTENT, note.content)
            put(FeedEntry.COLUMN_NAME_DATE, note.date)
            put(FeedEntry.COLUMN_NAME_TAGS, note.tags)
        }
        val newRowId = DB_Write?.insert(FeedEntry.NOTES_TABLE_NAME, null, values)
        return newRowId
    }

    fun updateNote(note: Note){
        val values = ContentValues().apply {
            //put("_id", null)
            put(FeedEntry.COLUMN_NAME_TITLE, note.title)
            put(FeedEntry.COLUMN_NAME_CONTENT, note.content)
            put(FeedEntry.COLUMN_NAME_DATE, note.date)
            put(FeedEntry.COLUMN_NAME_TAGS, note.tags)
        }
        DB_Write?.update(
                FeedEntry.NOTES_TABLE_NAME,
                values,
                "_id = ?",
                arrayOf(note._id.toString())
        )
    }

    fun deleteNote(_id: String) {
        DB_Write?.delete(
                FeedEntry.NOTES_TABLE_NAME,
                "_id = ?",
                arrayOf(_id)
        )
    }

    fun getNotes(search: String = "%", sortOrder: String? = null): ArrayList<Note> {
        val notes = ArrayList<Note>()
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
                FeedEntry.COLUMN_NAME_ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_CONTENT,
                FeedEntry.COLUMN_NAME_DATE,
                FeedEntry.COLUMN_NAME_TAGS)
        // Filter results WHERE "title" = 'My Title'
        val selection = "${FeedEntry.COLUMN_NAME_TITLE} like ? " +
                "OR ${FeedEntry.COLUMN_NAME_CONTENT} like ?" +
                "OR ${FeedEntry.COLUMN_NAME_TAGS} like ?" +
                "OR ${FeedEntry.COLUMN_NAME_DATE} like ?"
        val selectionArgs = arrayOf(search, search, search, search)

        val cursor = DB_Read.query(
                FeedEntry.NOTES_TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
        null,                   // don't group the rows
            null,                // don't filter by row groups
                sortOrder                    // The sort order ; Like filter
        )
        //It like a callback function for any asynchronous task
        with(cursor){
            while (moveToNext()) {
                val _id        = getInt(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_ID))
                val title   = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE))
                val content = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_CONTENT))
                val date    = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DATE))
                val tags    = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TAGS))
                val note    = Note(_id, title, content, date, tags)
                notes.add(note)
            }
        }
        return notes
    }

    //Functions for tags table
    fun insertTag(tag: Tag): Long? {
        val values = ContentValues().apply {
            put(FeedEntry.COLUMN_NAME_TITLE, tag.title)
            put(FeedEntry.COLUMN_NAME_DESC, tag.description)
        }

        val newRowId = DB_Write?.insert(FeedEntry.TAGS_TABLE_NAME, null, values)
        return newRowId
    }

    fun getAllTags(): ArrayList<Tag> {
        val tags = ArrayList<Tag>()

        val projection = arrayOf(
                FeedEntry.COLUMN_NAME_ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_DESC
        )
        val selection = "${FeedEntry.COLUMN_NAME_TITLE} like ?"
        val selectionArgs = arrayOf("%")
        val sortOrder = null
        val cursor = DB_Read.query(
                FeedEntry.TAGS_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        )
        with(cursor) {
            while (moveToNext()) {
                val _id      = getInt(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_ID))
                val title = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_TITLE))
                val desc  = getString(getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_DESC))
                val tag = Tag(_id, title, desc)
                tags.add(tag)
            }
        }
        return tags
    }

    // Database connection class
    private inner class SQLiteHelper() : SQLiteOpenHelper(context, SetupDB.DB_NAME, null, SetupDB.DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(Query.SQL_CREATE_NOTES_ENTRIES)
            db?.execSQL(Query.SQL_CREATE_TAGS_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL(
                    Query.SQL_DELETE_NOTES_ENTRIES
            )
            db?.execSQL( Query.SQL_DELETE_TAGS_ENTRIES)
            onCreate(db)
        }
    }

}