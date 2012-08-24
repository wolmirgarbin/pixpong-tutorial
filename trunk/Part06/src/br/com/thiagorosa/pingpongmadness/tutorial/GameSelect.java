package br.com.thiagorosa.pingpongmadness.tutorial;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

public class GameSelect extends TabActivity implements TabHost.TabContentFactory {

    // LOGS
    private static final String TAG = "[PPMT]";
    private static final String TAG_NAME = "[GameSelect] ";

    // TABS
    private static final String TAB_TUTORIAL = "1";
    private static final String TAB_LEVEL = "2";
    private static String mCurrentTAB = "";

    // STARS
    private static final int STAR_NONE = R.drawable.star_none;
    private static final int STAR_HALF = R.drawable.star_half;
    private static final int STAR_FULL = R.drawable.star_full;

    // VIEWS
    private ListView mListView = null;

    /*******************************************************************************************
     *******************************************************************************************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_list);

        // listview that will display the levels
        mListView = (ListView) findViewById(R.id.level_list);

        TabHost tabhost = getTabHost();
        TabHost.TabSpec tabspec;

        // init and add the tutorial tab
        tabspec = tabhost.newTabSpec(TAB_TUTORIAL);
        tabspec.setContent(this);
        tabspec.setIndicator(getText(R.string.mode_tutorial), getResources().getDrawable(STAR_NONE));
        tabhost.addTab(tabspec);

        // init and add the level tab
        tabspec = tabhost.newTabSpec(TAB_LEVEL);
        tabspec.setContent(this);
        tabspec.setIndicator(getText(R.string.mode_level), getResources().getDrawable(STAR_NONE));
        tabhost.addTab(tabspec);

        // add a tab changed listener
        tabhost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.d(TAG, TAG_NAME + "onTabChanged - " + tabId);
                Toast.makeText(getBaseContext(), "Tab changed to: " + tabId, Toast.LENGTH_SHORT).show();
                mCurrentTAB = tabId;

                // TODO: display the level list
            }
        });

    }

    @Override
    public View createTabContent(String tag) {
        mCurrentTAB = tag;
        return mListView;
    }

}
