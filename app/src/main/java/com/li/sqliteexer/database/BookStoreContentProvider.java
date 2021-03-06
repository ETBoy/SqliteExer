package com.li.sqliteexer.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by lsx on 2016/7/30.
 */
public class BookStoreContentProvider extends ContentProvider {
    private MyDatabaseHelper mDbHelper;
    private static final UriMatcher sURI_MATCHER = buildUriMatcher();
    private static final int BOOK_DIR = 0;
    private static final int BOOK_ITEM = 1;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = BookStoreContract.AUTHORITY;
        //content://com.li.sqliteexer/book
        matcher.addURI(authority, BookStoreContract.Book.TABLE_NAME, BOOK_DIR);
        matcher.addURI(authority, BookStoreContract.Book.TABLE_NAME + "/#", BOOK_ITEM);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MyDatabaseHelper(getContext(), "bookstore.db", null, 3);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        Cursor cursor=null;
        switch (sURI_MATCHER.match(uri)){
            case BOOK_DIR:
                cursor=db.query(BookStoreContract.Book.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;
            case BOOK_ITEM:
                String bookId=uri.getPathSegments().get(1);
                cursor=db.query(BookStoreContract.Book.TABLE_NAME,projection,"_id=?",new String[]{bookId},null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("unknown uri"+uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri,ContentValues values){
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        Uri uriReturn=null;
        switch (sURI_MATCHER.match(uri)){
            case  BOOK_DIR:
                break;
            case BOOK_ITEM:
                long newBookId=db.insert(BookStoreContract.Book.TABLE_NAME,null,values);
                uriReturn=Uri.parse("content://"+BookStoreContract.AUTHORITY+"/book/"+newBookId);
                break;
            default:
        }
        return  uriReturn;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case BOOK_DIR:
                return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BookStoreContract.AUTHORITY +
                        "/" + BookStoreContract.Book.TABLE_NAME;
            case BOOK_ITEM:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BookStoreContract.AUTHORITY +
                        "/" + BookStoreContract.Book.TABLE_NAME;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int count=0;
        switch (sURI_MATCHER.match(uri)){
            case BOOK_DIR:
                count=db.delete(BookStoreContract.Book.TABLE_NAME,selection,selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId=uri.getPathSegments().get(1);
                count=db.delete(BookStoreContract.Book.TABLE_NAME,"_id=?",new String[]{bookId});
                break;
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int count=0;
        switch (sURI_MATCHER.match(uri)){
            case BOOK_DIR:
                count=db.update(BookStoreContract.Book.TABLE_NAME,values,selection,selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId=uri.getPathSegments().get(1);
                count=db.update(BookStoreContract.Book.TABLE_NAME,values,"_id=?",new String[]{bookId});
                break;
        }
        return count;
    }
}
