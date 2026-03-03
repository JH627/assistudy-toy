package com.assistudy.commonservice.gms.service;

import com.assistudy.commonservice.gms.dto.response.GmsChatResponse;

public interface GmsService {
    GmsChatResponse sendChatMessage(String message, String systemPrompt);
}