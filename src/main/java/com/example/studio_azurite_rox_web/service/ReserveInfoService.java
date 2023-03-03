package com.example.studio_azurite_rox_web.service;

import com.example.studio_azurite_rox_web.dto.ReserveForm;
import com.example.studio_azurite_rox_web.dto.ReserveSearchForm;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.entity.ReserveInfo;
import com.example.studio_azurite_rox_web.repository.ReserveInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReserveInfoService {
    @Autowired
    private ReserveInfoRepository reserveInfoRepository;

    @Transactional
    public boolean insertData(LoginUserDetails loginUserDetails,
                              ReserveForm reserveForm) throws DataAccessException {
        return reserveInfoRepository.insert(loginUserDetails, reserveForm);
    }

    @Transactional
    public boolean updateData(ReserveForm reserveForm) throws DataAccessException {
        return reserveInfoRepository.change(reserveForm);
    }

    @Transactional
    public boolean deleteData(Integer id) throws DataAccessException  {
        return reserveInfoRepository.delete(id);
    }

    public int findOrCount(boolean mode, ReserveSearchForm reserveSearchForm) throws DataAccessException {
        return (int)reserveInfoRepository.findOrCount(mode, reserveSearchForm).get(0).get("Total Count");
    }

    public List<ReserveForm> findListPaging(boolean mode, ReserveSearchForm reserveSearchForm,
                                            int startIndex, int pageSize) throws DataAccessException {
        return reserveInfoRepository.findListPaging(mode, reserveSearchForm, startIndex, pageSize);
    }

/*
    public List<ReserveForm> findAll(Integer memberId) throws DataAccessException {
        return reserveInfoRepository.findAll(memberId);
    }
*/

    public ReserveForm findById(Integer id) throws DataAccessException {
        return reserveInfoRepository.findById(id);
    }

/*
    public List<ReserveForm> findBySearch(ReserveSearchForm reserveSearchForm) throws DataAccessException {
        return reserveInfoRepository.findBySearch(reserveSearchForm);
    }
*/
}
