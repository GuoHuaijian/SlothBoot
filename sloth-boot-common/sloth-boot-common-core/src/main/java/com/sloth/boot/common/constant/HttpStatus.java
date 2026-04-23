/*
 * Copyright 2025 Sloth Boot
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sloth.boot.common.constant;

/**
 * HTTP 状态码常量（接口+常量形式）
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class HttpStatus {

    private HttpStatus() {
    }

    // ==================== 1xx 信息性状态码 ====================
    /**
     * 100 Continue
     */
    public static final int CONTINUE = 100;

    /**
     * 101 Switching Protocols
     */
    public static final int SWITCHING_PROTOCOLS = 101;

    // ==================== 2xx 成功状态码 ====================
    /**
     * 200 OK
     */
    public static final int OK = 200;

    /**
     * 201 Created
     */
    public static final int CREATED = 201;

    /**
     * 202 Accepted
     */
    public static final int ACCEPTED = 202;

    /**
     * 204 No Content
     */
    public static final int NO_CONTENT = 204;

    // ==================== 3xx 重定向状态码 ====================
    /**
     * 300 Multiple Choices
     */
    public static final int MULTIPLE_CHOICES = 300;

    /**
     * 301 Moved Permanently
     */
    public static final int MOVED_PERMANENTLY = 301;

    /**
     * 302 Found
     */
    public static final int FOUND = 302;

    /**
     * 304 Not Modified
     */
    public static final int NOT_MODIFIED = 304;

    /**
     * 307 Temporary Redirect
     */
    public static final int TEMPORARY_REDIRECT = 307;

    /**
     * 308 Permanent Redirect
     */
    public static final int PERMANENT_REDIRECT = 308;

    // ==================== 4xx 客户端错误状态码 ====================
    /**
     * 400 Bad Request
     */
    public static final int BAD_REQUEST = 400;

    /**
     * 401 Unauthorized
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 402 Payment Required
     */
    public static final int PAYMENT_REQUIRED = 402;

    /**
     * 403 Forbidden
     */
    public static final int FORBIDDEN = 403;

    /**
     * 404 Not Found
     */
    public static final int NOT_FOUND = 404;

    /**
     * 405 Method Not Allowed
     */
    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * 406 Not Acceptable
     */
    public static final int NOT_ACCEPTABLE = 406;

    /**
     * 407 Proxy Authentication Required
     */
    public static final int PROXY_AUTHENTICATION_REQUIRED = 407;

    /**
     * 408 Request Timeout
     */
    public static final int REQUEST_TIMEOUT = 408;

    /**
     * 409 Conflict
     */
    public static final int CONFLICT = 409;

    /**
     * 410 Gone
     */
    public static final int GONE = 410;

    /**
     * 411 Length Required
     */
    public static final int LENGTH_REQUIRED = 411;

    /**
     * 412 Precondition Failed
     */
    public static final int PRECONDITION_FAILED = 412;

    /**
     * 413 Payload Too Large
     */
    public static final int PAYLOAD_TOO_LARGE = 413;

    /**
     * 414 URI Too Long
     */
    public static final int URI_TOO_LONG = 414;

    /**
     * 415 Unsupported Media Type
     */
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;

    /**
     * 416 Range Not Satisfiable
     */
    public static final int RANGE_NOT_SATISFIABLE = 416;

    /**
     * 417 Expectation Failed
     */
    public static final int EXPECTATION_FAILED = 417;

    /**
     * 418 I'm a teapot
     */
    public static final int IM_A_TEAPOT = 418;

    /**
     * 421 Misdirected Request
     */
    public static final int MISDIRECTED_REQUEST = 421;

    /**
     * 422 Unprocessable Entity
     */
    public static final int UNPROCESSABLE_ENTITY = 422;

    /**
     * 423 Locked
     */
    public static final int LOCKED = 423;

    /**
     * 424 Failed Dependency
     */
    public static final int FAILED_DEPENDENCY = 424;

    /**
     * 425 Too Early
     */
    public static final int TOO_EARLY = 425;

    /**
     * 426 Upgrade Required
     */
    public static final int UPGRADE_REQUIRED = 426;

    /**
     * 428 Precondition Required
     */
    public static final int PRECONDITION_REQUIRED = 428;

    /**
     * 429 Too Many Requests
     */
    public static final int TOO_MANY_REQUESTS = 429;

    /**
     * 431 Request Header Fields Too Large
     */
    public static final int REQUEST_HEADER_FIELDS_TOO_LARGE = 431;

    /**
     * 451 Unavailable For Legal Reasons
     */
    public static final int UNAVAILABLE_FOR_LEGAL_REASONS = 451;

    // ==================== 5xx 服务器错误状态码 ====================
    /**
     * 500 Internal Server Error
     */
    public static final int INTERNAL_SERVER_ERROR = 500;

    /**
     * 501 Not Implemented
     */
    public static final int NOT_IMPLEMENTED = 501;

    /**
     * 502 Bad Gateway
     */
    public static final int BAD_GATEWAY = 502;

    /**
     * 503 Service Unavailable
     */
    public static final int SERVICE_UNAVAILABLE = 503;

    /**
     * 504 Gateway Timeout
     */
    public static final int GATEWAY_TIMEOUT = 504;

    /**
     * 505 HTTP Version Not Supported
     */
    public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

    /**
     * 506 Variant Also Negotiates
     */
    public static final int VARIANT_ALSO_NEGOTIATES = 506;

    /**
     * 507 Insufficient Storage
     */
    public static final int INSUFFICIENT_STORAGE = 507;

    /**
     * 508 Loop Detected
     */
    public static final int LOOP_DETECTED = 508;

    /**
     * 510 Not Extended
     */
    public static final int NOT_EXTENDED = 510;

    /**
     * 511 Network Authentication Required
     */
    public static final int NETWORK_AUTHENTICATION_REQUIRED = 511;
}
