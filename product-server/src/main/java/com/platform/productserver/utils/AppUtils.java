package com.platform.productserver.utils;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.authcommon.common.ResultCode;
import com.platform.authcommon.exception.AppException;
import com.platform.productserver.entity.Account;
import com.platform.productserver.entity.Merchant;

import java.math.BigDecimal;

public class AppUtils {


    /**
     * 操作数据
     */
    public static void opt(Account account, BigDecimal amount, String opt, Boolean credit){
        BigDecimal balance = account.getBalance();
        if(StrUtil.equals("+", opt)){
            // 设置账户余额和累计收入
            account.setBalance(NumberUtil.add(balance, amount));
            account.setIncomeAmount(NumberUtil.add(account.getIncomeAmount(), amount));
            return;
        }
        if(StrUtil.equals("-", opt)){
            // 支出金额 = 原支出金额 + 交易金额
            account.setExpenseAmount(NumberUtil.add(account.getExpenseAmount(), amount));
            if(NumberUtil.isGreaterOrEqual(balance, amount)){
                account.setBalance(NumberUtil.sub(balance, amount));
            } else {
                // 允许欠款
                if(credit){
                    account.setBalance(BigDecimal.ZERO);
                    // 欠款金额 = 原欠款金额 + (交易金额 - 账户余额)
                    BigDecimal add = NumberUtil.add(account.getCreditAmount(), NumberUtil.sub(amount, balance));
                    account.setCreditAmount(add);
                } else {
                    throw new AppException(ResultCode.NOT_EXIST, "账户余额不足,操作失败!");
                }
            }
            return;
        }
        throw new AppException(ResultCode.NOT_EXIST, "操作类型不存在");
    }


    /**
     * B端操作金额
     */
    public static void opt(Merchant account, BigDecimal amount, String opt, Boolean credit){
        BigDecimal balance = account.getBalance();
        if(StrUtil.equals("+", opt)){
            // 设置账户余额和累计收入
            account.setBalance(NumberUtil.add(balance, amount));
            account.setIncomeAmount(NumberUtil.add(account.getIncomeAmount(), amount));
            return;
        }
        if(StrUtil.equals("-", opt)){
            // 支出金额 = 原支出金额 + 交易金额
            account.setExpenseAmount(NumberUtil.add(account.getExpenseAmount(), amount));
            // 可用余额 = 总金额 - 欠款金额 - 冻结金额
            if(NumberUtil.isGreaterOrEqual(NumberUtil.sub(balance, account.getCreditAmount(), account.getFreezeAmount()), amount)){
                account.setBalance(NumberUtil.sub(balance, amount));
            } else {
                // 允许欠款
                if(credit){
                    account.setBalance(BigDecimal.ZERO);
                    // 欠款金额 = 原欠款金额 + (交易金额 - 账户余额)
                    BigDecimal add = NumberUtil.add(account.getCreditAmount(), NumberUtil.sub(amount, balance));
                    account.setCreditAmount(add);
                } else {
                    throw new AppException(ResultCode.NOT_EXIST, "账户余额不足,操作失败!");
                }
            }
            return;
        }
        throw new AppException(ResultCode.NOT_EXIST, "操作类型不存在");
    }
}
