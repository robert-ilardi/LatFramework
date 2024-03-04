/**
 * Jul 20, 2010
 */
package com.ilardi.systems.util;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Robert C. Ilardi
 *
 */

@XmlType(name = "PerformanceMarkers")
public class PerformanceMarkers implements Serializable {

  private String itemName;

  private long startTime;
  private long endTime;

  private double beforeMemoryUsage;
  private double afterMemoryUsage;

  private String description;

  public PerformanceMarkers() {}

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public double getBeforeMemoryUsage() {
    return beforeMemoryUsage;
  }

  public void setBeforeMemoryUsage(double beforeMemoryUsage) {
    this.beforeMemoryUsage = beforeMemoryUsage;
  }

  public double getAfterMemoryUsage() {
    return afterMemoryUsage;
  }

  public void setAfterMemoryUsage(double afterMemoryUsage) {
    this.afterMemoryUsage = afterMemoryUsage;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
