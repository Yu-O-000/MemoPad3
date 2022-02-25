package local.hal.an25.android.memopad3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import local.hal.an25.android.memopad3.dataaccess.Memo;
import local.hal.an25.android.memopad3.viewmodel.MemoListViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * AN25 Android追加サンプル04 ViewModelとLiveData
 *
 * リスト画面表示用アクティビティクラス。
 *
 * @author Shinzo SAITO
 */
public class MainActivity extends AppCompatActivity {
	/**
	 * リサイクラービューを表すフィールド。
	 */
	private RecyclerView _rvMemo;
	/**
	 * リサイクラービューで利用するアダプタオブジェクト。
	 */
	private MemoListAdapter _adapter;
	/**
	 * 重要メモ情報リストのみに絞り込むかどうかを表すフィールド。trueの場合は絞り込む。
	 */
	private boolean _onlyImportant = false;
	/**
	 * メモ情報リストビューモデルオブジェクト。
	 */
	private MemoListViewModel _memoListViewModel;
	/**
	 * メモ情報リストLiveDataオブジェクト。
	 */
	private LiveData<List<Memo>> _memoListLiveData;
	/**
	 * メモ情報リストLiveData変更時に対応するオブザーバーオブジェクト。
	 */
	private MemoListObserver _memoListObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// RecyclerViewの設定
		_rvMemo = findViewById(R.id.rvMemo);
		LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
		_rvMemo.setLayoutManager(layout);
		DividerItemDecoration decoration = new DividerItemDecoration(MainActivity.this, layout.getOrientation());
		_rvMemo.addItemDecoration(decoration);
		// 空Listのset
		List<Memo> memoList = new ArrayList<>();
		_adapter = new MemoListAdapter(memoList);
		_rvMemo.setAdapter(_adapter);

		// ViewModel準備
		ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
			/* AndroidViewModel生成用のFactoryを取得 */
		ViewModelProvider provider = new ViewModelProvider(MainActivity.this, factory);
			/* ViewModelを継承したものと違い、factoryを用いてproviderを作成。 */
		_memoListViewModel = provider.get(MemoListViewModel.class);

		_memoListObserver = new MemoListObserver();		// List内に変更があった場合にViewを更新
		_memoListLiveData = new MutableLiveData<>();	// 値が割り当てられていないMutableLiveDataを作成

		createRecyclerView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_options_list, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuTitle = menu.findItem(R.id.menuTitle);
		menuTitle.setTitle(R.string.menu_list_all);
		if(_onlyImportant) {
			menuTitle.setTitle(R.string.menu_list_important);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		boolean returnVal = true;
		switch(id) {
			case R.id.menuListImportant:
				_onlyImportant = true;
				break;
			case R.id.menuListAll:
				_onlyImportant = false;
				break;
			default:
				returnVal = super.onOptionsItemSelected(item);
		}
		if(returnVal) {
			invalidateOptionsMenu();
			createRecyclerView();
		}
		return returnVal;
	}

	/**
	 * 新規ボタンが押されたときのイベント処理用メソッド。
	 *
	 * @param view 画面部品。
	 */
	public void onNewButtonClick(View view) {
		Intent intent = new Intent(getApplicationContext(), MemoEditActivity.class);
		intent.putExtra("mode", Consts.MODE_INSERT);
		startActivity(intent);
	}

	/**
	 * リスト画面表示用のデータを生成するメソッド。
	 * フィールド_onlyImportantの値に合わせて生成するデータを切り替える。
	 */
	private void createRecyclerView() {
		Log.d("__DEBUG__", "[RUN]: createRecyclerView");

		_memoListLiveData.removeObserver(_memoListObserver);
		_memoListLiveData = _memoListViewModel.getMemoList(_onlyImportant);
		_memoListLiveData.observe(MainActivity.this, _memoListObserver);
			/*
				observe = addObserver

				Q. なぜ一度removeする？
					A. 重要の絞り込み可否でLiveDataが違うものになってしまうため、自動更新ができない。
						そのため、毎度LiveDataを作り直してObserverをセットしてる。
						LiveDataが一つの場合はremove, observeは１回のみ。
			 */
	}

