package org.aulich.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.Configuration;
import org.aulich.model.ConfigurationModel;
import org.aulich.model.MeasurementTypeModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class delivers static values used in the applications jsp-pages.
 */
public class JspInjectorUtil {
    private static final Logger LOG =
            LogManager.getLogger(JspInjectorUtil.class);
    /**
     * Returns a Json-String with all configured Measurement-Types
     *
     * @return
     */
    public static String getMeasurementTypes() {
        ConfigurationModel cfgM =
                Configuration.getConfiguration().getConfigurationModel();
        List<MeasurementTypeModel> types = cfgM.getMeasurementTypes();
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(types);
            System.out.println(jsonString);
        } catch (Exception e) {
            LOG.error("Exception while creating Json", e);
        }
        return jsonString;
    }
}
