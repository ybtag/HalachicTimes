/*
 * Copyright 2012, Moshe Waisberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.times.compass;

import android.hardware.SensorManager;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.times.compass.lib.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test compass calculations.
 *
 * @author Moshe Waisberg
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class CompassTest {

    @Test
    public void rotationMatrixPortrait0() {
        float[] gravity = {0.0f, 9.8f, 0.0f};
        float[] geomagnetic = {22.0f, 5.90f, -43.10f};
        float[] matrixRExpected = {0.8906765f, -0.0f, 0.45463768f, 0.45463768f, 0.0f, -0.8906765f, 0.0f, 1.0f, 0.0f};

        float[] matrixR = new float[9];
        assertTrue(SensorManager.getRotationMatrix(matrixR, null, gravity, geomagnetic));
        assertEquals(matrixRExpected[0], matrixR[0]);
        assertEquals(matrixRExpected[1], matrixR[1]);
        assertEquals(matrixRExpected[2], matrixR[2]);
        assertEquals(matrixRExpected[3], matrixR[3]);
        assertEquals(matrixRExpected[4], matrixR[4]);
        assertEquals(matrixRExpected[5], matrixR[5]);
        assertEquals(matrixRExpected[6], matrixR[6]);
        assertEquals(matrixRExpected[7], matrixR[7]);
        assertEquals(matrixRExpected[8], matrixR[8]);

        float[] orientation = new float[3];
        SensorManager.getOrientation(matrixR, orientation);
        assertEquals(-0.0f, orientation[0]);
        assertEquals((float) (Math.PI / -2), orientation[1]);
        assertEquals(-0.0f, orientation[2]);
    }

    @Test
    public void rotationMatrixLandscape270() {
        float[] gravity = {9.8f, 0.0f, 0.0f};
        float[] geomagnetic = {5.90f, -22.0f, -43.10f};
        float[] matrixRExpected = {0.0f, -0.8906765f, 0.45463768f, 0.0f, -0.45463768f, -0.8906765f, 1.0f, 0.0f, 0.0f};

        float[] matrixR = new float[9];
        assertTrue(SensorManager.getRotationMatrix(matrixR, null, gravity, geomagnetic));
        assertEquals(matrixRExpected[0], matrixR[0]);
        assertEquals(matrixRExpected[1], matrixR[1]);
        assertEquals(matrixRExpected[2], matrixR[2]);
        assertEquals(matrixRExpected[3], matrixR[3]);
        assertEquals(matrixRExpected[4], matrixR[4]);
        assertEquals(matrixRExpected[5], matrixR[5]);
        assertEquals(matrixRExpected[6], matrixR[6]);
        assertEquals(matrixRExpected[7], matrixR[7]);
        assertEquals(matrixRExpected[8], matrixR[8]);

        float[] orientation = new float[3];
        SensorManager.getOrientation(matrixR, orientation);
        assertEquals(-2.0427618f, orientation[0]);
        assertEquals(-0.0f, orientation[1]);
        assertEquals((float) (Math.PI / -2), orientation[2]);
    }

    @Test
    public void rotationMatrixPortrait0_LG() {
        float[] gravity = {-0.10595338f, -0.528306f, 9.604765f};
        float[] geomagnetic = {26.638561f, -13.187685f, 29.62915f};
        float[] matrixRExpected = {-0.3933655f, -0.9177463f, -0.05481959f, 0.91931605f, -0.39335173f, -0.011494861f, -0.011014016f, -0.054918222f, 0.9984301f};
        float[] mapRExpected = matrixRExpected;
        float[] orientationExpected = {-1.9757174f, 0.054945864f, 0.011030887f};
        float[] orientationMappedExpected = orientationExpected;

        float[] matrixR = new float[9];
        assertTrue(SensorManager.getRotationMatrix(matrixR, null, gravity, geomagnetic));
        assertEquals(matrixRExpected[0], matrixR[0]);
        assertEquals(matrixRExpected[1], matrixR[1]);
        assertEquals(matrixRExpected[2], matrixR[2]);
        assertEquals(matrixRExpected[3], matrixR[3]);
        assertEquals(matrixRExpected[4], matrixR[4]);
        assertEquals(matrixRExpected[5], matrixR[5]);
        assertEquals(matrixRExpected[6], matrixR[6]);
        assertEquals(matrixRExpected[7], matrixR[7]);
        assertEquals(matrixRExpected[8], matrixR[8]);

        float[] orientation = new float[3];
        SensorManager.getOrientation(matrixR, orientation);
        assertEquals(orientationExpected[0], orientation[0]);
        assertEquals(orientationExpected[1], orientation[1]);
        assertEquals(orientationExpected[2], orientation[2]);

        float[] mapR = new float[9];
        SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_Y, mapR);
        assertEquals(mapRExpected[0], mapR[0]);
        assertEquals(mapRExpected[1], mapR[1]);
        assertEquals(mapRExpected[2], mapR[2]);
        assertEquals(mapRExpected[3], mapR[3]);
        assertEquals(mapRExpected[4], mapR[4]);
        assertEquals(mapRExpected[5], mapR[5]);
        assertEquals(mapRExpected[6], mapR[6]);
        assertEquals(mapRExpected[7], mapR[7]);
        assertEquals(mapRExpected[8], mapR[8]);

        SensorManager.getOrientation(mapR, orientation);
        assertEquals(orientationMappedExpected[0], orientation[0]);
        assertEquals(orientationMappedExpected[1], orientation[1]);
        assertEquals(orientationMappedExpected[2], orientation[2]);
    }

    @Test
    public void rotationMatrixLandscape270_LG() {
        float[] gravity = {-0.15977478f, -0.56747437f, 9.59285f};
        float[] geomagnetic = {47.165108f, -17.170334f, 18.540382f};
        float[] matrixRExpected = {-0.32009178f, -0.94540405f, -0.061257623f, 0.9472406f, -0.32050735f, -0.0031830538f, -0.016624246f, -0.059044573f, 0.9981168f};
        float[] mapRExpected = {-0.94540405f, 0.32009178f, -0.061257623f, -0.32050735f, -0.9472406f, -0.0031830538f, -0.059044573f, 0.016624246f, 0.9981168f};
        float[] orientationExpected = {-1.8976527f, 0.059078936f, 0.016654072f};
        float[] orientationMappedExpected = {2.8157196f, -0.016625011f, 0.059087116f};

        float[] matrixR = new float[9];
        assertTrue(SensorManager.getRotationMatrix(matrixR, null, gravity, geomagnetic));
        assertEquals(matrixRExpected[0], matrixR[0]);
        assertEquals(matrixRExpected[1], matrixR[1]);
        assertEquals(matrixRExpected[2], matrixR[2]);
        assertEquals(matrixRExpected[3], matrixR[3]);
        assertEquals(matrixRExpected[4], matrixR[4]);
        assertEquals(matrixRExpected[5], matrixR[5]);
        assertEquals(matrixRExpected[6], matrixR[6]);
        assertEquals(matrixRExpected[7], matrixR[7]);
        assertEquals(matrixRExpected[8], matrixR[8]);

        float[] orientation = new float[3];
        SensorManager.getOrientation(matrixR, orientation);
        assertEquals(orientationExpected[0], orientation[0]);
        assertEquals(orientationExpected[1], orientation[1]);
        assertEquals(orientationExpected[2], orientation[2]);

        float[] mapR = new float[9];
        SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, mapR);
        assertEquals(mapRExpected[0], mapR[0]);
        assertEquals(mapRExpected[1], mapR[1]);
        assertEquals(mapRExpected[2], mapR[2]);
        assertEquals(mapRExpected[3], mapR[3]);
        assertEquals(mapRExpected[4], mapR[4]);
        assertEquals(mapRExpected[5], mapR[5]);
        assertEquals(mapRExpected[6], mapR[6]);
        assertEquals(mapRExpected[7], mapR[7]);
        assertEquals(mapRExpected[8], mapR[8]);

        SensorManager.getOrientation(mapR, orientation);
        assertEquals(orientationMappedExpected[0], orientation[0]);
        assertEquals(orientationMappedExpected[1], orientation[1]);
        assertEquals(orientationMappedExpected[2], orientation[2]);
    }

}
