package org.aulich.model;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Global singleton configuration bean.
 *
 * @author Thomas Aulich
 */
public class Configuration {
    private static final String XML_HEADER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    public static final String XML_CONFIGURATION_FILENAME = "Configuration.xml";
    private static Configuration instance;
    private ConfigurationModel configurationModel = new ConfigurationModel();
    private long lastLoaded;
    private static final Logger LOG = LogManager.getLogger(Configuration.class);

    /**
     * Private constructor follows the singleton-pattern.
     */
    private Configuration() {
        reloadConfiguration();
    }

    /**
     * Following the singleton pattern this method provides the object.
     *
     * @return INSTANCE
     */
    public static synchronized Configuration getConfiguration() {
        if (instance == null) {
            instance = new Configuration();
        }
        if (instance != null) {
            instance.reloadConfiguration();
        }
        return instance;
    }

    private void reloadConfiguration() {
        File configurationFile = getConfigurationFile();
        if (configurationFile == null || !configurationFile.exists()
                || !configurationFile.isFile()) {
            LOG.error("Configurationfile not available.");
            return;
        }
        if (configurationFile.lastModified() > lastLoaded) {
            LOG.error("reloadConfiguration() now");
            XStream xStream = new XStream();
            xStream.addPermission(NoTypePermission.NONE);
            xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
            xStream.allowTypeHierarchy(Collection.class);
            xStream.allowTypesByWildcard(
                    new String[]{"org.aulich.model.**"});
            xStream.processAnnotations(ConfigurationModel.class);
            configurationModel =
                    (ConfigurationModel) xStream.fromXML(configurationFile);
            if (configurationModel == null) {
                configurationModel = new ConfigurationModel();
            } else {
                lastLoaded = configurationFile.lastModified();
            }
        }
    }

    private File getConfigurationFile() {
        String configPath = System.getProperty("configpath");
        String filePath;
        if (configPath != null && !configPath.isEmpty()) {
            filePath = configPath + File.separator + XML_CONFIGURATION_FILENAME;
        } else {
            filePath = XML_CONFIGURATION_FILENAME;
        }
        LOG.debug("Configuration-filename: " + filePath);
        return new File(filePath);
    }

    public boolean save() {
        return save("");
    }

    public boolean save(String path) {
        File configurationFile;
        if (path != null && !path.isEmpty()) {
            File filePath = new File(path);
            configurationFile = new File(filePath + File.separator + XML_CONFIGURATION_FILENAME);
        } else {
            configurationFile = new File(XML_CONFIGURATION_FILENAME);
        }
        XStream xStream = new XStream();
        xStream.processAnnotations(Configuration.class);
        OutputStream outputStream = null;
        Writer writer = null;
        try {
            outputStream = new FileOutputStream(configurationFile);
            writer = new OutputStreamWriter(outputStream,
                    StandardCharsets.UTF_8);
            outputStream.write(XML_HEADER.getBytes(StandardCharsets.UTF_8));
            xStream.toXML(configurationModel, outputStream);
            writer.close();
            outputStream.close();
        } catch (Exception exp) {
            return false;
        }
        writer = null;
        outputStream = null;
        return true;
    }

    /**
     * Created for test-purpose.
     *
     * @return ok or not ok
     */
    public static boolean deleteConfigurationFile() {

        File configFile = new File(XML_CONFIGURATION_FILENAME);
        if (configFile.exists()) {
            return configFile.delete();
        } else {
            return false;
        }
    }

    public ConfigurationModel getConfigurationModel() {
        return configurationModel;
    }

    public void setConfigurationModel(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }
}
