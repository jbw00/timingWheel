package com.chinamobile.operations.project.timeround.client.vo;

import lombok.Data;

import java.util.List;

/**
 * msg格式
 * @author Bowen
 */
@Data
public class SMSInfo {
    private String content;
    private String tempNo;
    private String sendTime;
    private List<Integer> userId;
}
