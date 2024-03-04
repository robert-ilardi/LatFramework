/**
 * Created Jun 14, 2023
 */
package com.ilardi.systems.lat.objmodel.chat;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author robert.ilardi
 *
 */

public abstract class LatAiChatBaseValueObject
    implements Serializable, Comparable<LatAiChatBaseValueObject> {

  @JsonIgnore
  protected LatAiChatValueObjectType oaVoType;

  @JsonIgnore
  protected long createdTs;

  @JsonIgnore
  protected long lstModTs;

  @JsonIgnore
  protected long oaVoCreationIndex;

  public LatAiChatBaseValueObject() {}

  public LatAiChatValueObjectType getOaVoType() {
    return oaVoType;
  }

  public void setOaVoType(LatAiChatValueObjectType oaVoType) {
    this.oaVoType = oaVoType;
  }

  public long getCreatedTs() {
    return createdTs;
  }

  public void setCreatedTs(long createdTs) {
    this.createdTs = createdTs;
  }

  public long getLstModTs() {
    return lstModTs;
  }

  public void setLstModTs(long lstModTs) {
    this.lstModTs = lstModTs;
  }

  public long getOaVoCreationIndex() {
    return oaVoCreationIndex;
  }

  public void setOaVoCreationIndex(long oaVoCreationIndex) {
    this.oaVoCreationIndex = oaVoCreationIndex;
  }

  @Override
  public int hashCode() {
    return Objects.hash(createdTs, lstModTs, oaVoCreationIndex, oaVoType);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof LatAiChatBaseValueObject)) {
      return false;
    }

    LatAiChatBaseValueObject other = (LatAiChatBaseValueObject) obj;

    return createdTs == other.createdTs && lstModTs == other.lstModTs
        && oaVoCreationIndex == other.oaVoCreationIndex
        && oaVoType == other.oaVoType;
  }

  @Override
  public int compareTo(LatAiChatBaseValueObject other) {
    int comp = -1;

    if (other != null && other instanceof LatAiChatBaseValueObject) {
      if (this.oaVoCreationIndex < other.oaVoCreationIndex) {
        comp = -1;
      }
      else if (this.oaVoCreationIndex == other.oaVoCreationIndex) {
        comp = 0;
      }
      else if (this.oaVoCreationIndex > other.oaVoCreationIndex) {
        comp = 1;
      }
    }

    return comp;
  }

  @Override
  public String toString() {
    return "LatAiChatBaseValueObject [oaVoType=" + oaVoType + ", createdTs="
        + createdTs + ", lstModTs=" + lstModTs + ", oaVoCreationIndex="
        + oaVoCreationIndex + "]";
  }

}
