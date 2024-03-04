/**
 * Created Mar 2, 2024
 */
package com.ilardi.systems.lat.mediator;

import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_CLASSNAME_PROPS_SUFFIX;
import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_LOAD_INDEX_PROPS_SUFFIX;
import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_MAX_INSTANCES_PROPS_SUFFIX;
import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_MIN_INSTANCES_PROPS_SUFFIX;
import static com.ilardi.systems.lat.LatConstants.LAT_SERVICE_POOL_SERVICE_TYPE_ENUM_PROPS_SUFFIX;

import java.util.Properties;

/**
 * @author robert.ilardi
 *
 */

public class LatServiceConfig {

  private String serviceName;

  private Properties serviceProps;

  private String serviceClassname;
  private LatServiceType serviceType;

  private int poolLoadIndex;
  private int minPoolInstances;
  private int maxPoolInstances;

  public LatServiceConfig(String serviceName, Properties serviceProps) {
    this.serviceName = serviceName;

    this.serviceProps = serviceProps;

    readProperties();
  }

  private void readProperties() {
    String tmp;

    tmp = serviceProps.getProperty(LAT_SERVICE_POOL_SERVICE_CLASSNAME_PROPS_SUFFIX);
    serviceClassname = tmp.trim();

    tmp = serviceProps.getProperty(LAT_SERVICE_POOL_SERVICE_TYPE_ENUM_PROPS_SUFFIX);
    tmp = tmp.trim();
    serviceType = LatServiceType.valueOf(tmp);

    tmp = serviceProps.getProperty(LAT_SERVICE_POOL_SERVICE_LOAD_INDEX_PROPS_SUFFIX);
    tmp = tmp.trim();
    poolLoadIndex = Integer.parseInt(tmp);

    tmp = serviceProps.getProperty(LAT_SERVICE_POOL_SERVICE_MIN_INSTANCES_PROPS_SUFFIX);
    tmp = tmp.trim();
    minPoolInstances = Integer.parseInt(tmp);

    tmp = serviceProps.getProperty(LAT_SERVICE_POOL_SERVICE_MAX_INSTANCES_PROPS_SUFFIX);
    tmp = tmp.trim();
    maxPoolInstances = Integer.parseInt(tmp);

    tmp = null;
  }

  public Properties getServiceProps() {
    return serviceProps;
  }

  public void setServiceProps(Properties serviceProps) {
    this.serviceProps = serviceProps;
  }

  public String getServiceClassname() {
    return serviceClassname;
  }

  public void setServiceClassname(String serviceClassname) {
    this.serviceClassname = serviceClassname;
  }

  public LatServiceType getServiceType() {
    return serviceType;
  }

  public void setServiceType(LatServiceType serviceType) {
    this.serviceType = serviceType;
  }

  public int getPoolLoadIndex() {
    return poolLoadIndex;
  }

  public void setPoolLoadIndex(int poolLoadIndex) {
    this.poolLoadIndex = poolLoadIndex;
  }

  public int getMinPoolInstances() {
    return minPoolInstances;
  }

  public void setMinPoolInstances(int minPoolInstances) {
    this.minPoolInstances = minPoolInstances;
  }

  public int getMaxPoolInstances() {
    return maxPoolInstances;
  }

  public void setMaxPoolInstances(int maxPoolInstances) {
    this.maxPoolInstances = maxPoolInstances;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Override
  public String toString() {
    return "LatServiceConfig [serviceName=" + serviceName + ", serviceProps=" + serviceProps + ", serviceClassname=" + serviceClassname + ", serviceType=" + serviceType + ", poolLoadIndex="
        + poolLoadIndex + ", minPoolInstances=" + minPoolInstances + ", maxPoolInstances=" + maxPoolInstances + "]";
  }

}
