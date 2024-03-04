/**
 * Created Jun 22, 2023
 */
package com.ilardi.systems.lat.session;

import static com.ilardi.systems.lat.LatConstants.DEFAULT_MESSAGE_PRIORITY;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatBaseValueObject;

/**
 * @author robert.ilardi
 *
 */

public abstract class SmartChatRingBufferValueObject
    extends LatAiChatBaseValueObject {

  @JsonIgnore
  protected boolean engineeredPrompt = false;

  @JsonIgnore
  protected long priority = DEFAULT_MESSAGE_PRIORITY;

  public SmartChatRingBufferValueObject() {
    super();
  }

  public boolean isEngineeredPrompt() {
    return engineeredPrompt;
  }

  public void setEngineeredPrompt(boolean engineeredPrompt) {
    this.engineeredPrompt = engineeredPrompt;
  }

  public long getPriority() {
    return priority;
  }

  public void setPriority(long priority) {
    this.priority = priority;
  }

  @Override
  public String toString() {
    return "SmartChatRingBufferValueObject [engineeredPrompt="
        + engineeredPrompt + ", priority=" + priority + ", oaVoType=" + oaVoType
        + ", createdTs=" + createdTs + ", lstModTs=" + lstModTs
        + ", oaVoCreationIndex=" + oaVoCreationIndex + "]";
  }

}
