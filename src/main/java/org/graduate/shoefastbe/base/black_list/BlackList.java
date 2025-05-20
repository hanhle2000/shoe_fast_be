package org.graduate.shoefastbe.base.black_list;

public interface BlackList {
    void putToUpdateAccountBlackList(Long userId);
    void putToLogOutAccountBlackList(String accessToken);
}
