package com.example.studio_azurite_rox_web.repository;

import com.example.studio_azurite_rox_web.dto.ReserveForm;
import com.example.studio_azurite_rox_web.dto.ReserveSearchForm;
import com.example.studio_azurite_rox_web.entity.LoginUserDetails;
import com.example.studio_azurite_rox_web.entity.ReserveInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ReserveInfoRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * スタジオ予約情報の登録
     *
     * @param reserveForm 登録する予約情報
     * @return boolean 処理の正常終了判定
     */
    public boolean insert(LoginUserDetails loginUserDetails, ReserveForm reserveForm) throws DataAccessException {
        reserveForm.setMemberId(loginUserDetails.getId());
        ReserveInfo reserveInfo = copyReserveData(reserveForm);

        String sql = "INSERT INTO reserve_info(member_id, " +
                "studio_type, start_datetime, end_datetime, " +
                "engineer, mastering, " +
                "note) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                reserveInfo.getMemberId(),
                reserveInfo.getStudioType(),
                reserveInfo.getStartDatetime(),
                reserveInfo.getEndDatetime(),
                reserveInfo.getEngineer(),
                reserveInfo.getMastering(),
                reserveInfo.getNote());

        return true;
    }

    /**
     * スタジオ予約情報の変更
     *
     * @param reserveForm 変更する予約情報データ
     * @return boolean 処理の正常終了判定
     */
    public boolean change(ReserveForm reserveForm) throws DataAccessException {
        ReserveInfo reserveInfo = copyReserveData(reserveForm);

        String sql = "UPDATE reserve_info SET member_id = ?, studio_type = ?," +
                "start_datetime = ?, end_datetime = ?, " +
                "engineer = ?, mastering = ?, note = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                reserveInfo.getMemberId(),
                reserveInfo.getStudioType(),
                reserveInfo.getStartDatetime(),
                reserveInfo.getEndDatetime(),
                reserveInfo.getEngineer(),
                reserveInfo.getMastering(),
                reserveInfo.getNote(),
                reserveForm.getId());

        return true;
    }

    /**
     * スタジオ予約情報の削除
     *
     * @param id 削除する予約ID
     * @return boolean 処理の正常終了判定
     */
    public boolean delete(Integer id) throws DataAccessException {
        String sql = "DELETE FROM reserve_info WHERE id = ?";

        jdbcTemplate.update(sql, id);

        return true;
    }

    /**
     * スタジオ予約情報一覧(ページング)
     *
     * @param reserveSearchForm 検索条件
     * @param startIndex        DBスタートインデックス
     * @param pageSize          ページごとに表示するデータ数
     * @return スタジオ予約情報
     * @throws DataAccessException
     * @Param mode true = 条件により検索, false = 条件なし検索
     */
    public List<ReserveForm> findListPaging(boolean mode, ReserveSearchForm reserveSearchForm, int startIndex, int pageSize) throws DataAccessException {
        String sql;
        List<Map<String, Object>> resultList;
        boolean isStartDate = false;
        Timestamp timestamp = null;
        if (mode) {
            if (reserveSearchForm.getStartDatetime() != null) {
                isStartDate = true;
                // TIMESTAMP形式に変換する
                String tempString = reserveSearchForm.getStartDatetime()
                        .replaceAll("T", " ")
                        .concat(":00");
                timestamp = Timestamp.valueOf(tempString);
            }

            // 管理者
            if (reserveSearchForm.getMemberId() == null) {
                // 名前, 予約情報
/*
                sql = "SELECT LU.name, RI.* " +
                        "FROM reserve_info RI " +
                        "LEFT JOIN login_user LU " +
                        "ON RI.member_id = LU.id " +
                        "WHERE ? <= RI.start_datetime " +
                        "ORDER BY RI.start_datetime ASC";
                resultList = jdbcTemplate.queryForList(sql, timestamp);
*/

                String startDateSql = "WHERE ? <= RI.start_datetime";
                String sortSql = " ORDER BY RI.start_datetime ASC";

                sql = "SELECT LU.name, RI.* " +
                        "FROM reserve_info RI " +
                        "LEFT JOIN login_user LU " +
                        "ON RI.member_id = LU.id ";
                if (isStartDate) {
                    sql = sql + startDateSql + sortSql;

                    resultList = jdbcTemplate.queryForList(sql, timestamp);
                } else {
                    sql = sql + sortSql;

                    resultList = jdbcTemplate.queryForList(sql);
                }
                // スタジオ会員
            } else {
                sql = "SELECT LU.name, RI.* " +
                        "FROM reserve_info RI " +
                        "LEFT JOIN login_user LU " +
                        "ON RI.member_id = LU.id " +
                        "WHERE RI.member_id = ?  AND ? <= RI.start_datetime " +
                        "ORDER BY RI.start_datetime ASC";
                resultList = jdbcTemplate.queryForList(sql, reserveSearchForm.getMemberId(), timestamp);
            }
        } else {
            resultList = findOrCount(true, reserveSearchForm);
        }

        // startIndex = DBスタートインデックス
        // pageSize = ページごとに表示するデータ数
        // 表示するデータのみ取得する
        List<Map<String, Object>> viewResultList = new ArrayList<>();
        resultList.stream()
                .skip(startIndex)
                .limit(pageSize)
                .forEach(e -> viewResultList.add(e));

        // 表示用のデータを準備する
        List<ReserveForm> reserveFormList = new ArrayList<>();
        for (Map<String, Object> result : viewResultList) {
            ReserveForm reserveForm = new ReserveForm();
            // 管理者
            if (reserveSearchForm.getMemberId() == null) {
                // 名前
                reserveForm.setName(result.get("name").toString());
            }

            // 予約情報
            reserveForm.setId((Integer) result.get("id"));
            reserveForm.setMemberId((Integer) result.get("member_id"));
            reserveForm.setName(result.get("name").toString());
            reserveForm.setStudioType((String) result.get("studio_type"));
            // "秒"部分を削除
            reserveForm.setStartDatetime(result.get("start_datetime").toString());
            reserveForm.setStartDatetime(reserveForm.getStartDatetime().substring(0, 16));
            // "秒"部分を削除
            reserveForm.setEndDatetime(result.get("end_datetime").toString());
            reserveForm.setEndDatetime(reserveForm.getEndDatetime().substring(0, 16));
            reserveForm.setEngineer((Boolean) result.get("engineer"));
            reserveForm.setMastering((Boolean) result.get("mastering"));
            reserveForm.setNote(result.get("note").toString());
            reserveFormList.add(reserveForm);
        }

        return reserveFormList;
    }

    /**
     * スタジオ予約情報一覧
     *
     * @param memberId スタジオ会員ID(null = すべてのスタジオ会員の予約情報取得)
     * @return List<ReserveForm> 予約情報一覧
     */
