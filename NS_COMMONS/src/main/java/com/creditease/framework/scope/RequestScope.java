package com.creditease.framework.scope;

import java.util.HashMap;
import java.util.Map;

public class RequestScope extends HashMap<String, Object>{
	
    public RequestScope(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    public RequestScope() {
    }

}
