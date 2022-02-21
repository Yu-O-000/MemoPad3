package local.hal.an25.android.memopad3.dataaccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * AN25 Android追加サンプル04 ViewModelとLiveData
 *
 * データベースオブジェクトクラス。
 *
 * @author Shinzo SAITO
 */
@Database(entities = {Memo.class}, version = 1)
@TypeConverters({TimestampConverter.class})
public abstract class AppDatabase extends RoomDatabase {
	/**
	 * データベースインスタンス。
	 */
	private static AppDatabase _instance;

	/**
	 * データベースインスタンスを得るメソッド。
	 * シングルトンパターンに従ってインスタンスを生成する。
	 *
	 * @param context コンテキスト。
	 * @return データベースインスタンス。
	 */
	public static AppDatabase getDatabase(Context context) {
		if (_instance == null) {
			_instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "memo_db").build();
		}
		return _instance;
	}

	/**
	 * MemoDAOオブジェクトを生成するメソッド。
	 *
	 * @return MemoDAOオブジェクト。
	 */
	public abstract MemoDAO createMemoDAO();
}
