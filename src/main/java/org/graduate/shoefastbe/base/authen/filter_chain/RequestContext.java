package org.graduate.shoefastbe.base.authen.filter_chain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestContext {
    private String accessToken;
    private List<String> rolesAcceptOfThisApi;
}
