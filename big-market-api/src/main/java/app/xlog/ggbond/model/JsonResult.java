package app.xlog.ggbond.model;

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
 * 自定义的http响应结果
 * <p>
 * - 使用示例
 * ----- 1. JsonResult.status(HttpStatus.BAD_GATEWAY).body("awardId", awardId, "awardName", awardName);
 * ----- 2. JsonResult.ok("awardId", awardId, "awardName", awardName);
 * ----- 3. 用于返回 message : JsonResult.ok("用户 " + userId + " 登录成功")
 * </p>
 */
public class JsonResult extends ResponseEntity<Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonResult(HttpStatusCode status) {
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
            return null;
        }

        public ResponseEntity<JsonNode> body(Object... objects) {
            if (objects.length % 2 != 0) throw new IllegalArgumentException("参数个数必须为偶数");

            ObjectNode dataNode = objectMapper.createObjectNode();
            for (int i = 0; i < objects.length; i += 2) {
                dataNode.putPOJO(objects[i].toString(), objects[i + 1]);
            }

            return new ResponseEntity<>(
                    objectMapper.createObjectNode()
                            .put("message", "ok")
                            .putPOJO("data", dataNode),
                    this.headers,
                    this.statusCode
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
                .put("message", "ok")
                .putPOJO("data", dataNode)
        );
    }

    public static ResponseEntity<JsonNode> ok(String message) {
        return ok().body(objectMapper
                .createObjectNode()
                .putPOJO("message", message)
        );
    }

}
