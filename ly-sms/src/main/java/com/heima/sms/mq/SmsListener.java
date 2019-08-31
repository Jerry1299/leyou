package com.heima.sms.mq;

import com.heima.common.utils.JsonUtils;
import com.heima.sms.config.SmsProperties;
import com.heima.sms.utils.SmsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.heima.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.heima.common.constants.MQConstants.Queue.SMS_VERIFY_CODE_QUEUE;
import static com.heima.common.constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

/**
 * @Classname SmsListener
 * @Description TODO
 * @Date 2019/8/28 20:45
 * @Created by YJF
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsProperties prop;

    @Autowired
    private SmsHelper smsHelper;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SMS_VERIFY_CODE_QUEUE),
            exchange = @Exchange(name = SMS_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = VERIFY_CODE_KEY
    ))
    public void listenVerifyCode(Map<String, String> msg) {
        if (msg == null) {
            return;
        }
//        获取phone,同时将其在Map中移除
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)) {
            return;
        }

        try {
            smsHelper.sendMessage(phone, prop.getSignName(),
                    prop.getVerifyCodeTemplate(), JsonUtils.toString(msg));
        } catch (Exception e) {
//            发送失败后不重发,需要捕获异常
            log.error("[SMS服务]短信验证码发送失败",e);
        }


    }


}
