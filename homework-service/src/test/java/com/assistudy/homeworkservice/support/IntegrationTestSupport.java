package com.assistudy.homeworkservice.support;

import com.assistudy.homeworkservice.global.client.RoomServiceClient;
import com.assistudy.homeworkservice.global.client.UserServiceClient;
import com.assistudy.homeworkservice.global.dto.response.UserInfoResponse;
import com.assistudy.shared.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;

/**
 * homework/feedback 통합테스트 공통 베이스.
 * RoomServiceClient/UserServiceClient는 common-service/user-service가 없는 테스트 환경이라
 * Mockito mock으로 대체한다. 각 테스트가 시나리오에 맞게 room/host 정보를 stub해야 하므로
 * (common-service의 IntegrationTestSupport와 달리) 여기서는 기본 stub을 깔지 않는다.
 *
 * MySQL 컨테이너는 싱글턴 패턴(static 블록에서 직접 start, 아무도 stop 안 함)이고,
 * 테스트 간 데이터 격리를 위해 클래스 레벨 @Transactional로 각 테스트를 롤백한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestSupport {

    static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("assistudy_homework_test");

    static {
        MYSQL.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected RoomServiceClient roomServiceClient;

    @MockitoBean
    protected UserServiceClient userServiceClient;

    @BeforeEach
    void resetMocks() {
        reset(roomServiceClient, userServiceClient);
        lenient().when(userServiceClient.getUserInfo(anyLong()))
                .thenAnswer(inv -> {
                    Long id = inv.getArgument(0);
                    return ApiResponse.onSuccess(new UserInfoResponse(id, "user" + id + "@test.com", "user" + id, null));
                });
    }
}
