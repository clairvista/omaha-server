package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;
import com.clairvista.liveexpert.omaha.server.response.UpdateCheckResponse;

public interface UpdateCheckService {

   public UpdateCheck recordUpdateCheck(ApplicationVersionRequest appRequest, Element updateCheckElem);
   public UpdateCheckResponse processUpdateCheck(UpdateCheck updateCheck);

}
