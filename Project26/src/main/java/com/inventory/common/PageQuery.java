package com.inventory.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {

    private Long current = 1L;
    private Long size = 10L;
    private String keyword;
    private String orderBy;
    private Boolean isAsc = true;
}
