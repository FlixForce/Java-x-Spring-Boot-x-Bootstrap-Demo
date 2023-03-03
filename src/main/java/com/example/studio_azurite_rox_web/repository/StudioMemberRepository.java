package com.example.studio_azurite_rox_web.repository;

import com.example.studio_azurite_rox_web.dto.*;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.entity.StudioMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class StudioMemberRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * スタジオ会員情報の登録
     *
     * @param studioMemberForm 登録する会員情報
     * @return boolean 処理の正常終了判定
     */
    public boolean insert(StudioMemberForm studioMemberForm) throws DataAccessException {
        StudioMember studioMember = copyStudioMemberData(studioMemberForm);

        // login_user
        // ※パスワード暗号化
        String sql = "INSERT INTO login_user(" +
                "id, name, email, password) " +
                "VALUES(?, ?, ?, ?)";

        log.info("\nInsert 0");

        jdbcTemplate.update(sql,
                studioMember.getId(),
                studioMember.getName(),
                studioMember.getEmail(),
                passwordEncoder.encode(studioMemberForm.getPassword()));

log.info("\nInsert 1");

        // user_role
        Integer roleId = 2;
        // 管理者
        if (studioMemberForm.getAdministrator()) {
            roleId = 1;
        }
        sql = "INSERT INTO user_role(" +
                "user_id, role_id) " +
                "VALUES(?, ?)";

        log.info("\nInsert 2");

        jdbcTemplate.update(sql, studioMember.getId(), roleId);

        log.info("\nInsert 3");

        // studio_member
        sql = "INSERT INTO studio_member(" +
                "id, name, furigana, address, phone, email, " +
                "artist_name, member_count, note, " +
                "registration_date) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                studioMember.getId(),
                studioMember.getName(),
                studioMember.getFurigana(),
                studioMember.getAddress(),
                studioMember.getPhone(),
                studioMember.getEmail(),
                studioMember.getArtistName(),
                studioMember.getMemberCount(),
                studioMember.getNote(),
                studioMember.getRegistrationDate());

        log.info("\nInsert 4");

        return true;
    }

    /**
     * スタジオ会員情報の変更
     *
     * @param studioMemberForm 変更する会員情報データ
     * @return boolean 処理の正常終了判定
     */
    public boolean update(StudioMemberForm studioMemberForm) throws DataAccessException {
        // login_user
        // ※パスワード暗号化
        String sql = "UPDATE login_user SET name = ?, email = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                studioMemberForm.getName(),
                studioMemberForm.getEmail(),
                studioMemberForm.getId());

//        log.info("\n" + studioMemberForm);

        // user_role
        // 管理者にチェックが入っている時は、変更する
        int updateRoleId = 2;
        if (studioMemberForm.getAdministrator()) {
            updateRoleId = 1;
        }
        sql = "UPDATE user_role SET role_id = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, updateRoleId, studioMemberForm.getId());

        // studio_member
        sql = "UPDATE studio_member SET name = ?, furigana = ?, address = ?, " +
                "phone = ?, email = ?, artist_name = ?, member_count = ?," +
                "note = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                studioMemberForm.getName(),
                studioMemberForm.getFurigana(),
                studioMemberForm.getAddress(),
                studioMemberForm.getPhone(),
                studioMemberForm.getEmail(),
                studioMemberForm.getArtistName(),
                studioMemberForm.getMemberCount(),
                studioMemberForm.getNote(),
                studioMemberForm.getId());

        return true;
    }

    /**
     * パスワード変更
     *
     * @param pwChangeForm 入力されたパスワード
     * @return
     */
    public boolean passwordChange(PwChangeForm pwChangeForm) throws DataAccessException {
        // 現在のパスワードを DB から取得する
        String sql = "SELECT password FROM login_user WHERE id = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, pwChangeForm.getId());

        // 一致しない
        if (!passwordEncoder.matches(pwChangeForm.getCurrentPassword(), result.get("password").toString())) {
            log.info("\nPW is Not Match!");

            return false;
        }

        // 新しいパスワードのハッシュ化と DB への保存
        String hashPW = passwordEncoder.encode(pwChangeForm.getNewPassword());
        sql = "UPDATE login_user SET password = ? WHERE id = ?";
        jdbcTemplate.update(sql, hashPW, pwChangeForm.getId());

        return true;
    }

    /**
     * スタジオ会員情報の削除
     *
     * @param id 削除する会員ID
     * @return boolean 処理の正常終了判定
     */
    public boolean delete(Integer id) throws DataAccessException {
        // reserve_info 削除
        String sql = "DELETE FROM reserve_info WHERE member_id = ?";
        jdbcTemplate.update(sql, id);

        // studio_member 削除
        sql = "DELETE FROM studio_member WHERE id = ?";
        jdbcTemplate.update(sql, id);

        // user_role 削除
        sql = "DELETE FROM user_role WHERE user_id = ?";
        jdbcTemplate.update(sql, id);

        // login_user 削除
        sql = "DELETE FROM login_user WHERE id = ?";
        jdbcTemplate.update(sql, id);

        return true;
    }

    /**
     * スタジオ会員情報一覧
     *
     * @return 会員情報一覧
     */
