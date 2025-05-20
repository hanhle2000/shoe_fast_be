package org.graduate.shoefastbe.base.black_list.caffeine;

import com.github.benmanes.caffeine.cache.Cache;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.authen.TokenHelper;
import org.graduate.shoefastbe.base.black_list.BlackList;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@AllArgsConstructor
public class CaffeineHelper implements BlackList {
    private final Cache<Long, OffsetDateTime> updateAccountBlackList;
    private final Cache<String, Boolean> logOutAccountBlackList;

    @Override
    public void putToUpdateAccountBlackList(Long userId){
        updateAccountBlackList.put(
                userId,
                OffsetDateTime.now()
        );
        print();
    }

    @Override
    public void putToLogOutAccountBlackList(String accessToken){
        String jti = TokenHelper.getJtiFromToken(accessToken);
        logOutAccountBlackList.put(jti, true);
        print();
    }

    private void print(){
        System.out.println("*** Update account blacklist");
        updateAccountBlackList.asMap().forEach((aLong, offsetDateTime) -> {
            System.out.println("key: " + aLong + " - " + "value: " + offsetDateTime);
        });
        System.out.println("*** Log out blacklist");
        logOutAccountBlackList.asMap().forEach((jtI, b) -> {
            System.out.println("key: " + jtI);
        });
    }
}
