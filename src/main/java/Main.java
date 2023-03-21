import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
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
                .putObject("graph.encoded_values", "max_slope,road_class,road_class_link,road_environment," +
                        "max_speed,road_access,track_type,surface,average_slope,smoothness,bike_network")
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
                                .addToPriority(If("average_slope >= 5", MULTIPLY, "0.1"))),
                new CustomProfile("custom_bike")
                        .setCustomModel(new CustomModel()
                                .addToPriority(If("bike_network == INTERNATIONAL", Statement.Op.MULTIPLY, "1.0"))
                                .addToPriority(If("bike_network == NATIONAL", Statement.Op.MULTIPLY, "0.8"))
                                .addToPriority(If("bike_network == REGIONAL", Statement.Op.MULTIPLY, "0.5"))
                                .addToPriority(If("bike_network == LOCAL", Statement.Op.MULTIPLY, "0.3"))
                                .addToPriority(If("bike_network == OTHER", Statement.Op.MULTIPLY, "0.1"))
                                .addToPriority(If("bike_network == MISSING", Statement.Op.MULTIPLY, "0.1"))
                                .addToPriority(If("road_class == MOTORWAY", Statement.Op.MULTIPLY, "0.0"))
                                .addToPriority(If("road_class == TRUNK", Statement.Op.MULTIPLY, "0.0"))
                                .addToPriority(If("road_class == PRIMARY", Statement.Op.MULTIPLY, "0.9"))
                                .addToPriority(If("road_class == SECONDARY", Statement.Op.MULTIPLY, "0.6"))
                                .addToPriority(If("road_class == TERTIARY", Statement.Op.MULTIPLY, "0.3"))
                                .addToPriority(If("road_class == RESIDENTIAL", Statement.Op.MULTIPLY, "0.1"))
                                .addToPriority(If("road_class == UNCLASSIFIED", Statement.Op.MULTIPLY, "0.5"))
                                .addToPriority(If("surface == ASPHALT", Statement.Op.MULTIPLY, "1.0"))
                                .addToPriority(If("surface == CONCRETE", Statement.Op.MULTIPLY, "0.9"))
                                .addToPriority(If("surface == PAVED", Statement.Op.MULTIPLY, "0.8"))
                                .addToPriority(If("surface == COMPACTED", Statement.Op.MULTIPLY, "0.6"))
                                .addToPriority(If("surface == UNPAVED", Statement.Op.MULTIPLY, "0.1"))
                                .addToPriority(If("smoothness == EXCELLENT", Statement.Op.MULTIPLY, "1.0"))
                                .addToPriority(If("smoothness == GOOD", Statement.Op.MULTIPLY, "0.8"))
                                .addToPriority(If("smoothness == INTERMEDIATE", Statement.Op.MULTIPLY, "0.6"))
                                .addToPriority(If("smoothness == BAD", Statement.Op.MULTIPLY, "0.3"))
                                .addToPriority(If("smoothness == VERY_BAD", Statement.Op.MULTIPLY, "0.3"))
                                .addToPriority(If("smoothness == HORRIBLE", Statement.Op.MULTIPLY, "0.0"))
                                .addToPriority(If("smoothness == VERY_HORRIBLE", Statement.Op.MULTIPLY, "0.0"))
                                .addToPriority(If("smoothness == IMPASSABLE", Statement.Op.MULTIPLY, "0.0"))
                                .addToPriority(If("smoothness == OTHER", Statement.Op.MULTIPLY, "0.3"))
                                .setDistanceInfluence(69.0)
                                .setHeadingPenalty(22.0)
                        )
        );

        graphhopper.importOrLoad();
    }
}
