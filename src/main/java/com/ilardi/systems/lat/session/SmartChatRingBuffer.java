/**
 * Created Jun 12, 2023
 */
package com.ilardi.systems.lat.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ilardi.systems.lat.LatException;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatBaseValueObject;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatChoice;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatErrorResponse;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatMessage;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatRequest;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatResponse;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatSuccessResponse;
import com.ilardi.systems.lat.objmodel.chat.LatAiChatValueObjectType;
import com.ilardi.systems.util.StringUtils;

/**
 * @author robert.ilardi
 *
 */

public class SmartChatRingBuffer implements Serializable {

  private static final Logger logger = LogManager
      .getLogger(SmartChatRingBuffer.class);

  public static final long DEFAULT_OA_CREATION_INDEX = 4096;

  private List<LatAiChatMessage> dialog;

  private List<LatAiChatRequest> requestHistory;
  private List<LatAiChatResponse> responseHistory;

  private int tokenSafetyMarginPercentage;
  private int maxTokenCount;
  private int maxRequestCount;
  private int maxResponseCount;

  private String chatGptModel;
  private String chatGptRole;

  private long oaVoCreationIndex;

  private ChatKey chatKey;

  public SmartChatRingBuffer(ChatKey chatKey) {
    this.chatKey = chatKey;
  }

  public synchronized void init() {
    logger.debug("Initializing Smart Chat Ring Buffer: " + chatKey);

    oaVoCreationIndex = DEFAULT_OA_CREATION_INDEX; //Give us some space for weird cases

    setDialog(new ArrayList<LatAiChatMessage>());

    setRequestHistory(new ArrayList<LatAiChatRequest>());
    setResponseHistory(new ArrayList<LatAiChatResponse>());
  }

  public ChatKey getChatKey() {
    return chatKey;
  }

  public List<LatAiChatMessage> getDialog() {
    return dialog;
  }

  public void setDialog(List<LatAiChatMessage> dialog) {
    this.dialog = dialog;
  }

  public int getMaxTokenCount() {
    return maxTokenCount;
  }

  public void setMaxTokenCount(int maxTokenCount) {
    this.maxTokenCount = maxTokenCount;
  }

  public List<LatAiChatRequest> getRequestHistory() {
    return requestHistory;
  }

  public void setRequestHistory(List<LatAiChatRequest> requestHistory) {
    this.requestHistory = requestHistory;
  }

  public List<LatAiChatResponse> getResponseHistory() {
    return responseHistory;
  }

  public void setResponseHistory(List<LatAiChatResponse> responseHistory) {
    this.responseHistory = responseHistory;
  }

  public int getMaxRequestCount() {
    return maxRequestCount;
  }

  public void setMaxRequestCount(int maxRequestCount) {
    this.maxRequestCount = maxRequestCount;
  }

  public int getMaxResponseCount() {
    return maxResponseCount;
  }

  public void setMaxResponseCount(int maxResponseCount) {
    this.maxResponseCount = maxResponseCount;
  }

  public String getChatGptModel() {
    return chatGptModel;
  }

  public void setChatGptModel(String chatGptModel) {
    this.chatGptModel = chatGptModel;
  }

  public String getChatGptRole() {
    return chatGptRole;
  }

  public void setChatGptRole(String chatGptRole) {
    this.chatGptRole = chatGptRole;
  }

  public synchronized void addMessageToDialog(LatAiChatMessage vo) {
    if (dialog == null) {
      dialog = new ArrayList<LatAiChatMessage>();
    }

    dialog.add(vo);
  }

  public int getTokenSafetyMarginPercentage() {
    return tokenSafetyMarginPercentage;
  }

  public void setTokenSafetyMarginPercentage(int tokenSafetyMarginPercentage) {
    this.tokenSafetyMarginPercentage = tokenSafetyMarginPercentage;
  }

  public synchronized List<LatAiChatMessage> smartlyRemoveDialogEntries() {
    LatAiChatMessage vo;
    List<LatAiChatMessage> removedEntries = null;
    ListIterator<LatAiChatMessage> iter;
    //LatAiChatValueObjectType oaVoType;
    int tokenCnt, removals = 0;

    logger.debug("Executing Smart Ring Buffer Maintenance Process...");

    if (dialog != null) {
      tokenCnt = 0;
      iter = dialog.listIterator(dialog.size());

      tokenCnt = countCurrentTokens();

      logger.debug("Current Dialog Token Count: " + tokenCnt);

      removedEntries = new ArrayList<LatAiChatMessage>();

      while (iter.hasPrevious()) {
        vo = iter.previous();
        //oaVoType = vo.getOaVoType();

        //if (!LatAiChatValueObjectType.ENGINEERED_PROMPT.equals(oaVoType))
        if (tokenSafetyMarginReached(tokenCnt)) {
          iter.remove();

          logger.debug("Token Safety Margin Reached Removing Message: " + vo);

          removedEntries.add(vo);

          tokenCnt -= vo.getMesgTokenCnt();

          removals++;

          logger.debug("Current Dialog Token Count: " + tokenCnt);
        }
        else {
          break;
        }
      } //end while iter loop
    } //End null dialog check

    logger.debug("Removed: " + removals + " messages from Smart Ring Buffer");

    return removedEntries;
  }

