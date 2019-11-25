package com.xitu.app.model;

import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;

@TableName("xitu_xiangtu")
public class Xiangtu {

    public Xiangtu() {

    }

    @IdFlag
    private Integer id;
    private String unitse;
    private String arease;
    private String path;
    private String name;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnitse() {
        return unitse;
    }

    public void setUnitse(String unitse) {
        this.unitse = unitse;
    }

    public String getArease() {
        return arease;
    }

    public void setArease(String arease) {
        this.arease = arease;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}
