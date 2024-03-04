/**
 * Created Jan 7, 2021
 */
package com.ilardi.systems.lat;

import com.ilardi.systems.util.IlardiSystemsException;

/**
 * @author rilardi
 *
 */

public class LatException extends IlardiSystemsException {

  public LatException() {
    super();
  }

  /**
   * @param message
   */
  public LatException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public LatException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public LatException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public LatException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
