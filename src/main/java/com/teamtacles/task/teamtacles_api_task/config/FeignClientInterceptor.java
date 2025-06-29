package com.teamtacles.task.teamtacles_api_task.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        System.out.println("--- EXECUTANDO O FEIGN CLIENT INTERCEPTOR ---"); // <-- ADICIONE ESTA LINHA

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                System.out.println("--> Token encontrado! Anexando ao cabeçalho..."); // <-- ADICIONE ESTA LINHA
                requestTemplate.header(AUTHORIZATION_HEADER, authorizationHeader);
            } else {
                System.out.println("--> AVISO: Nenhum cabeçalho de autorização encontrado na requisição original."); // <-- ADICIONE ESTA LINHA
            }
        }
    }
}