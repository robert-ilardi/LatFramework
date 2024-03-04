/**
 * Apr 29, 2009
 */

package com.ilardi.systems.util;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Robert C. Ilardi
 */

@XmlType(name = "PerformanceMetricsReport")
public class PerformanceMetricsReport implements Serializable {

  private static String[] HEADER = { "Name", "Description", "Total Mem(MB)", "Prev Free Mem(MB)", "Free Mem(MB)", "Mem Delta(MB)", "Mem Free %", "Hits", "Last Hit", "Max Exec(s)", "Min Exec(s)",
      "Last Exec(s)", "Avg Exec(s)" };

  private HashMap<String, PerformanceMetrics> metricsMap;

  public PerformanceMetricsReport() {
    metricsMap = new HashMap<String, PerformanceMetrics>();
  }

  public HashMap<String, PerformanceMetrics> getMetricsMap() {
    return metricsMap;
  }

  public void setMetricsMap(HashMap<String, PerformanceMetrics> metricsMap) {
    this.metricsMap = metricsMap;
  }

  public ArrayList<PerformanceMetrics> getMetricsList() {
    return new ArrayList<PerformanceMetrics>(metricsMap.values());
  }

  public synchronized void addPerformanceMetrics(PerformanceMetrics itemStats) {
    if (itemStats != null) {
      metricsMap.put(itemStats.getName(), itemStats);
    }
  }

  public PerformanceMetrics getPerformanceMetrics(String itemName) {
    return (PerformanceMetrics) metricsMap.get(itemName);
  }

  public synchronized void clear() {
    metricsMap.clear();
  }

  public void updatePerformanceMetrics(String itemName, long startTime, long endTime, double beforeMemoryUsage, double afterMemoryUsage) {
    updatePerformanceMetrics(itemName, startTime, endTime, beforeMemoryUsage, afterMemoryUsage, null);
  }

  public void updatePerformanceMetrics(String itemName, long startTime, long endTime, double beforeMemoryUsage, double afterMemoryUsage, String description) {
    PerformanceMetrics itemMetrics = null;
    long executionTime;

    synchronized (this) {
      itemMetrics = (PerformanceMetrics) metricsMap.get(itemName);

      if (itemMetrics == null) {
        itemMetrics = new PerformanceMetrics();
        itemMetrics.setName(itemName);
        itemMetrics.setTotalMemory(SystemUtils.GetTotalMemory());
        itemMetrics.setDescription(description);
        metricsMap.put(itemMetrics.getName(), itemMetrics);
      }
    }

    if (itemMetrics != null) {
      synchronized (itemMetrics) {
        executionTime = endTime - startTime;

        itemMetrics.setHitCount(itemMetrics.getHitCount() + 1);
        itemMetrics.setLastHit(startTime);
        itemMetrics.setLastExecutionTime(executionTime);
        itemMetrics.setAvailableMemory(afterMemoryUsage);
        itemMetrics.setPreviousAvailableMemory(beforeMemoryUsage);

        itemMetrics.updateCalculations();
      }
    }
  }

  public ArrayList<ArrayList<String>> getDataList() {
    Collection<PerformanceMetrics> items = metricsMap.values();
    Iterator<PerformanceMetrics> iter = items.iterator();
    ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
    ArrayList<String> row;
    PerformanceMetrics metrics;
    NumberFormat nf = NumberFormat.getInstance();

    nf.setGroupingUsed(true);
    nf.setMaximumFractionDigits(3);

    while (iter.hasNext()) {
      metrics = (PerformanceMetrics) iter.next();
      row = new ArrayList<String>();

      row.add(metrics.getName());
      row.add(metrics.getDescription() != null ? metrics.getDescription() : "");
      row.add(nf.format(metrics.getTotalMemory()));
      row.add(nf.format(metrics.getPreviousAvailableMemory()));
      row.add(nf.format(metrics.getAvailableMemory()));
      row.add(nf.format(metrics.getAvailableMemory() - metrics.getPreviousAvailableMemory()));
      if (metrics.getTotalMemory() != 0.0) {
        row.add(nf.format((metrics.getAvailableMemory() / metrics.getTotalMemory()) * 100));
      }
      else {
        row.add("N/A");
      }
      row.add(nf.format(metrics.getHitCount()));
      row.add((new Date(metrics.getLastHit())).toString());
      row.add(nf.format(metrics.getMaxExecutionTime() / 1000.0d));
      row.add(nf.format(metrics.getMinExecutionTime() / 1000.0d));
      row.add(nf.format(metrics.getLastExecutionTime() / 1000.0d));
      row.add(nf.format(metrics.getAverageExecutionTime() / 1000.0d));

      rows.add(row);
    } //End while iter.hasNext()

    return rows;
  }

  public String[] getHeader() {
    return HEADER;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    Collection<PerformanceMetrics> items = metricsMap.values();
    Iterator<PerformanceMetrics> iter = items.iterator();

    sb.append("\n************ System Performance Report ************\n");
    while (iter.hasNext()) {
      sb.append(iter.next());
    }
    sb.append("\n***************************************************\n");

    return sb.toString();
  }

  public void updatePerformanceMetrics(PerformanceMarkers markers) {
    updatePerformanceMetrics(markers.getItemName(), markers.getStartTime(), markers.getEndTime(), markers.getBeforeMemoryUsage(), markers.getAfterMemoryUsage(), markers.getDescription());
  }

  public PerformanceMarkers startMetering(String itemName) {
    PerformanceMarkers markers = new PerformanceMarkers();

    markers.setItemName(itemName);
    markers.setStartTime(System.currentTimeMillis());
    markers.setBeforeMemoryUsage(SystemUtils.GetAvailableMemory());

    return markers;
  }

  public void endMetering(PerformanceMarkers markers) {
    markers.setEndTime(System.currentTimeMillis());
    markers.setAfterMemoryUsage(SystemUtils.GetAvailableMemory());
    updatePerformanceMetrics(markers);
  }

  public PerformanceMarkers startMeteringCurrentMethod() {
    return startMetering(StringUtils.GetCallingMethodName());
  }

}
