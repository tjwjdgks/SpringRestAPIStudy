package seo.study.springrestapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 테스트 코드 리팩토링
 * 여러 컨트롤러 간의 중복 코드 제거
 * 클래스 상속 사용
 * @Ignore or @Disabled 애토테이션으로 테스트로 간주되지 않도록 설정
 */
@SpringBootTest
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected ModelMapper modelMapper;

}
