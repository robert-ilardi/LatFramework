/**
 * Created Feb 24, 2024
 */
package com.ilardi.systems.lat.mediator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilardi.systems.lat.LatException;
import com.ilardi.systems.lat.Version;
import com.ilardi.systems.util.ApplicationContext;
import com.ilardi.systems.util.IlardiSystemsException;

/**
 * @author rober
 *
 */

public final class LatServicesMediator {

  private static final Logger logger = LogManager.getLogger(LatServicesMediator.class);

  private static LatServicesMediator instance = null;

  private final Object mediatorLock;

  private volatile boolean mediatorInited = false;

  private volatile boolean checkEngineLightOn = false;

  private Exception lastException;

  private ApplicationContext appContext;

  private LatServicePool servicePool;

  private LatServicesMediator() {
    mediatorLock = new Object();
  }

  public static synchronized LatServicesMediator getInstance() throws LatException {
    if (instance == null) {
      instance = new LatServicesMediator();
      instance.init();
    }

    return instance;
  }

  public boolean isMediatorInited() {
    return mediatorInited;
  }

  public boolean isCheckEngineLightOn() {
    return checkEngineLightOn;
  }

  private void init() throws LatException {
    synchronized (mediatorLock) {
      try {
        if (mediatorInited) {
          checkEngineLightOn = true;
          logger.warn("Warning?!?!? " + Version.APP_SHORT_NAME + " Services Mediator Already Inited? How Was This Called Twice? Check Enine Light On!");
        }
        else {
          logger.info("Initializing " + Version.APP_SHORT_NAME + " Services Mediator...");

          printVersionInfo();

          obtainApplicationContext();

          createServicesPool();

          mediatorInited = true;
        } //End else block

        mediatorLock.notifyAll();
      } //End try block
      catch (Exception e) {
        LatException ie = new LatException("An error occurred while attemping to Initialize Services Mediator! System Message: " + e.getMessage(), e);
        lastException = ie;
        throw ie;
      }
    } //End synchronized block
  }

  private void printVersionInfo() {
    String ver;

    ver = Version.getVersionInfo();

    logger.info("Version Info:\n" + ver);
  }

  private void obtainApplicationContext() throws IlardiSystemsException {
    logger.info("Obtaining Application Context");
    appContext = ApplicationContext.getInstance();
  }

  private void createServicesPool() throws LatException {
    logger.info("Creating Services Pool");

    servicePool = new LatServicePool();
    servicePool.init();
  }

  public void destroy(boolean gracefully) throws LatException {
    synchronized (mediatorLock) {
      try {
        logger.info("Destroying " + Version.APP_SHORT_NAME + " Services Mediator...");

        servicePool.destroy(gracefully);
        servicePool = null;

        mediatorLock.notifyAll();
      } //End try block
      catch (Exception e) {
        checkEngineLightOn = true;
        LatException ie = new LatException("An error occurred while attemping to Destroy Services Mediator! System Message: " + e.getMessage(), e);
        lastException = ie;
        throw ie;
      }
    } //End synchronized block
  }

  public Exception getLastException() {
    return lastException;
  }

}
