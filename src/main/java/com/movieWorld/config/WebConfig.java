package com.movieWorld.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 웹 설정
 * - 뷰만 보여주는 URL을 컨트롤러 없이 매핑할 때 사용
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/**
     * 단순히 뷰 이름만 매핑 (컨트롤러 없이 URL → view 이름 연결)
     * 예: "/" → "home" 뷰
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
	
}
