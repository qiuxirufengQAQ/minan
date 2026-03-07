package cn.qrfeng.lianai.game.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 * 允许前端 H5 和小程序访问后端 API
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // 允许的源
            .allowedOriginPatterns("*")
            // 允许的请求方法
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            // 允许的请求头
            .allowedHeaders("*")
            // 允许携带凭证（Cookie、Authorization headers 等）
            .allowCredentials(true)
            // 预检请求缓存时间（秒）
            .maxAge(3600);
    }
}
