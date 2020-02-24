package com.zf.pojo;

import com.zf.utils.ParameterMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/24 9:59
 * @description
 */
public class BoundSql {

    private String sqlText; //解析过后的sql

    private List<ParameterMapping> parameterMappingList = new ArrayList<ParameterMapping>();

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }

    public void setParameterMappingList(List<ParameterMapping> parameterMappingList) {
        this.parameterMappingList = parameterMappingList;
    }
}
