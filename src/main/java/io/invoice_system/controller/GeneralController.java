package io.invoice_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.invoice_system.dto.QuestionDTO;
import io.invoice_system.service.GeneralService;


@RestController
public class GeneralController {
	@Autowired
	GeneralService generalService;

	 @PreAuthorize("hasAnyRole('SUPPORT_USER','AUDITOR')")
	 @PostMapping("/execute")
	 public ResponseEntity<List<Object>> executeSQL(@RequestBody String question) {	      
	            System.out.println(question);
	            List<Object> result = generalService.executeSql(question);
	            return new ResponseEntity<>(result, HttpStatus.OK);
	    }
	 
	 
	 @PreAuthorize("hasAnyRole('SUPPORT_USER','AUDITOR')")
	 @PostMapping("/jan/prompt")
	 public ResponseEntity<List<Object>> janAiTextToSQL(@RequestBody QuestionDTO questionDTO) {	    
		 

	            List<Object> result = generalService.promoteToSqlJanAi(questionDTO.getQuestion());
	            return new ResponseEntity<>(result, HttpStatus.OK);
	    }
	 
	 @PreAuthorize("hasAnyRole('SUPPORT_USER','AUDITOR')")
	 @PostMapping("/gemini/prompt")
	 public ResponseEntity<List<Object>> geminiTextToSQL(@RequestBody QuestionDTO questionDTO) {	      
	            List<Object> result = generalService.promoteToSqlGemini(questionDTO.getQuestion());
	            return new ResponseEntity<>(result, HttpStatus.OK);
	    }
	  
}

