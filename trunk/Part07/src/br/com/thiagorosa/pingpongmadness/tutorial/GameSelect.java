package br.com.thiagorosa.pingpongmadness.tutorial;

import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import br.com.thiagorosa.pingpongmadness.tutorial.database.DatabaseAdapter;

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
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, TAG_NAME + "onItemClick - " + arg3);
                Toast.makeText(getBaseContext(), "Selected item: id=" + arg3, Toast.LENGTH_SHORT).show();

                // TODO: start the game on this level
            }
        });

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
                mCurrentTAB = tabId;

                // display the level list
                showLevelList(tabId);
            }
        });

    }

    @Override
    public View createTabContent(String tag) {
        mCurrentTAB = tag;
        showLevelList(mCurrentTAB);
        return mListView;
    }

    /*******************************************************************************************
     *******************************************************************************************/

    // show the level list by type
    private void showLevelList(String type) {
        Cursor tutorialCursor = DatabaseAdapter.fetchLevelsByType(this, Integer.valueOf(type));
        startManagingCursor(tutorialCursor);

        // set the listview adapter
        mListView.setAdapter(new LevelAdapter(this, tutorialCursor, new String[] {}, new int[] {}));
    }

    // get the level info for each position
    // columns: 0=LEVEL_ID, 1=LEVEL_TYPE, 2=LEVEL_SHOTS, 3=LEVEL_NAME
    private String getLevelInfo(int position, int column) {
        return ((Cursor) ((LevelAdapter) mListView.getAdapter()).getItem(position)).getString(column);
    }

    // custom adapter with level name, score, ratio and a star
    private class LevelAdapter extends SimpleCursorAdapter {

        private ContentValues recordInfo = null;
        private Activity mActivity;
        private ImageView mStar;
        private TextView mScore;
        private TextView mRatio;
        private TextView mLevel;
        private int misses = 0;
        private int total = 0;

        LevelAdapter(Activity context, Cursor c, String[] from, int[] to) {
            super(context, R.layout.level_row, c, from, to);
            this.mActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.level_row, null);
            }

            // set the level name (column 3)
            mLevel = (TextView) convertView.findViewById(R.id.row_level);
            mLevel.setText(getLevelInfo(position, 3));

            // set the default star image
            mStar = (ImageView) convertView.findViewById(R.id.row_star);
            mStar.setImageResource(STAR_NONE);

            // set the default score value
            mScore = (TextView) convertView.findViewById(R.id.row_score);
            mScore.setText("-");

            // set the default ratio value
            mRatio = (TextView) convertView.findViewById(R.id.row_ratio);
            mRatio.setText("-");

            // fetch the record info for this level
            recordInfo = DatabaseAdapter.fetchRecord(getBaseContext(), getLevelInfo(position, 0));
            if (recordInfo != null && recordInfo.size() > 0) {
                misses = recordInfo.getAsInteger(DatabaseAdapter.RECORD_MISSES);
                total = Integer.valueOf(getLevelInfo(position, 2));

                if (misses > 0) {
                    // if there are any misses, then display the half star
                    mStar.setImageResource(STAR_HALF);
                }
                else {
                    // if there are no misses, then display the full star
                    mStar.setImageResource(STAR_FULL);
                }

                // display the total score and the ratio
                if (total != 0) {
                    mScore.setText(recordInfo.getAsString(DatabaseAdapter.RECORD_SCORE) + " " + getText(R.string.points));
                    mRatio.setText(((total - misses) * 100 / total) + "%");
                }

            }

            return convertView;
        }

    }

}
