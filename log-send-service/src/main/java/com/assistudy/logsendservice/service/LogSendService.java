package com.assistudy.logsendservice.service;

import com.assistudy.logsendservice.dto.request.OnDeviceLogRequest;

public interface LogSendService {
    void processAndSendLog(OnDeviceLogRequest request);
}