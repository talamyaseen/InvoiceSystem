package io.invoice_system.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.invoice_system.repository.GeneralRepository;

@Service
public class GeneralService {
    
	@Autowired
	GeneralRepository generalRepository;
	@Autowired
	AiService aiService;
 
	public List<Object> executeSql(String question) {
		String cleaned = question.replaceAll("\\\\n", " ")
                .replaceAll("(?i)^```sql|```$", " ")
                .replaceAll("\\n", " ");
	    Pattern pattern = Pattern.compile("SELECT.*?;",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(cleaned);
		 if (matcher.find()) {
		        String sql = matcher.group();
		        System.out.println("Found SQL query: " + sql);  
		        return generalRepository.executeSql(sql);
		    } else {
		    	 throw new IllegalStateException("No valid SQL query found.");
		    }
	}

	public List<Object> promoteToSqlJanAi(String question) {
		 System.out.println("hi2"+question);
	      return executeSql(aiService.getJanAIResponse(question));
	}

	public List<Object> promoteToSqlGemini(String question) {
		  return executeSql(aiService.getGeminiResponse(question));
	}
}

