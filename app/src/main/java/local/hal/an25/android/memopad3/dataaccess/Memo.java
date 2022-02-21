package local.hal.an25.android.memopad3.dataaccess;

import java.sql.Timestamp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * AN25 Android追加サンプル04 ViewModelとLiveData
 *
 * メモ情報のエンティティクラス。
 *
 * @author Shinzo SAITO
 */
@Entity
public class Memo {
	/**
	 * 主キーのid。
	 */
	@PrimaryKey(autoGenerate = true)
	public int id;
	/**
	 * タイトル。
	 */
	@NonNull
	public String title;
	/**
	 * 内容。
	 */
	public String content;
	/**
	 * 重要フラグ。
	 * 1=ON、0=OFF。
	 */
	@NonNull
	@ColumnInfo(defaultValue = "0")
	public int important;
	/**
	 * 更新日時。
	 */
	@NonNull
	public Timestamp updatedAt;

	/**
	 * 重要フラグのBool値を取得するメソッド。
	 *
	 * @return 重要な場合はtrue、そうでない場合はfalse。
	 */
	public boolean getImportantBool() {
		boolean importantBool = false;
		if(important == 1) {
			importantBool = true;
		}
		return importantBool;
	}
}
