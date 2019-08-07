package com.whoops.blog.vo;

import com.whoops.blog.pojo.Catalog;

import java.io.Serializable;

public class CatalogVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private Catalog catalog;

    public CatalogVo() {
    }

    public CatalogVo(String username, Catalog catalog) {
        this.username = username;
        this.catalog = catalog;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }
}
