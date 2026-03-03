package com.assistudy.commonservice.time.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAnalysisResultResponse {

    private String result;
    private LocalDateTime updatedAt;
    private boolean isCached; // 캐시된 결과인지 여부
}
