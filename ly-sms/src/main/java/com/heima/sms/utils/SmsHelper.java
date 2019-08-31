package com.heima.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.heima.common.constants.ExceptionEnum;
import com.heima.common.exception.LyException;
import com.heima.common.utils.JsonUtils;
import com.heima.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.heima.sms.constants.SmsConstants.*;

/**
 * Aliyun短信服务
 */
@Slf4j
@Component
public class SmsHelper {

    private IAcsClient iAcsClient;

    private SmsProperties properties;

    public SmsHelper(IAcsClient iAcsClient, SmsProperties properties) {
        this.iAcsClient = iAcsClient;
        this.properties = properties;
    }


    /**
     * 给指定手机号发送短信
     * @param phone 手机号
     * @param signName 签名
     * @param template 模板名
     * @param param  发送的参数
     */
    public void sendMessage(String phone, String signName, String template, String param) {

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(properties.getDomain());
        request.setVersion(properties.getVersion());
        request.setAction(properties.getAction());
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter(SMS_PARAM_KEY_PHONE, phone);
        request.putQueryParameter(SMS_PARAM_KEY_SIGN_NAME, signName);
        request.putQueryParameter(SMS_PARAM_KEY_TEMPLATE_CODE, template);
        request.putQueryParameter(SMS_PARAM_KEY_TEMPLATE_PARAM, param);
        try {
            CommonResponse response = iAcsClient.getCommonResponse(request);
            if (response.getHttpStatus() >= 300) {
                log.error("[SMS服务]发送短信失败,响应信息是:{}",response.getData());
            }

//            获取响应体
            Map<String, String> resp = JsonUtils.toMap(response.getData(), String.class, String.class);
            if (!StringUtils.equals(OK, resp.get(SMS_RESPONSE_KEY_CODE))) {
//                若发送不成功,报错
                log.error("[SMS服务]发送短信失败,原因{}", resp.get(SMS_RESPONSE_KEY_MESSAGE));
                throw new LyException(ExceptionEnum.SEND_MESSAGE_ERROR);
            }
//            若发送成功,记录日志
            log.info("[SMS服务]发送短信成功,手机号:{},响应:{}", phone, response.getData());

        } catch (ServerException e) {
            log.error("【SMS服务】发送短信失败，服务端异常。", e);
        } catch (ClientException e) {
            log.error("【SMS服务】发送短信失败，服务端异常。", e);
        }

    }


}