/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.webtrekk.mailsender.Controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.webtrekk.mailsender.controller.MailSenderController;
import org.webtrekk.mailsender.controller.exception.GlobalExceptionHandler;
import org.webtrekk.mailsender.service.MailSenderService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MailSenderControllerTests {

    MockMvc mockMvc = null;

    @Mock
    MailSenderService mailSenderService;

    @InjectMocks
    MailSenderController mailSenderController;

    @Before
    public void init(){
        initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(mailSenderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    public void ShouldRespondHttpStatusOkWhenAllParameterAreSetted() throws Exception {

        when(mailSenderService.sendMailMessage(any() ,any() , any())).thenReturn(new AsyncResult<Boolean>(true));

        mockMvc.perform(put("/sendMail")
                .param("to" , "to")
                .param("subject" , "subject")
                .param("text" , "text"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void ShouldRespondBadRequestIfParameterIsMissing() throws Exception {

        when(mailSenderService.sendMailMessage(any() ,any() , any())).thenReturn(new AsyncResult<Boolean>(false));

        mockMvc.perform(put("/sendMail")
                .param("subject" , "subject")
                .param("text" , "text"))
                .andDo(print())
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }


}
