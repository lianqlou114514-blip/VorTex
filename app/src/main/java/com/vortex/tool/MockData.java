package com.vortex.tool;

import com.vortex.tool.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Resource> getResources() {
        List<Resource> resources = new ArrayList<>();
        resources.add(new Resource("r1", "高清材质包", "包含光影、水面、植被等多种类型", "材质", "v2.1", "128MB", 3420, -1, ""));
        resources.add(new Resource("r2", "存档备份工具", "一键备份和恢复游戏存档", "工具", "v1.0", "2MB", 1205, 100, ""));
        resources.add(new Resource("r3", "帧率优化模组", "提升游戏帧率，降低卡顿", "模组", "v3.5", "15MB", 5680, -1, ""));
        return resources;
    }

    public static List<String> getTopics() {
        List<String> topics = new ArrayList<>();
        topics.add("全部");
        topics.add("综合");
        topics.add("技术");
        topics.add("反馈");
        topics.add("公告");
        return topics;
    }
}
