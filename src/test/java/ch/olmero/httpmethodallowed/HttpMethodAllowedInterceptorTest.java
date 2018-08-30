package ch.olmero.httpmethodallowed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.server.MethodNotAllowedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class HttpMethodAllowedInterceptorTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void putWithMethodAllowed_NotCompleted_ReturnsOK() throws Exception {
		mockMvc.perform(put("/task/supertask")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{}"))
			.andExpect(status().isOk());
	}

	@Test
	void putWithMethodAllowed_Completed_ReturnsNotAllowed() throws Exception {
		ResultActions resultActions = mockMvc.perform(put("/task/completed")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{}"))
			.andExpect(status().isMethodNotAllowed());
		assertThat(resultActions.andReturn().getResolvedException())
			.isInstanceOf(MethodNotAllowedException.class);
		MethodNotAllowedException resolvedException = (MethodNotAllowedException) resultActions.andReturn().getResolvedException();
		assertThat(resolvedException.getSupportedMethods())
			.containsExactly(HttpMethod.GET);
	}

}