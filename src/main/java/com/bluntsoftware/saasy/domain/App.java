package com.bluntsoftware.saasy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class App {

	@Id
	private String id;
	private String owner;
	private String name;
	private String jwkSetUri;
	private List<Plan> plans;
	private List<String> roles;
	private BraintreeCredentials braintree;
}
