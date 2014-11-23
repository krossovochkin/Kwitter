package com.krossovochkin.kwitter.toolbox;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

/**
 * Created by user on 17.11.2014.
 */
public class FileManager {


    private static final String FILE_NAME_STATUS = "status-";
    private static final String FILE_NAME_EXTENTION_JSON = ".json";

    public static final String FOLDER_NAME_HOME_TIMELINE = "home_timeline";
    public static final String FOLDER_NAME_MENTIONS_TIMELINE = "mentions_timeline";

    private static File getRootFolder(Context context) {
        return context.getExternalCacheDir();
    }

    public static File getFolder(Context context, String folderName) {
        switch (folderName) {
            case FOLDER_NAME_HOME_TIMELINE:
                return getHomeTimelineFolder(context);
            case FOLDER_NAME_MENTIONS_TIMELINE:
                return getMentionsTimelineFolder(context);
            default:
                return getRootFolder(context);
        }
    }

    private static File getHomeTimelineFolder(Context context) {
        File dir = new File(getRootFolder(context), FOLDER_NAME_HOME_TIMELINE);
        if (dir.exists()) {
            return dir;
        } else {
            return dir.mkdir() ? dir : null;
        }
    }

    private static File getMentionsTimelineFolder(Context context) {
        File dir = new File(getRootFolder(context), FOLDER_NAME_MENTIONS_TIMELINE);
        if (dir.exists()) {
            return dir;
        } else {
            return dir.mkdir() ? dir : null;
        }
    }

    private static String getStatusFileName(long id) {
        return FILE_NAME_STATUS + id + FILE_NAME_EXTENTION_JSON;
    }

    public static void saveStatuses(Context context, ResponseList<Status> statuses, String folder) {
        File dir = getFolder(context, folder);

        for (Status status : statuses) {
            File file = new File(dir, getStatusFileName(status.getId()));
            String rawJson = TwitterObjectFactory.getRawJSON(status);

            if (rawJson != null) {
                try {
                    FileManager.storeJSON(rawJson, file.getAbsolutePath());
                } catch (IOException e) {
                    // TODO: do something
                }
            } else {
                // TODO: do something
            }
        }
    }

    public static List<Status> loadStatuses(Context context, String folder) {
        File dir = getFolder(context, folder);

        if (dir == null) {
            // TODO: do something
            return null;
        }

        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_NAME_EXTENTION_JSON);
            }
        });

        if (files == null || files.length == 0) {
            return null;
        }

        List<Status> statuses = new ArrayList<Status>();

        for (File file : files) {
            try {
                String rawJSON = FileManager.readFirstLine(file);
                Status status = TwitterObjectFactory.createStatus(rawJSON);
                if (status != null) {
                    statuses.add(status);
                }
            } catch (IOException e) {
                // do nothing
            } catch (TwitterException e) {
                // do nothing
            }
        }

        return statuses;
    }

    private static void storeJSON(String rawJSON, String fileName) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(fileName);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            bw.write(rawJSON);
            bw.flush();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignore) {
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException ignore) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static String readFirstLine(File fileName) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(fileName);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            return br.readLine();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignore) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignore) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
}