/*
    public List<ReserveForm> findAll(Integer memberId) throws DataAccessException {
        String sql;
        List<Map<String, Object>> resultList;

        // 管理者
        if (memberId == null) {
            // 名前, 予約情報
            sql = "SELECT LU.name, RI.* " +
                    "FROM reserve_info RI " +
                    "LEFT JOIN login_user LU " +
                    "ON RI.member_id = LU.id " +
                    "ORDER BY RI.start_datetime ASC";

            resultList = jdbcTemplate.queryForList(sql);
            // スタジオ会員
        } else {
            sql = "SELECT * FROM reserve_info WHERE member_id = ? ORDER BY start_datetime ASC ";
            resultList = jdbcTemplate.queryForList(sql, memberId);
        }

        List<ReserveForm> reserveFormList = new ArrayList<>();
        if (resultList != null) {
            for (Map<String, Object> result : resultList) {
                ReserveForm reserveForm = new ReserveForm();
                // 管理者
                if (memberId == null) {
                    // 名前
                    reserveForm.setName(result.get("name").toString());
                }

                // 予約情報
                reserveForm.setId((Integer) result.get("id"));
                reserveForm.setMemberId((Integer) result.get("member_id"));
                reserveForm.setStudioType((String) result.get("studio_type"));
                // "秒"部分を削除
                reserveForm.setStartDatetime(result.get("start_datetime").toString());
                reserveForm.setStartDatetime(reserveForm.getStartDatetime().substring(0, 16));
                // "秒"部分を削除
                reserveForm.setEndDatetime(result.get("end_datetime").toString());
                reserveForm.setEndDatetime(reserveForm.getEndDatetime().substring(0, 16));
                reserveForm.setEngineer((Boolean) result.get("engineer"));
                reserveForm.setMastering((Boolean) result.get("mastering"));
                reserveForm.setNote(result.get("note").toString());
                reserveFormList.add(reserveForm);
            }
        }

        return reserveFormList;
    }
*/

    /**
     * スタジオ予約情報検索(会員別)
     *
     * @param id 予約番号
     * @return 予約情報
     */
    public ReserveForm findById(Integer id) throws DataAccessException {
        String sql = "SELECT LU.name, RI.* " +
                "FROM reserve_info RI " +
                "LEFT JOIN login_user LU " +
                "ON RI.member_id = LU.id " +
                "WHERE RI.id = ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);
        ReserveForm reserveForm = new ReserveForm();
        reserveForm.setId((Integer) result.get("id"));
        reserveForm.setName(result.get("name").toString());
        reserveForm.setMemberId((Integer) result.get("member_id"));
        reserveForm.setStudioType((String) result.get("studio_type"));
        reserveForm.setStartDatetime(result.get("start_datetime").toString());
        reserveForm.setEndDatetime(result.get("end_datetime").toString());
        reserveForm.setEngineer((Boolean) result.get("engineer"));
        reserveForm.setMastering((Boolean) result.get("mastering"));
        reserveForm.setNote(result.get("note").toString());

        return reserveForm;
    }

    /**
     * スタジオ予約情報の検索または総数をDBから取得
     *
     * @param mode              true = 検索, false = 総数カウント
     * @param reserveSearchForm 予約情報検索条件
     * @return 検索結果のデータ
     * @throws DataAccessException
     */
    public List<Map<String, Object>> findOrCount(boolean mode, ReserveSearchForm reserveSearchForm) throws DataAccessException {
        String sql;
        // 総数カウント
        if (!mode) {
            sql = "SELECT COUNT(*) FROM reserve_info RI ";
            // 検索
        } else {
            sql = "SELECT RI.*, SM.name, SM.furigana FROM reserve_info RI ";
        }

        sql = sql + "LEFT JOIN studio_member SM ON SM.id = RI.member_id WHERE";

        String paramReserveId = " RI.id = ?";
        String paramMemberId = " RI.member_id = ?";
        String paramName = " name LIKE CONCAT('%', ?, '%')";
        String paramStartDate = " ? <= RI.start_datetime";
        String paramSort = " ORDER BY SM.furigana, RI.start_datetime ASC";

        // SQLパラメータ格納用リスト
        List<Object> argsList = new ArrayList<>();
        List<Integer> argTypesList = new ArrayList<>();

        String sqlOr = " OR";
        boolean parameterCheck = false;
        // 予約ID
        if (reserveSearchForm.getId() != null && !reserveSearchForm.getId().equals("")) {
            parameterCheck = true;

            argsList.add(reserveSearchForm.getId());
            argTypesList.add(Types.INTEGER);

            sql = sql + paramReserveId;

//            log.info("\n予約ID");
        }

        // 会員ID
        if (reserveSearchForm.getMemberId() != null && !reserveSearchForm.getMemberId().equals("")) {
            parameterCheck = true;

            argsList.add(reserveSearchForm.getMemberId());
            argTypesList.add(Types.INTEGER);

            if (argTypesList.size() > 1) {
                sql = sql + sqlOr;
            }
            sql = sql + paramMemberId;

//            log.info("\n会員ID");
        }

        // 名前
        if (reserveSearchForm.getName() != null && !reserveSearchForm.getName().equals("")) {
            parameterCheck = true;

            argsList.add(reserveSearchForm.getName());
            argTypesList.add(Types.VARCHAR);

            if (argTypesList.size() > 1) {
                sql = sql + sqlOr;
            }
            sql = sql + paramName;

//            log.info("\n名前");
        }

        // 開始日時
        if (reserveSearchForm.getStartDatetime() != null && !reserveSearchForm.getStartDatetime().equals("")) {
            parameterCheck = true;

            // Timestamp 型に変換
            String tempString = reserveSearchForm.getStartDatetime()
                    .replaceAll("T", " ")
                    .concat(":00");
            Timestamp timestamp = Timestamp.valueOf(tempString);
            argsList.add(timestamp);
            argTypesList.add(Types.TIMESTAMP);

            if (argTypesList.size() > 1) {
                sql = sql + " AND";
            }
            sql = sql + paramStartDate;

//            log.info("\n開始日時: " + tempString);
        }

        if (mode) {
            sql = sql + paramSort;
        }

        // SQLパラメータ格納リストをint配列に変換
        Object[] args = argsList.toArray();
        int[] argTypes = argTypesList.stream().mapToInt(Integer::intValue).toArray();
        List<Map<String, Object>> resultList;

//log.info("\n" + sql);

        // 総数カウント
        if (!mode) {
            if (!parameterCheck) {
                sql = sql.replaceAll(" WHERE", "");
            }

            Integer totalCount = jdbcTemplate.queryForObject(sql, args, argTypes, Integer.class);

            resultList = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("Total Count", totalCount);
            resultList.add(resultMap);

//            log.info("\nTotal Count = " + resultList.get(0).get("Total Count"));
            // 検索
        } else {
            resultList = jdbcTemplate.queryForList(sql, args, argTypes);

//            log.info("\n" + resultList);
        }

        return resultList;
    }

    /**
     * スタジオ予約情報の検索
     *
     * @return 会員情報リスト
     * @Param ReserveSearchForm 予約ID, 会員ID, 名前, 予約日時
     */
