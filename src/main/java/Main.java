import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.weighting.custom.CustomProfile;
import com.graphhopper.util.CustomModel;
import utils.io.FolderUtils;

import java.io.File;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

public class Main {

    public static void main(final String[] args) throws Exception {

        File osmFile = new File(FolderUtils.getOsmFolder(), "osm.pbf");
        File cacheFolder = FolderUtils.getGraphhopperCacheFolder();

        GraphHopperConfig config = new GraphHopperConfig()
                .putObject("graph.vehicles", "bike,car,foot,wheelchair,roads")
                .putObject("prepare.min_network_size", 200)
                .putObject("datareader.file", osmFile.getAbsolutePath())
                .putObject("graph.location", cacheFolder.getAbsolutePath())
                .putObject("graph.encoded_values", "max_slope,road_class,road_class_link,road_environment,max_speed,road_access,track_type,surface,average_slope")
                .putObject("custom_model_folder", "./src/test/resources/com/graphhopper/application/resources")
                .putObject("import.osm.ignored_highways", "");
        GraphHopper graphhopper = new GraphHopper().init(config);

        graphhopper.setProfiles(
                new Profile("car")
                        .setVehicle("car")
                        .setTurnCosts(false)
                        .setWeighting("fastest"),
                new CustomProfile("custom_foot")
                        .setCustomModel(new CustomModel()
                                .addToPriority(If("road_class == STEPS", MULTIPLY, "0.0"))
                                .addToPriority(If("road_class == FOOTWAY", MULTIPLY, "0.5"))
                                .addToPriority(If("surface == DIRT", MULTIPLY, "0.0"))
                                .addToPriority(If("surface == SAND", MULTIPLY, "0.0"))
                                .addToPriority(If("surface == PAVED", MULTIPLY, "1.0"))
                                .addToPriority(If("average_slope >= 5", MULTIPLY, "0.1")))
        );

        graphhopper.importOrLoad();
    }
}
