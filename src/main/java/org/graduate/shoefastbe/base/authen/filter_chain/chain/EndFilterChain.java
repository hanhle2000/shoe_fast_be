package org.graduate.shoefastbe.base.authen.filter_chain.chain;

import org.graduate.shoefastbe.base.authen.filter_chain.RequestContext;
import org.graduate.shoefastbe.base.authen.filter_chain.TokenFilterChain;
import org.springframework.stereotype.Component;

@Component("end-filter-chain")
public class EndFilterChain extends TokenFilterChain {
    @Override
    public void validate(RequestContext requestContext) {
        System.out.println("*** Success validate token");
    }
}
