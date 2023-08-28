package com.bluntsoftware.ludwig.dto;

import com.bluntsoftware.ludwig.model.Connection;
import com.bluntsoftware.ludwig.model.ConnectionMap;
import com.bluntsoftware.ludwig.model.FlowActivity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowDto {

	private String id;
	private String name;
	private Boolean locked = false;
	private List<FlowActivity> activities;
	private List<Connection> connections;
	private List<ConnectionMap> connectionMaps;
}