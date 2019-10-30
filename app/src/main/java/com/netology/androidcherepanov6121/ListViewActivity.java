package com.netology.androidcherepanov6121;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {

    private List<Map<String, String>> simpleAdapterContent = new ArrayList<>();
    private String largeText;

    final private String[] keys = {"title", "subtitle"};
    final private int[] fields = {R.id.textTitle, R.id.textTitleDown};
    final private String LARGE_TEXT_KEY = "large_text";

    private ListView list;
    private SharedPreferences preferences;
    private BaseAdapter listContentAdapter;
    private SwipeRefreshLayout swipeRefresher;

    private ArrayList<Integer> deletedItemPositions = new ArrayList<>();
    final private String DELETED_ITEM = "deleted_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = findViewById(R.id.list);

        preferences = getPreferences(MODE_PRIVATE);

        largeText = preferences.getString(LARGE_TEXT_KEY, null);

        if (largeText == null) {

            largeText = getString(R.string.large_text);
            preferences.edit().putString(LARGE_TEXT_KEY, largeText).apply();

        }

        listContentAdapter = createAdapter();

        list.setAdapter(listContentAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(DELETED_ITEM)) {

            deletedItemPositions = savedInstanceState.getIntegerArrayList(DELETED_ITEM);

            for (Integer position : deletedItemPositions) {

                simpleAdapterContent.remove((int) position);

            }
            listContentAdapter.notifyDataSetChanged();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                simpleAdapterContent.remove(i);
                deletedItemPositions.add(i);
                listContentAdapter.notifyDataSetChanged();

            }
        });

        swipeRefresher = findViewById(R.id.textUpdate);

        swipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                prepareContent(keys);
                listContentAdapter.notifyDataSetChanged();
                swipeRefresher.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putIntegerArrayList(DELETED_ITEM, deletedItemPositions);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    private BaseAdapter createAdapter() {
        prepareContent(keys);
        return new SimpleAdapter(this,
                simpleAdapterContent,
                R.layout.activity_title,
                keys,
                fields);
    }

    private void prepareContent(String[] keys) {
        String[] titles = largeText.split("\n\n");
        simpleAdapterContent.clear();
        for (String title : titles) {
            Map<String, String> item = new HashMap<>();
            item.put(keys[0], title);
            item.put(keys[1], Integer.toString(title.length()));
            simpleAdapterContent.add(item);
        }
    }
}