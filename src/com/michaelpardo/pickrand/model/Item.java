package com.michaelpardo.pickrand.model;

import java.util.List;

import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Items")
public class Item extends Model implements IValue {
	@Column(name = "Name")
	public String name;

	@Column(name = "Category")
	public Category category;

	public static Item addItem(Context context, String name, Category category) {
		Item item = new Item();
		item.name = name;
		item.category = category;
		item.save();

		return item;
	}

	public static Item getRandom(Context context) {
		return new Select().from(Item.class).orderBy("RANDOM()").executeSingle();
	}

	public static Item getRandom(Context context, Category category) {
		return new Select().from(Item.class).where("Category = ?", category.getId()).orderBy("RANDOM()")
				.executeSingle();
	}

	public static List<Item> getAll(Context context, Category category) {
		return new Select().from(Item.class).where("Category = ?", category.getId()).orderBy("Name ASC").execute();
	}

	// IValue implementation

	@Override
	public String getValue() {
		return name;
	}
}
