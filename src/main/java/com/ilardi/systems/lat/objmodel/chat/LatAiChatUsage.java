/**
 * Created Jun 5, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author robert.ilardi
 *
 */

public class LatAiChatUsage extends LatAiChatBaseValueObject {

  @JsonProperty("prompt_tokens")
  private int promptTokens;

  @JsonProperty("completion_tokens")
  private int completionTokens;

  @JsonProperty("total_tokens")
  private int totalTokens;

  public LatAiChatUsage() {}

  public int getPromptTokens() {
    return promptTokens;
  }

  public void setPromptTokens(int promptTokens) {
    this.promptTokens = promptTokens;
  }

  public int getCompletionTokens() {
    return completionTokens;
  }

  public void setCompletionTokens(int completionTokens) {
    this.completionTokens = completionTokens;
  }

  public int getTotalTokens() {
    return totalTokens;
  }

  public void setTotalTokens(int totalTokens) {
    this.totalTokens = totalTokens;
  }

  @Override
  public String toString() {
    return "LatAiChatUsage [promptTokens=" + promptTokens
        + ", completionTokens=" + completionTokens + ", totalTokens="
        + totalTokens + ", oaVoType=" + oaVoType + ", createdTs=" + createdTs
        + ", lstModTs=" + lstModTs + ", oaVoCreationIndex=" + oaVoCreationIndex
        + "]";
  }

}
