package br.com.thiagorosa.pingpongmadness.tutorial.game;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.util.Log;
import br.com.thiagorosa.pingpongmadness.tutorial.R;

public class Level {

    // LOGS
    private static final String TAG = "[PPMT]";
    private static final String TAG_NAME = "[Level] ";

    // SEQUENCE TYPES
    public static final int TYPE_NONE = 0;
    public static final int TYPE_TUTORIAL_001 = 1; // sequences 1,2,3 (37)
    protected int levelType = TYPE_NONE;

    // LEVEL MODES
    public static final int MODE_NONE = 0;
    public static final int MODE_NORMAL = 1;
    public static final int MODE_ENDLESS = 2;
    protected int levelMode = MODE_NONE;

    // MOVEMENT PATH
    public ArrayList<Level.Sequences> allLevel = null;
    private boolean isLevelLoaded = false;

    public Level(Resources resources, int type) {
        levelType = type;

        if (isLevelLoaded == false) {
            allLevel = loadLevel(resources, getLevelList(type));
            if (allLevel != null && allLevel.size() > 0) {
                if (allLevel.get(0).getSequenceType() != 0) {
                    levelMode = MODE_NORMAL;
                }
                else {
                    levelMode = MODE_ENDLESS;
                }
            }
            isLevelLoaded = true;
        }
    }

    private int getLevelList(int type) {
        switch (type) {
            case TYPE_TUTORIAL_001:
                return R.xml.level_001;
            default:
                return R.xml.level_001;
        }
    }

    public int getLevelMode() {
        return levelMode;
    }

    public static boolean isTutorial(int level) {
        boolean tutorial = false;
        if (level == TYPE_TUTORIAL_001) {
            tutorial = true;
        }
        return tutorial;
    }

    /*******************************************************************************************
     *******************************************************************************************/

    protected ArrayList<Level.Sequences> loadLevel(Resources resources, int content) {
        ArrayList<Level.Sequences> mSequence = new ArrayList<Level.Sequences>();

        try {
            XmlResourceParser xrp = resources.getXml(content);

            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String s = xrp.getName();

                    if (s.equals("level")) {
                        // start level tag
                    }
                    else if (s.equals("sequence")) {
                        // start sequence tag

                        int type = xrp.getAttributeIntValue(null, "type", 0);

                        // add sequence to the level
                        mSequence.add(new Sequences(type));
                    }
                }
                else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
                    String s = xrp.getName();
                    if (s.equals("level")) {
                        // end level tag
                    }
                }
                xrp.next();
            }
            xrp.close();
        }
        catch (XmlPullParserException e) {
            Log.e(TAG, TAG_NAME + "error - exception / xmlpullparser " + e);
        }
        catch (IOException e) {
            Log.e(TAG, TAG_NAME + "error - exception / io " + e);
        }
        catch (NotFoundException e) {
            Log.e(TAG, TAG_NAME + "error - exception / notfound " + e);
        }

        return mSequence;
    }

    /*******************************************************************************************
     *******************************************************************************************/

    public class Sequences {
        private int sequenceType = 0;
        private int sequenceStart = 0;
        private int sequenceEnd = 0;
        private int sequenceSeed = 0;
        private int sequenceAcceleration = 0;

        public Sequences(int type) {
            sequenceType = type;
        }

        public Sequences(int start, int end, int seed, int acceleration) {
            sequenceStart = start;
            sequenceEnd = end;
            sequenceSeed = seed;
            sequenceAcceleration = acceleration;
        }

        public int getSequenceType() {
            return sequenceType;
        }

        public int getSequenceStart() {
            return sequenceStart;
        }

        public int getSequenceEnd() {
            return sequenceEnd;
        }

        public int getSequenceSeed() {
            return sequenceSeed;
        }

        public int getSequenceAcceleration() {
            return sequenceAcceleration;
        }

    }

}