/*
    public List<StudioMemberForm> findAll() throws DataAccessException {
        String sql = "SELECT * FROM studio_member ORDER BY furigana ASC";
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

        List<StudioMemberForm> studioMemberFormList = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            StudioMemberForm studioMemberForm = new StudioMemberForm();
            studioMemberForm.setId((Integer) result.get("id"));
            studioMemberForm.setName(result.get("name").toString());
            studioMemberForm.setFurigana(result.get("furigana").toString());
            studioMemberForm.setAddress(result.get("address").toString());
            studioMemberForm.setPhone(result.get("phone").toString());
            studioMemberForm.setEmail(result.get("email").toString());
            studioMemberForm.setArtistName(result.get("artist_name").toString());
            studioMemberForm.setMemberCount(Short.parseShort(result.get("member_count").toString()));
            studioMemberForm.setNote(result.get("note").toString());
            // "秒"部分を削除
            studioMemberForm.setRegistrationDate(result.get("registration_date").toString());
            studioMemberForm.setRegistrationDate(studioMemberForm.getRegistrationDate().substring(0, 16));
            studioMemberFormList.add(studioMemberForm);
        }

        return studioMemberFormList;
    }
*/

    /**
     * スタジオ会員情報の検索
     *
     * @param id 会員ID
     * @return 会員情報
     */
    public StudioMemberForm findById(Integer id) throws DataAccessException {
        String sql = "SELECT SM.*, LU.password, UR.role_id FROM studio_member SM " +
                "LEFT JOIN login_user LU ON SM.id = LU.id " +
                "LEFT JOIN user_role UR ON SM.id = UR.user_id " +
                "WHERE SM.id = ? ORDER BY furigana ASC";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        StudioMemberForm studioMemberForm = new StudioMemberForm();
        studioMemberForm.setId((Integer) result.get("id"));
        studioMemberForm.setName(result.get("name").toString());
        studioMemberForm.setFurigana(result.get("furigana").toString());
        studioMemberForm.setPassword(result.get("password").toString());
        studioMemberForm.setAddress(result.get("address").toString());
        studioMemberForm.setPhone(result.get("phone").toString());
        studioMemberForm.setEmail(result.get("email").toString());
        studioMemberForm.setArtistName(result.get("artist_name").toString());
        studioMemberForm.setMemberCount(Short.parseShort(result.get("member_count").toString()));
        studioMemberForm.setNote(result.get("note").toString());
        if ((Integer)result.get("role_id") == 1) {
            studioMemberForm.setAdministrator(true);
        }
        // "秒"部分を削除
        studioMemberForm.setRegistrationDate(result.get("registration_date").toString());
        studioMemberForm.setRegistrationDate(studioMemberForm.getRegistrationDate().substring(0, 16));

//        log.info("\n" + studioMemberForm);

        return studioMemberForm;
    }

    /**
     * スタジオ会員情報の検索
     *
     * @return 会員情報リスト
     * @Param StudioNenverSearchForm 会員ID, 名前
     */
