package org.aulich.utilities;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aulich.model.Configuration;
import org.aulich.processors.BufferProcessor;

/**
 * Derived from ServletContextListener, does initial
 * Application setup.
 */
@WebListener
public class AppStartupListener implements ServletContextListener {
    private static final Logger LOG = LogManager.getLogger(AppStartupListener.class);
    private Thread bufferProcessorThread;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.debug("Webapplication started!");
        // Instantiate Configuration bean
        Configuration cfg = Configuration.getConfiguration();
        // Start BufferProcessor
        bufferProcessorThread = new Thread(new BufferProcessor());
        bufferProcessorThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.debug("Webapplication stopped!");
    }
}