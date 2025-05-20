package org.graduate.shoefastbe.base.authen.filter_chain.chain;
import org.graduate.shoefastbe.base.authen.filter_chain.RequestContext;
import org.graduate.shoefastbe.base.authen.filter_chain.TokenFilterChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("start-filter-chain")
public class StartFilterChain extends TokenFilterChain {
    private TokenFilterChain tokenHandle;

    public StartFilterChain(@Qualifier("expire-token-filter-chain") TokenFilterChain tokenHandle) {
        this.tokenHandle = tokenHandle;
    }

    @Override
    public void validate(RequestContext requestContext) {
        tokenHandle.validate(requestContext);
    }
}
