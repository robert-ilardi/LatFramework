/**
 * Created Feb 19, 2024
 */

package com.ilardi.systems.lat.web;

import static com.ilardi.systems.lat.LatConstants.LAT_REST_SERVICES_CLASSNAME_LIST;
import static com.ilardi.systems.lat.LatConstants.LAT_WEB_SERVER_CONTEXT_PATH;
import static com.ilardi.systems.lat.LatConstants.LAT_WEB_SERVER_NAME;
import static com.ilardi.systems.lat.LatConstants.LAT_WEB_SERVER_PATH_SPEC;
import static com.ilardi.systems.lat.LatConstants.LAT_WEB_SERVER_PORT;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.ilardi.systems.lat.LatException;
import com.ilardi.systems.lat.mediator.LatServicesMediator;
import com.ilardi.systems.util.ApplicationContext;
import com.ilardi.systems.util.IlardiSystemsException;

/**
 * @author robert.ilardi
 *
 */

public class LightWeightWebServer {

  private static final Logger logger = LogManager.getLogger(LightWeightWebServer.class);

  private static final String DEFAULT_WEB_SERVER_NAME = "LightWeightWebServer.LlmAssistantsTeam";

  private static final String JMX_ROOT_NAME = "Lat.LightWeightWebServer";

  private MBeanServer jmxServer;
  private ObjectName jmxObjName;
  private LightWeightWebServerJmxMBean lwWebServerMb;

  private final Object serverLock;

  private ApplicationContext appContext;

  private volatile boolean inited;
  private volatile boolean serverRunning;

  private String serverName;
  private int port;
  private String contextPath;
  private String pathSpec;
  private ArrayList<String> rsClassnameLst;

  private Server jetty;

  private LatServicesMediator servicesMediator;

  public LightWeightWebServer() {
    serverLock = new Object();
    inited = false;
    serverRunning = false;
    serverName = DEFAULT_WEB_SERVER_NAME;
  }

  public void init() throws LatException {
    synchronized (serverLock) {
      try {
        if (!inited) {
          logger.info("Initializing LightWeightWebServer...");

          createAppContect();

          readProperties();

          createServicesMediator();

          setupJmx();

          inited = true;
        } //end if block
        else {
          logger.warn("LightWeightWebServer ALREADY Initialized!");
        }
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attempting to Initialize LightWeightWebServer! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  private void readProperties() throws IlardiSystemsException {
    String tmp;
    String[] tmpArr;

    logger.info("Reading Properties");

    tmp = appContext.getUserProperty(LAT_WEB_SERVER_PORT);
    tmp = tmp.trim();
    port = Integer.parseInt(tmp);

    tmp = appContext.getUserProperty(LAT_WEB_SERVER_NAME);
    tmp = tmp.trim();
    serverName = tmp;

    tmp = appContext.getUserProperty(LAT_WEB_SERVER_CONTEXT_PATH);
    tmp = tmp.trim();
    contextPath = tmp;

    tmp = appContext.getUserProperty(LAT_WEB_SERVER_PATH_SPEC);
    tmp = tmp.trim();
    pathSpec = tmp;

    tmp = appContext.getUserProperty(LAT_REST_SERVICES_CLASSNAME_LIST);
    tmp = tmp.trim();

    tmpArr = tmp.split(",");
    rsClassnameLst = new ArrayList<String>();

    for (int i = 0; i < tmpArr.length; i++) {
      tmp = tmpArr[i];
      tmp = tmp.trim();

      rsClassnameLst.add(tmp);
    }
  }

  private void createAppContect() throws LatException {
    try {
      logger.info("Creating Application Context");

      appContext = ApplicationContext.getInstance();
      appContext.loadUserProperties();
    } //End try block
    catch (Exception e) {
      throw new LatException("An error occurred while attempting to Create Application Context. System Message: " + e.getMessage(), e);
    }
  }

  private void setupJmx() throws LatException {
    StringBuilder sb;
    String tmp;
    LightWeightWebServerJmx mbImpl;

    try {
      logger.info("Setting Up JMX Management for LightWeightWebServer");

      sb = new StringBuilder();
      sb.append(JMX_ROOT_NAME);
      sb.append(":appDbName=");
      sb.append(serverName);

      tmp = sb.toString();
      sb = null;

      mbImpl = new LightWeightWebServerJmx();
      mbImpl.setLightWeightWebServer(this);

      lwWebServerMb = mbImpl;
      mbImpl = null;

      jmxServer = ManagementFactory.getPlatformMBeanServer();
      jmxObjName = new ObjectName(tmp);
      tmp = null;

      jmxServer.registerMBean(lwWebServerMb, jmxObjName);
    } //End try block
    catch (Exception e) {
      throw new LatException("An error occurred while attempting to Setup JMX. System Message: " + e.getMessage(), e);
    }
  }

  private void createServicesMediator() throws LatException {
    logger.info("Creating Services Mediator...");

    servicesMediator = LatServicesMediator.getInstance();
  }

  public void enableShutdownHook() {
    logger.info("Enabling Shutdown Hook");

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          if (serverRunning) {
            logger.info("Running Shutdown Hook");
            LightWeightWebServer.this.stopServer();
          }
        }
        catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
    });
  }

