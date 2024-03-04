/**
 * Created Feb 21, 2024
 */
package com.ilardi.systems.lat.objmodel;

/**
 * @author rober
 *
 */

public class LatHelloWorldRequest extends LatBaseRequest {

  private String name;

  public LatHelloWorldRequest() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
