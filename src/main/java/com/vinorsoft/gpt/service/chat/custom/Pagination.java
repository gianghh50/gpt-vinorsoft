package com.vinorsoft.gpt.service.chat.custom;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vinorsoft.gpt.service.chat.dto.PaginationDto;


@Component
public class Pagination {

	public PaginationDto toPage(List list, Integer page, Integer limit) {
		Integer totalElement = list.size();
		Integer start = (page - 1) * limit;
		Integer end = page * limit;
		PaginationDto result = new PaginationDto();
		result.setTotalElements(totalElement);
		if(end >= totalElement) {
			end = totalElement;
		}
		if(start < totalElement && start < end) {
			result.setData(list.subList(start, end));
		}
		else {
			result.setData(new ArrayList<>());
		}
		return result;
	}
}
