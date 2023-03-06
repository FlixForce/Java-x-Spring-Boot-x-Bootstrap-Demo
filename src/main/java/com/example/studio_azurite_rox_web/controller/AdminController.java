package com.example.studio_azurite_rox_web.controller;

import com.example.studio_azurite_rox_web.Utility.ErrorMessageGenerator;
import com.example.studio_azurite_rox_web.dto.*;
import com.example.studio_azurite_rox_web.service.ReserveInfoService;
import com.example.studio_azurite_rox_web.service.StudioMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@Slf4j
@Controller
public class AdminController {
    @Autowired
    ReserveInfoService reserveInfoService;
    @Autowired
    StudioMemberService studioMemberService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    ErrorMessageGenerator errorMessageGenerator;

    private Integer backupId;
    private Integer backupMemberId;
    private boolean isClear;

    private ReserveSearchForm backupReserveSearchForm;

    private StudioMemberSearchForm backupStudioMemberSearchForm;

    @GetMapping("/admin")
    public String admin(@RequestParam("p") String requestParam,
                        @RequestParam(value = "page", defaultValue = "1") int requestParamPage,
                        Model model) {
        Integer id = null;
        if (requestParam.indexOf("-") > -1) {
            id = Integer.parseInt(requestParam.substring(requestParam.indexOf("-") + 1, requestParam.length()));
            requestParam = requestParam.substring(0, requestParam.indexOf("-"));
        }

        switch (requestParam) {
            // 予約一覧
            case "reserve_list":
                boolean mode = false;
                ReserveSearchForm reserveSearchForm = new ReserveSearchForm();
                try {
                    if ((backupReserveSearchForm.getId() == null || backupReserveSearchForm.getId().equals("")) &&
                            (backupReserveSearchForm.getMemberId() == null || backupReserveSearchForm.getMemberId().equals("")) &&
                            backupReserveSearchForm.getName().equals("") &&
                            (backupReserveSearchForm.getStartDatetime() == null || backupReserveSearchForm.getStartDatetime().equals(""))) {
                        mode = true;
                    } else {
                        reserveSearchForm.setId(backupReserveSearchForm.getId());
                        reserveSearchForm.setMemberId(backupReserveSearchForm.getMemberId());
                        reserveSearchForm.setName(backupReserveSearchForm.getName());
                        reserveSearchForm.setStartDatetime(backupReserveSearchForm.getStartDatetime());
                    }
                } catch (Exception e) {
                    mode = true;

                    if (!isClear) {
                        // 現在の日時を取得して、デフォルト値とする
                        Calendar calendar = Calendar.getInstance();
                        String dateTime = String.format("%04d-%02d-%02dT10:00",
                                calendar.get(Calendar.YEAR),
                                (calendar.get(Calendar.MONTH) + 1),
                                calendar.get(Calendar.DATE));
                        reserveSearchForm.setStartDatetime(dateTime);
                        reserveSearchForm.setId(null);
                        reserveSearchForm.setMemberId(null);
                        reserveSearchForm.setName(null);
                        backupReserveSearchForm = new ReserveSearchForm();
                        backupReserveSearchForm.setId(null);
                        backupReserveSearchForm.setMemberId(null);
                        backupReserveSearchForm.setName(null);
                        backupReserveSearchForm.setStartDatetime(dateTime);
                    }
                }
                model.addAttribute("reserveSearchForm", reserveSearchForm);
                model.addAttribute("actionUrl", "/admin/reserve_search");
                try {
                    int totalListCount = reserveInfoService.findOrCount(false, reserveSearchForm);

                    // トータルデータ数と現在のページを設定
                    Pagination pagination = new Pagination(totalListCount, requestParamPage);

                    // DBスタートインデックス
                    int startIndex = pagination.getStartIndex();

                    // ページごとに表示するデータ最大数
                    int pageSize = pagination.getPageSize();

                    // データ取得
                    List<ReserveForm> reserveFormList = reserveInfoService.findListPaging(mode, reserveSearchForm, startIndex, pageSize);

                    model.addAttribute("reserveFormList", reserveFormList);
                    model.addAttribute("pagination", pagination);
                    model.addAttribute("pageLinkUrl", "/admin?p=reserve_list&page=");
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    List<ReserveForm> reserveFormList = new ArrayList<>();
                    model.addAttribute("reserveFormList", reserveFormList);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "01-00-00-00");
                }

                break;
            // 予約変更
            case "reserve_change":
                try {
                    ReserveForm reserveForm = reserveInfoService.findById(id);
                    model.addAttribute("reserveForm", reserveForm);
                    model.addAttribute("actionUrl", "/admin");

                    backupId = reserveForm.getId();
                    backupMemberId = reserveForm.getMemberId();
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    ReserveForm reserveForm = new ReserveForm();
                    model.addAttribute("reserveForm", reserveForm);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "01-00-00-01");
                }

