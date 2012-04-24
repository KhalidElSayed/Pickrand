package com.michaelpardo.pickrand.model;

import java.util.List;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Categories")
public class Category extends Model implements IValue {
	@Column(name = "Name")
	public String name;

	public List<Item> items() {
		return getMany(Item.class, "Category");
	}

	public static Category addCategory(Context context, String name) {
		Category category = new Category();
		category.name = name;
		category.save();

		return category;
	}

	public static void delete(Context context, long id) {
		Item.delete(Item.class, "Category = ?", id);
		Category.delete(Category.class, id);
	}

	public static List<Category> getAll(Context context) {
		return new Select().from(Category.class).orderBy("Name ASC").execute();
	}

	// IValue implementation

	@Override
	public String getValue() {
		return name;
	}
}
