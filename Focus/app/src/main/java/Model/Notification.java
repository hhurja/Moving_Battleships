package Model;

/**
 * Created by aaronrschrock on 10/16/17.
 */

public class Notification {
    private String appName;
    private String title;
    private String tickerText;

    public Notification(String appName, String title, String tickerText) {
        this.appName = appName;
        this.title = title;
        this.tickerText = tickerText;
    }

    public String getAppName() {
        return appName;
    }

    public String getTitle() {
        return title;
    }

    public String getTickerText() {
        return tickerText;
    }
}
