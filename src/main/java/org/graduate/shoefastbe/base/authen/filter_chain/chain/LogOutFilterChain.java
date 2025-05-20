package org.graduate.shoefastbe.base.authen.filter_chain.chain;

import com.github.benmanes.caffeine.cache.Cache;
import org.graduate.shoefastbe.base.authen.TokenHelper;
import org.graduate.shoefastbe.base.authen.filter_chain.RequestContext;
import org.graduate.shoefastbe.base.authen.filter_chain.TokenFilterChain;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("log-out-filter-chain")
public class LogOutFilterChain extends TokenFilterChain {
    private final TokenFilterChain tokenHandle;
    private final Cache<String, Boolean> logOutBlackList;

    public LogOutFilterChain(@Qualifier("update-account-filter-chain") TokenFilterChain tokenHandle,
                             Cache<String, Boolean> logOutBlackList) {
        this.tokenHandle = tokenHandle;
        this.logOutBlackList = logOutBlackList;
    }

    @Override
    public void validate(RequestContext requestContext) {
        String jti = TokenHelper.getJtiFromToken(requestContext.getAccessToken());
        if (logOutBlackList.asMap().containsKey(jti)){
            throw new RuntimeException(CodeAndMessage.ME0101);
        }
        System.out.println("Validate logout success");
        if (Objects.nonNull(tokenHandle)){
            tokenHandle.validate(requestContext);
        }
    }
}
