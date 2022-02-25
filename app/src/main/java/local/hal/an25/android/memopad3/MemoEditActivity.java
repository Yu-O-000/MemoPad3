package local.hal.an25.android.memopad3;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import local.hal.an25.android.memopad3.dataaccess.Memo;
import local.hal.an25.android.memopad3.viewmodel.MemoEditViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.sql.Timestamp;

/**
 * AN25 Android追加サンプル04 ViewModelとLiveData
 *
 * 編集画面表示用アクティビティクラス。
 *
 * @author Shinzo SAITO
 */
public class MemoEditActivity extends AppCompatActivity {
	/**
	 * 新規登録モードか更新モードかを表すフィールド。
	 */
	private int _mode = Consts.MODE_INSERT;
	/**
	 * 更新モードの際、現在表示しているメモ情報のデータベース上の主キー値。
	 */
	private int _idNo = 0;
	/**
	 * メモ情報リストビューモデルオブジェクト。
	 */
	private MemoEditViewModel _memoEditViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo_edit);

		ViewModelProvider.AndroidViewModelFactory factory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
		ViewModelProvider provider = new ViewModelProvider(MemoEditActivity.this, factory);
		_memoEditViewModel = provider.get(MemoEditViewModel.class);

		Intent intent = getIntent();
		_mode = intent.getIntExtra("mode", Consts.MODE_INSERT);

		if(_mode == Consts.MODE_INSERT) {
			TextView tvTitleEdit = findViewById(R.id.tvTitleEdit);
			tvTitleEdit.setText(R.string.tv_title_insert);

		} else {
			_idNo = intent.getIntExtra("idNo", 0);
			Memo memo = _memoEditViewModel.getMemo(_idNo);
			EditText etInputTitle = findViewById(R.id.etInputTitle);
			etInputTitle.setText(memo.title);
			SwitchMaterial swImportant = findViewById(R.id.swImportant);
			swImportant.setChecked(memo.getImportantBool());
			EditText etInputContent = findViewById(R.id.etInputContent);
			etInputContent.setText(memo.content);
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if(_mode == Consts.MODE_INSERT) {
			inflater.inflate(R.menu.menu_options_add, menu);

		} else {
			inflater.inflate(R.menu.menu_options_edit, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id) {
			case R.id.btnSave:
				EditText etInputTitle = findViewById(R.id.etInputTitle);
				String inputTitle = etInputTitle.getText().toString();
				if(inputTitle.equals("")) {
					Toast.makeText(MemoEditActivity.this, R.string.msg_input_title, Toast.LENGTH_SHORT).show();

				} else {
					EditText etInputContent = findViewById(R.id.etInputContent);
					String inputContent = etInputContent.getText().toString();
					SwitchMaterial swImportant = findViewById(R.id.swImportant);
					int inputImportant = 0;

					if(swImportant.isChecked()) {
						inputImportant = 1;
					}
					long result = 0;
					Memo memo = new Memo();
					memo.title = inputTitle;
					memo.content = inputContent;
					memo.important = inputImportant;
					memo.updatedAt = new Timestamp(System.currentTimeMillis());

					if(_mode == Consts.MODE_INSERT) {
						result = _memoEditViewModel.insert(memo);

					} else {
						memo.id = _idNo;
						result = _memoEditViewModel.update(memo);
					}

					if(result <= 0) {
						Toast.makeText(MemoEditActivity.this, R.string.msg_save_error, Toast.LENGTH_SHORT).show();

					} else {
						finish();
					}
				}
				return true;

			case R.id.btnDelete:
				DeleteConfirmDialogFragment dialog = new DeleteConfirmDialogFragment(_memoEditViewModel);
				Bundle extras = new Bundle();
				extras.putInt("id", _idNo);
				dialog.setArguments(extras);
				FragmentManager manager = getSupportFragmentManager();
				dialog.show(manager, "DeleteConfirmDialogFragment");
				return true;

			case android.R.id.home:
				finish();

			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