  public int countCurrentTokens() {
    LatAiChatMessage mesg;
    int tokenCnt = 0, mesgTokens;

    for (int i = 0; i < dialog.size(); i++) {
      mesg = dialog.get(i);

      mesgTokens = mesg.getMesgTokenCnt();

      tokenCnt += mesgTokens;
    }

    return tokenCnt;
  }

  private boolean tokenSafetyMarginReached(int tokenCnt) {
    boolean reached;
    int safeTokenMargin, maxTokensBelowMargin;
    double dTmp;

    dTmp = tokenSafetyMarginPercentage / 100.0d;
    dTmp = dTmp * maxTokenCount;

    safeTokenMargin = (int) Math.ceil(dTmp);

    maxTokensBelowMargin = maxTokenCount - safeTokenMargin;

    reached = (tokenCnt >= maxTokensBelowMargin);

    logger.debug("Checking Token Safety Margin: tokenSafetyMarginPercentage = "
        + tokenSafetyMarginPercentage + "%, safeTokenMargin = "
        + safeTokenMargin + ", maxTokenCount = " + maxTokenCount
        + ", maxTokensBelowMargin: " + maxTokensBelowMargin + ", tokenCnt = "
        + tokenCnt + ", tokenSafetyMarginReached? " + (reached ? "YES" : "NO"));

    return reached;
  }

  /*public synchronized List<OpenAiMessage> smartlyGetImportantDialogEntries() {
    OpenAiMessage mesgVo;
    List<OpenAiMessage> importantEntries = null;
    Iterator<OpenAiMessage> iter;
    int tokenCnt;
  
    tokenCnt = 0;
    importantEntries = new ArrayList<OpenAiMessage>();
  
    if (dialog != null) {
      iter = dialog.listIterator(dialog.size());
  
      while (iter.hasNext()) {
        mesgVo = iter.next();
  
        if (keep(mesgVo)) {
          tokenCnt += mesgVo.getMesgTokenCnt();
  
          if (tokenCnt <= maxTokenCount) {
            importantEntries.add(mesgVo);
          }
        }
      }
    }
  
    return importantEntries;
  }
  
  private boolean keep(OpenAiMessage mesgVo) {
    return true;
  }*/

  public synchronized void addToRequestHistory(LatAiChatRequest vo) {
    if (requestHistory == null) {
      requestHistory = new ArrayList<LatAiChatRequest>();
    }

    requestHistory.add(vo);
  }

  public synchronized List<LatAiChatRequest> trimRequestHistory() {
    LatAiChatRequest vo;
    List<LatAiChatRequest> removedEntries = null;

    if (requestHistory != null) {
      removedEntries = new ArrayList<LatAiChatRequest>();

      for (int i = 0; i < requestHistory.size(); i++) {
        vo = requestHistory.remove(i);
        removedEntries.add(vo);
      }
    }

    return removedEntries;
  }

  private synchronized void addToResponseHistory(LatAiChatResponse vo) {
    if (responseHistory == null) {
      responseHistory = new ArrayList<LatAiChatResponse>();
    }

    responseHistory.add(vo);
  }

  public synchronized List<LatAiChatResponse> trimResponseHistory() {
    LatAiChatResponse vo;
    List<LatAiChatResponse> removedEntries = null;

    if (responseHistory != null) {
      removedEntries = new ArrayList<LatAiChatResponse>();

      for (int i = 0; i < responseHistory.size(); i++) {
        vo = responseHistory.remove(i);
        removedEntries.add(vo);
      }
    }

    return removedEntries;
  }

  public synchronized void setAoVoCreationIndex(LatAiChatBaseValueObject oaVo) {
    oaVo.setOaVoCreationIndex(oaVoCreationIndex);
    oaVoCreationIndex++;
  }

