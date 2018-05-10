package de.seidfred.accountservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.seidfred.accountservice.entity.AccountRequestBody;
import de.seidfred.accountservice.entity.AccountResponseBody;

@RestController
public class AccountController {

	@PostMapping("/getAccountInfo")
	public ResponseEntity<AccountResponseBody> getAccountInfo(
			@RequestBody AccountRequestBody accountRequestBody) {

		System.out.println("Hello World!");

		return new ResponseEntity<AccountResponseBody>(
				new AccountResponseBody(), HttpStatus.OK);
	}

}
