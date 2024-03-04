/**
 * Created Jun 5, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import com.ilardi.systems.lat.session.SmartChatRingBufferValueObject;

/**
 * @author robert.ilardi
 *
 */

public abstract class LatAiChatResponse extends SmartChatRingBufferValueObject {

  protected boolean responseProcessed;

  public LatAiChatResponse() {
    super();
    responseProcessed = false;
  }

  public boolean isResponseProcessed() {
    return responseProcessed;
  }

  public void setResponseProcessed(boolean responseProcessed) {
    this.responseProcessed = responseProcessed;
  }

  @Override
  public String toString() {
    return "LatAiChatResponse [responseProcessed=" + responseProcessed
        + ", engineeredPrompt=" + engineeredPrompt + ", priority=" + priority
        + ", oaVoType=" + oaVoType + ", createdTs=" + createdTs + ", lstModTs="
        + lstModTs + ", oaVoCreationIndex=" + oaVoCreationIndex + "]";
  }

}