  public synchronized void addNewRequestToDialog(String inputText,
      LatAiChatValueObjectType oaVoType) {
    LatAiChatRequest oaReq;
    LatAiChatMessage oaMesg;
    List<LatAiChatMessage> mesgLst;
    long now, origOaVoCreationIndex;

    now = System.currentTimeMillis();

    oaReq = new LatAiChatRequest();

    oaReq.setCreatedTs(now);
    //oaReq.setKeeper(true); //New Message should be Considered Important
    oaReq.setLstModTs(now);

    oaMesg = new LatAiChatMessage(chatGptRole, inputText, oaVoType); //LatAiChatValueObjectType.PROGRAMMER_REQUEST);
    oaMesg.setCreatedTs(now);
    oaMesg.setLstModTs(now);

    setAoVoCreationIndex(oaMesg);

    mesgLst = new ArrayList<LatAiChatMessage>();
    mesgLst.add(oaMesg);

    oaReq.setMessages(mesgLst);

    oaReq.setModel(chatGptModel);
    oaReq.setOaVoType(oaVoType); //(LatAiChatValueObjectType.PROGRAMMER_REQUEST);

    setAoVoCreationIndex(oaReq);

    origOaVoCreationIndex = oaReq.getOaVoCreationIndex();
    oaMesg.setOrigOaVoCreationIndex(origOaVoCreationIndex);

    //Add to both Dialog and Request History
    addMessageToDialog(oaMesg);
    addToRequestHistory(oaReq);
  }

  public synchronized void addMessageToRequestIndexMap(LatAiChatMessage vo) {
    if (dialog == null) {
      dialog = new ArrayList<LatAiChatMessage>();
    }

    dialog.add(vo);
  }

  public LatAiChatRequest getCompleteDialogAsRequest() throws LatException {
    LatAiChatRequest oaReq;
    LatAiChatMessage oaMesg;
    List<LatAiChatMessage> mesgLst;
    long now;
    Iterator<LatAiChatMessage> iter;
    String tmpStr;
    LatAiChatValueObjectType lastReqType = LatAiChatValueObjectType.BASIC_CHAT;

    now = System.currentTimeMillis();

    oaReq = new LatAiChatRequest();

    oaReq.setCreatedTs(now);
    //oaReq.setKeeper(true); //New Message should be Considered Important
    oaReq.setLstModTs(now);

    mesgLst = new ArrayList<LatAiChatMessage>();

    iter = dialog.iterator();

    while (iter.hasNext()) {
      oaMesg = iter.next();
      lastReqType = oaMesg.getOaVoType();

      //Make sure we filter non-UTF8 Chars
      //ChatGPT / OpenAI API returned an error
      //On July 29, 2023 when it's own reply
      //contained a non-UTF8 character.
      tmpStr = oaMesg.getContent();
      tmpStr = StringUtils.filterNonUtf8Characters(tmpStr);
      oaMesg.setContent(tmpStr);

      mesgLst.add(oaMesg);
    }

    if (lastReqType == null) {
      throw new LatException("Last Req Type CANNOT Be NULL!");
    }

    oaReq.setMessages(mesgLst);

    oaReq.setModel(chatGptModel);
    oaReq.setOaVoType(lastReqType); //(LatAiChatValueObjectType.PROGRAMMER_GENERAL_CHAT);

    setAoVoCreationIndex(oaReq);

    return oaReq;
  }

  /*public void addResponseToDialog(OpenAiChatResponse oaRes) throws LatException {
    OpenAiChatSuccessResponse oaSuccessRes;
    OpenAiChatErrorResponse oaErrorRes;
  
    if (oaRes != null) {
      switch (oaRes.getOaVoType()) {
        case BOT_SUCCESS_RESPONSE:
        case BOT_SUCCESS_COMMENTS_RESPONSE:
          oaSuccessRes = (OpenAiChatSuccessResponse) oaRes;
  
          addSuccessResponseToDialog(oaSuccessRes);
  
          break;
        case BOT_ERROR_RESPONSE:
          oaErrorRes = (OpenAiChatErrorResponse) oaRes;
  
          addErrorResponseToDialog(oaErrorRes);
  
          break;
        default:
          throw new LatException("Unsupported Response Type: " + oaRes);
      }
    }
    else {
      throw new LatException("Response is NULL!");
    }
  }*/

  public void addResponseToDialog(LatAiChatResponse oaRes) throws LatException {
    LatAiChatSuccessResponse oaSuccessRes;
    LatAiChatErrorResponse oaErrorRes;

    if (oaRes != null) {
      if (oaRes instanceof LatAiChatSuccessResponse) {
        oaSuccessRes = (LatAiChatSuccessResponse) oaRes;

        addSuccessResponseToDialog(oaSuccessRes);
      }
      else if (oaRes instanceof LatAiChatErrorResponse) {
        oaErrorRes = (LatAiChatErrorResponse) oaRes;

        addErrorResponseToDialog(oaErrorRes);
      }
      else {
        throw new LatException("Unsupported Response Type: " + oaRes);
      }
    }
    else {
      throw new LatException("Response is NULL!");
    }
  }

