package org.aulich.utilities;

import org.aulich.model.MeasurementTypeModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataMapperUtilTest {
    @Test
    public void test() {
        System.setProperty("configpath","C:\\Users\\thoma\\IdeaProjects" +
                "\\weatherstation\\resources");
        DataMapperUtil util = new DataMapperUtil();
        MeasurementTypeModel model = util.getDerivedType("TempOutF");
        if (model==null) {
            fail();
        } else {
            assertEquals("TempOutC", model.getMeasurementId());
        }
    }
}
