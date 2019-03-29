package com.creditease.ns.chains.chain;

import org.mvel2.UnresolveablePropertyException;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.BaseVariableResolverFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class JVMapVariableResolverFactory extends BaseVariableResolverFactory {
    /**
     * Holds the instance of the variables.
     */
    protected Map<String, Object> variables;

    public JVMapVariableResolverFactory() {
        this.variables = new HashMap();
    }

    public JVMapVariableResolverFactory(Map variables) {
        this.variables = variables;
    }

    public JVMapVariableResolverFactory(Map<String, Object> variables, VariableResolverFactory nextFactory) {
        this.variables = variables;
        this.nextFactory = nextFactory;
    }

    public JVMapVariableResolverFactory(Map<String, Object> variables, boolean cachingSafe) {
        this.variables = variables;
    }

    public VariableResolver createVariable(String name, Object value) {
        VariableResolver vr;

        try {
            (vr = getVariableResolver(name)).setValue(value);
            return vr;
        } catch (UnresolveablePropertyException e) {
            addResolver(name, vr = new ProtoBufferValueMapVariableResolver(variables, name)).setValue(value);
            return vr;
        }
    }

    public VariableResolver createVariable(String name, Object value, Class<?> type) {
        VariableResolver vr;
        try {
            vr = getVariableResolver(name);
        } catch (UnresolveablePropertyException e) {
            vr = null;
        }

        if (vr != null && vr.getType() != null) {
            throw new RuntimeException("variable already defined within scope: " + vr.getType() + " " + name);
        } else {
            addResolver(name, vr = new ProtoBufferValueMapVariableResolver(variables, name, type)).setValue(value);
            return vr;
        }
    }

    public VariableResolver getVariableResolver(String name) {
        VariableResolver vr = variableResolvers.get(name);
        if (vr != null) {
            return vr;
        } else if (variables.containsKey(name)) {
            variableResolvers.put(name, vr = new ProtoBufferValueMapVariableResolver(variables, name));
            return vr;
        } else if (nextFactory != null) {
            return nextFactory.getVariableResolver(name);
        }

        throw new UnresolveablePropertyException("unable to resolve variable '" + name + "'");
    }


    public boolean isResolveable(String name) {
        return (variableResolvers.containsKey(name))
                || (variables != null && variables.containsKey(name))
                || (nextFactory != null && nextFactory.isResolveable(name));
    }

    protected VariableResolver addResolver(String name, VariableResolver vr) {
        variableResolvers.put(name, vr);
        return vr;
    }


    public boolean isTarget(String name) {
        return variableResolvers.containsKey(name);
    }

    public Set<String> getKnownVariables() {
        if (nextFactory == null) {
            if (variables != null) return new HashSet<String>(variables.keySet());
            return new HashSet<String>(0);
        } else {
            if (variables != null) return new HashSet<String>(variables.keySet());
            return new HashSet<String>(0);
        }
    }

    public void clear() {
        variableResolvers.clear();
        variables.clear();
    }
}
