package com.estore.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer pages;
    
    public static <T> PageResult<T> of(List<T> records, Long total, Integer pageNum, Integer pageSize) {
        PageResult<T> result = new PageResult<>();
        result.records = records;
        result.total = total;
        result.pageNum = pageNum;
        result.pageSize = pageSize;
        result.pages = (int) Math.ceil((double) total / pageSize);
        return result;
    }
}
