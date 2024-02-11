package com.jorgesacristan.englishCard.utils;

import com.jorgesacristan.englishCard.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class CorrelationIdUtils {

    public String generateAndStoreId (Map<String,String> entry){
        String correlationId = "";
        if(entry==null || entry.get(Configuration.CORRELATION_ID_HEADER_NAME).equals("")){
            correlationId = UUID.randomUUID().toString();
            MDC.put(Configuration.CORRELATION_ID_HEADER_NAME,correlationId);
            return correlationId;
        }
        else{
            MDC.put(Configuration.CORRELATION_ID_HEADER_NAME,entry.get(Configuration.CORRELATION_ID_HEADER_NAME));
            return entry.get(Configuration.CORRELATION_ID_HEADER_NAME);
        }
    }

    public String getActualCorrelationIdOrNew (){
        String correlationId = MDC.get(Configuration.CORRELATION_ID_HEADER_NAME);
        if(StringUtils.isBlank(correlationId)){
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }

    public String getActualCorrelationId(){
        return MDC.get(Configuration.CORRELATION_ID_HEADER_NAME);
    }

    public void removeCorrelationId () {
        MDC.remove(Configuration.CORRELATION_ID_HEADER_NAME);
    }

}
