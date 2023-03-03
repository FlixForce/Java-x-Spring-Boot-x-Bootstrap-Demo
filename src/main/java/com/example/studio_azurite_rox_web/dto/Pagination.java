package com.example.studio_azurite_rox_web.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Pagination {
    // ページごとに表示するデータ数
    private int pageSize = 5;

    // ページングしたブロック数
    private int blockSize = 5;

    // 現在のページ
    private int page = 1;

    // 現在のブロック
    private double block = 1.0;

    // 総データ数
    private int totalListCount;

    // 総ページ数
    private int totalPageCount;

    // 総ブロック数
    private int totalBlockCount;

    // ブロックスタートページ
    private int startPage = 1;

    // ブロックエンドページ
    private int endPage = 1;

    // DBスタートインデックス
    private int startIndex = 0;

    // 前のブロックの最後のページ
    private int previousBlock;

    // 次のブロックの最後のページ
    private int nextBlock;

    /**
     * ページネーション
     * @param totalListCount 総データ数
     * @param page 現在のページ
     */
    public Pagination(int totalListCount, int page) {
        // 現在のページを設定
        setPage(page);

        // 総データ数を設定
        setTotalListCount(totalListCount);

        // 総ページ数を計算
        setTotalPageCount((int)Math.ceil(totalListCount * 1.0 / pageSize));
        if (totalListCount == 0) {
            setTotalPageCount(1);
        }

        // 総ブロック数を計算
        setTotalBlockCount((int)Math.ceil(totalPageCount * 1.0 / blockSize));

        // 現在のブロックを計算
        setBlock(Math.ceil(page * 1.0) / blockSize);

        // ブロックスタートページ(※1ページに表示するブロック数の上限)
        setStartPage(page - (page - 1) % pageSize);

        // ブロックエンドページ
        int totalPage = (totalListCount + Integer.valueOf(blockSize) - 1);
        setEndPage((startPage + pageSize - 1 > totalPage) ? totalPage : startPage + pageSize - 1);

        // ブロックエンドページ処理
        if (endPage > totalPageCount) {
            this.endPage = totalPageCount;
        } else if (totalListCount == 0) {
            this.endPage = 1;
        }

        // 前のブロックのエンドページ
        setPreviousBlock((int)(block * blockSize) - 1);

        // 1ページ以下の時の処理
        if (previousBlock < 1) {
            this.previousBlock = 1;
        }

        // 次のブロックのエンドページ
        setNextBlock((int)(block * blockSize) + 1);

        // 最大ページ数を超えた時の処理
        if (nextBlock > totalPageCount) {
            nextBlock = totalPageCount;
        } else if (totalListCount == 0) {
            nextBlock = 1;
        }

        // DBスタートインデックス
        setStartIndex((page - 1) * pageSize);

/*
        log.info("\npage = " + page +
                "\nTotal Page Count = " + getTotalPageCount() +
                "\nTotal Block Count = " + getTotalBlockCount() +
                "\nBlock = " + getBlock() +
                "\nStart Index = " + getStartIndex() +
                "\nStart Page = " + getStartPage() +
                "\nEnd Page = " + getEndPage() +
                "\nPrevious Block = " + getPreviousBlock() +
                "\nNext Block = " + getNextBlock());
*/
    }
}
