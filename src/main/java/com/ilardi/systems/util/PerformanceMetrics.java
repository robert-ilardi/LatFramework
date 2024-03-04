/**
 * Apr 29, 2009
 */
package com.ilardi.systems.util;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;

import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Robert C. Ilardi
 *
 */

@XmlType(name = "PerformanceMetrics")
public class PerformanceMetrics implements Serializable {

  private String name;
  private String description;
  private long hitCount;
  private long lastHit;
  private long maxExecutionTime;
  private long minExecutionTime;
  private long lastExecutionTime;
  private long totalExecutionTime;
  private double availableMemory;
  private double totalMemory;
  private double previousAvailableMemory;

  public PerformanceMetrics() {
    hitCount = 0;
    maxExecutionTime = 0;
    minExecutionTime = 0;
    lastExecutionTime = 0;
    totalExecutionTime = 0;
    availableMemory = 0.0;
    totalMemory = 0;
    previousAvailableMemory = 0.0;
  }

  /**
   * @return Returns the description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return Returns the hitCount.
   */
  public long getHitCount() {
    return hitCount;
  }

  /**
   * @param hitCnt The hitCount to set.
   */
  public void setHitCount(long hitCount) {
    this.hitCount = hitCount;
  }

  /**
   * @return Returns the lastExecutionTime.
   */
  public long getLastExecutionTime() {
    return lastExecutionTime;
  }

  /**
   * @param lastExecutionTime The lastExecutionTime to set.
   */
  public void setLastExecutionTime(long lastExecutionTime) {
    this.lastExecutionTime = lastExecutionTime;
  }

  /**
   * @return Returns the lastHit.
   */
  public long getLastHit() {
    return lastHit;
  }

  /**
   * @param lastHit The lastHit to set.
   */
  public void setLastHit(long lastHit) {
    this.lastHit = lastHit;
  }

  /**
   * @return Returns the maxExecutionTime.
   */
  public long getMaxExecutionTime() {
    return maxExecutionTime;
  }

  /**
   * @return Returns the minExecutionTime.
   */
  public long getMinExecutionTime() {
    return minExecutionTime;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return Returns the totalExecutionTime.
   */
  public long getTotalExecutionTime() {
    return totalExecutionTime;
  }

  public double getAverageExecutionTime() {
    return (totalExecutionTime / ((double) hitCount));
  }

  /**
   * @return Returns the availableMemory.
   */
  public double getAvailableMemory() {
    return availableMemory;
  }

  /**
   * @param availableMemory The availableMemory to set.
   */
  public void setAvailableMemory(double availableMemory) {
    this.availableMemory = availableMemory;
  }

  /**
   * @return Returns the totalMemory.
   */
  public double getTotalMemory() {
    return totalMemory;
  }

  /**
   * @param totalMemory The totalMemory to set.
   */
  public void setTotalMemory(double totalMemory) {
    this.totalMemory = totalMemory;
  }

  public void updateCalculations() {
    totalExecutionTime += lastExecutionTime;

    if (lastExecutionTime > maxExecutionTime) {
      maxExecutionTime = lastExecutionTime;
    }

    if (minExecutionTime == 0 || lastExecutionTime < minExecutionTime) {
      minExecutionTime = lastExecutionTime;
    }
  }

  /**
   * @return Returns the previousAvailableMemory.
   */
  public double getPreviousAvailableMemory() {
    return previousAvailableMemory;
  }

  /**
   * @param previousAvailableMemory The previousAvailableMemory to set.
   */
  public void setPreviousAvailableMemory(double previousAvailableMemory) {
    this.previousAvailableMemory = previousAvailableMemory;
  }

  public void setMaxExecutionTime(long maxExecutionTime) {
    this.maxExecutionTime = maxExecutionTime;
  }

  public void setMinExecutionTime(long minExecutionTime) {
    this.minExecutionTime = minExecutionTime;
  }

  public void setTotalExecutionTime(long totalExecutionTime) {
    this.totalExecutionTime = totalExecutionTime;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    NumberFormat nf = NumberFormat.getInstance();
    nf.setGroupingUsed(true);
    nf.setMaximumFractionDigits(3);

    sb.append("\n------------- Metrics -------------\n");

    sb.append("Name: ");
    sb.append(name);
    sb.append("\n");

    sb.append("Description: ");
    sb.append(description);
    sb.append("\n");

    sb.append("Total JVM Memory: ");
    sb.append(nf.format(totalMemory));
    sb.append(" MB\n");

    sb.append("Previously Available JVM Memory: ");
    sb.append(nf.format(previousAvailableMemory));
    sb.append(" MB\n");

    sb.append("Available JVM Memory: ");
    sb.append(nf.format(availableMemory));
    sb.append(" MB\n");

    sb.append("Available Memory Delta: ");
    sb.append(nf.format((availableMemory - previousAvailableMemory)));
    sb.append(" MB\n");

    sb.append("JVM Memory Free%: ");
    if (totalMemory != 0.0) {
      sb.append(nf.format((availableMemory / totalMemory) * 100));
      sb.append("%\n");
    }
    else {
      sb.append("N/A\n");
    }

    sb.append("Hit Count: ");
    sb.append(hitCount);
    sb.append("\n");

    sb.append("Last Hit: ");
    sb.append(new Date(lastHit));
    sb.append("\n");

    sb.append("Max Execution Time: ");
    sb.append(nf.format((maxExecutionTime / 1000.0d)));
    sb.append(" secs.\n");

    sb.append("Min Execution Time: ");
    sb.append(nf.format((minExecutionTime / 1000.0d)));
    sb.append(" secs.\n");

    sb.append("Last Execution Time: ");
    sb.append(nf.format((lastExecutionTime / 1000.0d)));
    sb.append(" secs.\n");

    sb.append("Average Execution Time: ");
    sb.append(nf.format((getAverageExecutionTime() / 1000.0d)));
    sb.append(" secs.\n");

    sb.append("Total Execution Time: ");
    sb.append(nf.format((totalExecutionTime / 1000.0d)));
    sb.append(" secs.\n");

    sb.append("-----------------------------------\n");

    return sb.toString();
  }

}
