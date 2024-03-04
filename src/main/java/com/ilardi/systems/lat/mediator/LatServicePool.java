/**
 * Created Feb 24, 2024
 */
package com.ilardi.systems.lat.mediator;

import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_LIST;
import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_PROPS_PREFIX;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilardi.systems.lat.LatException;
import com.ilardi.systems.lat.services.BaseLatService;
import com.ilardi.systems.util.ApplicationContext;
import com.ilardi.systems.util.IlardiSystemsException;
import com.ilardi.systems.util.MultiObjectPool;
import com.ilardi.systems.util.StringUtils;

/**
 * @author rober
 *
 */

public class LatServicePool {

  private static final Logger logger = LogManager.getLogger(LatServicePool.class);

  private final Object lspLock;

  private volatile boolean inited;

  private ApplicationContext appContext;

  private LatServicePoolConfig servicePoolConfig;

  private MultiObjectPool<String, BaseLatService> servicePool;

  public LatServicePool() {
    inited = false;
    lspLock = new Object();
  }

  public void init() throws LatException {
    synchronized (lspLock) {
      try {
        if (!inited) {
          logger.info("Initializing Services Pool");

          obtainApplicationContext();

          loadServices();

          inited = true;
          lspLock.notifyAll();
        } //End if block
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attemping to Initialize Services Pool! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  private void obtainApplicationContext() throws IlardiSystemsException {
    synchronized (lspLock) {
      logger.info("Obtaining Application Context");
      appContext = ApplicationContext.getInstance();
    } //End synchronized block
  }

  private void loadServices() throws LatException {
    synchronized (lspLock) {
      try {
        logger.info("Initializing Service Pool");

        if (servicePool == null) {
          logger.info("Creating Service Multi Object Pool Instance...");
          servicePool = new MultiObjectPool<String, BaseLatService>();
        }
        else {
          logger.warn("Service Multi Object Pool Instance ALEADY Exists!");
        }

        loadServicePoolConfig();
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attemping to Initialize Services Pool! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block
  }

  private void loadServicePoolConfig() throws IlardiSystemsException {
    synchronized (lspLock) {
      String serviceLstStr, serviceName;
      String[] serviceLstArr;
      LatServiceConfig config;

      logger.info("Loading Service Pool Config");

      servicePoolConfig = new LatServicePoolConfig();

      serviceLstStr = appContext.getUserProperty(LAT_SERVICE_POOL_SERVICE_LIST);

      serviceLstArr = serviceLstStr.split(",");

      serviceLstArr = StringUtils.Trim(serviceLstArr);

      for (int i = 0; i < serviceLstArr.length; i++) {
        serviceName = serviceLstArr[i];

        config = loadServiceConfig(serviceName);

        servicePoolConfig.addServiceConfig(config);
      }

    } //End synchronized block
  }

  private LatServiceConfig loadServiceConfig(String serviceName) {
    LatServiceConfig config;
    StringBuilder sb;
    String snPropPrefix;
    Properties props;

    logger.info("Loading Service Config for Service: " + serviceName);

    sb = new StringBuilder();
    sb.append(LAT_SERVICE_POOL_SERVICE_PROPS_PREFIX);
    sb.append(".");
    sb.append(serviceName);
    sb.append(".");
    snPropPrefix = sb.toString();
    sb = null;

    props = appContext.getUserPropertiesByPropPrefix(snPropPrefix);

    props = StringUtils.RemovePrefixFromPropNames(props, snPropPrefix);

    config = new LatServiceConfig(serviceName, props);

    logger.info(config);

    return config;
  }

  public void destroy(boolean gracefully) throws LatException {
    synchronized (lspLock) {
      try {
        logger.info("Destroying Service Pool. Gracefully? " + (gracefully ? "YES" : "NO"));

        servicePool.destroy(gracefully);
        servicePool = null;
      } //End try block
      catch (Exception e) {
        throw new LatException("An error occurred while attemping to Initialize Services Pool! System Message: " + e.getMessage(), e);
      }
    } //End synchronized block   
  }

}
