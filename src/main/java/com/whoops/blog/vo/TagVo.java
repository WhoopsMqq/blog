package com.whoops.blog.vo;

import java.io.Serializable;

public class TagVo implements Serializable {

    private static final long serialVersionUID = 1l;

    private String name;
    private Long count;

    protected TagVo(){}

    public TagVo(String name, Long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
