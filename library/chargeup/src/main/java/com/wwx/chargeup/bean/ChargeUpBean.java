package com.wwx.chargeup.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-14 22:58:59
 *      description:    记账信息实体
 *      version:        v1.0
 * </pre>
 */
@Getter
@Setter
@Builder
@ToString
public class ChargeUpBean {
    /** 编辑人 */
    @Builder.Default
    private String userName = "admin";

    /** 账目类型 */
    private String accountsType;

    /** 账目状态 <p>+收入 -支出 " "</p> */
    private String accountsState;

    /** 账目金额 */
    private String accountsMoney;

    /** 创建时间 */
    private String createTime;

    /** 修改时间 */
    private String updateTime;


}
