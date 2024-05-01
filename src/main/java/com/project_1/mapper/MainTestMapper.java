package com.project_1.mapper;

import com.project_1.dto.MainTestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MainTestMapper {

    String mainTest(MainTestDto.search dto);

}
