package br.com.thiagorosa.pingpongmadness.tutorial.game;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import br.com.thiagorosa.pingpongmadness.tutorial.R;

public class Shot {

    // LOGS
    private static final String TAG = "[PPMT]";
    private static final String TAG_NAME = "[Shot] ";

    // SHOT TYPES
    public static final int TYPE_NONE = 0;
    public static final int TYPE_SHOT_001 = 1; // straight x05 constant
    protected int shotType = TYPE_NONE;

    // SHOT MOVEMENTS
    private static boolean areResourcesLoaded = false;
    public static ArrayList<Shot.Movement> shotMoves001 = null;
    private ArrayList<Shot.Movement> shotAllMoves = null;

    // SHOT PICTURES
    private static Bitmap shotPictureYellow = null;
    private Bitmap shotPicture = null;

    // SHOT STATES
    public static final int STATE_NONE = 0;
    public static final int STATE_VALID = 1;
    public static final int STATE_HIT = 2;
    public static final int STATE_MISS = 3;
    public static final int STATE_INVALID = 4;
    protected int shotState = STATE_NONE;

    // SHOT MOVES
    private Movement move1 = null;
    private Movement move2 = null;
    private int currentMove = 0;
    protected int shotMoves = 0;

    // SHOT COORDINATES
    private int coordX = 0;
    private int coordY = 0;

    // SHOT DIRECTIONS
    public static final int X_DIRECTION_RIGHT = 1;
    public static final int X_DIRECTION_LEFT = -1;
    public static final int Y_DIRECTION_DOWN = 1;
    public static final int Y_DIRECTION_UP = -1;
    private int _xDirection = X_DIRECTION_RIGHT;
    private int _yDirection = Y_DIRECTION_DOWN;
    protected int shotDirection = 1;

    /*******************************************************************************************
    *******************************************************************************************/

    public Shot(Resources res, int type, int startX, int startY) {

        // load moves and picture accordingly
        switch (type) {
            case TYPE_SHOT_001:
                shotAllMoves = shotMoves001;
                shotPicture = shotPictureYellow;
                break;
            default:
                shotAllMoves = shotMoves001;
                shotPicture = shotPictureYellow;
                break;
        }

        // initial setup
        shotState = STATE_VALID;
        shotType = type;

        // initial coordinates
        setCoordX((int) (startX - getGraphic().getWidth() / 2));
        setCoordY((int) (startY - getGraphic().getHeight() / 2));
    }

    public static Shot createShot(Resources res, int type, int startX, int startY) {

        // load resources if they weren't loaded yet
        if (areResourcesLoaded == false) {
            loadResources(res);
        }

        // create a new shot
        return new Shot(res, type, startX, startY);
    }

    /*******************************************************************************************
    *******************************************************************************************/

    public static void loadResources(Resources res) {
        // load all moves from all shots
        shotMoves001 = loadMoves(res, R.xml.shot_001);

        // load all picture from all shots
        shotPictureYellow = BitmapFactory.decodeResource(res, R.drawable.shot_yellow);

        areResourcesLoaded = true;
    }

