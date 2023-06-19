package com.example.vkrmain.db

object AppSQLiteConstants {

    object CategoryTable {
        const val TABLE_NAME = "Category"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_MAX_COUNT_REPEAT = "max_count_repeat"
        const val COLUMN_SYSTEM = "system"
    }

    object BlockTable {
        const val TABLE_NAME = "Block"
        const val COLUMN_ID = "id"
        const val COLUMN_QUESTION = "question"
        const val COLUMN_ANSWER = "answer"
        const val COLUMN_COUNT_REPEAT = "count_repeat"
        const val COLUMN_DATE_REPEAT = "date_repeat"
        const val COLUMN_CATEGORY_ID = "Category_id"
    }

}