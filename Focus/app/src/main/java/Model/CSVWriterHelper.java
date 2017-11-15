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
    CSVWriter writer;

    public CSVWriterHelper(FocusModel fm){
        this.fm = fm;
    }

    private List<String[]> writeApps(){
        List<String[]> data = new ArrayList<String[]>();
        for(App a: fm.getAllApps()){
            data.add(new String[] {"app", Integer.toString(a.getAppID()), a.getAppName(), Boolean.toString(a.isBlocked()),
            a.getPackageName()});
            for(int bpid: a.getBlockedProfileIDs()){
                data.add(new String[] {"app", Integer.toString(a.getAppID()), Integer.toString(bpid)});
            }
        }


        return data;
    }

    private List<String[]> writeProfiles(){
        List<String[]> data = new ArrayList<String[]>();

        return data;
    }

    public void writeOut() throws IOException {
        String csv = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        CSVWriter writer = new CSVWriter(new FileWriter(csv));

        ArrayList<String[]> data = new ArrayList<String[]>();

        writer.writeAll(writeApps());
        writer.writeAll(writeProfiles());
    }

}
