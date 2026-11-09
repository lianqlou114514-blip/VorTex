package com.vortex.tool;

import java.util.Map;
import java.util.function.IntFunction;

/**
 * DSL 配置：面板文本定义 + 回调映射
 * 单独文件，方便修改面板内容
 */
public class DslConfig {


    // ==================== 快捷键配置 ====================
    // {标签, 回调名, 类型}  类型: toggle=开关 button=按钮
    public static final String[][] SHORTCUTS = {
        {"杀戮光环", "killAura", "toggle"},
        {"无敌模式", "godMode", "toggle"},
        {"自动回血", "autoHeal", "toggle"},
        {"防闪退", "antiCrash", "toggle"},
        {"全部应用", "applyAll", "button"},
        {"清理缓存", "clearCache", "button"},
        {"查看物品ID", "showItemId", "button"},
    };

    public static final String DSL_TEXT = String.join("\n",
                                        "分页 战斗 {",
        "    图标 = ic_battle",
        "    标签 近战配置 {",
        "        卡片 光环 {",
        "            说明 高风险功能，谨慎开启",
        "            分隔线",
        "            开关 杀戮光环 { 默认 = 关  回调 = killAura  颜色 = #F44336  提示 = 自动攻击范围内敌人 }",
        "            开关 背敌光环 { 默认 = 关  回调 = backAura  颜色 = #F44336 }",
        "            开关 自动回血 { 默认 = 开  回调 = autoHeal  颜色 = #4CAF50 }",
        "        }",
        "        卡片 范围 {",
        "            拉条 间隔 { 默认 = 50  最小 = 0  最大 = 100  步长 = 1  单位 = ms }",
        "            拉条 距离 { 默认 = 30  最小 = 0  最大 = 100  步长 = 1  单位 = m }",
        "            开关 传送至头顶 { 默认 = 关  回调 = teleportHead  颜色 = #FF9800  提示 = 需先设置间隔 }",
        "        }",
        "        卡片 其他 {",
        "            开关 旧版攻击模式 { 默认 = 关  回调 = oldAttack  颜色 = #FF9800 }",
        "            开关 联机全图杀戮 { 默认 = 关  回调 = globalKill  颜色 = #F44336  提示 = 高风险 }",
        "        }",
        "    }",
        "    标签 远程枪械 {",
        "        卡片 枪械 {",
        "            开关 无后座力 { 默认 = 关  回调 = noRecoil  颜色 = #2196F3 }",
        "            开关 自动瞄准 { 默认 = 关  回调 = autoAim  颜色 = #4CAF50 }",
        "            开关 爆头模式 { 默认 = 关  回调 = headshot  颜色 = #F44336 }",
        "            开关 射速提升 { 默认 = 关  回调 = speedUp  颜色 = #FF9800 }",
        "            开关 无限弹药 { 默认 = 关  回调 = infiniteAmmo  颜色 = #2196F3 }",
        "        }",
        "        卡片 倍率 {",
        "            拉条 伤害倍率 { 默认 = 10  最小 = 1  最大 = 50  步长 = 1  单位 = x }",
        "            开关 快速换弹 { 默认 = 关  回调 = fastReload  颜色 = #4CAF50 }",
        "            开关 自动射击 { 默认 = 关  回调 = autoFire  颜色 = #2196F3 }",
        "        }",
        "        卡片 穿透 {",
        "            开关 透视子弹 { 默认 = 关  回调 = bulletTrace  颜色 = #F44336  提示 = 高风险 }",
        "            开关 连发模式 { 默认 = 关  回调 = burstMode  颜色 = #FF9800 }",
        "        }",
        "    }",
        "}",
        "分页 通用 {",
        "    图标 = ic_home",
        "    标签 基础 {",
        "        卡片 系统 {",
        "            说明 核心系统功能，建议保持开启",
        "            分隔线",
        "            开关 防闪退 { 默认 = 开  回调 = antiCrash  颜色 = #4CAF50 }",
        "            开关 显示FPS { 默认 = 关  回调 = showFps  颜色 = #2196F3 }",
        "            开关 后台保活 { 默认 = 开  回调 = keepAlive  颜色 = #4CAF50 }",
        "        }",
        "        卡片 快捷操作 {",
        "            按钮 清理缓存 { 回调 = clearCache  颜色 = #FF9800 }",
        "            按钮 查看手持物品ID { 回调 = showItemId  颜色 = #2196F3 }",
        "            按钮 一键打理家园 { 回调 = tidyHome  颜色 = #4CAF50 }",
        "        }",
        "    }",
        "    标签 高级优化 {",
        "        卡片 性能 {",
        "            拉条 最高帧数 { 默认 = 80  最小 = 30  最大 = 144  步长 = 1  单位 = 帧 }",
        "            拉条 玩家视野 { 默认 = 50  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "        }",
        "        卡片 音画 {",
        "            拉条 音量 { 默认 = 70  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "            拉条 亮度 { 默认 = 60  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "        }",
        "    }",
        "}",
        "分页 属性 {",
        "    图标 = ic_property",
        "    标签 移速 {",
        "        卡片 移动 {",
        "            拉条 灵敏度 { 默认 = 50  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "            拉条 移动速度 { 默认 = 60  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "            拉条 跳跃高度 { 默认 = 40  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "        }",
        "        卡片 辅助 {",
        "            开关 通知提醒 { 默认 = 开  回调 = notifySwitch  颜色 = #4CAF50 }",
        "            开关 音效反馈 { 默认 = 关  回调 = soundFeedback  颜色 = #2196F3 }",
        "            开关 自动更新 { 默认 = 开  回调 = autoUpdate  颜色 = #4CAF50 }",
        "        }",
        "    }",
        "    标签 攻击属性 {",
        "        卡片 战斗属性 {",
        "            拉条 攻击速度 { 默认 = 55  最小 = 0  最大 = 100  步长 = 1  单位 = % }",
        "            开关 无敌模式 { 默认 = 关  回调 = godMode  颜色 = #F44336  提示 = 免疫所有伤害 }",
        "            开关 无限体力 { 默认 = 关  回调 = infiniteStamina  颜色 = #4CAF50 }",
        "        }",
        "    }",
        "}",
        "分页 瞄准 {",
        "    图标 = ic_aim",
        "    标签 配置管理 {",
        "        卡片 配置 {",
        "            说明 导入导出配置文件",
        "            按钮 导入配置 { 回调 = importConfig  颜色 = #2196F3 }",
        "            按钮 导出配置 { 回调 = exportConfig  颜色 = #4CAF50 }",
        "        }",
        "    }",
        "}",
        "分页 功能 {",
        "    图标 = ic_settings",
        "    标签 画面 {",
        "        卡片 渲染 {",
        "            开关 高清渲染 { 默认 = 关  回调 = hdRender  颜色 = #2196F3 }",
        "            开关 光影效果 { 默认 = 关  回调 = shaderEffect  颜色 = #E91E63 }",
        "            开关 水面反射 { 默认 = 关  回调 = waterReflect  颜色 = #2196F3 }",
        "        }",
        "        卡片 优化 {",
        "            开关 植被优化 { 默认 = 关  回调 = grassOpt  颜色 = #4CAF50 }",
        "            开关 抗锯齿 { 默认 = 关  回调 = antiAlias  颜色 = #2196F3 }",
        "            开关 动态阴影 { 默认 = 关  回调 = dynamicShadow  颜色 = #FF9800 }",
        "        }",
        "    }",
        "    标签 系统 {",
        "        卡片 环境 {",
        "            开关 粒子效果 { 默认 = 开  回调 = particleEffect  颜色 = #E91E63 }",
        "            开关 天气系统 { 默认 = 开  回调 = weatherSystem  颜色 = #2196F3 }",
        "        }",
        "        卡片 操作 {",
        "            按钮 全部应用 { 回调 = applyAll  颜色 = #4CAF50 }",
        "            按钮 恢复默认 { 回调 = resetDefault  颜色 = #F44336  提示 = 重置所有设置 }",
        "        }",
        "    }",
        "}",
        "分页 视觉 {",
        "    图标 = ic_float_window",
        "    标签 系统设置 {",
        "        卡片 关于 {",
        "            按钮 检查更新 { 回调 = checkUpdate  颜色 = #2196F3 }",
        "            按钮 关于 { 回调 = aboutInfo  颜色 = #4CAF50 }",
        "        }",
        "        卡片 网络 {",
        "            开关 自动下载 { 默认 = 关  回调 = autoDownload  颜色 = #2196F3 }",
        "            开关 仅WiFi下载 { 默认 = 开  回调 = wifiOnly  颜色 = #4CAF50 }",
        "        }",
        "    }",
        "}",
        "分页 修改 {",
        "    图标 = ic_apply",
        "    标签 编辑 {",
        "        卡片 修改 {",
        "            开关 自动修改 { 默认 = 关  回调 = autoEdit  颜色 = #FF9800 }",
        "            开关 高级模式 { 默认 = 关  回调 = advancedMode  颜色 = #F44336  提示 = 显示更多选项 }",
        "        }",
        "    }",
        "    标签 备份 {",
        "        卡片 备份 {",
        "            开关 备份原值 { 默认 = 开  回调 = backupOriginal  颜色 = #4CAF50 }",
        "        }",
        "    }",
        "}",
        "分页 列表 {",
        "    图标 = ic_resource",
        "    标签 物品列表 {",
        "        卡片 物品 {",
        "            按钮 物品1 { 回调 = selectItem1  颜色 = #2196F3 }",
        "            按钮 物品2 { 回调 = selectItem2  颜色 = #FF9800 }",
        "            按钮 物品3 { 回调 = selectItem3  颜色 = #4CAF50 }",
        "            按钮 物品4 { 回调 = selectItem4  颜色 = #E91E63 }",
        "        }",
        "    }",
        "}",
        "分页 快捷 {",
        "    图标 = ic_fast",
        "    标签 快捷键配置 {",
        "        卡片 快捷按钮 {",
        "            说明 开启后显示对应的快捷键",
        "            分隔线",
        "            开关 杀戮光环-K { 默认 = 关  回调 = sc_killAura  颜色 = #E91E63  提示 = 显示杀戮光环快捷键 }",
        "            开关 无敌模式-K { 默认 = 关  回调 = sc_godMode  颜色 = #E91E63  提示 = 显示无敌模式快捷键 }",
        "            开关 自动回血-K { 默认 = 关  回调 = sc_autoHeal  颜色 = #E91E63  提示 = 显示自动回血快捷键 }",
        "            开关 防闪退-K { 默认 = 关  回调 = sc_antiCrash  颜色 = #E91E63  提示 = 显示防闪退快捷键 }",
        "            开关 全部应用-K { 默认 = 关  回调 = sc_applyAll  颜色 = #4CAF50  提示 = 显示全部应用快捷键 }",
        "            开关 清理缓存-K { 默认 = 关  回调 = sc_clearCache  颜色 = #4CAF50  提示 = 显示清理缓存快捷键 }",
        "        }",
        "    }",
        "}",
        "分页 传送 {",
        "    图标 = ic_teleport",
        "    卡片 传送 {",
        "        说明 坐标传送功能",
        "        按钮 记录坐标 { 回调 = recordCoord  颜色 = #2196F3 }",
        "        按钮 传送 { 回调 = teleport  颜色 = #FF9800 }",
        "    }",
        "}",
        "分页 其他 {",
        "    图标 = ic_more",
        "    标签 调试 {",
        "        卡片 调试 {",
        "            开关 调试模式 { 默认 = 关  回调 = debugMode  颜色 = #FF9800 }",
        "        }",
        "    }",
        "    标签 日志 {",
        "        卡片 日志 {",
        "            开关 日志输出 { 默认 = 关  回调 = logOutput  颜色 = #2196F3 }",
        "        }",
        "    }",
        "}",
        "分页 退出 {",
        "    图标 = ic_download",
        "}",
        ""
    );

public static void initCallbacks(
            final FloatBallService service,
            Map<String, Runnable> callbacks,
            Map<String, IntFunction<String>> seekFormatters) {

        // ==================== 回调示例（从简单到复杂）====================
        // 【初级】纯通知——告诉你功能已切换
        callbacks.put("antiCrash", () -> service.toast("防闪退已切换"));

        // 【中级】带条件判断——根据开关状态做不同事
        // 注意：这里只是演示，实际 toggle 状态存在 toggleStates 里
        callbacks.put("killAura", () -> {
            // 用 service.toast 显示当前状态
            // 实际使用时，这里可以写：开启杀戮光环的代码
            boolean isOn = service.getToggleState("杀戮光环");
            if (isOn) {
                service.toast("杀戮光环已开启，注意高风险！");
                // 这里写"开启功能"的代码
            } else {
                service.toast("杀戮光环已关闭");
                // 这里写"关闭功能"的代码
            }
        });

        // 【中级】执行多个操作
        callbacks.put("globalKill", () -> {
            service.toast("联机全图杀戮已切换");
            // 可以写多行代码
            // 例：检查条件 -> 执行 -> 反馈
            if (service.getToggleState("杀戮光环")) {
                service.toast("提示：杀戮光环已同时开启");
            }
        });

        // 【高级】调用 service 里的方法（需要在 FloatBallService 中定义 public 方法）
        callbacks.put("godMode", () -> {
            service.toast("无敌模式已切换");
            // 例：service.setGodMode(service.getToggleState("无敌模式"));
        });

        // ===== 按钮类回调（点击执行一次性操作）=====

        // 【中级】确认后执行
        callbacks.put("resetDefault", () -> {
            service.toast("已恢复默认设置");
            // 这里可以写：遍历所有 toggle 重置为默认值
        });

        // 【中级】读取数据并显示
        callbacks.put("showItemId", () -> {
            // 模拟读取手持物品 ID
            String itemId = "12345";  // 实际应从游戏读取
            service.toast("手持物品ID: " + itemId);
        });

        // 【高级】调用 service 的复杂方法
        callbacks.put("confirmExit", () -> {
            // animateMorph 和 stopSelf 是 FloatBallService 的方法
            service.animateMorph(false);
            service.stopSelf();
        });

        // ===== 测试回调 =====
        callbacks.put("testToggle", () -> service.toast("测试开关已切换"));
        callbacks.put("testButton", () -> service.toast("测试按钮被点击"));
        // 快捷键开关回调——控制对应快捷键的显示/隐藏
        callbacks.put("sc_killAura", () -> service.toggleShortcut("杀戮光环", "killAura", "toggle"));
        callbacks.put("sc_godMode", () -> service.toggleShortcut("无敌模式", "godMode", "toggle"));
        callbacks.put("sc_autoHeal", () -> service.toggleShortcut("自动回血", "autoHeal", "toggle"));
        callbacks.put("sc_antiCrash", () -> service.toggleShortcut("防闪退", "antiCrash", "toggle"));
        callbacks.put("sc_applyAll", () -> service.toggleShortcut("全部应用", "applyAll", "button"));
        callbacks.put("sc_clearCache", () -> service.toggleShortcut("清理缓存", "clearCache", "button"));

        
        // ===== 简单回调（保持原有功能）=====
        callbacks.put("showFps", () -> service.toast("显示FPS已切换"));
        callbacks.put("keepAlive", () -> service.toast("后台保活已切换"));
        callbacks.put("backAura", () -> service.toast("背敌光环已切换"));
        callbacks.put("autoHeal", () -> service.toast("自动回血已切换"));
        callbacks.put("meleeRange", () -> service.toast("近战范围已切换"));
        callbacks.put("teleportHead", () -> service.toast("传送至头顶已切换"));
        callbacks.put("oldAttack", () -> service.toast("旧版攻击模式已切换"));
        callbacks.put("noRecoil", () -> service.toast("无后座力已切换"));
        callbacks.put("autoAim", () -> service.toast("自动瞄准已切换"));
        callbacks.put("headshot", () -> service.toast("爆头模式已切换"));
        callbacks.put("speedUp", () -> service.toast("射速提升已切换"));
        callbacks.put("infiniteAmmo", () -> service.toast("无限弹药已切换"));
        callbacks.put("fastReload", () -> service.toast("快速换弹已切换"));
        callbacks.put("autoFire", () -> service.toast("自动射击已切换"));
        callbacks.put("bulletTrace", () -> service.toast("透视子弹已切换"));
        callbacks.put("burstMode", () -> service.toast("连发模式已切换"));
        callbacks.put("notifySwitch", () -> service.toast("通知提醒已切换"));
        callbacks.put("soundFeedback", () -> service.toast("音效反馈已切换"));
        callbacks.put("autoUpdate", () -> service.toast("自动更新已切换"));
        callbacks.put("infiniteStamina", () -> service.toast("无限体力已切换"));
        callbacks.put("hdRender", () -> service.toast("高清渲染已切换"));
        callbacks.put("shaderEffect", () -> service.toast("光影效果已切换"));
        callbacks.put("waterReflect", () -> service.toast("水面反射已切换"));
        callbacks.put("grassOpt", () -> service.toast("植被优化已切换"));
        callbacks.put("antiAlias", () -> service.toast("抗锯齿已切换"));
        callbacks.put("dynamicShadow", () -> service.toast("动态阴影已切换"));
        callbacks.put("particleEffect", () -> service.toast("粒子效果已切换"));
        callbacks.put("weatherSystem", () -> service.toast("天气系统已切换"));
        callbacks.put("autoDownload", () -> service.toast("自动下载已切换"));
        callbacks.put("wifiOnly", () -> service.toast("仅WiFi下载已切换"));
        callbacks.put("autoEdit", () -> service.toast("自动修改已切换"));
        callbacks.put("advancedMode", () -> service.toast("高级模式已切换"));
        callbacks.put("backupOriginal", () -> service.toast("备份原值已切换"));
        callbacks.put("debugMode", () -> service.toast("调试模式已切换"));
        callbacks.put("logOutput", () -> service.toast("日志输出已切换"));
        callbacks.put("clearCache", () -> service.toast("缓存已清理"));
        callbacks.put("tidyHome", () -> service.toast("家园已整理"));
        callbacks.put("importConfig", () -> service.toast("配置已导入"));
        callbacks.put("exportConfig", () -> service.toast("配置已导出"));
        callbacks.put("applyAll", () -> service.toast("所有配置已应用"));
        callbacks.put("checkUpdate", () -> service.toast("已是最新版本 v1.0.0"));
        callbacks.put("aboutInfo", () -> service.toast("VorTex v1.0.0"));
        callbacks.put("selectItem1", () -> service.toast("选中: 物品1"));
        callbacks.put("selectItem2", () -> service.toast("选中: 物品2"));
        callbacks.put("selectItem3", () -> service.toast("选中: 物品3"));
        callbacks.put("selectItem4", () -> service.toast("选中: 物品4"));
        callbacks.put("recordCoord", () -> service.toast("当前坐标已记录"));
        callbacks.put("teleport", () -> service.toast("传送成功"));
        callbacks.put("minimizeToBall", () -> service.animateMorph(false));

        // SeekBar 显示格式化器
        seekFormatters.put("灵敏度", v -> { if (v < 25) return "低"; if (v < 50) return "中"; if (v < 75) return "高"; return "最高"; });
        seekFormatters.put("玩家视野", v -> { if (v < 33) return "低"; if (v < 66) return "中"; return "高"; });
        seekFormatters.put("伤害倍率", v -> v + "x");
    }
}
