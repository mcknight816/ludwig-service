package com.bluntsoftware.ludwig.dto;

import com.bluntsoftware.ludwig.domain.Connection;
import com.bluntsoftware.ludwig.domain.ConnectionMap;
import com.bluntsoftware.ludwig.domain.FlowActivity;
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
	private String path;
	private Boolean locked = false;
	private List<FlowActivity> activities;
	private List<Connection> connections;
	private List<ConnectionMap> connectionMaps;
}