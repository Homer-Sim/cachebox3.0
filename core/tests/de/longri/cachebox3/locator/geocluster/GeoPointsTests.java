package de.longri.cachebox3.locator.geocluster;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.longri.cachebox3.locator.geocluster.Places.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;



public class GeoPointsTests {

    @Test
    public void testDistanceMiles() throws IOException {
        assertThat("Distance (mi)", GeoBoundingBox.distanceTo( LAS_VEGAS,SAN_DIEGO, GeoDistanceUnit.MILES), closeTo(250.0, 2.0));
    }

    @Test
    public void testDistanceKilometers() throws IOException {
        assertThat("Distance (km)", GeoBoundingBox.distanceTo(LAS_VEGAS, SAN_DIEGO, GeoDistanceUnit.KILOMETERS), closeTo(405, 3.0));
    }

    @Test
    public void testDistanceMeters() throws IOException {
        assertThat("Distance (m)", GeoBoundingBox.distanceTo( LAS_VEGAS,SAN_DIEGO, GeoDistanceUnit.METERS), closeTo(405000, 3000.0));
    }

    @Test
    public void testDistanceSame() throws IOException {
        assertThat("Distance zero",  GeoBoundingBox.distanceTo( BARDU,BARDU, GeoDistanceUnit.KILOMETERS), equalTo(0.0));
    }

    @Test
    public void testLocationOffsetBy() throws IOException {
        assertThat("Location offsetBy", GeoBoundingBox.distanceTo( GeoBoundingBox.offsetBy(BARDU,5.0, 180.0, GeoDistanceUnit.KILOMETERS),BARDU_5KM_SOUTH, GeoDistanceUnit.KILOMETERS), closeTo(0.0, 0.01));
    }
}
