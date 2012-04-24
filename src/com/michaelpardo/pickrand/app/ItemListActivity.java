package com.michaelpardo.pickrand.app;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;

import com.activeandroid.query.Select;
import com.michaelpardo.pickrand.Params;
import com.michaelpardo.pickrand.R;
import com.michaelpardo.pickrand.model.Category;
import com.michaelpardo.pickrand.model.Item;
import com.michaelpardo.pickrand.widget.ValueAdapter;

public class ItemListActivity extends ListActivity {
	public static final int DIALOG_EDIT = 0;
	public static final int DIALOG_RANDOM = 1;

	private LayoutInflater mInflater;
	private Category mCategory;
	private Item mEditItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		registerForContextMenu(getListView());

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final long categoryId = getIntent().getLongExtra(Params.Extras.CategoryId, -1);
		if (categoryId < 0) {
			finish();
		}
		else {
			mCategory = Category.load(Category.class, categoryId);
		}

		loadList();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.edit: {
			editItem(info.id);
			return true;
		}
		case R.id.delete: {
			deleteItem(info.id);
			return true;
		}
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.item_menu, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final List<Item> items = new Select().from(Item.class).where("Category = ?", mCategory.getId()).execute();

		if (items.size() > 0) {
			menu.setGroupEnabled(R.id.random_group, true);
		}
		else {
			menu.setGroupEnabled(R.id.random_group, false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add: {
			addItem();
			return true;
		}
		case R.id.random: {
			showDialog(DIALOG_RANDOM);
			return true;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_EDIT: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final View view = mInflater.inflate(R.layout.edit_dialog, null);
			String title = getString(R.string.add_item);

			if (mEditItem != null) {
				EditText editText = (EditText) view.findViewById(R.id.text);
				editText.setText(mEditItem.name);
				editText.setSelection(0, mEditItem.name.length());

				title = getString(R.string.edit_item);
			}

			builder.setTitle(title);
			builder.setView(view);
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_EDIT);
				}
			});
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					EditText editText = (EditText) view.findViewById(R.id.text);

					if (mEditItem == null) {
						mEditItem = new Item();
					}

					mEditItem.name = editText.getText().toString();
					mEditItem.category = mCategory;
					mEditItem.save();

					loadList();
					removeDialog(DIALOG_EDIT);
				}
			});

			return builder.create();
		}
		case DIALOG_RANDOM: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(mCategory.name);
			builder.setMessage(Item.getRandom(this, mCategory).name);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_RANDOM);
				}
			});

			return builder.create();
		}
		}

		return super.onCreateDialog(id);
	}

	private void loadList() {
		List<Item> items = Item.getAll(this, mCategory);
		ValueAdapter adapter = new ValueAdapter(this, items);
		setListAdapter(adapter);
	}

	private void addItem() {
		mEditItem = null;
		showDialog(DIALOG_EDIT);
	}

	private void editItem(long id) {
		mEditItem = Item.load(Item.class, id);
		showDialog(DIALOG_EDIT);
	}

	private void deleteItem(long id) {
		Item.delete(Item.class, id);
		loadList();
	}
}