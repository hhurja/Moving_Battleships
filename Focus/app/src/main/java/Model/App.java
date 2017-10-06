package Model;

/**
 * Created by aaronrschrock on 10/6/17.
 */


/**
 * App is a class that stores the name of an application
 * and if the app is blocked or not
 */

public class App {
    private String name;
    private boolean blocked;

    /**
     * Constructors
     */
    public App(String name, boolean blocked) {
        this.name = name;
        this.blocked = blocked;
    }

    public App(String name) {
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

    public void blockApp() {
        blocked = true;
    }

    public void unblockApp() {
        blocked = false;
    }

}
