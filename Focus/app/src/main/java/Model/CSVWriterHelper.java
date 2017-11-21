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

    public CSVWriterHelper(){
        this.fm = FocusModel.getInstance();
    }

    private List<String[]> writeApps(){
        List<String[]> data = new ArrayList<String[]>();
        for(App a: fm.getAllApps()){
            data.add(new String[] {"app", "meta", Integer.toString(a.getAppID()), a.getAppName(), Boolean.toString(a.isBlocked()),
                    a.getPackageName()});
            ArrayList<String> str = new ArrayList<>();// {"app", Integer.toString(a.getAppID())};
            str.add("app");
            str.add("blocked");
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
            for (Schedule s: fm.getAllSchedules()){
                for(TimeRange tr: s.getTimeRanges()) {
//                    System.out.println(s.getScheduleName() + " " + s.getProfileIDs().size() + " " + s.getProfiles().size());
                    if (tr.getProfileIDs().contains(p.getProfileID())) {
                        System.out.println("FOLLOW_UP " + s.getScheduleID());
                        str.add(Integer.toString(s.getScheduleID()));
                    }
                }
            }
            data.add(str.toArray(new String[str.size()]));
        }
        return data;
    }

    private List<String[]> writeSchedules() {
        List<String[]> data = new ArrayList<String[]>();
        for (Schedule s: fm.getAllSchedules()){
            data.add(new String[] {"sched", Integer.toString(s.getScheduleID()), s.getScheduleName(),
                    Boolean.toString(s.invisible), Boolean.toString(s.activated), Integer.toString(s.getColor())});
        }
        return data;
    }

    private List<String[]> writeTimeRanges() {
        List<String[]> data = new ArrayList<String[]>();
        for (Schedule s: fm.getAllSchedules()){
            for(TimeRange tr: s.getTimeRanges()) {
                ArrayList<String> str = new ArrayList<>();
                str.add("tr");
                str.add(Integer.toString(s.getScheduleID()));
                str.add(Integer.toString(tr.getStartHour()));
                str.add(Integer.toString(tr.getStartMinute()));
                str.add(Integer.toString(tr.getEndHour()));
                str.add(Integer.toString(tr.getEndMinute()));




                ArrayList<Integer> days = tr.getDays();
                if(tr.getDays().contains(1)) str.add("TRUE");
                else str.add("FALSE");
                if(tr.getDays().contains(2)) str.add("TRUE");
                else str.add("FALSE");
                if(tr.getDays().contains(3)) str.add("TRUE");
                else str.add("FALSE");
                if(tr.getDays().contains(4)) str.add("TRUE");
                else str.add("FALSE");
                if(tr.getDays().contains(5)) str.add("TRUE");
                else str.add("FALSE");
                if(tr.getDays().contains(6)) str.add("TRUE");
                else str.add("FALSE");
                if(tr.getDays().contains(7)) str.add("TRUE");
                else str.add("FALSE");


                for(Profile p: tr.getProfiles()){
                    str.add(Integer.toString(p.getProfileID()));
                }
                data.add(str.toArray(new String[str.size()]));
//                data.add(str2.toArray(new String[str2.size()]));
            }


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
        writer.writeAll(writeSchedules());
        writer.writeAll(writeTimeRanges());
        writer.close();
    }

}