  private void addSuccessResponseToDialog(LatAiChatSuccessResponse oaRes) {
    LatAiChatMessage oaMesg;
    List<LatAiChatChoice> choiceLst;
    LatAiChatChoice choice;

    logger.debug("Adding Success Response to Dialog");

    choiceLst = oaRes.getChoices();

    for (int i = 0; i < choiceLst.size(); i++) {
      choice = choiceLst.get(i);

      oaMesg = choice.getMessage();

      addMessageToDialog(oaMesg); //Add to Dialog History
    }

    addToResponseHistory(oaRes); //Add to Response History
  }

  private void addErrorResponseToDialog(LatAiChatErrorResponse oaRes) {
    logger.debug("Adding Error Response to Dialog");

    addToResponseHistory(oaRes); //Add to Response History
  }

  public void populateProBudFields(LatAiChatResponse oaRes, boolean keeper,
      LatAiChatValueObjectType reqType) throws LatException {
    if (oaRes instanceof LatAiChatSuccessResponse) {
      populateProBudFields((LatAiChatSuccessResponse) oaRes, keeper, reqType);
    }
    else if (oaRes instanceof LatAiChatErrorResponse) {
      populateProBudFields((LatAiChatErrorResponse) oaRes, keeper, reqType);
    }
    else {
      throw new LatException("Unsupported OpenAI Chat Response Type: " + oaRes);
    }
  }

  private void populateProBudFields(LatAiChatSuccessResponse oaSuccessRes,
      boolean keeper, LatAiChatValueObjectType reqType) throws LatException {
    LatAiChatMessage oaMesg;
    List<LatAiChatChoice> choiceLst;
    LatAiChatChoice choice;
    long now;
    LatAiChatValueObjectType resType;

    now = System.currentTimeMillis();

    //Map Request Type to Response Type
    resType = LatAiChatValueObjectType.mapReqTypeToResType(reqType, true);

    //oaSuccessRes.setKeeper(keeper);
    oaSuccessRes.setCreatedTs(now);
    oaSuccessRes.setLstModTs(now);

    choiceLst = oaSuccessRes.getChoices();

    for (int i = 0; i < choiceLst.size(); i++) {
      choice = choiceLst.get(i);

      oaMesg = choice.getMessage();

      //oaMesg.setKeeper(keeper);
      oaMesg.setCreatedTs(now);
      oaMesg.setLstModTs(now);

      setAoVoCreationIndex(oaMesg);

      oaMesg.setOaVoType(resType);
    }

    oaSuccessRes.setOaVoType(resType);

    setAoVoCreationIndex(oaSuccessRes);
  }

  private void populateProBudFields(LatAiChatErrorResponse oaErrorRes,
      boolean keeper, LatAiChatValueObjectType reqType) throws LatException {
    long now;
    LatAiChatValueObjectType resType;

    now = System.currentTimeMillis();

    //Map Request Type to Response Type
    //resType = LatAiChatValueObjectType.BOT_ERROR_RESPONSE;
    resType = LatAiChatValueObjectType.mapReqTypeToResType(reqType, false);

    //oaErrorRes.setKeeper(keeper);
    oaErrorRes.setCreatedTs(now);
    oaErrorRes.setLstModTs(now);

    oaErrorRes.setOaVoType(resType);

    setAoVoCreationIndex(oaErrorRes);
  }

  public void clearRingBuffer() {
    dialog.clear();

    requestHistory.clear();
    responseHistory.clear();

    //Reset to Default Index
    //oaVoCreationIndex = DEFAULT_OA_CREATION_INDEX;
  }

  public LatAiChatResponse getLastResponseFromHistory() {
    LatAiChatResponse oaRes = null;
    int sz;

    if (responseHistory != null) {
      sz = responseHistory.size();

      oaRes = responseHistory.get(sz - 1);
    }

    return oaRes;
  }

  public int requestHistorySize() {
    return (requestHistory != null ? requestHistory.size() : -1);
  }

  public int responseHistorySize() {
    return (responseHistory != null ? responseHistory.size() : -1);
  }

  public int dialogSize() {
    return (dialog != null ? dialog.size() : -1);
  }

  public List<LatAiChatResponse> getUnprocessedResponsesFromHistory() {
    ArrayList<LatAiChatResponse> oaResLst = null;
    LatAiChatResponse oaRes;
    int sz;

    if (responseHistory != null) {
      oaResLst = new ArrayList<LatAiChatResponse>();

      sz = responseHistory.size();

      for (int i = 0; i < sz; i++) {
        oaRes = responseHistory.get(i);

        if (!oaRes.isResponseProcessed()) {
          oaResLst.add(oaRes);
        }

        oaRes = null;
      }
    }

    return oaResLst;
  }

}
