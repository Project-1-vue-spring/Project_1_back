package com.project_1.mapper;

import com.project_1.dto.MainTestDto;
import com.project_1.dvo.UserDvo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int join(UserDvo dvo);

    String selectUserEncodePassWord(String userPassword);

    UserDvo selectUserInfo(String userId);
}
