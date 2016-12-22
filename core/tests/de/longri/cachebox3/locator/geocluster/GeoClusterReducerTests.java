package de.longri.cachebox3.locator.geocluster;


import org.junit.jupiter.api.Test;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static de.longri.cachebox3.locator.geocluster.Places.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;


public class GeoClusterReducerTests {

    @Test
    public void testReduceNone() {

        GeoClusterReducer reducer = new GeoClusterReducer(0.0);
        List<GeoCluster> clusters = new ArrayList<GeoCluster>();
        assertThat(reducer.reduce(clusters).size(), equalTo(0));

        clusters.add(new GeoCluster(DENVER));
        assertThat("Cluster after adding Denver", reducer.reduce(clusters), hasItems(
                new GeoCluster(1, DENVER, new GeoBoundingBox(DENVER))));

        clusters.add(new GeoCluster(DENVER));
        assertThat("Cluster after adding Denver again", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER))));

        clusters.add(new GeoCluster(SAN_DIEGO));
        assertThat("Cluster after adding San Diego", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER)),
                new GeoCluster(1, SAN_DIEGO, new GeoBoundingBox(SAN_DIEGO))));

        clusters.add(new GeoCluster(LAS_VEGAS));
        assertThat("Cluster after adding Las Vegas", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER)),
                new GeoCluster(1, SAN_DIEGO, new GeoBoundingBox(SAN_DIEGO)),
                new GeoCluster(1, LAS_VEGAS, new GeoBoundingBox(LAS_VEGAS))));
    }

    @Test
    public void testReduceSome() {

        GeoClusterReducer reducer = new GeoClusterReducer(0.5);
        List<GeoCluster> clusters = new ArrayList<GeoCluster>();
        assertThat(reducer.reduce(clusters).size(), equalTo(0));

        clusters.add(new GeoCluster(DENVER));
        assertThat("Cluster after adding Denver", reducer.reduce(clusters), hasItems(
                new GeoCluster(1, DENVER, new GeoBoundingBox(DENVER))));

        clusters.add(new GeoCluster(DENVER));
        assertThat("Cluster after adding Denver again", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER))));

        clusters.add(new GeoCluster(SAN_DIEGO));
        assertThat("Cluster after adding San Diego", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER)),
                new GeoCluster(1, SAN_DIEGO, new GeoBoundingBox(SAN_DIEGO))));

        clusters.add(new GeoCluster(LAS_VEGAS));
        assertThat("Cluster after adding Las Vegas", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER)),
                new GeoCluster(2, new GeoPoint(34.4500, -116.1500), new GeoBoundingBox(SAN_DIEGO).extend(LAS_VEGAS))));
    }

    @Test
    public void testReduceAll() {

        GeoClusterReducer reducer = new GeoClusterReducer(1.0);
        List<GeoCluster> clusters = new ArrayList<GeoCluster>();
        assertThat(reducer.reduce(clusters).size(), equalTo(0));

        clusters.add(new GeoCluster(DENVER));
        assertThat("Cluster after adding Denver", reducer.reduce(clusters), hasItems(
                new GeoCluster(1, DENVER, new GeoBoundingBox(DENVER))));

        clusters.add(new GeoCluster(DENVER));
        assertThat("Cluster after adding Denver again", reducer.reduce(clusters), hasItems(
                new GeoCluster(2, DENVER, new GeoBoundingBox(DENVER))));

        clusters.add(new GeoCluster(SAN_DIEGO));
        assertThat("Cluster after adding San Diego", reducer.reduce(clusters), hasItems(
                new GeoCluster(3, new GeoPoint(37.4400, -108.95666666666666), new GeoBoundingBox(DENVER).extend(SAN_DIEGO))));

        clusters.add(new GeoCluster(LAS_VEGAS));
        assertThat("Cluster after adding Las Vegas", reducer.reduce(clusters), hasItems(
                new GeoCluster(4, new GeoPoint(37.1000, -110.5100), new GeoBoundingBox(DENVER).extend(SAN_DIEGO).extend(LAS_VEGAS))));
    }
}
