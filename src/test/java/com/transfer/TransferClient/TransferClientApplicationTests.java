package com.transfer.TransferClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.TransferClient.controller.TransferClientController;
import com.transfer.TransferClient.dto.TransferDto;
import com.transfer.TransferClient.entity.EFraisType;
import com.transfer.TransferClient.entity.ETransferType;
import com.transfer.TransferClient.service.TransferClientService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(TransferClientController.class)
class TransferClientApplicationTests {

	@MockBean
	TransferClientService transferClientService;

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void tesTransferAccountToAccount()throws Exception {
		TransferDto transferDto = new TransferDto(null, 1000, new Date(), "from",
				"to", ETransferType.ACCOUNT_TO_ACCOUNT, EFraisType.FROM_ME, "21-20-2022");


	}

}
