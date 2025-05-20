package org.graduate.shoefastbe.base.authen.filter_chain.chain;

import com.github.benmanes.caffeine.cache.Cache;
import org.graduate.shoefastbe.base.authen.TokenHelper;
import org.graduate.shoefastbe.base.authen.filter_chain.RequestContext;
import org.graduate.shoefastbe.base.authen.filter_chain.TokenFilterChain;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Component("update-account-filter-chain")
public class UpdateAccountFilterChain extends TokenFilterChain {
    private final Cache<Long, OffsetDateTime> updateAccountBlackList;
    private final TokenFilterChain tokenHandle;

    public UpdateAccountFilterChain(Cache<Long, OffsetDateTime> updateAccountBlackList,
                                    @Qualifier("role-filter-chain") TokenFilterChain tokenHandle) {
        this.updateAccountBlackList = updateAccountBlackList;
        this.tokenHandle = tokenHandle;
    }

    @Override
    public void validate(RequestContext requestContext) {
        Long userId = TokenHelper.getUserIdFromToken(requestContext.getAccessToken());
        if (updateAccountBlackList.asMap().containsKey(userId)){
            OffsetDateTime acceptAfter = updateAccountBlackList.asMap().get(userId).toInstant().atOffset(ZoneOffset.UTC);
            OffsetDateTime iat = TokenHelper.getIatFromToken(requestContext.getAccessToken());
            if (iat.isBefore(acceptAfter)){
                throw new RuntimeException(CodeAndMessage.ME0101);
            }
        }
        System.out.println("Validate update success");
        if (Objects.nonNull(tokenHandle)){
            tokenHandle.validate(requestContext);
        }
    }
}