                break;
            // 予約削除
            case "reserve_delete":
                try {
                    ReserveForm reserveForm = reserveInfoService.findById(id);
                    model.addAttribute("reserveForm", reserveForm);

                    reserveInfoService.deleteData(backupId);
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "01-00-00-02");

                    model.addAttribute("contentName", "content/reserve_change");

                    return "admin";
                }

                requestParam = "reserve_delete_ok";

                break;
            // 会員一覧
            case "member_list":
                boolean queryMode = false;
                StudioMemberSearchForm studioMemberSearchForm = new StudioMemberSearchForm();
                try {
                    if ((backupStudioMemberSearchForm.getId() == null || backupStudioMemberSearchForm.getId().equals("")) &&
                            backupStudioMemberSearchForm.getName().equals("")) {
                        queryMode = true;
                    } else {
                        studioMemberSearchForm.setId(backupStudioMemberSearchForm.getId());
                        studioMemberSearchForm.setName(backupStudioMemberSearchForm.getName());
                    }
                } catch (Exception e) {
                    queryMode = true;
                }

                model.addAttribute("studioMemberSearchForm", studioMemberSearchForm);
                model.addAttribute("contentName", "content/member_list");
                try {
                    int totalListCount = studioMemberService.findOrCount(false, studioMemberSearchForm);

                    // トータルデータ数と現在のページを設定
                    Pagination pagination = new Pagination(totalListCount, requestParamPage);

                    // DBスタートインデックス
                    int startIndex = pagination.getStartIndex();

                    // ページごとに表示するデータ最大数
                    int pageSize = pagination.getPageSize();

                    // データ取得
                    List<StudioMemberForm> studioMemberFormList = studioMemberService.findListPaging(queryMode, studioMemberSearchForm, startIndex, pageSize);

                    model.addAttribute("studioMemberFormList", studioMemberFormList);
                    model.addAttribute("pagination", pagination);
                    model.addAttribute("pageLinkUrl", "/admin?p=member_list&page=");
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    List<StudioMemberForm> studioMemberFormList = new ArrayList<>();
                    model.addAttribute("studioMemberFormList", studioMemberFormList);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "01-00-00-03");

                    return "admin";
                }

                break;
            // 会員情報変更
            case "member_change":
                backupMemberId = id;
                try {
                    StudioMemberForm studioMemberForm = studioMemberService.findById(id);
                    model.addAttribute("studioMemberForm", studioMemberForm);
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    StudioMemberForm studioMemberForm = new StudioMemberForm();
                    model.addAttribute("studioMemberForm", studioMemberForm);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "01-00-00-04");

                    model.addAttribute("contentName", "content/member_change");

                    return "admin";
                }

                break;
            // パスワード変更
            case "member_password_change":
                PwChangeForm pwChangeForm = new PwChangeForm();
                pwChangeForm.setId(backupMemberId);
                model.addAttribute("pwChangeForm", pwChangeForm);

                break;
            // 会員情報削除
            case "member_delete":
                try {
                    studioMemberService.deleteData(id);
                } catch (DataAccessException e) {
                    e.printStackTrace();

                    StudioMemberForm studioMemberForm = studioMemberService.findById(id);
                    model.addAttribute("studioMemberForm", studioMemberForm);

                    // エラー表示
                    errorMessageGenerator.getErrorMessage(model, "01-00-00-05");

                    model.addAttribute("contentName", "content/member_change");

                    return "admin";
                }

                requestParam = "member_delete_ok";

                break;
        }

        requestParam = "content/" + requestParam;
        model.addAttribute("contentName", requestParam);

        return "admin";
    }

    /**
     * スタジオ予約内容変更
     *
     * @param reserveForm   予約内容のデータ
     * @param bindingResult 入力された内容
     * @return View
     */
    @PostMapping("/admin")
    public String admin(@Validated @ModelAttribute ReserveForm reserveForm,
                        BindingResult bindingResult,
                        Model model) {
        reserveForm.setId(backupId);
        reserveForm.setMemberId(backupMemberId);

        model.addAttribute("reserveForm", reserveForm);
        model.addAttribute("actionUrl", "/admin");
        model.addAttribute("contentName", "content/reserve_change");

        if (bindingResult.hasErrors()) {
            return "admin";
        }

        // 予約情報の保存
        try {
            reserveInfoService.updateData(reserveForm);
        } catch (DataAccessException e) {
            e.printStackTrace();

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "01-01-00-06");

            return "admin";
        }

        return "redirect:/page?cn=reserve_change_ok";
    }

    /**
     * 会員情報変更
     *
     * @param studioMemberForm 会員情報のデータ
     * @param bindingResult    入力された内容
     * @return View 変更後の画面
     */
    @PostMapping("/admin/member_change")
    public String adminMemberChange(@Validated @ModelAttribute StudioMemberForm studioMemberForm,
                                    BindingResult bindingResult,
                                    Model model) {
        studioMemberForm.setId(backupMemberId);

        model.addAttribute("contentName", "content/member_change");

        if (bindingResult.hasErrors()) {
            if (bindingResult.getErrorCount() == 1 && bindingResult.getFieldError("password") != null) {
                //
            } else {
                return "admin";
            }
        }

        // 会員情報の変更
        try {
            studioMemberService.updateData(studioMemberForm);
        } catch (DataAccessException e) {
            e.printStackTrace();

            studioMemberForm = studioMemberService.findById(backupMemberId);
            model.addAttribute("studioMemberForm", studioMemberForm);

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "01-01-00-07");

            return "admin";
        }

        return "redirect:/page?cn=member_change_ok";
    }

    /**
     * パスワード変更
     *
     * @param pwChangeForm  新しいパスワード
     * @param bindingResult 入力された内容
     * @return View 変更後の画面
     */
    @PostMapping("/admin/pw_change")
    public String adminPasswordChange(@Validated @ModelAttribute PwChangeForm pwChangeForm,
                                      BindingResult bindingResult,
                                      Model model) {
        pwChangeForm.setId(backupMemberId);
        model.addAttribute("pwChangeForm", pwChangeForm);
        model.addAttribute("contentName", "content/member_password_change");
        if (bindingResult.hasErrors()) {
            return "admin";
        }

        // パスワードチェック & 変更
        try {
            if (!studioMemberService.passwordChangeData(pwChangeForm)) {
                // バリデーションエラー表示
                String localizeMessage = messageSource.getMessage("i18nfields.memberCurrentPasswordNotMatch", null, Locale.getDefault());
                FieldError fieldError = new FieldError(bindingResult.getObjectName(),
                        "currentPassword", localizeMessage);
                bindingResult.addError(fieldError);

                return "admin";
            }
        } catch (DataAccessException e) {
            e.printStackTrace();

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "01-01-00-08");

            return "admin";
        }

        return "redirect:/page?cn=member_password_change_ok";
    }

    /**
     * 会員情報検索フォームクリア
     *
     * @return 会員一覧画面へリダイレクト
     */
    @GetMapping("/admin/member_search_clear")
    public String adminMemberSearchInit() {
        backupStudioMemberSearchForm.setId(null);
        backupStudioMemberSearchForm.setName(null);

        return "redirect:/admin?p=member_list";
    }

    /**
     * 会員情報検索
     *
     * @param studioMemberSearchForm 検索する会員情報
     * @param model                  表示するデータの格納先
     * @return StudioMemberFormList 検索結果
     */
    @PostMapping("/admin/member_search")
    public String adminMemberSearch(@ModelAttribute StudioMemberSearchForm studioMemberSearchForm,
                                    @RequestParam(value = "page", defaultValue = "1") int requestParamPage,
                                    Model model) {
        backupStudioMemberSearchForm = new StudioMemberSearchForm();
        backupStudioMemberSearchForm.setId(studioMemberSearchForm.getId());
        backupStudioMemberSearchForm.setName(studioMemberSearchForm.getName());

        Pagination pagination = null;
        List<StudioMemberForm> studioMemberFormList = new ArrayList<>();
        try {
            int totalListCount = studioMemberService.findOrCount(false, studioMemberSearchForm);

            // トータルデータ数と現在のページを設定
            pagination = new Pagination(totalListCount, requestParamPage);

            // DBスタートインデックス
            int startIndex = pagination.getStartIndex();

            // ページごとに表示するデータ最大数
            int pageSize = pagination.getPageSize();

            boolean mode = false;
            if ((studioMemberSearchForm.getId() == null || studioMemberSearchForm.getId().equals("")) &&
                    studioMemberSearchForm.getName().equals("")) {
                mode = true;
            }

            // データ取得
            if (totalListCount > 0) {
                studioMemberFormList = studioMemberService.findListPaging(mode, studioMemberSearchForm, startIndex, pageSize);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "01-01-00-09");
        }

        model.addAttribute("studioMemberFormList", studioMemberFormList);
        model.addAttribute("pagination", pagination);
        model.addAttribute("pageLinkUrl", "/admin?p=member_list&page=");
        model.addAttribute("contentName", "content/member_list");

        return "admin";
    }

    /**
     * スタジオ予約検索フォームクリア
     *
     * @return 予約一覧画面へリダイレクト
     */
    @GetMapping("/admin/reserve_search_clear")
    public String adminReserveSearchInit() {
        isClear = true;

        backupReserveSearchForm.setId(null);
        backupReserveSearchForm.setName(null);
        backupReserveSearchForm.setMemberId(null);
        backupReserveSearchForm.setStartDatetime(null);

        return "redirect:/admin?p=reserve_list";
    }

    /**
     * スタジオ予約情報検索
     *
     * @param reserveSearchForm 検索するスタジオ予約情報
     * @param model             表示するデータの格納先
     * @return ReserveFormList 検索結果
     */
    @PostMapping("/admin/reserve_search")
    public String adminReserveSearch(@ModelAttribute ReserveSearchForm reserveSearchForm,
                                     @RequestParam(value = "page", defaultValue = "1") int requestParamPage,
                                     Model model) {
        backupReserveSearchForm = new ReserveSearchForm();
        backupReserveSearchForm.setId(reserveSearchForm.getId());
        backupReserveSearchForm.setMemberId(reserveSearchForm.getMemberId());
        backupReserveSearchForm.setName(reserveSearchForm.getName());
        backupReserveSearchForm.setStartDatetime(reserveSearchForm.getStartDatetime());
        Pagination pagination = null;
        List<ReserveForm> reserveFormList = new ArrayList<>();
        try {
            int totalListCount = reserveInfoService.findOrCount(false, reserveSearchForm);

            // トータルデータ数と現在のページを設定
            pagination = new Pagination(totalListCount, requestParamPage);

            // DBスタートインデックス
            int startIndex = pagination.getStartIndex();

            // ページごとに表示するデータ最大数
            int pageSize = pagination.getPageSize();

            boolean mode = false;
            if ((reserveSearchForm.getId() == null || reserveSearchForm.getId().equals("")) &&
                    (reserveSearchForm.getMemberId() == null || reserveSearchForm.getMemberId().equals("")) &&
                    reserveSearchForm.getName().equals("") &&
                    (reserveSearchForm.getStartDatetime() == null || reserveSearchForm.getStartDatetime().equals(""))) {
                mode = true;

                reserveSearchForm.setId(null);
                reserveSearchForm.setMemberId(null);
                reserveSearchForm.setName(null);
                reserveSearchForm.setStartDatetime(null);
                backupReserveSearchForm.setId(null);
                backupReserveSearchForm.setMemberId(null);
                backupReserveSearchForm.setName(null);
                backupReserveSearchForm.setStartDatetime(null);
            }

            // データ取得
            if (totalListCount > 0) {
                reserveFormList = reserveInfoService.findListPaging(mode, reserveSearchForm, startIndex, pageSize);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();

            // エラー表示
            errorMessageGenerator.getErrorMessage(model, "01-01-00-10");
        }

        model.addAttribute("reserveFormList", reserveFormList);
        model.addAttribute("pagination", pagination);
        model.addAttribute("pageLinkUrl", "/admin?p=reserve_list&page=");
        model.addAttribute("contentName", "content/reserve_list");
        model.addAttribute("actionUrl", "/admin/reserve_search");

        return "admin";
    }
}
