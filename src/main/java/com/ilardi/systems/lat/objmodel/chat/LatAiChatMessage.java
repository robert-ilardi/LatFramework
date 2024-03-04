/**
 * Created Jun 8, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ilardi.systems.lat.session.SmartChatRingBufferValueObject;

/**
 * @author robert.ilardi
 *
 */

public class LatAiChatMessage extends SmartChatRingBufferValueObject {

  @JsonProperty("role")
  private String role;

  @JsonProperty("content")
  private String content;

  @JsonIgnore
  private int mesgTokenCnt;

  @JsonIgnore
  private long origOaVoCreationIndex;

  public LatAiChatMessage() {
    this(null, null, null);
  }

  public LatAiChatMessage(String role, String content,
      LatAiChatValueObjectType oaVoType) {
    this.role = role;
    this.content = content;
    this.oaVoType = oaVoType;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  /**
   * @return the mesgTokenCnt
   */
  public int getMesgTokenCnt() {
    return mesgTokenCnt;
  }

  /**
   * @param mesgTokenCnt the mesgTokenCnt to set
   */
  public void setMesgTokenCnt(int mesgTokenCnt) {
    this.mesgTokenCnt = mesgTokenCnt;
  }

  public long getOrigOaVoCreationIndex() {
    return origOaVoCreationIndex;
  }

  public void setOrigOaVoCreationIndex(long origOaVoCreationIndex) {
    this.origOaVoCreationIndex = origOaVoCreationIndex;
  }

  @Override
  public String toString() {
    return "LatAiChatMessage [role=" + role + ", content=" + content
        + ", mesgTokenCnt=" + mesgTokenCnt + ", origOaVoCreationIndex="
        + origOaVoCreationIndex + ", engineeredPrompt=" + engineeredPrompt
        + ", priority=" + priority + ", oaVoType=" + oaVoType + ", createdTs="
        + createdTs + ", lstModTs=" + lstModTs + ", oaVoCreationIndex="
        + oaVoCreationIndex + "]";
  }

}
