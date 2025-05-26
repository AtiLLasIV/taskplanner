package com.hwmipt.planner.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_DESCRIPTION TEXT,
                $COL_IS_DONE INTEGER DEFAULT 0,
                $COL_DEADLINE INTEGER,
                $COL_URGENCY INTEGER,
                $COL_TAGS TEXT
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //
    }

    companion object {
        const val DB_NAME = "storage.db"
        const val DB_VERSION = 1

        const val TABLE_NAME = "tasks"

        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DESCRIPTION = "description"
        const val COL_IS_DONE = "is_done"
        const val COL_DEADLINE = "deadline"
        const val COL_URGENCY = "urgency"
        const val COL_TAGS = "tags"
    }
}