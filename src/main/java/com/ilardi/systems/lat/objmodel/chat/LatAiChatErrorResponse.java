/**
 * Created Jun 5, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author robert.ilardi
 *
 */

public class LatAiChatErrorResponse extends LatAiChatResponse {

  @JsonProperty("error")
  private LatAiChatError error;

  public LatAiChatErrorResponse() {
    super();
  }

  /**
   * @return the error
   */
  public LatAiChatError getError() {
    return error;
  }

  /**
   * @param error the error to set
   */
  public void setError(LatAiChatError error) {
    this.error = error;
  }

  public String getErrorText() {
    StringBuilder sb = new StringBuilder();
    String tmp;

    sb.append("Message: ");
    sb.append(error.getMessage());

    sb.append("; Type: ");
    sb.append(error.getType());

    sb.append("; Param: ");
    sb.append(error.getParam());

    sb.append("; Code: ");
    sb.append(error.getCode());

    tmp = sb.toString();
    sb = null;

    return tmp;
  }

  @Override
  public String toString() {
    return "LatAiChatErrorResponse [error=" + error + ", responseProcessed="
        + responseProcessed + ", engineeredPrompt=" + engineeredPrompt
        + ", priority=" + priority + ", oaVoType=" + oaVoType + ", createdTs="
        + createdTs + ", lstModTs=" + lstModTs + ", oaVoCreationIndex="
        + oaVoCreationIndex + "]";
  }

}
