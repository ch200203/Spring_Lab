package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
    // ComponentScan에서 제외할 목록 (필터)를 지정
    // 단, 실무에서는 이렇게 지정하지는 않음, 예제코드를 사용하기 위해서 필터를 지정함
    excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {

}