  public void startServer() throws LatException {
    synchronized (serverLock) {
      try {
        if (!serverRunning) {
          logger.info("Starting Light Weight Web Server...");

          startJetty();

          serverRunning = true;
        }
        else {
          logger.warn("Light Weight Web Server Already Running!");
        }
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attempting to Start Server! System Message: " + e.getMessage(), e);
      }
      finally {
        serverLock.notifyAll();
      }
    } //End synchronized block
  }

  private void startJetty() throws LatException {
    try {
      ServletContextHandler handler;

      logger.info(">> Starting Jetty on Port: " + port);

      jetty = new Server(port);

      handler = createRestContainer();
      jetty.setHandler(handler);

      jetty.start();
    } //End try block
    catch (Exception e) {
      throw new LatException("An error occurred while attempting to Start Jetty. System Message: " + e.getMessage(), e);
    }
  }

  private ServletContextHandler createRestContainer() throws ClassNotFoundException {
    ServletContextHandler handler;
    ResourceConfig resConf;

    logger.info(">>>> Creating REST Container");

    resConf = createServletConfig();

    handler = createServletHandler(resConf);

    return handler;
  }

  private ResourceConfig createServletConfig() throws ClassNotFoundException {
    ResourceConfig resConf;
    String cn;
    Class<?> c;

    logger.info(">>>>>> Creating REST Container");

    resConf = new ResourceConfig();

    for (int i = 0; i < rsClassnameLst.size(); i++) {
      cn = rsClassnameLst.get(i);

      logger.info("Registering REST Service Class: " + cn);

      c = Class.forName(cn);

      resConf.register(c);
    }

    return resConf;
  }

  private ServletContextHandler createServletHandler(ResourceConfig resConf) {
    ServletContextHandler handler;
    ServletHolder holder;
    ServletContainer container;

    logger.info(">>>>>> Creating Servlet Handler");

    handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
    handler.setContextPath(contextPath);

    container = new ServletContainer(resConf);

    holder = new ServletHolder(container);

    handler.addServlet(holder, pathSpec);

    return handler;
  }

  private void stopJetty() throws LatException {
    synchronized (serverLock) {
      try {
        logger.info(">> Stopping Jetty");

        jetty.stop();
        jetty.destroy();
        jetty = null;
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attempting to Stop Jetty. System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  private void destroyServicesMediator(boolean gracefully) throws LatException {
    synchronized (serverLock) {
      try {
        logger.info(">> Destroying Services Mediator");

        servicesMediator.destroy(gracefully);
        
        servicesMediator = null;
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attempting to Destroy Services Mediator. System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  public void stopServer() {
    synchronized (serverLock) {
      try {
        if (serverRunning) {
          logger.info("Stopping Light Weight Web Server...");

          stopJetty();

          destroyServicesMediator(true);

          serverRunning = false;
        }
        else {
          logger.warn("Light Weight Web Server NOT Running!");
        }
      } //End try block
      catch (Exception e) {
        logger.error("An error occurred while attempting to Stop Server! System Message: " + e.getMessage(), e);
      }
      finally {
        serverLock.notifyAll();
      }
    } //End synchronized block
  }

  public boolean isServerRunning() {
    synchronized (serverLock) {
      return serverRunning;
    } //End synchronized block
  }

  public void waitWhileServerRunning() throws InterruptedException {
    synchronized (serverLock) {
      StringBuilder sb;
      String tmp;

      sb = new StringBuilder();

      sb.append("Thread[name='");
      sb.append(Thread.currentThread().getName());

      sb.append("', id=");
      sb.append(Thread.currentThread().getId());

      sb.append("] Entering Server Running Waiting State.");

      tmp = sb.toString();
      sb = null;

      logger.info(tmp);
      tmp = null;

      while (serverRunning) {
        serverLock.wait();
      }

      sb = new StringBuilder();

      sb.append("Thread[name='");
      sb.append(Thread.currentThread().getName());

      sb.append("', id=");
      sb.append(Thread.currentThread().getId());

      sb.append("] Exited Server Running Waiting State.");

      tmp = sb.toString();
      sb = null;

      logger.info(tmp);
      tmp = null;
    } //End synchronized block
  }

  public static void main(String[] args) {
    int exitCd;
    LightWeightWebServer webServer = null;

    try {
      webServer = new LightWeightWebServer();
      webServer.init();

      webServer.startServer();

      webServer.waitWhileServerRunning();

      exitCd = 0;

      Thread.sleep(100); //In case JMX is still running...
    } //End try block
    catch (Exception e) {
      exitCd = 1;

      try {
        logger.error("An error occurred while running Light Weight Web Server! System Message: " + e.getMessage(), e);
      } //End try block
      catch (Throwable t) {
        t.printStackTrace();
      } //End catch block
    } //End catch block
    finally {
      try {
        if (webServer != null && webServer.isServerRunning()) {
          webServer.stopServer();
          webServer = null;
        }
      } //End try block
      catch (Exception e) {
        try {
          logger.error("An error occurred while attempting to Stop Light Weight Web Server! System Message: " + e.getMessage(), e);
        } //End try block
        catch (Throwable t) {
          t.printStackTrace();
        } //End catch block
      } //End catch block
    } //End finally block

    System.exit(exitCd);
  } //End main method

}
