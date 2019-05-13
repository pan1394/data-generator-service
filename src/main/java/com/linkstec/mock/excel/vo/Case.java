package com.linkstec.mock.excel.vo;
import java.util.List;

public class Case {
    public Case() {
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    private List<Condition> conditions;

    private String caseNumber;

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }
}
