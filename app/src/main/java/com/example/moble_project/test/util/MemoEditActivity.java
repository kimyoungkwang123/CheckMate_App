package com.example.moble_project.test.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moble_project.R;
import com.example.moble_project.test.util.MemoDbHelper;
import com.example.moble_project.test.login;

public class MemoEditActivity extends AppCompatActivity {

    private EditText memoEditText;
    private MemoDbHelper dbHelper;
    private long memoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        dbHelper = new MemoDbHelper(this);

        memoEditText = findViewById(R.id.editTextMemo);
        Button saveButton = findViewById(R.id.btnSave);
        Button cancelButton = findViewById(R.id.btnCancel);
        Button deleteButton = findViewById(R.id.btnDelete);

        memoId = getIntent().getLongExtra("memo_id", -1);
        if (memoId != -1) {
            // Retrieve memo content from the database based on memoId
            String memoContent = getMemoContent(memoId);
            if (memoContent != null) {
                memoEditText.setText(memoContent);
            } else {
                memoEditText.setText("해당 메모를 찾을 수 없습니다.");
                Log.i("paul", "Memo content is null.");
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemo();
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMemo(); // 메모 삭제 메서드 호출
            }
        });
    }

    private String getMemoContent(long memoId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                MemoDbHelper.COLUMN_CONTENT
        };

        String selection = MemoDbHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(memoId)};

        Cursor cursor = db.query(
                MemoDbHelper.TABLE_MEMOS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String memoContent = cursor.getString(cursor.getColumnIndexOrThrow(MemoDbHelper.COLUMN_CONTENT));
            cursor.close();
            return memoContent;
        }

        return null;
    }

    private void saveMemo() {
        String memoContent = memoEditText.getText().toString();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MemoDbHelper.COLUMN_CONTENT, memoContent);

        if (memoId != -1) {
            String selection = MemoDbHelper.COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(memoId)};
            db.update(MemoDbHelper.TABLE_MEMOS, values, selection, selectionArgs);
        } else {
            db.insert(MemoDbHelper.TABLE_MEMOS, null, values);
        }
    }

    private void deleteMemo() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 메모 ID를 기반으로 메모를 삭제합니다.
        String selection = MemoDbHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(memoId)};

        // 삭제 작업 실행
        int deletedRows = db.delete(MemoDbHelper.TABLE_MEMOS, selection, selectionArgs);

        if (deletedRows > 0) {
            // 삭제 성공
            Toast.makeText(MemoEditActivity.this, "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 화면을 닫는 예시
        } else {
            // 삭제 실패
            Toast.makeText(MemoEditActivity.this, "메모가 삭제되지 않았습니다재.", Toast.LENGTH_SHORT).show();
        }
    }
}