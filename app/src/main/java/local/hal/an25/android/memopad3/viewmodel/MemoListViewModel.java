package local.hal.an25.android.memopad3.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import local.hal.an25.android.memopad3.dataaccess.AppDatabase;
import local.hal.an25.android.memopad3.dataaccess.Memo;
import local.hal.an25.android.memopad3.dataaccess.MemoDAO;

/**
 * AN25 Android追加サンプル04 ViewModelとLiveData
 *
 * メモ情報リストに関するデータを管理するビューモデルクラス。
 *
 * @author Shinzo SAITO
 */
public class MemoListViewModel extends AndroidViewModel {
    /**
     * データベースオブジェクト。
     */
    private AppDatabase _db;

    /**
     * コンストラクタ。
     *
     * @param application アプリケーションオブジェクト。
     */
    public MemoListViewModel(Application application) {
        super(application);
        _db = AppDatabase.getDatabase(application);
    }

    /**
     * メモ情報リストを取得するメソッド。
     *
     * @param onlyImportant 重要メモ情報のみに絞り込むかどうかの値。trueならば絞り込む。
     * @return メモ情報リストに関連するLiveDateオブジェクト。
     */
    public LiveData<List<Memo>> getMemoList(boolean onlyImportant) {
        MemoDAO memoDAO = _db.createMemoDAO();
        LiveData<List<Memo>> memoList;

        if (onlyImportant) {
            memoList = memoDAO.findAllImportant();

        } else {
            memoList = memoDAO.findAll();
        }
        return memoList;
    }
}
