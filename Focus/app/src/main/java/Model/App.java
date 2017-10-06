package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */


import java.util.Set;

/**
 * App is a class that stores the name of an application
 * and if the app is blocked or not
 */

public class App {
    private String name;
    private int id;
    private boolean blocked;
    private Set<Integer> blockedProfileIDs;

    /**
     * Constructors
     */
    public App(String name, boolean blocked) {
        this.name = name;
        this.blocked = blocked;
    }

    public App(int id, String name) {
        this.id = id;
        this.name = name;
        blocked = false;
    }

    /**
     *
     * Getter and Setter functions
     *
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void blockApp(Integer profileID) {
        blocked = true;
        blockedProfileIDs.add(profileID);
    }

    public void unblockApp(Integer profileID) {
        if (blockedProfileIDs.contains(profileID)) {
            blockedProfileIDs.remove(profileID);
        }

        if (blockedProfileIDs.isEmpty()) {
            blocked = false;
        }
    }

}
