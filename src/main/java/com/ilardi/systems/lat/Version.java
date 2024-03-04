/**
 * Created Jan 14, 2024
 */
package com.ilardi.systems.lat;

/**
 * @author robert.ilardi
 *
 */

public class Version {

  public static final String VERSION = "0.9.0";

  public static final String APP_SHORT_NAME = "LAT";

  public static final String APP_LONG_NAME = "LLM Assistants Team";

  public static final String DESCRIPTION = "LLM Assistants Team (LAT) is a Team of LLM Assistants Framework for Java";

  public static final String COPYRIGHT = "Copyright (c) 2024 By: Robert C. Ilardi";

  public static String getVersionInfo() {
    StringBuilder sb = new StringBuilder();

    sb.append(APP_LONG_NAME);
    sb.append(" (");
    sb.append(APP_SHORT_NAME);
    sb.append(")\n");

    sb.append("Version: ");
    sb.append(VERSION);
    sb.append("\n");

    sb.append(DESCRIPTION);
    sb.append("\n");

    sb.append(COPYRIGHT);

    return sb.toString();
  }

  public Version() {}

  public static void main(String[] args) {
    int exitCd;
    String verInfo;

    try {
      exitCd = 0;

      verInfo = getVersionInfo();

      System.out.println(verInfo);
    }
    catch (Exception e) {
      exitCd = 1;
      e.printStackTrace();
    }
    finally {
      verInfo = null;
    }

    System.exit(exitCd);
  }

}
