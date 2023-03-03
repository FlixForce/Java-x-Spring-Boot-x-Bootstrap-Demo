package com.example.studio_azurite_rox_web.controller;

import com.example.studio_azurite_rox_web.Utility.ErrorMessageGenerator;
import com.example.studio_azurite_rox_web.dto.Pagination;
import com.example.studio_azurite_rox_web.dto.ReserveForm;
import com.example.studio_azurite_rox_web.dto.ReserveSearchForm;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.service.ReserveInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Controller
public class MemberController {
    @Autowired
    ReserveInfoService reserveInfoService;
    @Autowired
    ErrorMessageGenerator errorMessageGenerator;

    private Integer backupId;
    private Integer backupMemberId;

    @GetMapping("/member")
    public String member(@RequestParam("p") String requestParam,
                         @RequestParam(value = "page", defaultValue = "1") int requestParamPage,
                         Model model,
                         @AuthenticationPrincipal LoginUserDetails loginUserDetails) {
        Integer id = null;
        if (requestParam.indexOf("-") > -1) {
            id = Integer.parseInt(requestParam.substring(requestParam.indexOf("-") + 1, requestParam.length()));
            requestParam = requestParam.substring(0, requestParam.indexOf("-"));
        }

        switch (requestParam) {
            // 予約一覧(スタジオ会員ID)
            case "reserve_list":
                Pagination pagination = null;
                ReserveSearchForm reserveSearchForm = new ReserveSearchForm();
                model.addAttribute("pageLinkUrl", "/member?p=reserve_list&page=");
                try {
                    // 会員ID
                    reserveSearchForm.setMemberId(loginUserDetails.getId());

                    // 現在の日時を取得して、デフォルト値とする
                    Calendar calendar = Calendar.getInstance();
                    String dateTime = String.format("%04d-%02d-%02dT10:00",
                            calendar.get(Calendar.YEAR),
                            (calendar.get(Calendar.MONTH) + 1),
                            calendar.get(Calendar.DATE));
                    reserveSearchForm.setStartDatetime(dateTime);

                    // 予約情報データの総数
                    int totalListCount = reserveInfoService.findOrCount(false, reserveSearchForm);

                    // トータルデータ数と現在のページを設定
                    pagination = new Pagination(totalListCount, requestParamPage);

                    // DBスタートインデックス
                    int startIndex = pagination.getStartIndex();

                    // ページごとに表示するデータ最大数
                    int pageSize = pagination.getPageSize();

                    // データ取得
                    List<ReserveForm> reserveFormList = reserveInfoService.findListPaging(true, reserveSearchForm, startIndex, pageSize);

                    model.addAttribute("reserveFormList", reserveFormList);
                    model.addAttribute("pagination", pagination);
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    List<ReserveForm> reserveFormList = new ArrayList<>();
                    model.addAttribute("reserveFormList", reserveFormList);
                    model.addAttribute("pagination", pagination);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "02-00-00-12");
                }

                break;
            // 予約変更
            case "reserve_change":
                ReserveForm reserveForm;
                try {
                    reserveForm = reserveInfoService.findById(id);
                    model.addAttribute("reserveForm", reserveForm);
                    model.addAttribute("actionUrl", "/member");

                    backupId = reserveForm.getId();
                    backupMemberId = reserveForm.getMemberId();
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    reserveForm = new ReserveForm();
                    model.addAttribute("reserveForm", reserveForm);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "02-00-00-13");
                }

                break;
            // 予約削除
            case "reserve_delete":
                try {
                    reserveForm = reserveInfoService.findById(id);
                    model.addAttribute("reserveForm", reserveForm);

                    reserveInfoService.deleteData(backupId);
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "02-00-00-14");
                    model.addAttribute("contentName", "content/reserve_change");

                    return "member";
                }

                requestParam = "reserve_delete_ok";

                break;
        }

        requestParam = "content/" + requestParam;
        model.addAttribute("contentName", requestParam);

        return "member";
    }

    /**
     * スタジオ予約内容変更
     *
     * @param reserveForm   予約内容のデータ
     * @param bindingResult 入力された内容
     * @return View
     */
    @PostMapping("/member")
    public String member(@Validated @ModelAttribute ReserveForm reserveForm,
                         BindingResult bindingResult,
                         Model model) {
        reserveForm.setId(backupId);
        reserveForm.setMemberId(backupMemberId);

        model.addAttribute("reserveForm", reserveForm);
        model.addAttribute("actionUrl", "/member");
        model.addAttribute("contentName", "content/reserve_change");

        if (bindingResult.hasErrors()) {
            return "member";
        }

        // 予約情報の保存
        try {
            reserveInfoService.updateData(reserveForm);
        } catch (DataAccessException e) {
            e.printStackTrace();

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "02-01-00-15");

            return "member";
        }

        return "redirect:/page?cn=reserve_change_ok";
    }
}
