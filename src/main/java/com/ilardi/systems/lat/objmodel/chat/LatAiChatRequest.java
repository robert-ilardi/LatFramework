/**
 * Created Jun 8, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ilardi.systems.lat.session.SmartChatRingBufferValueObject;

/**
 * @author robert.ilardi
 *
 */

public class LatAiChatRequest extends SmartChatRingBufferValueObject {

  @JsonProperty("model")
  private String model;

  @JsonProperty("messages")
  private List<LatAiChatMessage> messages;

  public LatAiChatRequest() {
    super();
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public List<LatAiChatMessage> getMessages() {
    return messages;
  }

  public void setMessages(List<LatAiChatMessage> messages) {
    this.messages = messages;
  }

  public void addMessage(LatAiChatMessage mesg) {
    if (messages == null) {
      messages = new ArrayList<>();
    }

    messages.add(mesg);
  }

  public void addMessage(String role, String txtMesg,
      LatAiChatValueObjectType oaVoType) {
    LatAiChatMessage mesg;

    mesg = new LatAiChatMessage(role, txtMesg, oaVoType);

    addMessage(mesg);
  }

  @Override
  public String toString() {
    return "LatAiChatMessage [model=" + model + ", messages=" + messages
        + ", engineeredPrompt=" + engineeredPrompt + ", priority=" + priority
        + ", oaVoType=" + oaVoType + ", createdTs=" + createdTs + ", lstModTs="
        + lstModTs + ", oaVoCreationIndex=" + oaVoCreationIndex + "]";
  }

}
