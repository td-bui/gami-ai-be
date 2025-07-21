package com.project.gamiai.dto.request;
import java.util.List;

public class RunUserCodeRequest {
    private String userCode;
    private List<TestCaseDto> testCases;

    // getters and setters

    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public List<TestCaseDto> getTestCases() {
        return testCases;
    }
    public void setTestCases(List<TestCaseDto> testCases) {
        this.testCases = testCases;
    }
}