package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */


import java.util.Set;
import java.util.HashSet;

/**
 * App is a class that stores the name of an application
 * and if the app is blocked or not
 */

public class App {
    private String name;
    private int id;
    private boolean blocked;
    private HashSet<Integer> blockedProfileIDs;
    private String packageName;

    /**
     * Constructors
     */
    public App(int id, String name, boolean blocked, String packageName) {
        this.id = id;
        this.name = name;
        this.blocked = blocked;
        this.packageName = packageName;

        blockedProfileIDs = new HashSet<>();
    }

    public App(int id, String name, String packageName) {
        this.id = id;
        this.name = name;
        blocked = false;
        this.packageName = packageName;

        blockedProfileIDs = new HashSet<>();
    }

    /**
     *
     * Getter and Setter functions
     *
     */

    public String getAppName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void blockApp(Integer profileID) {
        blocked = true;
        blockedProfileIDs.add(profileID);
    }

    public int getAppID(){
        return id;
    }

    public void unblockApp(Integer profileID) {
        if (blockedProfileIDs.contains(profileID)) {
            blockedProfileIDs.remove(profileID);
        }

        if (blockedProfileIDs.isEmpty()) {
            blocked = false;
        }
    }

    public boolean isBlocked(){
        return blocked;
    }

    public String getPackageName(){
        return packageName;
    }


}
