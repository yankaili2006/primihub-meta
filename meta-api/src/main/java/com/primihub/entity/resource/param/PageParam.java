package com.primihub.entity.resource.param;

import lombok.Data;

@Data
public class PageParam {
    private Integer pageNo = 1;
    private Integer pageSize = 5;
    private Integer offset;

    public Integer getOffset() {
        int pn = pageNo != null ? pageNo : 1;
        int ps = pageSize != null ? pageSize : 5;
        return (pn - 1) * ps;
    }
}
