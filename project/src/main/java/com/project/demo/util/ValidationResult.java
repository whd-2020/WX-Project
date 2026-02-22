package com.project.demo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验结果封装类
 * 用于收集所有校验错误，统一返回给前端
 */
public class ValidationResult {

    /** 是否通过校验 */
    private boolean valid;

    /** 错误信息列表 */
    private List<String> errors;

    public ValidationResult() {
        this.valid = true;
        this.errors = new ArrayList<>();
    }

    /**
     * 添加单条错误信息
     *
     * @param error 错误文本
     */
    public void addError(String error) {
        if (error == null || error.trim().isEmpty()) {
            return;
        }
        this.valid = false;
        this.errors.add(error);
    }

    /**
     * 添加多条错误信息
     *
     * @param errors 错误列表
     */
    public void addErrors(List<String> errors) {
        if (errors == null || errors.isEmpty()) {
            return;
        }
        this.valid = false;
        this.errors.addAll(errors);
    }

    /**
     * 是否通过校验
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * 获取所有错误信息
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * 使用中文分号拼接错误信息
     */
    public String getErrorMessage() {
        if (errors == null || errors.isEmpty()) {
            return "";
        }
        return String.join("；", errors);
    }

    /**
     * 使用换行符拼接错误信息
     */
    public String getErrorMessageWithLineBreak() {
        if (errors == null || errors.isEmpty()) {
            return "";
        }
        return String.join("\n", errors);
    }
}