/*
    public List<StudioMemberForm> findByIdAndName(StudioMemberSearchForm studioMemberSearchForm) throws DataAccessException {
        String paramId = "SM.id = ?";
        String paramName = "SM.name LIKE CONCAT('%', ?, '%')";
        String paramSort = " ORDER BY furigana ASC";

        String sql = "SELECT SM.*, LU.password FROM studio_member SM " +
                "LEFT JOIN login_user LU ON SM.id = LU.id " +
                "WHERE ";
//                "SM.id = ? OR SM.name LIKE CONCAT('%', ?, '%') ORDER BY furigana ASC";

        List<Map<String, Object>> resultList = null;
        // 会員ID + 名前
        if (studioMemberSearchForm.getId() != null && !studioMemberSearchForm.getId().equals("") &&
                studioMemberSearchForm.getName() != null && !studioMemberSearchForm.getName().equals("")) {
            sql = sql + paramId + " OR " + paramName + paramSort;
            resultList = jdbcTemplate.queryForList(sql, studioMemberSearchForm.getId(), studioMemberSearchForm.getName());
        // 会員IDのみ
        } else if (studioMemberSearchForm.getId() != null && !studioMemberSearchForm.getId().equals("")) {
            sql = sql + paramId + paramSort;
            resultList = jdbcTemplate.queryForList(sql, studioMemberSearchForm.getId());
        // 名前のみ
        } else if (studioMemberSearchForm.getName() != null && !studioMemberSearchForm.getName().equals("")) {
            sql = sql + paramName + paramSort;
            resultList = jdbcTemplate.queryForList(sql, studioMemberSearchForm.getName());
        }

//        List<Map<String, Object>> resultList = findOrCount(true, studioMemberSearchForm);

        List<StudioMemberForm> studioMemberFormList = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            StudioMemberForm studioMemberForm = new StudioMemberForm();
            studioMemberForm.setId((Integer) result.get("id"));
            studioMemberForm.setName(result.get("name").toString());
            studioMemberForm.setFurigana(result.get("furigana").toString());
            studioMemberForm.setAddress(result.get("address").toString());
            studioMemberForm.setPhone(result.get("phone").toString());
            studioMemberForm.setEmail(result.get("email").toString());
            studioMemberForm.setArtistName(result.get("artist_name").toString());
            studioMemberForm.setRegistrationDate(result.get("registration_date").toString());
            studioMemberFormList.add(studioMemberForm);
        }

        return studioMemberFormList;
    }
*/

    /**
     * スタジオ会員情報一覧(ページング)
     *
     * @param studioMemberSearchForm 検索条件
     * @param startIndex　DBスタートインデックス
     * @param pageSize ページごとに表示するデータ数
     * @return スタジオ会員情報
     * @throws DataAccessException
     */
    public List<StudioMemberForm> findListPaging(boolean mode, StudioMemberSearchForm studioMemberSearchForm, int startIndex, int pageSize) throws DataAccessException {
        List<Map<String, Object>> resultList;
        if (mode) {
            String sql = "SELECT * FROM studio_member ORDER BY furigana ASC";

            resultList = jdbcTemplate.queryForList(sql);
        } else {
            resultList = findOrCount(true, studioMemberSearchForm);
        }

        // startIndex = DBスタートインデックス
        // pageSize = ページごとに表示するデータ数
        // 表示するデータのみ取得する
        List<Map<String, Object>> viewResultList = new ArrayList<>();
        resultList.stream()
                .skip(startIndex)
                .limit(pageSize)
                .forEach(e -> viewResultList.add(e));

        String tempRegistrationDate;
        List<StudioMemberForm> studioMemberFormList = new ArrayList<>();
        for (Map<String, Object> result : viewResultList) {
            StudioMemberForm studioMemberForm = new StudioMemberForm();
            studioMemberForm.setId((Integer) result.get("id"));
            studioMemberForm.setName(result.get("name").toString());
            studioMemberForm.setFurigana(result.get("furigana").toString());
            studioMemberForm.setAddress(result.get("address").toString());
            studioMemberForm.setPhone(result.get("phone").toString());
            studioMemberForm.setEmail(result.get("email").toString());
            studioMemberForm.setArtistName(result.get("artist_name").toString());
            tempRegistrationDate = result.get("registration_date").toString();
            tempRegistrationDate = tempRegistrationDate.substring(0, tempRegistrationDate.indexOf('.'));
            studioMemberForm.setRegistrationDate(tempRegistrationDate);

            studioMemberFormList.add(studioMemberForm);
        }

        return studioMemberFormList;
    }

    /**
     * スタジオ会員情報の検索または総数をDBから取得
     * @param mode true = 検索, false = 総数カウント
     * @param studioMemberSearchForm 会員情報検索条件
     * @return 検索結果のデータ
     * @throws DataAccessException
     */
    public List<Map<String, Object>> findOrCount(boolean mode, StudioMemberSearchForm studioMemberSearchForm) throws DataAccessException {
        String sql;
        // 総数カウント
        if (!mode) {
            sql = "SELECT COUNT(*) FROM studio_member SM ";
        // 検索
        } else {
            sql = "SELECT SM.*, LU.password FROM studio_member SM ";
        }

        sql = sql + "LEFT JOIN login_user LU ON SM.id = LU.id WHERE ";

        String paramId = " SM.id = ?";
        String paramName = " SM.name LIKE CONCAT('%', ?, '%')";
        String paramSort = " ORDER BY furigana ASC";

        // SQLパラメータ格納用リスト
        List<Object> argsList = new ArrayList<>();
        List<Integer> argTypesList = new ArrayList<>();

        boolean parameterCheck = false;
        String sqlOr = " OR";

        // 会員ID
        if (studioMemberSearchForm.getId() != null && !studioMemberSearchForm.getId().equals("")) {
            parameterCheck = true;

            // 会員ID
            argsList.add(studioMemberSearchForm.getId());
            argTypesList.add(Types.INTEGER);

            sql = sql + paramId;
        }

        // 名前
        if (studioMemberSearchForm.getName() != null && !studioMemberSearchForm.getName().equals("")) {
            parameterCheck = true;

            // 名前
            argsList.add(studioMemberSearchForm.getName());
            argTypesList.add(Types.VARCHAR);

            if (argTypesList.size() > 1) {
                sql = sql + sqlOr;
            }
            sql = sql + paramName;
        }

        if (mode) {
            sql = sql + paramSort;
        }

        // SQLパラメータ格納リストをint配列に変換
        Object[] args = argsList.toArray();
        int[] argTypes = argTypesList.stream().mapToInt(Integer::intValue).toArray();
        List<Map<String, Object>> resultList;

        if (!parameterCheck) {
            sql = sql.replaceAll(" WHERE", "");
        }

        // 総数カウント
        if (!mode) {
            Integer totalCount = jdbcTemplate.queryForObject(sql, args, argTypes, Integer.class);

            resultList = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("Total Count", totalCount);
            resultList.add(resultMap);

//            log.info("\nTotal Count = " + resultList.get(0).get("Total Count"));
        } else {
            resultList = jdbcTemplate.queryForList(sql, args, argTypes);

//            log.info("\n" + resultList);
        }

        return resultList;
    }

    /**
     * 会員情報をEntityへ代入
     *
     * @param studioMemberForm フォームの会員情報
     * @return 会員情報データ
     */
    private StudioMember copyStudioMemberData(StudioMemberForm studioMemberForm) throws DataAccessException {
        // IDの総数を取得する
        int count = jdbcTemplate.queryForObject("SELECT MAX(id) FROM studio_member", Integer.class);

        StudioMember studioMember = new StudioMember();
        studioMember.setId(++count);
        studioMember.setName(studioMemberForm.getName());
        studioMember.setFurigana(studioMemberForm.getFurigana());
        studioMember.setAddress(studioMemberForm.getAddress());
        studioMember.setPhone(studioMemberForm.getPhone());
        studioMember.setEmail(studioMemberForm.getEmail());
        studioMember.setArtistName(studioMemberForm.getArtistName());
        studioMember.setMemberCount(studioMemberForm.getMemberCount());
        studioMember.setNote(studioMemberForm.getNote());
        studioMember.setRegistrationDate(new Timestamp(System.currentTimeMillis()));

/*
        log.info("\n" + studioMember);
*/

        return studioMember;
    }
}
