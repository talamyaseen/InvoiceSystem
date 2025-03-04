package io.invoice_system.dto;

import java.util.List;

import io.invoice_system.model.Role;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer ";
    private List<String> roles;


    public AuthResponseDTO(String accessToken,List<String> roles) {
        this.accessToken = accessToken;
        this.setRoles(roles);
    }

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}



    
}