package com.assistudy.commonservice.support;

import com.assistudy.commonservice.global.client.UserServiceClient;
import com.assistudy.commonservice.global.dto.response.UserInfoResponse;
import com.assistudy.shared.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;

/**
 * room/homework/webrtc 통합테스트 공통 베이스.
 * Testcontainers(MySQL+Redis)로 실제 DB/캐시를 띄우고, MockMvc로 컨트롤러까지 왕복 검증한다.
 * UserServiceClient는 user-service가 없는 테스트 환경이라 Mockito mock으로 대체한다
 * (room/homework/webrtc 컨트롤러는 @LoginUser만 쓰고 @VerifiedUser는 안 써서, 인증 경로엔 영향 없음).
 * 기본으로 "요청한 id 그대로 닉네임을 만들어 돌려주는" lenient stub을 깔아두므로,
 * 개별 테스트는 특정 실패/커스텀 응답이 필요할 때만 재정의하면 된다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Testcontainers
public abstract class IntegrationTestSupport {

    @Container
    static MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("assistudy_common_test");

    @Container
    static GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserServiceClient userServiceClient;

    protected static UserInfoResponse stubUser(Long id) {
        return new UserInfoResponse(id, "user" + id + "@test.com", "user" + id, null);
    }

    @BeforeEach
    void setUpUserServiceClientMock() {
        reset(userServiceClient);
        lenient().when(userServiceClient.getUserInfo(anyLong()))
                .thenAnswer(inv -> ApiResponse.onSuccess(stubUser(inv.getArgument(0))));
        lenient().when(userServiceClient.getUsersInfo(anyList()))
                .thenAnswer(inv -> {
                    List<Long> ids = inv.getArgument(0);
                    return ApiResponse.onSuccess(ids.stream().map(IntegrationTestSupport::stubUser).toList());
                });
    }
}
