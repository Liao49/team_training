package com.zjnu.demo.common;

import java.util.regex.Pattern;

/**
 * 提示注入防护工具类（实验四安全落地，为实验五"用户输入 → 大模型"链路铺路）：
 * 1. 拦截典型注入指令（中英文常见话术）；2. 用定界符包裹用户输入，防止内容逃逸出"用户资料区"。
 */
public final class PromptGuard {

    private static final Pattern DANGEROUS = Pattern.compile(
            "(?i)(ignore (all )?(previous|prior) instructions|" +
            "忽略(以上|之前)(的)?(所有)?指令|" +
            "disregard (the )?(system|above)|你现在是|扮演(开发者模式|Jailbreak))");

    private PromptGuard() {}

    /** 用户输入拼入提示词前的防护：拦截危险指令 + 定界符包裹 */
    public static String sanitize(String input) {
        if (input == null) return "";
        if (DANGEROUS.matcher(input).find()) {
            throw new BusinessException("输入包含不安全的指令，已被拦截");
        }
        // 用定界标记包裹，防止内容逃逸出"用户资料区"覆盖系统指令
        return "<user_input>\n" + input.replace("<user_input>", "") + "\n</user_input>";
    }
}
