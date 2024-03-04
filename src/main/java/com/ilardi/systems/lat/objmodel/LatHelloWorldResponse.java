/**
 * Created Feb 22, 2024
 */
package com.ilardi.systems.lat.objmodel;

/**
 * @author rober
 *
 */

public class LatHelloWorldResponse extends LatBaseResponse {

  private String helloMesg;

  public LatHelloWorldResponse() {
    super();
  }

  public String getHelloMesg() {
    return helloMesg;
  }

  public void setHelloMesg(String helloMesg) {
    this.helloMesg = helloMesg;
  }

}
