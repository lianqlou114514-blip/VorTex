package com.vortex.tool.model;

public class Resource {
    public String id;
    public String title;
    public String description;
    public String category;
    public String version;
    public String size;
    public int downloadCount;
    public int progress; // 0-100, -1=未开始, 100=已完成
    public String downloadUrl;

    public Resource(String id, String title, String description, String category,
                    String version, String size, int downloadCount, int progress,
                    String downloadUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.version = version;
        this.size = size;
        this.downloadCount = downloadCount;
        this.progress = progress;
        this.downloadUrl = downloadUrl;
    }
}
