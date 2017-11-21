package Model;

        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

        import com.opencsv.CSVWriter;

/**
 * Created by HunterHurja on 11/15/17.
 */

public class CSVWriterHelper {
    FocusModel fm;

    public CSVWriterHelper(FocusModel fm){
        this.fm = fm;
    }

    private List<String[]> writeApps(){
        List<String[]> data = new ArrayList<String[]>();
        for(App a: fm.getAllApps()){
            data.add(new String[] {"app", Integer.toString(a.getAppID()), a.getAppName(), Boolean.toString(a.isBlocked()),
                    a.getPackageName()});
            ArrayList<String> str = new ArrayList<>();// {"app", Integer.toString(a.getAppID())};
            str.add("app");
            str.add(Integer.toString(a.getAppID()));

            for(int bpid: a.getBlockedProfileIDs()){
                str.add(Integer.toString(bpid));
            }
            data.add(str.toArray(new String[str.size()]));
        }

        return data;
    }

    private List<String[]> writeProfiles(){
        List<String[]> data = new ArrayList<String[]>();

        for(Profile p: fm.getAllProfiles()){
            ArrayList<String> str = new ArrayList<>();
            str.add("prof");
            str.add(Integer.toString(p.getProfileID()));
            str.add(p.getProfileName());
            for(App a: p.getApps()) {
                str.add(a.getAppName());
            }
            data.add(str.toArray(new String[str.size()]));
        }
        for(Profile p: fm.getAllProfiles()){
            ArrayList<String> str = new ArrayList<>();
            str.add("prof");
            str.add("scheds");
            str.add(Integer.toString(p.getProfileID()));
            for(Integer sched: fm.getSchedulesFromProfile(p.getProfileID())) {
                str.add(Integer.toString(sched));
            }
            data.add(str.toArray(new String[str.size()]));
        }
        return data;
    }

    public void writeOut() throws IOException {
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/output.csv";
        System.out.println("******** CSV LOCATION: "+csv);
        CSVWriter writer = new CSVWriter(new FileWriter(csv));

        ArrayList<String[]> data = new ArrayList<String[]>();

        writer.writeAll(writeApps());
        writer.writeAll(writeProfiles());
        writer.close();
    }

}


