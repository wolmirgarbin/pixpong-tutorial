package br.com.thiagorosa.pingpongmadness.tutorial.game;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.util.Log;
import br.com.thiagorosa.pingpongmadness.tutorial.R;

public class Sequence {

    // LOGS
    private static final String TAG = "[PPMT]";
    private static final String TAG_NAME = "[Sequence] ";

    // SEQUENCE TYPES
    public static final int TYPE_NONE = 0;
    public static final int TYPE_SEQUENCE_001 = 1; // straight shots in a row (10)
    public static final int TYPE_SEQUENCE_002 = 2; // straight shots alternated (10)
    public static final int TYPE_SEQUENCE_003 = 3; // straight shots sequential (17)
    protected int sequenceType = TYPE_NONE;

    // SEQUENCE SHOTS
    public static ArrayList<Sequence.Shots> sequenceShots001 = null;
    public static ArrayList<Sequence.Shots> sequenceShots002 = null;
    public static ArrayList<Sequence.Shots> sequenceShots003 = null;
    public ArrayList<Sequence.Shots> sequenceAllShots = null;

    public Sequence(Resources res, int type) {
        switch (type) {
            case TYPE_SEQUENCE_001:
                sequenceAllShots = sequenceShots001;
                break;
            case TYPE_SEQUENCE_002:
                sequenceAllShots = sequenceShots002;
                break;
            case TYPE_SEQUENCE_003:
                sequenceAllShots = sequenceShots003;
                break;
            default:
                sequenceAllShots = sequenceShots001;
                break;
        }
        sequenceType = type;
    }

    /*******************************************************************************************
    *******************************************************************************************/

    public static void loadAllShots(Resources res) {
        sequenceShots001 = loadSequence(res, R.xml.sequence_001);
        sequenceShots002 = loadSequence(res, R.xml.sequence_002);
        sequenceShots003 = loadSequence(res, R.xml.sequence_003);
    }

    private static ArrayList<Sequence.Shots> loadSequence(Resources res, int content) {
        ArrayList<Sequence.Shots> mSequence = new ArrayList<Sequence.Shots>();

        try {
            XmlResourceParser xrp = res.getXml(content);

            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String s = xrp.getName();

                    if (s.equals("sequence")) {
                        // start sequence tag
                    }
                    else if (s.equals("shot")) {
                        // shot tag
                        int type = xrp.getAttributeIntValue(null, "type", 0);
                        int start = xrp.getAttributeIntValue(null, "start", 0);
                        int interval = xrp.getAttributeIntValue(null, "interval", 0);

                        // add shot to the sequence list
                        mSequence.add(new Shots(type, start, interval));
                    }
                }
                else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
                    String s = xrp.getName();
                    if (s.equals("sequence")) {
                        // end sequence tag
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

    public static class Shots {
        private int shotType;
        private int shotStart;
        private int shotInterval;

        public Shots(int type, int start, int interval) {
            shotType = type;
            shotStart = start;
            shotInterval = interval;
        }

        public int getShotType() {
            return shotType;
        }

        public int getShotStart() {
            return shotStart;
        }

        public int getShotInterval() {
            return shotInterval;
        }

    }

}
