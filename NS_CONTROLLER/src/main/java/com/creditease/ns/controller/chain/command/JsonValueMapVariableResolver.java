package com.creditease.ns.controller.chain.command;


import com.creditease.framework.util.JsonUtil;
import org.mvel2.integration.VariableResolver;

import java.util.Map;

import static org.mvel2.DataConversion.canConvert;
import static org.mvel2.DataConversion.convert;

public class JsonValueMapVariableResolver implements VariableResolver {
    private String name;
    private Class<?> knownType;
    private Map<String, Object> variableMap;

    public JsonValueMapVariableResolver(Map<String, Object> variableMap, String name) {
        this.variableMap = variableMap;
        this.name = name;
    }

    public JsonValueMapVariableResolver(Map<String, Object> variableMap, String name, Class knownType) {
        this.name = name;
        this.knownType = knownType;
        this.variableMap = variableMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStaticType(Class knownType) {
        this.knownType = knownType;
    }

    public void setVariableMap(Map<String, Object> variableMap) {
        this.variableMap = variableMap;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return knownType;
    }

    public void setValue(Object value) {
        if (knownType != null && value != null && value.getClass() != knownType) {
            if (!canConvert(knownType, value.getClass())) {
                throw new RuntimeException("cannot assign " + value.getClass().getName() + " to type: "
                        + knownType.getName());
            }
            try {
                value = convert(value, knownType);
            } catch (Exception e) {
                throw new RuntimeException("cannot convert value of " + value.getClass().getName()
                        + " to: " + knownType.getName());
            }
        }

        //noinspection unchecked
        variableMap.put(name, value);
    }

    public Object getValue() {
        try {
            return JsonUtil.objectFromJson((String) variableMap.get(name), String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("name:" + name + "对应的值不是基础数据类型，不能进行比较，value:" + variableMap.get(name));
        }
    }

    public int getFlags() {
        return 0;
    }

}
