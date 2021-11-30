package com.example.newsaggregator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final List<NewsSource> newsSources = new ArrayList<>();
    private final List<NewsSource> originalNewSources = new ArrayList<>();
    private final List<NewsArticle> newsArticles = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayAdapter<NewsSource> arrayAdapter;
    private Menu optMenu;
    private ViewPager2 viewPager;
    private NewsArticleAdapter newsArticleAdapter;
    private Map<String, String> menuSubMenuFilterMap = new HashMap<>();
    private Map<String, Map<String, String>> optionMenus = new HashMap<>();
    private NewsSource currentNewsSource = new NewsSource();
    private RotateData rotateData;
    private int articlePosition = 0;
    private int topicId = 0;
    private int languageId = 1;
    private int countriesId = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("News Gateway");
        setContentView(R.layout.activity_main);
        setDrawer(savedInstanceState);

        newsArticleAdapter = new NewsArticleAdapter(this, newsArticles);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(newsArticleAdapter);
        menuSubMenuFilterMap.put(getString(R.string.topics), "all");
        menuSubMenuFilterMap.put(getString(R.string.languages), "all");
        menuSubMenuFilterMap.put(getString(R.string.countries), "all");
    }

    private void setDrawer(Bundle savedInstanceState) {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        // Set up the drawer item click callback method
        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    NewsSource newsSource = newsSources.get(position);
                    this.currentNewsSource = newsSource;
                    String source = newsSource.getId();
                    String sourceName = newsSource.getName();
                    this.articlePosition = 0;
                    if(Utilities.isNetworkConnectionAvailable(this)) {
                        NewsArticlesApiRunnable newsArticlesApiRunnable = new NewsArticlesApiRunnable(source, sourceName,this);
                        new Thread(newsArticlesApiRunnable).start();
                    }
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        // Load the data
        if (Utilities.isNetworkConnectionAvailable(this) && savedInstanceState == null) {
            NewsSourcesApiRunnable newsSourcesApiRunnable = new NewsSourcesApiRunnable(this);
            new Thread(newsSourcesApiRunnable).start();
        }
    }

    public void downloadFailed() {
        Log.d(TAG, "downloadFailed: ");
    }

    public void updateNewsResourcesAndCreateMenu(List<NewsSource> newsSources) {
        this.originalNewSources.clear();
        this.originalNewSources.addAll(newsSources);
        Utilities.updateCountryNameAndLanguageName(this, this.originalNewSources);
        optionMenus = Utilities.getMenuOptions(this, this.originalNewSources);
        Utilities.updateNewsSourceColorCode(this.originalNewSources, optionMenus.get(getString(R.string.topics)));
        makeMenu(optionMenus);
        updateNewsResources();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public void updateNewsResources() {
        List<NewsSource> newsSources = Utilities.filterNewsSources(this.originalNewSources, menuSubMenuFilterMap, this);
        this.newsSources.clear();
        this.newsSources.addAll(newsSources);
        setTitle("News Gateway (" + newsSources.size() + ")");
        arrayAdapter = new ArrayAdapter<NewsSource>
                (this, android.R.layout.simple_list_item_1, this.newsSources){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                NewsSource newsSource = newsSources.get(position);
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setText(newsSource.getName());
                tv.setTextColor(Color.parseColor(newsSource.getColorCode()));
                return view;
            }
        };
        mDrawerList.setAdapter(arrayAdapter);
        if(newsSources.size() == 0) {
            showAlertDialog();
        }
    }

    public void updateNewsArticles(String sourceName, List<NewsArticle> newsArticles) {
        setTitle(sourceName);
        mDrawerLayout.setBackgroundResource(0);
        viewPager.setBackground(null);
        this.newsArticles.clear();
        this.newsArticles.addAll(newsArticles);
        newsArticleAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(articlePosition);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState(); // <== IMPORTANT
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig); // <== IMPORTANT
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Important!
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        if (!item.hasSubMenu()) {
            String title = item.getTitle().toString();
            if(item.getItemId() == topicId) {
                this.menuSubMenuFilterMap.put(getString(R.string.topics), title);
            } else if (item.getItemId() == countriesId) {
                this.menuSubMenuFilterMap.put(getString(R.string.countries), title);
            } else if(item.getItemId() == languageId) {
                this.menuSubMenuFilterMap.put(getString(R.string.languages), title);
            }
            updateNewsResources();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optMenu = menu;
        if(this.rotateData != null) {
            updateNewsResourcesAndCreateMenu(this.rotateData.getNewsSources());
            if(this.rotateData.getCurrentNewsArticles().size() > 0) {
                updateNewsArticles(this.currentNewsSource.getName(), this.rotateData.getCurrentNewsArticles());
            }
        }
        this.rotateData = null;
        return super.onCreateOptionsMenu(menu);
    }

    public void makeMenu(Map<String, Map<String, String>> optionMenus) {
        optMenu.clear();
        for(String menu : optionMenus.keySet()) {
            SubMenu subMenu;
            int id;
            if(menu.equalsIgnoreCase(getString(R.string.topics))) {
                id = topicId;
                subMenu = optMenu.addSubMenu(0, topicId, 0, menu);
            } else if(menu.equalsIgnoreCase(getString(R.string.countries))) {
                id = countriesId;
                subMenu = optMenu.addSubMenu(0, countriesId, 0, menu);
            } else {
                id = languageId;
                subMenu = optMenu.addSubMenu(0, languageId, 0, menu);
            }
            Map<String, String> subOptions = optionMenus.get(menu);
            subMenu.add(0, id, 0, "all");
            int j = 0;
            for(String subOption : subOptions.values()) {
                MenuItem item = subMenu.add(0, id, 0, subOption);
                if(id == topicId) {
                    SpannableString spanString = new SpannableString(item.getTitle().toString());
                    spanString.setSpan(new ForegroundColorSpan(Color.parseColor(Utilities.getListOfColors().get(j++))), 0,     spanString.length(), 0); //fix the color to white
                    item.setTitle(spanString);
                }
            }
        }
        hideKeyboard();
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Ok", (dialog, id) -> dialog.dismiss());

        String title = "No News Sources Exist for following:-";
        builder.setTitle(title);
        String content = "Topics : " + menuSubMenuFilterMap.get(getString(R.string.topics)) + "\n";
        content += "Country : " + menuSubMenuFilterMap.get(getString(R.string.countries)) + "\n";
        content += "Language : " + menuSubMenuFilterMap.get(getString(R.string.languages)) + "\n";
        builder.setMessage(content);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) return;

        //Find the currently focused view
        View view = getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
            view = new View(this);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        this.articlePosition = viewPager.getCurrentItem();
        RotateData rotateData = new RotateData(this.menuSubMenuFilterMap, this.originalNewSources,
                this.currentNewsSource, this.newsArticles, this.articlePosition);
        outState.putSerializable("rotateData", rotateData);
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onRestoreInstanceState(Bundle savedInstance) {
        super.onRestoreInstanceState(savedInstance);
        RotateData rotateData = (RotateData) savedInstance.getSerializable("rotateData");
        this.menuSubMenuFilterMap = rotateData.getMenuSubMenuFilterMap();
        this.articlePosition = rotateData.getArticlePosition();
        this.currentNewsSource = rotateData.getCurrentNewsSource();
        this.rotateData = rotateData;
    }
}