package com.michaelpardo.pickrand.app;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.michaelpardo.pickrand.Params;
import com.michaelpardo.pickrand.R;
import com.michaelpardo.pickrand.model.Category;
import com.michaelpardo.pickrand.model.Item;
import com.michaelpardo.pickrand.widget.ValueAdapter;

public class CategoryListActivity extends ListActivity {
	public static final int DIALOG_EDIT = 0;
	public static final int DIALOG_RANDOM = 1;
	public static final int DIALOG_ABOUT = 2;

	private LayoutInflater mInflater;
	private Category mEditCategory;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		registerForContextMenu(getListView());

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
			editCategory(info.id);
			return true;
		}
		case R.id.delete: {
			deleteCategory(info.id);
			return true;
		}
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.category_menu, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		List<Item> items = Item.all(Item.class);

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
			addCategory();
			return true;
		}
		case R.id.random: {
			showDialog(DIALOG_RANDOM);
			return true;
		}
		case R.id.about: {
			showDialog(DIALOG_ABOUT);
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
			String title = getString(R.string.add_category);

			if (mEditCategory != null) {
				EditText editText = (EditText) view.findViewById(R.id.text);
				editText.setText(mEditCategory.name);
				editText.setSelection(0, mEditCategory.name.length());

				title = getString(R.string.edit_category);
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

					if (mEditCategory == null) {
						mEditCategory = new Category();
					}

					mEditCategory.name = editText.getText().toString();
					mEditCategory.save();

					loadList();
					removeDialog(DIALOG_EDIT);
				}
			});

			return builder.create();
		}
		case DIALOG_RANDOM: {
			Item item = Item.getRandom(this);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(item.category.name);
			builder.setMessage(item.name);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DIALOG_RANDOM);
				}
			});

			return builder.create();
		}
		case DIALOG_ABOUT: {
			ScrollView scrollView = new ScrollView(this);
			TextView textView = new TextView(this);

			textView.setAutoLinkMask(Linkify.ALL);
			textView.setPadding(5, 5, 5, 5);
			textView.setTextColor(Color.WHITE);
			textView.setText(Html.fromHtml(getString(R.string.about_message)));

			scrollView.addView(textView);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.about_title));
			builder.setView(scrollView);
			builder.setPositiveButton(android.R.string.ok, null);

			return builder.create();
		}
		}

		return super.onCreateDialog(id);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ItemListActivity.class);
		intent.putExtra(Params.Extras.CategoryId, id);

		startActivity(intent);
	}

	private void loadList() {
		List<Category> categories = Category.getAll(this);
		ValueAdapter adapter = new ValueAdapter(this, categories);
		setListAdapter(adapter);
	}

	private void addCategory() {
		mEditCategory = null;
		showDialog(DIALOG_EDIT);
	}

	private void editCategory(long id) {
		mEditCategory = Category.load(Category.class, id);
		showDialog(DIALOG_EDIT);
	}

	private void deleteCategory(long id) {
		Category.delete(this, id);
		loadList();
	}
}