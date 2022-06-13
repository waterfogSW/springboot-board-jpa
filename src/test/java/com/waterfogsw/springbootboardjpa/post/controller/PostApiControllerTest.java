package com.waterfogsw.springbootboardjpa.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waterfogsw.springbootboardjpa.post.controller.dto.PostResponse;
import com.waterfogsw.springbootboardjpa.post.entity.Post;
import com.waterfogsw.springbootboardjpa.post.service.PostService;
import com.waterfogsw.springbootboardjpa.post.util.PostConverter;
import com.waterfogsw.springbootboardjpa.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostApiController.class)
@MockBeans({
        @MockBean(JpaMetamodelMappingContext.class),
})
@AutoConfigureRestDocs()
class PostApiControllerTest {
    private static final String URL = "/api/v1/posts";

    @MockBean
    private PostConverter postConverter;

    @MockBean
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Nested
    @DisplayName("addPost 메서드는")
    class Describe_addPost {

        @Nested
        @DisplayName("title, content 가 빈문자열이 아니고, userId 가 양수이면")
        class Context_with_ValidData {

            @Test
            @DisplayName("created 응답을 반환한다")
            void It_ResponseCreated() throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                verify(postService).addPost(anyLong(), any());
                response.andExpect(status().isCreated())
                        .andDo(document("post-add",
                                requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용"),
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("작성자 아이디")
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("title 의 길이가 100자 이상이면")
        class Context_with_OverHundredsCharactersTitle {

            @Test
            @DisplayName("BadRequest 를 응답한다")
            void It_ResponseBadRequest() throws Exception {
                final var testTitle = "t".repeat(101);

                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", testTitle);
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }


        @Nested
        @DisplayName("title 이 빈 문자열이거나, 존재하지 않으면")
        class Context_with_BlankTitle {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("BadRequest 를 응답한다")
            void It_ResponseBadRequest(String src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", src);
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("content 가 빈 문자열이거나, 존재하지 않으면")
        class Context_with_BlankContent {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("BadRequest 를 응답한다")
            void It_responseBadRequest(String src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", src);
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("userId 가 양수가 아니면")
        class Context_with_NotPositiveUserId {

            @ParameterizedTest
            @ValueSource(longs = {-1, 0})
            @DisplayName("BadRequest 를 응답한다")
            void It_responseBadRequest(long src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", "test");
                requestMap.put("userId", src);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("userId 가 없으면")
        class Context_with_NullUserId {

            @Test
            @DisplayName("BadRequest 를 응답한다")
            void It_responseBadRequest() throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", "test");

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("updatePost 메서드는")
    class Describe_updatePost {

        final Long testPostId = 1L;

        @Nested
        @DisplayName("모든값이 유효하면")
        class Context_with_ValidData {

            @Test
            @DisplayName("ok 응답을 반환한다")
            void It_ResponseCreated() throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = RestDocumentationRequestBuilders.put(URL + "/{id}", testPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                verify(postService).updatePost(anyLong(), anyLong(), any());
                response.andExpect(status().isOk())
                        .andDo(document("post-update",
                                pathParameters(
                                        parameterWithName("id").description("게시물 번호")
                                ),
                                requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용"),
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("작성자 아이디")
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("id 값이 양수가 아니면")
        class Context_with_NotPositiveId {

            @ParameterizedTest
            @ValueSource(longs = {-1, 0})
            @DisplayName("BadRequest 를 응답한다")
            void It_ResponseBadRequest(long src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.put(URL + "/" + src)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }


        @Nested
        @DisplayName("title 의 길이가 100자 이상이면")
        class Context_with_OverHundredsCharactersTitle {

            @Test
            @DisplayName("BadRequest 를 응답한다")
            void It_ResponseBadRequest() throws Exception {
                final var testTitle = "t".repeat(101);

                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", testTitle);
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.put(URL + "/" + testPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }


        @Nested
        @DisplayName("title 이 빈 문자열이거나, 존재하지 않으면")
        class Context_with_BlankTitle {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("BadRequest 를 응답한다")
            void It_ResponseBadRequest(String src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", src);
                requestMap.put("content", "testContent");
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.put(URL + "/" + testPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("content 가 빈 문자열이거나, 존재하지 않으면")
        class Context_with_BlankContent {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("BadRequest 를 응답한다")
            void It_responseBadRequest(String src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", src);
                requestMap.put("userId", 1);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.put(URL + "/" + testPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }

        @Nested
        @DisplayName("userId 가 양수가 아니면")
        class Context_with_NotPositiveUserId {

            @ParameterizedTest
            @ValueSource(longs = {-1, 0})
            @DisplayName("BadRequest 를 응답한다")
            void It_responseBadRequest(long src) throws Exception {
                final var requestMap = new HashMap<String, Object>();
                requestMap.put("title", "test");
                requestMap.put("content", "test");
                requestMap.put("userId", src);

                final var content = mapper.writeValueAsString(requestMap);

                final var request = MockMvcRequestBuilders.put(URL + "/" + testPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

                final var response = mockMvc.perform(request);
                response.andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("getOne 메서드는")
    class Describe_GetOne {

        @Nested
        @DisplayName("id 값이 양수이면")
        class Context_with_PositiveId {

            final Long testGetId = 1L;

            @Test
            @DisplayName("ok 응답을 반환한다")
            void It_ResponseOk() throws Exception {
                final var user = User.builder()
                        .name("test-username")
                        .email("test-email")
                        .build();

                final var post = Post.builder()
                        .title("test-title")
                        .content("test-content")
                        .user(user)
                        .build();

                given(postService.getOne(eq(1L))).willReturn(post);

                final var postResponse = new PostResponse("test", "test", "test", "test");
                given(postConverter.toDto(eq(post))).willReturn(postResponse);

                final var request = RestDocumentationRequestBuilders.get(URL + "/{id}", testGetId);
                final var response = mockMvc.perform(request);

                response.andExpect(status().isOk())
                        .andDo(document("post-detail",
                                pathParameters(
                                        parameterWithName("id").description("게시물 번호")
                                ),
                                responseFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용"),
                                        fieldWithPath("userName").type(JsonFieldType.STRING).description("작성자 이름"),
                                        fieldWithPath("userEmail").type(JsonFieldType.STRING).description("작성자 이메일")
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("id 값이 양수가 아니면")
        class Context_with_NotPositiveId {

            @ParameterizedTest
            @ValueSource(longs = {-1, 0})
            @DisplayName("ok 응답을 반환한다")
            void It_ResponseOk(long src) throws Exception {
                final var request = MockMvcRequestBuilders.get(URL + "/" + src);

                final var response = mockMvc.perform(request);

                response.andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("getAll 메서드는")
    class Describe_getAll {

        @Nested
        @DisplayName("요청되면")
        class Context_with_Requested {

            @Test
            @DisplayName("ok 응답을 반환한다")
            void It_ResponseOk() throws Exception {
                final var request = MockMvcRequestBuilders.get(URL);

                final var response = mockMvc.perform(request);

                verify(postService).getAll(any());
                response.andExpect(status().isOk())
                        .andDo(document("post-list",
                                responseFields(
                                        fieldWithPath("[]").type(JsonFieldType.ARRAY).optional().description("게시물 정보")
                                )
                        ));
            }
        }
    }
}
