package com.assistudy.webrtcservice.controller;

import com.assistudy.shared.annotation.LoginUser;
import com.assistudy.webrtcservice.dto.request.CreateTokenRequest;
import com.assistudy.webrtcservice.dto.response.TokenResponse;
import com.assistudy.webrtcservice.service.WebRTCService;
import com.assistudy.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * WebRTC 관련 API 컨트롤러
 * 화상회의 토큰 생성 및 방 상태 조회를 제공합니다.
 */
@RestController
@RequestMapping("/webrtc")
@RequiredArgsConstructor
@Tag(name = "WebRTC", description = "화상회의 관련 API")
public class WebRTCController {

    private final WebRTCService webRTCService;

    @PostMapping("/tokens")
    @Operation(summary = "화상회의 토큰 생성", description = "방에 참가하기 위한 LiveKit JWT 토큰을 생성합니다. (OpenVidu 3.3.0)")
    public ApiResponse<TokenResponse> createToken(@LoginUser Long userId, @RequestBody CreateTokenRequest request) {
        TokenResponse response = webRTCService.createToken(request, userId);
        return ApiResponse.onSuccess(response);
    }
}
