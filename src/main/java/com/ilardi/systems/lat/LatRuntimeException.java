/**
 * Created Jan 31, 2021
 */
package com.ilardi.systems.lat;

import com.ilardi.systems.util.IlardiSystemsRuntimeException;

/**
 * @author rilardi
 *
 */

public class LatRuntimeException extends IlardiSystemsRuntimeException {

  public LatRuntimeException() {
    super();
  }

  /**
   * @param message
   */
  public LatRuntimeException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public LatRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public LatRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public LatRuntimeException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
