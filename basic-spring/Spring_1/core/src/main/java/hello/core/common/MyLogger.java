package hello.core.common;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyLogger {

    private String uuid;
    private String requestURL;

    /**
     * requestURL 빈이 생성되는 시점에 알 수 없으므로 외부에서 setter로 입력 받는다.
     * @param requestURL
     */
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public void log(String message) {
        System.out.println("[" +uuid + "]" + "[" + requestURL + "] " + message);
    }

    /**
     * 빈이 생성되는 시점에 @PostConstruct를 사용해서 uuid를 생성하여 저장한다.
     * HTTP 요청 하나당 하나씩 생성되며, uuid를 저장하면 다른 요청과 구분이 가능하다.
     */
    @PostConstruct
    public void init() {
        uuid = UUID.randomUUID().toString();
        System.out.println("[" +uuid + "]" + "request scope bean create : " + this);
    }

    /**
     * 빈요 소멸하는 시점에 @PreDestroy를 사용하여 종료 메시지를 남긴다.
     */
    @PreDestroy
    public void close() {
        System.out.println("[" +uuid + "]" + "request scope bean close : " + this);
    }

}