    private static ArrayList<Movement> loadMoves(Resources res, int content) {
        ArrayList<Movement> mMovement = new ArrayList<Movement>();

        try {
            XmlResourceParser xrp = res.getXml(content);

            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String s = xrp.getName();

                    if (s.equals("movement")) {
                        // begin movement tag
                    }
                    else if (s.equals("move")) {
                        // move tag
                        int start = xrp.getAttributeIntValue(null, "start", 0);
                        int speedX = xrp.getAttributeIntValue(null, "speedX", 0);
                        int speedY = xrp.getAttributeIntValue(null, "speedY", 0);

                        // create a new movement and add it on the movement list
                        mMovement.add(new Movement(start, speedX, speedY));
                    }
                }
                else if (xrp.getEventType() == XmlResourceParser.END_TAG) {
                    String s = xrp.getName();
                    if (s.equals("movement")) {
                        // end movement tag
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

        return mMovement;
    }

    /*******************************************************************************************
    *******************************************************************************************/

    private void moveShot(ArrayList<Movement> allMoves, float position, float acceleration, float adjustmentX, float adjustmentY) {
        // check if it is not the last move
        if (currentMove < allMoves.size() - 1) {

            // get the current and next move
            move1 = allMoves.get(currentMove);
            move2 = allMoves.get(currentMove + 1);

            // set the new coordinates for the shot
            setCoordX((int) (getCoordX() - Math.floor(move1.getShotSpeedX() * adjustmentX * acceleration)));
            setCoordY((int) (getCoordY() - Math.floor(move1.getShotSpeedY() * adjustmentY * shotDirection)));

            // check if it is time for the next move
            if (position * 100 < (float) move1.getShotStart() && position * 100 > (float) move2.getShotStart()) {
                // still same move
            }
            else {
                // next move
                currentMove++;
            }
        }
        else {
            // get the current move
            move1 = allMoves.get(currentMove);

            // set the new coordinates for the shot
            setCoordX((int) (getCoordX() - move1.getShotSpeedX() * adjustmentX * acceleration));
            setCoordY((int) (getCoordY() - move1.getShotSpeedY() * adjustmentY * shotDirection));
        }

    }

    public void moveShot(int type, float position, float acceleration, float adjustmentX, float adjustmentY) {
        moveShot(shotAllMoves, position, acceleration, adjustmentX, adjustmentY);
    }

    /*******************************************************************************************
    *******************************************************************************************/

    public static class Movement {
        private int shotStart;
        private int shotSpeedX;
        private int shotSpeedY;

        public Movement(int start, int speedx, int speedy) {
            shotStart = start;
            shotSpeedX = speedx;
            shotSpeedY = speedy;
        }

        public int getShotStart() {
            return shotStart;
        }

        public int getShotSpeedX() {
            return shotSpeedX;
        }

        public int getShotSpeedY() {
            return shotSpeedY;
        }

    }

    /*******************************************************************************************
    *******************************************************************************************/

    public void toggleShotDirection() {
        if (shotDirection == X_DIRECTION_RIGHT) {
            shotDirection = X_DIRECTION_LEFT;
        }
        else {
            shotDirection = X_DIRECTION_RIGHT;
        }
    }

    public void toggleXDirection() {
        if (_xDirection == X_DIRECTION_RIGHT) {
            _xDirection = X_DIRECTION_LEFT;
        }
        else {
            _xDirection = X_DIRECTION_RIGHT;
        }
    }

    public void toggleYDirection() {
        if (_yDirection == Y_DIRECTION_DOWN) {
            _yDirection = Y_DIRECTION_UP;
        }
        else {
            _yDirection = Y_DIRECTION_DOWN;
        }
    }

    public Bitmap getGraphic() {
        return shotPicture;
    }

    public int getHeight() {
        return shotPicture.getHeight();
    }

    public int getXDirection() {
        return _xDirection;
    }

    public int getYDirection() {
        return _yDirection;
    }

    public int getShotState() {
        return shotState;
    }

    public void setShotState(int state) {
        shotState = state;
    }

    public int getLeftEdge() {
        return coordX;
    }

    public int getRightEdge() {
        return coordX + shotPicture.getWidth();
    }

    public int getCoordX() {
        return coordX + shotPicture.getWidth() / 2;
    }

    public void setCoordX(int value) {
        coordX = value - shotPicture.getWidth() / 2;
    }

    public int getTopEdge() {
        return coordY;
    }

    public int getBottomEdge() {
        return coordY + shotPicture.getHeight();
    }

    public int getCoordY() {
        return coordY + shotPicture.getHeight() / 2;
    }

    public void setCoordY(int value) {
        coordY = value - shotPicture.getHeight() / 2;
    }

}
