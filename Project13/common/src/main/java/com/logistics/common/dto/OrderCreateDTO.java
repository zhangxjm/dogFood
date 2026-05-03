package com.logistics.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderCreateDTO {
    private Long userId;

    @NotBlank(message = "寄件人姓名不能为空")
    private String senderName;

    @NotBlank(message = "寄件人电话不能为空")
    private String senderPhone;

    @NotBlank(message = "寄件人地址不能为空")
    private String senderAddress;

    private Double senderLat;
    private Double senderLng;

    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收件人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收件人地址不能为空")
    private String receiverAddress;

    private Double receiverLat;
    private Double receiverLng;

    @NotBlank(message = "物品名称不能为空")
    private String goodsName;

    private Double weight;
    private Double volume;
    private String remark;
}
