package com.vinorsoft.gpt.service.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDto {

	private List<Object> data;
	private Integer totalElements;
}