	/**
	 * ビューモデル中のメモ情報リストに変更があった際に、画面の更新を行う処理が記述されたクラス。
	 */
	private class MemoListObserver implements Observer<List<Memo>> {
		/**
		 * どこかの通知メソッドを実行した際に実行される。
		 * 	Q. もとはどこ？
		 *		A. LiveData上
		 * @param memoList	List[Memo]: 更新対象リスト
		 */
		@Override
		public void onChanged(List<Memo> memoList) {
			Log.d("__DEBUG__", "[RUN]: onChanged");

			_adapter.changeMemoList(memoList);
		}
	}

	/**
	 * リサイクラービューで利用するビューホルダクラス。
	 */
	private class MemoViewHolder extends RecyclerView.ViewHolder {
		/**
		 * メモタイトル表示用TextViewフィールド。
		 */
		public TextView _tvTitleRow;
		/**
		 * 重要マーク表示用ImageViewフィールド。
		 */
		public ImageView _imStarRow;

		/**
		 * コンストラクタ。
		 *
		 * @param itemView リスト1行分の画面部品。
		 */
		public MemoViewHolder(View itemView) {
			super(itemView);
			_tvTitleRow = itemView.findViewById(R.id.tvTitleRow);
			_imStarRow = itemView.findViewById(R.id.imStarRow);
		}
	}

	/**
	 * リサイクラービューで利用するアダプタクラス。
	 */
	private class MemoListAdapter extends RecyclerView.Adapter<MemoViewHolder> {
		/**
		 * リストデータを表すフィールド。
		 */
		private List<Memo> _listData;

		/**
		 * コンストラクタ。
		 *
		 * @param listData リストデータ。
		 */
		public MemoListAdapter(List<Memo> listData) {
			_listData = listData;
		}

		@Override
		public MemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
			View row = inflater.inflate(R.layout.row_activity_main, parent, false);
			row.setOnClickListener(new ListItemClickListener());
			MemoViewHolder holder = new MemoViewHolder(row);
			return holder;
		}

		@Override
		public void onBindViewHolder(MemoViewHolder holder, int position) {
			Memo item = _listData.get(position);
			holder._tvTitleRow.setTag(item.id);
			holder._tvTitleRow.setText(item.title);
			holder._imStarRow.setVisibility(View.INVISIBLE);
			if(item.getImportantBool()) {
				holder._imStarRow.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public int getItemCount() {
			return _listData.size();
		}

		/**
		 * 内部で保持しているリストデータを丸ごと入れ替えるメソッド。
		 *
		 * @param listData 新しいリストデータ。
		 */
		public void changeMemoList(List<Memo> listData) {
			_listData = listData;
			notifyDataSetChanged();
			Log.d("__DEBUG__", "[RUN]: notifyDataSetChanged");
				/*
					notifyDataSetChanged()
						... Subjectの通知用メソッド。RecyclerViewに属する。
				 */
		}
	}

	/**
	 * リストをタップした時の処理が記述されたメンバクラス。
	 */
	private class ListItemClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			TextView tvTitleRow = view.findViewById(R.id.tvTitleRow);
			int idNo = (int) tvTitleRow.getTag();
			Intent intent = new Intent(MainActivity.this, MemoEditActivity.class);
			intent.putExtra("mode", Consts.MODE_EDIT);
			intent.putExtra("idNo", idNo);
			startActivity(intent);
		}
	}
}

/*
	Observer ... オブジェクトを観測し、変更があった場合に任意の動作をするデザインパターン

	ListenableFuture ... 一回限りのSQL（One-Shot）を実行する際のもの。
	LiveData ... ObserverのSubject（監視対象）
		DAO上の各Queryの戻り値をListenableFutureかLiveDataかによってどちらの手法を取るか自動生成してくれる。

		1. ActivityのonCreateでListViewオブジェクトに空のAdapterをsetする。
		2. Observerをセットする。
 */