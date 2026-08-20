package com.assistudy.userservice.grpc;

import com.assistudy.shared.grpc.CheckTokenReply;
import com.assistudy.shared.grpc.CheckTokenRequest;
import com.assistudy.shared.grpc.UserGrpcServiceGrpc;
import com.assistudy.shared.grpc.UserInfoReply;
import com.assistudy.shared.grpc.UserInfoRequest;
import com.assistudy.shared.grpc.UsersInfoReply;
import com.assistudy.shared.grpc.UsersInfoRequest;
import com.assistudy.userservice.dto.response.UserInfoResponse;
import com.assistudy.userservice.service.query.AuthQueryService;
import com.assistudy.userservice.service.query.UserQueryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * common-service가 호출하는 내부 전용 gRPC 서버. REST {@link com.assistudy.userservice.controller.InternalUserController}와
 * 동일한 서비스 로직(UserQueryService/AuthQueryService)을 그대로 재사용한다.
 */
@Service
@RequiredArgsConstructor
public class UserGrpcServiceImpl extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserQueryService userQueryService;
    private final AuthQueryService authQueryService;

    @Override
    public void getUserInfo(UserInfoRequest request, StreamObserver<UserInfoReply> responseObserver) {
        UserInfoResponse response = userQueryService.getUserInfo(request.getUserId());
        responseObserver.onNext(toReply(response));
        responseObserver.onCompleted();
    }

    @Override
    public void getUsersInfo(UsersInfoRequest request, StreamObserver<UsersInfoReply> responseObserver) {
        List<UserInfoResponse> responses = userQueryService.getUsersInfo(request.getUserIdsList());

        UsersInfoReply.Builder builder = UsersInfoReply.newBuilder();
        responses.forEach(r -> builder.addUsers(toReply(r)));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkUserToken(CheckTokenRequest request, StreamObserver<CheckTokenReply> responseObserver) {
        boolean blacklisted = authQueryService.isBlacklisted(request.getToken());
        responseObserver.onNext(CheckTokenReply.newBuilder().setBlacklisted(blacklisted).build());
        responseObserver.onCompleted();
    }

    private UserInfoReply toReply(UserInfoResponse response) {
        UserInfoReply.Builder builder = UserInfoReply.newBuilder()
                .setId(response.getId())
                .setNickname(response.getNickname() == null ? "" : response.getNickname());
        if (response.getEmail() != null) {
            builder.setEmail(response.getEmail());
        }
        if (response.getProfileImage() != null) {
            builder.setProfileImage(response.getProfileImage());
        }
        return builder.build();
    }
}
