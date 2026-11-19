package com.city.system.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class AuthIssueIntegrationTest {
  @Autowired MockMvc mvc;
  @Test void registerAndRejectUnauthenticatedIssue() throws Exception{
    MvcResult otpResult=mvc.perform(post("/api/auth/otp/send").contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"test@example.com\",\"purpose\":\"REGISTER\"}")).andExpect(status().isOk()).andReturn();
    JsonNode otpJson=new ObjectMapper().readTree(otpResult.getResponse().getContentAsString());
    String otp=otpJson.path("developmentOtp").asText();
    mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
      .content("{\"name\":\"Test Citizen\",\"email\":\"test@example.com\",\"password\":\"secret123\",\"confirmPassword\":\"secret123\",\"otp\":\""+otp+"\"}"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());
    mvc.perform(get("/api/issues/my")).andExpect(status().isForbidden());
  }
  @Test void adminStatsAvailableOnlyToAdmin() throws Exception{
    mvc.perform(get("/api/admin/stats")).andExpect(status().isForbidden());
  }

  @Test void resetPasswordChangesLoginCredential() throws Exception{
    MvcResult registerOtp=mvc.perform(post("/api/auth/otp/send").contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"reset-check@example.com\",\"purpose\":\"REGISTER\"}")).andExpect(status().isOk()).andReturn();
    String registerCode=new ObjectMapper().readTree(registerOtp.getResponse().getContentAsString()).path("developmentOtp").asText();
    mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
      .content("{\"name\":\"Reset Check\",\"email\":\"reset-check@example.com\",\"password\":\"OldPass@123\",\"confirmPassword\":\"OldPass@123\",\"otp\":\""+registerCode+"\"}"))
      .andExpect(status().isOk());

    MvcResult forgotOtp=mvc.perform(post("/api/auth/forgot-password").contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"reset-check@example.com\"}")).andExpect(status().isOk()).andReturn();
    String resetCode=new ObjectMapper().readTree(forgotOtp.getResponse().getContentAsString()).path("developmentOtp").asText();
    mvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"reset-check@example.com\",\"otp\":\""+resetCode+"\",\"password\":\"NewPass@123\",\"confirmPassword\":\"NewPass@123\"}"))
      .andExpect(status().isOk());

    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"reset-check@example.com\",\"password\":\"NewPass@123\",\"confirmPassword\":\"NewPass@123\"}"))
      .andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());
    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
      .content("{\"email\":\"reset-check@example.com\",\"password\":\"OldPass@123\",\"confirmPassword\":\"OldPass@123\"}"))
      .andExpect(status().isBadRequest());
  }

}
