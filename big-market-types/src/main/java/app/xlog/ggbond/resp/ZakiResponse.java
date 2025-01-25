package app.xlog.ggbond.resp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

/**
 * 自定义的 http 响应结果
 * <p>
 * - 使用示例
 * ----- 1. ZakiResponse.status(HttpStatus.BAD_GATEWAY).body("awardId", awardId, "awardName", awardName);
 * ----- 2. ZakiResponse.ok("awardId", awardId, "awardName", awardName);
 * ----- 3. 用于返回 message : ZakiResponse.ok("用户 " + userId + " 登录成功")
 * ----- 4. ZakiResponse.error(ZakiResponse.BigMarketRespCode.PARAMETER_VERIFICATION_FAILED, "错误信息")
 * </p>
 */
public class ZakiResponse extends ResponseEntity<Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ZakiResponse(HttpStatusCode status) {
        super(status);
    }

    public static Builder status(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode must not be null");
        return new Builder(status);
    }

    public static class Builder implements BodyBuilder {

        private final HttpStatusCode statusCode;
        private final HttpHeaders headers = new HttpHeaders();

        public Builder(int statusCode) {
            this(HttpStatusCode.valueOf(statusCode));
        }

        public Builder(HttpStatusCode statusCode) {
            this.statusCode = statusCode;
        }

        public Builder header(String headerName, String... headerValues) {
            for (String headerValue : headerValues) {
                this.headers.add(headerName, headerValue);
            }
            return this;
        }

        public Builder headers(@Nullable HttpHeaders headers) {
            if (headers != null) {
                this.headers.putAll(headers);
            }
            return this;
        }

        public Builder headers(Consumer<HttpHeaders> headersConsumer) {
            headersConsumer.accept(this.headers);
            return this;
        }

        public Builder allow(HttpMethod... allowedMethods) {
            this.headers.setAllow(new LinkedHashSet<>(Arrays.asList(allowedMethods)));
            return this;
        }

        public Builder contentLength(long contentLength) {
            this.headers.setContentLength(contentLength);
            return this;
        }

        public Builder contentType(MediaType contentType) {
            this.headers.setContentType(contentType);
            return this;
        }

        public Builder eTag(@Nullable String etag) {
            if (etag != null) {
                if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
                    etag = "\"" + etag;
                }
                if (!etag.endsWith("\"")) {
                    etag = etag + "\"";
                }
            }
            this.headers.setETag(etag);
            return this;
        }

        public Builder lastModified(ZonedDateTime date) {
            this.headers.setLastModified(date);
            return this;
        }

        public Builder lastModified(Instant date) {
            this.headers.setLastModified(date);
            return this;
        }

        public Builder lastModified(long date) {
            this.headers.setLastModified(date);
            return this;
        }

        public Builder location(URI location) {
            this.headers.setLocation(location);
            return this;
        }

        public Builder cacheControl(CacheControl cacheControl) {
            this.headers.setCacheControl(cacheControl);
            return this;
        }

        public Builder varyBy(String... requestHeaders) {
            this.headers.setVary(Arrays.asList(requestHeaders));
            return this;
        }

        public ResponseEntity<JsonNode> build() {
            return body(null);
        }

        public <T> ResponseEntity<T> body(T body) {
            return new ResponseEntity<>(
                    body,
                    this.headers,
                    this.statusCode
            );
        }

        public ResponseEntity<JsonNode> body(Object... objects) {
            if (objects.length % 2 != 0) throw new IllegalArgumentException("参数个数必须为偶数");

            ObjectNode dataNode = objectMapper.createObjectNode();
            for (int i = 0; i < objects.length; i += 2) {
                dataNode.putPOJO(objects[i].toString(), objects[i + 1]);
            }

            HttpStatus httpStatus = (HttpStatus) this.statusCode;
            return new ResponseEntity<>(
                    objectMapper.createObjectNode()
                            .put("code", httpStatus.value())
                            .put("message", httpStatus.getReasonPhrase())
                            .putPOJO("data", dataNode),
                    this.headers,
                    httpStatus
            );
        }
    }

    public static ResponseEntity<JsonNode> ok(Object... objects) {
        if (objects.length % 2 != 0) throw new IllegalArgumentException("参数个数必须为偶数");

        ObjectNode dataNode = objectMapper.createObjectNode();
        for (int i = 0; i < objects.length; i += 2) {
            dataNode.putPOJO(objects[i].toString(), objects[i + 1]);
        }

        return ok().body(objectMapper
                .createObjectNode()
                .put("code", BigMarketRespCode.SUCCESS.getCode())
                .put("message", BigMarketRespCode.SUCCESS.getMessage())
                .putPOJO("data", dataNode)
        );
    }

    public static ResponseEntity<JsonNode> ok(String dataMessage) {
        return ok().body(objectMapper
                .createObjectNode()
                .put("code", BigMarketRespCode.SUCCESS.getCode())
                .put("message", BigMarketRespCode.SUCCESS.getMessage())
                .putPOJO("data", dataMessage)
        );
    }

    public static ResponseEntity<JsonNode> error(BigMarketRespCode respCode, String dataMessage) {
        return status(HttpStatus.INTERNAL_SERVER_ERROR).body(objectMapper
                .createObjectNode()
                .put("code", respCode.getCode())
                .put("message", respCode.getMessage())
                .putPOJO("data", dataMessage)
        );
    }

}
