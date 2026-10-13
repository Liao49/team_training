package com.zjnu.demo.common;

import java.util.regex.Pattern;

/**
 * 提示注入防护（实验四）：拦截危险指令，正常输入用定界符包裹
 */
public final class PromptGuard {

    private static final Pattern DANGEROUS = Pattern.compile(
            "(?i)(ignore (all )?(previous|prior) instructions|" +
            "忽略(以上|之前)(的)?(所有)?指令|" +
            "disregard (the )?(system|above)|你现在是|扮演(开发者模式|Jailbreak))");

    private PromptGuard() {}

    /** 拼入提示词前先过这道防护 */
    public static String sanitize(String input) {
        if (input == null) return "";
        if (DANGEROUS.matcher(input).find()) {
            throw new BusinessException("输入包含不安全的指令，已被拦截");
        }
        // 定界符包裹，防止内容逃逸覆盖系统指令
        return "<user_input>\n" + input.replace("<user_input>", "") + "\n</user_input>";
    }
}
