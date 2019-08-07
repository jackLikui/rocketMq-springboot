package com.rocketmq.producerandconsumer.event;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ProjectName: producerandconsumer
 * @Package: com.rocketmq.producerandconsumer.event
 * @ClassName: fs
 * @Author: chinasoft.k.li
 * @Description:
 * @Date: 2019/8/6 13:34
 * @Version: 1.0
 */
public class OrderPaidEvent implements Serializable {
    private String orderId;

    private BigDecimal paidMoney;

    public OrderPaidEvent() {
    }

    public OrderPaidEvent(String orderId, BigDecimal paidMoney) {
        this.orderId = orderId;
        this.paidMoney = paidMoney;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getPaidMoney() {
        return paidMoney;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setPaidMoney(BigDecimal paidMoney) {
        this.paidMoney = paidMoney;
    }

    @Override
    public String toString() {
        return "OrderPaidEvent{" +
                "orderId='" + orderId + '\'' +
                ", paidMoney=" + paidMoney +
                '}';
    }
}