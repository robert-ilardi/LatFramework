/**
 * Created Jun 5, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author robert.ilardi
 *
 */

public class LatAiChatChoice extends LatAiChatBaseValueObject {

  @JsonProperty("message")
  private LatAiChatMessage message;

  @JsonProperty("finish_reason")
  private String finishReason;

  @JsonProperty("index")
  private int index;

  public LatAiChatChoice() {}

  public LatAiChatMessage getMessage() {
    return message;
  }

  public void setMessage(LatAiChatMessage message) {
    this.message = message;
  }

  public String getFinishReason() {
    return finishReason;
  }

  public void setFinishReason(String finishReason) {
    this.finishReason = finishReason;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public String toString() {
    return "LatAiChatChoice [message=" + message + ", finishReason="
        + finishReason + ", index=" + index + ", oaVoType=" + oaVoType
        + ", createdTs=" + createdTs + ", lstModTs=" + lstModTs
        + ", oaVoCreationIndex=" + oaVoCreationIndex + "]";
  }

}
