package cn.felord.spring.security;

import cn.felord.spring.security.jwt.JwtTokenGenerator;
import cn.felord.spring.security.jwt.JwtTokenPair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityLearningApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;


    @Test
    @WithMockUser(value = "Felordcn", password = "12345")
    public void contextLoads() throws Exception {
       mockMvc.perform(MockMvcRequestBuilders.get("/foo/test")).andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    public void jwtTest(){
        HashSet<String> roles = new HashSet<>();
        HashMap<String, String> additional = new HashMap<>();
        additional.put("uname","Felordcn");

        JwtTokenPair jwtTokenPair = jwtTokenGenerator.jwtTokenPair("133", roles, additional);

        System.out.println("jwtTokenPair = " + jwtTokenPair);
    }
}
