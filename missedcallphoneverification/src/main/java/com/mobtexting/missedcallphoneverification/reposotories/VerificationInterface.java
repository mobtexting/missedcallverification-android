package com.mobtexting.missedcallphoneverification.reposotories;

import com.mobtexting.missedcallphoneverification.model.ServerResponse;

public interface VerificationInterface {
    void onResponse(ServerResponse response);
    void onError(ServerResponse modelError);
    void missedCallReceived(boolean isError, String missedCallNumber);
}
