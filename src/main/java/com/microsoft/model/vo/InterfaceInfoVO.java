package com.microsoft.model.vo;

import com.microsoft.model.entity.InterfaceInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class InterfaceInfoVO {
    private Long id;
    private String name;
    private String url;
    private String requestHeader;
    private String responseHeader;
    private String description;
    private String method;
    private Integer status;
    private Long userId;
    // 创建人昵称
    private String username;

    /**
     * 包装类转对象
     */
    public static InterfaceInfo voToObj(InterfaceInfoVO interfaceInfoVO) {
        if (interfaceInfoVO == null) {
            return null;
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoVO, interfaceInfo);
        return interfaceInfo;
    }

    /**
     * 对象转包装类
     */
    public static InterfaceInfoVO objToVo(InterfaceInfo interfaceInfo) {
        if (interfaceInfo == null) {
            return null;
        }
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
        return interfaceInfoVO;
    }
}
