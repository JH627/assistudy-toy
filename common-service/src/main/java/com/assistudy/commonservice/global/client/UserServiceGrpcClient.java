package com.assistudy.commonservice.global.client;

import com.assistudy.shared.grpc.CheckTokenReply;
import com.assistudy.shared.grpc.CheckTokenRequest;
import com.assistudy.shared.grpc.UserGrpcServiceGrpc;
import com.assistudy.shared.grpc.UserInfoReply;
import com.assistudy.shared.grpc.UserInfoRequest;
import com.assistudy.shared.grpc.UsersInfoReply;
import com.assistudy.shared.grpc.UsersInfoRequest;
import com.assistudy.shared.response.ApiResponse;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * user-service 원격 호출을 gRPC로 수행. REST 응답 타입(ApiResponse/UserInfoResponse)으로
 * 변환해서 반환하므로, UserServiceClientWrapper 입장에서는 REST/gRPC 어느 쪽을 쓰든 동일한
 * 타입을 다룸
 */
@Component
@RequiredArgsConstructor
public class UserServiceGrpcClient {

    private final UserGrpcServiceGrpc.UserGrpcServiceBlockingStub stub;

    public ApiResponse<UserInfoResponse> getUserInfo(Long userId) {
        UserInfoReply reply = stub.getUserInfo(UserInfoRequest.newBuilder().setUserId(userId).build());
        return ApiResponse.onSuccess(toUserInfoResponse(reply));
    }

    public ApiResponse<List<UserInfoResponse>> getUsersInfo(List<Long> userIds) {
        UsersInfoReply reply = stub.getUsersInfo(UsersInfoRequest.newBuilder().addAllUserIds(userIds).build());
        List<UserInfoResponse> responses = reply.getUsersList().stream()
                .map(this::toUserInfoResponse)
                .toList();
        return ApiResponse.onSuccess(responses);
    }

    public ApiResponse<Boolean> checkUserToken(String token) {
        CheckTokenReply reply = stub.checkUserToken(CheckTokenRequest.newBuilder().setToken(token).build());
        return ApiResponse.onSuccess(reply.getBlacklisted());
    }

    private UserInfoResponse toUserInfoResponse(UserInfoReply reply) {
        return new UserInfoResponse(reply.getId(), reply.getEmail(), reply.getNickname(), reply.getProfileImage());
    }
}
