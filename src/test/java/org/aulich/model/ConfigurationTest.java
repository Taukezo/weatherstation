package org.aulich.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigurationTest {
    @Test
    public void test() {
        Configuration.deleteConfigurationFile();
        Configuration cfg = Configuration.getConfiguration();
        ConfigurationModel cfgM = cfg.getConfigurationModel();
        cfgM.setBufferPath("C:\\temp");
        if (cfg.save("resources")) {
            assertTrue(true);
        } else {
            fail();
        }
    }
}
