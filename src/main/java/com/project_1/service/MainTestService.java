package com.project_1.service;

import com.project_1.dto.MainTestDto;
import com.project_1.mapper.MainTestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.project_1.config.MainConst.LOG;

@Service
@RequiredArgsConstructor
public class MainTestService {

    private final MainTestMapper mapper;

    public String test(MainTestDto.search dto) {

        LOG.info("log_서비스호출.");
        LOG.info("log_파라메터 dname : " + dto.dname());
        LOG.info("log_파라메터 dno : " + dto.dno());

        String result = mapper.mainTest(dto);

        LOG.info("log_서비스 결과 : " + result);

        return result;
    }


}
