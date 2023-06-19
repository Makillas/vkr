package com.example.vkrmain.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.vkrmain.db.AppSQLiteConstants.BlockTable
import com.example.vkrmain.db.AppSQLiteConstants.CategoryTable

class AppSQLiteManager (context: Context){
    val appSQLiteHelper = AppSQLiteHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){
        db = appSQLiteHelper.writableDatabase
    }

    fun readDbDataCategories(): ArrayList<Categories> {
        val dataList = ArrayList<Categories>()
        val cursor = db?.query(
            CategoryTable.TABLE_NAME,
            null, null,null,null,null,null
        )
        while (cursor?.moveToNext()!!) {
            val categories = Categories(
                cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_MAX_COUNT_REPEAT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_SYSTEM))
                )
            dataList.add(categories)
        }
        cursor.close()
        return dataList
    }

    fun readDbDataBlocks(id: Int): ArrayList<Blocks> {
        val dataList = ArrayList<Blocks>()
        val selection = BlockTable.COLUMN_CATEGORY_ID + "=$id"
        val cursor = db?.query(
            BlockTable.TABLE_NAME,
            null, selection,null,null,null,null
        )
        while (cursor?.moveToNext()!!) {
            val blocks = Blocks(
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_QUESTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_ANSWER)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_COUNT_REPEAT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_DATE_REPEAT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_CATEGORY_ID))
            )
            dataList.add(blocks)
        }
        cursor.close()
        return dataList
    }

    fun readDbDataBlocksElement(): ArrayList<Blocks> {
        val dataList = ArrayList<Blocks>()
        val cursor = db?.query(
            BlockTable.TABLE_NAME,
            null, null,null,null,null,null
        )
        while (cursor?.moveToNext()!!) {
            val blocks = Blocks(
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_QUESTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_ANSWER)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_COUNT_REPEAT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_DATE_REPEAT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_CATEGORY_ID))
            )
            dataList.add(blocks)
        }
        cursor.close()
        return dataList
    }

    fun readDbDataBlocksStudy(): ArrayList<Blocks> {
        val dataList = ArrayList<Blocks>()
        val query = "SELECT Block.*" +
                " FROM Block" +
                " INNER JOIN Category" +
                " ON Block.count_repeat < Category.max_count_repeat " +
                " AND Block.Category_id = Category.id " +
                " AND Block.date_repeat < " + System.currentTimeMillis() / 1000 +
                " AND Block.count_repeat > 0"
        val cursor = db?.rawQuery(query,null)
        while (cursor?.moveToNext()!!) {
            val blocks = Blocks(
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_QUESTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_ANSWER)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_COUNT_REPEAT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_DATE_REPEAT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_CATEGORY_ID))
            )
            dataList.add(blocks)
        }
        cursor.close()
        return dataList
    }

    fun searchCategoryCountRepeat(id: Int): Int {
        val selection = CategoryTable.COLUMN_ID + "=$id"
        var countRepeatMax = -1
        val cursor = db?.query(
            CategoryTable.TABLE_NAME,
            null, selection,null,null,null,null
        )
        while (cursor?.moveToNext()!!) {
            countRepeatMax = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_MAX_COUNT_REPEAT))
        }
        cursor.close()
        return countRepeatMax
    }

    fun searchBlockCountRepeat(id: Int): Int {
        val selection = BlockTable.COLUMN_ID + "=$id"
        var countRepeat = -1
        val cursor = db?.query(
            BlockTable.TABLE_NAME,
            null, selection,null,null,null,null
        )
        while (cursor?.moveToNext()!!) {
            countRepeat = cursor.getInt(cursor.getColumnIndexOrThrow(BlockTable.COLUMN_COUNT_REPEAT))
        }
        cursor.close()
        return countRepeat
    }

    fun insertToDbCategories(name: String, maxCountRepeat: String){
        val values = ContentValues().apply {
            put(CategoryTable.COLUMN_NAME, name)
            put(CategoryTable.COLUMN_SYSTEM, 0)
            put(CategoryTable.COLUMN_MAX_COUNT_REPEAT, maxCountRepeat)
        }
        db?.insert(CategoryTable.TABLE_NAME, null, values)
    }

    fun insertToDbBlock(question: String, answer: String, category_id: Int){
        val values = ContentValues().apply {
            put(BlockTable.COLUMN_QUESTION, question)
            put(BlockTable.COLUMN_ANSWER, answer)
            put(BlockTable.COLUMN_COUNT_REPEAT, 0)
            put(BlockTable.COLUMN_DATE_REPEAT, System.currentTimeMillis() / 1000)
            put(BlockTable.COLUMN_CATEGORY_ID, category_id)
        }
        db?.insert(BlockTable.TABLE_NAME, null, values)
    }

    fun updateItemCategories(id: Int, name: String, categorySystem: Int, maxCountRepeat: String){
        val selection = CategoryTable.COLUMN_ID + "=$id"
        val values = ContentValues().apply {
            put(CategoryTable.COLUMN_NAME, name)
            put(CategoryTable.COLUMN_SYSTEM, categorySystem)
            put(CategoryTable.COLUMN_MAX_COUNT_REPEAT, maxCountRepeat)
        }
        db?.update(CategoryTable.TABLE_NAME, values, selection, null)
    }

    fun updateCountRepeatInCategory(category_id: Int){
        val selection = BlockTable.COLUMN_CATEGORY_ID + "=$category_id"
        val values = ContentValues().apply {
            put(BlockTable.COLUMN_COUNT_REPEAT, 0)
        }
        db?.update(BlockTable.TABLE_NAME, values, selection, null)
    }

    fun updateItemBlocks(id: Int, COUNT_REPEAT: Int){
        val flag = if(COUNT_REPEAT == 0)
            0
        else
            3600
        val selection = BlockTable.COLUMN_ID + "=$id"
        val values = ContentValues().apply {
            put(BlockTable.COLUMN_COUNT_REPEAT, COUNT_REPEAT)
            put(BlockTable.COLUMN_DATE_REPEAT, (System.currentTimeMillis() / 1000) + flag)
        }
        db?.update(BlockTable.TABLE_NAME, values, selection, null)
    }

    fun updateItemBlocksStudy(id: Int, countRepeat: Int, plusUnixTime: Long){
        val selection = BlockTable.COLUMN_ID + "=$id"
        val values = ContentValues().apply {
            put(BlockTable.COLUMN_COUNT_REPEAT, countRepeat)
            put(BlockTable.COLUMN_DATE_REPEAT, plusUnixTime)
        }
        db?.update(BlockTable.TABLE_NAME, values, selection, null)
    }

    fun updateItemBlocks(id: Int, question: String, answer: String){
        val selection = BlockTable.COLUMN_ID + "=$id"
        val values = ContentValues().apply {
            put(BlockTable.COLUMN_QUESTION, question)
            put(BlockTable.COLUMN_ANSWER, answer)
            put(BlockTable.COLUMN_DATE_REPEAT, System.currentTimeMillis() / 1000)
            put(BlockTable.COLUMN_COUNT_REPEAT, 0)
        }
        db?.update(BlockTable.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id : Int){
        val selection = CategoryTable.COLUMN_ID + "=$id"
        db?.delete(CategoryTable.TABLE_NAME, selection, null)
    }

    fun removeItemFromDbBlock(id : Int){
        val selection = BlockTable.COLUMN_ID + "=$id"
        db?.delete(BlockTable.TABLE_NAME, selection, null)
    }

    fun closeDb(){
        appSQLiteHelper.close()
    }


}