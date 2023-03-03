package com.example.studio_azurite_rox_web.service;

import com.example.studio_azurite_rox_web.dto.*;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.repository.StudioMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class StudioMemberService {
    @Autowired
    private StudioMemberRepository studioMemberRepository;

    @Transactional
    public boolean insertData(StudioMemberForm studioMemberForm) throws DataAccessException {
        return studioMemberRepository.insert(studioMemberForm);
    }

    @Transactional
    public boolean updateData(StudioMemberForm studioMemberForm) throws DataAccessException {
        return studioMemberRepository.update(studioMemberForm);
    }

    @Transactional
    public boolean passwordChangeData(PwChangeForm pwChangeForm) throws DataAccessException {
        return studioMemberRepository.passwordChange(pwChangeForm);
    }

    @Transactional
    public boolean deleteData(Integer id) throws DataAccessException  {
        return studioMemberRepository.delete(id);
    }

    public int findOrCount(boolean mode, StudioMemberSearchForm studioMemberSearchForm) throws DataAccessException {
        return (int)studioMemberRepository.findOrCount(mode, studioMemberSearchForm).get(0).get("Total Count");
    }

    public List<StudioMemberForm> findListPaging(boolean mode, StudioMemberSearchForm studioMemberSearchForm,
                                            int startIndex, int pageSize) throws DataAccessException {
        return studioMemberRepository.findListPaging(mode, studioMemberSearchForm, startIndex, pageSize);
    }

/*
    public List<StudioMemberForm> findAll() throws DataAccessException  {
        return studioMemberRepository.findAll();
    }
*/

    public StudioMemberForm findById(Integer id) throws DataAccessException  {
        return studioMemberRepository.findById(id);
    }

/*
    public List<StudioMemberForm> findByIdAndName(StudioMemberSearchForm studioMemberSearchForm) throws DataAccessException {
        return studioMemberRepository.findByIdAndName(studioMemberSearchForm);
    }
*/
}
