package com.homemaking.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;
}