/*
    public List<ReserveForm> findBySearch(ReserveSearchForm reserveSearchForm) throws DataAccessException {
*/
/*
        String sql = "SELECT RI.*, SM.name, SM.furigana FROM reserve_info RI " +
                "LEFT JOIN studio_member SM ON SM.id = RI.member_id " +
                "WHERE";

        String paramReserveId = " RI.id = ?";
        String paramMemberId = " RI.member_id = ?";
        String paramName = " name LIKE CONCAT('%', ?, '%')";
        String paramStartDate = " ? <= RI.start_datetime";
        String paramSort = " ORDER BY SM.furigana, RI.start_datetime ASC";

        // SQLパラメータ格納用リスト
        List<Object> argsList = new ArrayList<>();
        List<Integer> argTypesList = new ArrayList<>();

        String sqlOr = " OR";
        // 予約ID
        if (reserveSearchForm.getId() != null && !reserveSearchForm.getId().equals("")) {
            argsList.add(reserveSearchForm.getId());
            argTypesList.add(Types.INTEGER);

            sql = sql + paramReserveId;
        }

        // 会員ID
        if (reserveSearchForm.getMemberId() != null && !reserveSearchForm.getMemberId().equals("")) {
            argsList.add(reserveSearchForm.getMemberId());
            argTypesList.add(Types.INTEGER);

            if (argTypesList.size() > 1) {
                sql = sql + sqlOr;
            }
            sql = sql + paramMemberId;

            log.info("\n会員ID");
        }

        // 名前
        if (reserveSearchForm.getName() != null && !reserveSearchForm.getName().equals("")) {
            argsList.add(reserveSearchForm.getName());
            argTypesList.add(Types.VARCHAR);

            if (argTypesList.size() > 1) {
                sql = sql + sqlOr;
            }
            sql = sql + paramName;

            log.info("\n名前");
        }

        // 開始日時
        if (reserveSearchForm.getStartDatetime() != null && !reserveSearchForm.getStartDatetime().equals("")) {
            // Timestamp 型に変換
            String tempString = reserveSearchForm.getStartDatetime()
                    .replaceAll("T", " ")
                    .concat(":00");
            Timestamp timestamp = Timestamp.valueOf(tempString);
            argsList.add(timestamp);
            argTypesList.add(Types.TIMESTAMP);

            if (argTypesList.size() > 1) {
                sql = sql + " AND";
            }
            sql = sql + paramStartDate;

            log.info("\n開始日時");
        }

        sql = sql + paramSort;

        // SQLパラメータ格納リストをint配列に変換
        Object[] args = argsList.toArray();
        int[] argTypes = argTypesList.stream().mapToInt(Integer::intValue).toArray();
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, args, argTypes);
*//*


        List<Map<String, Object>> resultList = findOrCount(true, reserveSearchForm);

        List<ReserveForm> reserveFormList = new ArrayList<>();
        for (Map<String, Object> result : resultList) {
            ReserveForm reserveForm = new ReserveForm();
            // 管理者
            if (reserveSearchForm.getMemberId() == null) {
                // 名前
                reserveForm.setName(result.get("name").toString());
            }

            // 予約情報
            reserveForm.setId((Integer) result.get("id"));
            reserveForm.setMemberId((Integer) result.get("member_id"));
            reserveForm.setStudioType(result.get("studio_type").toString());
            reserveForm.setName(result.get("name").toString());
            // "秒"部分を削除
            reserveForm.setStartDatetime(result.get("start_datetime").toString());
            reserveForm.setStartDatetime(reserveForm.getStartDatetime().substring(0, 16));
            // "秒"部分を削除
            reserveForm.setEndDatetime(result.get("end_datetime").toString());
            reserveForm.setEndDatetime(reserveForm.getEndDatetime().substring(0, 16));
            reserveForm.setEngineer((Boolean) result.get("engineer"));
            reserveForm.setMastering((Boolean) result.get("mastering"));
            reserveForm.setNote(result.get("note").toString());
            reserveFormList.add(reserveForm);
        }

        return reserveFormList;
    }
*/

    /**
     * 予約情報をEntityへ代入
     *
     * @param reserveForm フォームの予約情報
     * @return 予約情報データ
     */
    private ReserveInfo copyReserveData(ReserveForm reserveForm) {
        ReserveInfo reserveInfo = new ReserveInfo();
        reserveInfo.setId(reserveForm.getId());
        reserveInfo.setMemberId(reserveForm.getMemberId());
        reserveInfo.setStudioType(reserveForm.getStudioType());
        reserveInfo.setStartDatetime(Timestamp.valueOf(
                reserveForm.getStartDatetime().replaceAll("T", " ").replaceAll("/", "-").concat(":00")));
        reserveInfo.setEndDatetime(Timestamp.valueOf(
                reserveForm.getEndDatetime().replaceAll("T", " ").replaceAll("/", "-").concat(":00")));
        reserveInfo.setEngineer(reserveForm.getEngineer());
        reserveInfo.setMastering(reserveForm.getMastering());
        reserveInfo.setNote(reserveForm.getNote());

        return reserveInfo;
    }
}
