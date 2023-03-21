package utils.io;

import java.io.File;

public class FolderUtils {
    public static File getDataFolder() {
        File file = new File("data");
        if(!file.exists())
            file.mkdirs();
        return file;
    }

    public static File getOsmFolder() {
        File rootFile = getDataFolder();
        File file = new File(rootFile, "osm");
        if(!file.exists())
            file.mkdirs();
        return file;
    }

    public static File getGraphhopperCacheFolder() {
        File rootFile = getDataFolder();
        File file = new File(rootFile, "cache");
        if(!file.exists())
            file.mkdirs();
        return file;
    }
}

