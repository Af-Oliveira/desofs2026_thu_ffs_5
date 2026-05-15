# ZAP Scanning Report

ZAP by [Checkmarx](https://checkmarx.com/).


## Summary of Alerts

| Risk Level | Number of Alerts |
| --- | --- |
| High | 0 |
| Medium | 0 |
| Low | 5 |
| Informational | 4 |




## Insights

| Level | Reason | Site | Description | Statistic |
| --- | --- | --- | --- | --- |
| Low | Warning |  | ZAP errors logged - see the zap.log file for details | 2    |
| Low | Warning |  | ZAP warnings logged - see the zap.log file for details | 3    |
| Low | Exceeded High | http://host.docker.internal:8080 | Percentage of responses with status code 4xx | 73 % |
| Info | Informational |  | Percentage of network failures | 1 % |
| Info | Informational | http://host.docker.internal:8080 | Percentage of responses with status code 2xx | 1 % |
| Info | Exceeded Low | http://host.docker.internal:8080 | Percentage of responses with status code 5xx | 25 % |
| Info | Informational | http://host.docker.internal:8080 | Percentage of endpoints with content type application/json | 99 % |
| Info | Informational | http://host.docker.internal:8080 | Percentage of endpoints with content type text/plain | 1 % |
| Info | Informational | http://host.docker.internal:8080 | Percentage of endpoints with method GET | 45 % |
| Info | Informational | http://host.docker.internal:8080 | Percentage of endpoints with method POST | 52 % |
| Info | Informational | http://host.docker.internal:8080 | Percentage of endpoints with method PUT | 1 % |
| Info | Informational | http://host.docker.internal:8080 | Count of total endpoints | 661    |




## Alerts

| Name | Risk Level | Number of Instances |
| --- | --- | --- |
| A Server Error response code was returned by the server | Low | 47 |
| Application Error Disclosure | Low | 1 |
| Cross-Origin-Resource-Policy Header Missing or Invalid | Low | 4 |
| Information Disclosure - Debug Error Messages | Low | 1 |
| Unexpected Content-Type was returned | Low | 5 |
| A Client Error response code was returned by the server | Informational | 614 |
| Authentication Request Identified | Informational | 2 |
| Non-Storable Content | Informational | Systemic |
| User Agent Fuzzer | Informational | Systemic |




## Alert Detail



### [ A Server Error response code was returned by the server ](https://www.zaproxy.org/docs/alerts/100000/)



##### Low (High)

### Description

A response code of 500 was returned by the server.
This may indicate that the application is failing to handle unexpected input correctly.
Raised by the 'Alert on HTTP Response Code Error' script

* URL: http://host.docker.internal:8080/api/health/
  * Node Name: `http://host.docker.internal:8080/api/health/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/.env
  * Node Name: `http://host.docker.internal:8080/api/health/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/health/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/2381376295263367022
  * Node Name: `http://host.docker.internal:8080/api/health/2381376295263367022`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/ping/
  * Node Name: `http://host.docker.internal:8080/api/health/ping/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/health/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public
  * Node Name: `http://host.docker.internal:8080/api/public`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/public (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/public (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/
  * Node Name: `http://host.docker.internal:8080/api/public/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/.env
  * Node Name: `http://host.docker.internal:8080/api/public/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/public/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/257033700894497461
  * Node Name: `http://host.docker.internal:8080/api/public/257033700894497461`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/info/
  * Node Name: `http://host.docker.internal:8080/api/public/info/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/public/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/swagger-ui/
  * Node Name: `http://host.docker.internal:8080/swagger-ui/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/api-docs/
  * Node Name: `http://host.docker.internal:8080/v3/api-docs/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/login (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/login (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/register (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/register (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health
  * Node Name: `http://host.docker.internal:8080/api/health ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/health (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/health (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/ping
  * Node Name: `http://host.docker.internal:8080/api/health/ping ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/ping%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/health/ping (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/ping%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/health/ping (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public
  * Node Name: `http://host.docker.internal:8080/api/public ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/public (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/public (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/info
  * Node Name: `http://host.docker.internal:8080/api/public/info ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/info%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/public/info (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/info%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/public/info (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/telemetry (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/telemetry (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks
  * Node Name: `http://host.docker.internal:8080/api/webhooks ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/webhooks (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/webhooks (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment/
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment/ ()("John Doe")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/api-docs
  * Node Name: `http://host.docker.internal:8080/v3/api-docs ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/api-docs%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/v3/api-docs (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/api-docs%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/v3/api-docs (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `500`
  * Other Info: ``


Instances: 47

### Solution



### Reference



#### CWE Id: [ 388 ](https://cwe.mitre.org/data/definitions/388.html)


#### WASC Id: 20

#### Source ID: 4

### [ Application Error Disclosure ](https://www.zaproxy.org/docs/alerts/90022/)



##### Low (Medium)

### Description

This page contains an error/warning message that may disclose sensitive information like the location of the file that produced the unhandled exception. This information can be used to launch further attacks against the web application. The alert could be a false positive if the error message is found inside a documentation page.

* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `HTTP/1.1 500`
  * Other Info: ``


Instances: 1

### Solution

Review the source code of this page. Implement custom error pages. Consider implementing a mechanism to provide a unique error reference/identifier to the client (browser) while logging the details on the server side and not exposing them to the user.

### Reference



#### CWE Id: [ 550 ](https://cwe.mitre.org/data/definitions/550.html)


#### WASC Id: 13

#### Source ID: 3

### [ Cross-Origin-Resource-Policy Header Missing or Invalid ](https://www.zaproxy.org/docs/alerts/90004/)



##### Low (Medium)

### Description

Cross-Origin-Resource-Policy header is an opt-in header designed to counter side-channels attacks like Spectre. Resource should be specifically set as shareable amongst different origins.

* URL: http://host.docker.internal:8080/api/health
  * Node Name: `http://host.docker.internal:8080/api/health`
  * Method: `GET`
  * Parameter: `Cross-Origin-Resource-Policy`
  * Attack: ``
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health/ping
  * Node Name: `http://host.docker.internal:8080/api/health/ping`
  * Method: `GET`
  * Parameter: `Cross-Origin-Resource-Policy`
  * Attack: ``
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/public/info
  * Node Name: `http://host.docker.internal:8080/api/public/info`
  * Method: `GET`
  * Parameter: `Cross-Origin-Resource-Policy`
  * Attack: ``
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/api-docs
  * Node Name: `http://host.docker.internal:8080/v3/api-docs`
  * Method: `GET`
  * Parameter: `Cross-Origin-Resource-Policy`
  * Attack: ``
  * Evidence: ``
  * Other Info: ``


Instances: 4

### Solution

Ensure that the application/web server sets the Cross-Origin-Resource-Policy header appropriately, and that it sets the Cross-Origin-Resource-Policy header to 'same-origin' for all web pages.
'same-site' is considered as less secured and should be avoided.
If resources must be shared, set the header to 'cross-origin'.
If possible, ensure that the end user uses a standards-compliant and modern web browser that supports the Cross-Origin-Resource-Policy header (https://caniuse.com/mdn-http_headers_cross-origin-resource-policy).

### Reference


* [ https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Embedder-Policy ](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Cross-Origin-Embedder-Policy)


#### CWE Id: [ 693 ](https://cwe.mitre.org/data/definitions/693.html)


#### WASC Id: 14

#### Source ID: 3

### [ Information Disclosure - Debug Error Messages ](https://www.zaproxy.org/docs/alerts/10023/)



##### Low (Medium)

### Description

The response appeared to contain common error messages returned by platforms such as ASP.NET, and Web-servers such as IIS and Apache. You can configure the list of common debug messages.

* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `Internal Server Error`
  * Other Info: ``


Instances: 1

### Solution

Disable debugging messages before pushing to production.

### Reference



#### CWE Id: [ 1295 ](https://cwe.mitre.org/data/definitions/1295.html)


#### WASC Id: 13

#### Source ID: 3

### [ Unexpected Content-Type was returned ](https://www.zaproxy.org/docs/alerts/100001/)



##### Low (High)

### Description

A Content-Type of text/html was returned by the server.
This is not one of the types expected to be returned by an API.
Raised by the 'Alert on Unexpected Content Types' script

* URL: http://host.docker.internal:8080%3Faaa=bbb
  * Node Name: `http://host.docker.internal:8080 (aaa)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `text/html`
  * Other Info: ``
* URL: http://host.docker.internal:8080%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080 (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `text/html`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health
  * Node Name: `http://host.docker.internal:8080/api/health`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `text/html`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/health%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/health (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `text/html`
  * Other Info: ``
* URL: http://host.docker.internal:8080/swagger-ui/index.html
  * Node Name: `http://host.docker.internal:8080/swagger-ui/index.html`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `text/html`
  * Other Info: ``


Instances: 5

### Solution



### Reference




#### Source ID: 4

### [ A Client Error response code was returned by the server ](https://www.zaproxy.org/docs/alerts/100000/)



##### Informational (High)

### Description

A response code of 401 was returned by the server.
This may indicate that the application is failing to handle unexpected input correctly.
Raised by the 'Alert on HTTP Response Code Error' script

* URL: http://host.docker.internal:8080
  * Node Name: `http://host.docker.internal:8080`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080%3Faaa=bbb
  * Node Name: `http://host.docker.internal:8080 (aaa)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `400`
  * Other Info: ``
* URL: http://host.docker.internal:8080%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080 (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `400`
  * Other Info: ``
* URL: http://host.docker.internal:8080/
  * Node Name: `http://host.docker.internal:8080/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/ (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.DS_Store
  * Node Name: `http://host.docker.internal:8080/.DS_Store`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/._darcs
  * Node Name: `http://host.docker.internal:8080/._darcs`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.bzr
  * Node Name: `http://host.docker.internal:8080/.bzr`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.env
  * Node Name: `http://host.docker.internal:8080/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.git/config
  * Node Name: `http://host.docker.internal:8080/.git/config`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.hg
  * Node Name: `http://host.docker.internal:8080/.hg`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.htaccess
  * Node Name: `http://host.docker.internal:8080/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.idea/WebServers.xml
  * Node Name: `http://host.docker.internal:8080/.idea/WebServers.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.php_cs.cache
  * Node Name: `http://host.docker.internal:8080/.php_cs.cache`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.ssh/id_dsa
  * Node Name: `http://host.docker.internal:8080/.ssh/id_dsa`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.ssh/id_rsa
  * Node Name: `http://host.docker.internal:8080/.ssh/id_rsa`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.svn/entries
  * Node Name: `http://host.docker.internal:8080/.svn/entries`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.svn/wc.db
  * Node Name: `http://host.docker.internal:8080/.svn/wc.db`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/.zap4108915357069805325
  * Node Name: `http://host.docker.internal:8080/.zap4108915357069805325`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/8370182327408144813
  * Node Name: `http://host.docker.internal:8080/8370182327408144813`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/BitKeeper
  * Node Name: `http://host.docker.internal:8080/BitKeeper`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/CHANGELOG.txt
  * Node Name: `http://host.docker.internal:8080/CHANGELOG.txt`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/CVS/root
  * Node Name: `http://host.docker.internal:8080/CVS/root`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/DEADJOE
  * Node Name: `http://host.docker.internal:8080/DEADJOE`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/FileZilla.xml
  * Node Name: `http://host.docker.internal:8080/FileZilla.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/WEB-INF/applicationContext.xml
  * Node Name: `http://host.docker.internal:8080/WEB-INF/applicationContext.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/WEB-INF/web.xml
  * Node Name: `http://host.docker.internal:8080/WEB-INF/web.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/WS_FTP.INI
  * Node Name: `http://host.docker.internal:8080/WS_FTP.INI`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/WS_FTP.ini
  * Node Name: `http://host.docker.internal:8080/WS_FTP.ini`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/WinSCP.ini
  * Node Name: `http://host.docker.internal:8080/WinSCP.ini`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/_framework/blazor.boot.json
  * Node Name: `http://host.docker.internal:8080/_framework/blazor.boot.json`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/_wpeprivate/config.json
  * Node Name: `http://host.docker.internal:8080/_wpeprivate/config.json`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/adminer.php
  * Node Name: `http://host.docker.internal:8080/adminer.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api
  * Node Name: `http://host.docker.internal:8080/api`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api-docs
  * Node Name: `http://host.docker.internal:8080/api-docs`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/
  * Node Name: `http://host.docker.internal:8080/api/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/.env
  * Node Name: `http://host.docker.internal:8080/api/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/8631125112325178555
  * Node Name: `http://host.docker.internal:8080/api/8631125112325178555`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin
  * Node Name: `http://host.docker.internal:8080/api/admin`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/
  * Node Name: `http://host.docker.internal:8080/api/admin/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/.env
  * Node Name: `http://host.docker.internal:8080/api/admin/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/admin/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/9198919339218057572
  * Node Name: `http://host.docker.internal:8080/api/admin/9198919339218057572`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard/
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations
  * Node Name: `http://host.docker.internal:8080/api/admin/operations`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/operations (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/operations (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/.env
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/3786537755933161435
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/3786537755933161435`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/.env
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/9170411281754432289
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/9170411281754432289`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports
  * Node Name: `http://host.docker.internal:8080/api/admin/reports`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/reports (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/reports (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports/
  * Node Name: `http://host.docker.internal:8080/api/admin/reports/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/admin/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users
  * Node Name: `http://host.docker.internal:8080/api/admin/users`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users
  * Node Name: `http://host.docker.internal:8080/api/admin/users ()({email,password,name,role})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/users (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/users (class.module.classLoader.DefaultAssertio...)({email,password,name,role})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/users (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/
  * Node Name: `http://host.docker.internal:8080/api/admin/users/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/.env
  * Node Name: `http://host.docker.internal:8080/api/admin/users/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/admin/users/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 ()({name,role,accountStatus})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 (class.module.classLoader.DefaultAssertio...)({name,role,accountStatus})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/1694147732625781795
  * Node Name: `http://host.docker.internal:8080/api/admin/users/1694147732625781795`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/admin/users/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth
  * Node Name: `http://host.docker.internal:8080/api/auth`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/auth (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/
  * Node Name: `http://host.docker.internal:8080/api/auth/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/.env
  * Node Name: `http://host.docker.internal:8080/api/auth/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/auth/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/2717976256281667157
  * Node Name: `http://host.docker.internal:8080/api/auth/2717976256281667157`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims
  * Node Name: `http://host.docker.internal:8080/api/auth/claims`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/auth/claims (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth/claims (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims/
  * Node Name: `http://host.docker.internal:8080/api/auth/claims/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login
  * Node Name: `http://host.docker.internal:8080/api/auth/login`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login
  * Node Name: `http://host.docker.internal:8080/api/auth/login ()({email,password})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth/login (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me
  * Node Name: `http://host.docker.internal:8080/api/auth/me`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/auth/me (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth/me (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me/
  * Node Name: `http://host.docker.internal:8080/api/auth/me/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/.env
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/1885809278398607812
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/1885809278398607812`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify ()({email,code})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify (class.module.classLoader.DefaultAssertio...)({email,code})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register
  * Node Name: `http://host.docker.internal:8080/api/auth/register`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register
  * Node Name: `http://host.docker.internal:8080/api/auth/register ()({email,password,name})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/auth/register (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/auth/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines
  * Node Name: `http://host.docker.internal:8080/api/machines`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines
  * Node Name: `http://host.docker.internal:8080/api/machines ()({code,location})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines (class.module.classLoader.DefaultAssertio...)({code,location})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/machines (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/
  * Node Name: `http://host.docker.internal:8080/api/machines/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/.env
  * Node Name: `http://host.docker.internal:8080/api/machines/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/machines/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10
  * Node Name: `http://host.docker.internal:8080/api/machines/10`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10
  * Node Name: `http://host.docker.internal:8080/api/machines/10 ()({id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines/10 (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines/10 (class.module.classLoader.DefaultAssertio...)({id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/machines/10 (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/
  * Node Name: `http://host.docker.internal:8080/api/machines/10/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/.env
  * Node Name: `http://host.docker.internal:8080/api/machines/10/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/machines/10/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/1345466352309466160
  * Node Name: `http://host.docker.internal:8080/api/machines/10/1345466352309466160`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/.env
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10 (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10 (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/.env
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/8570293743019471675
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/8570293743019471675`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock ()({quantity})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock (class.module.classLoader.DefaultAssertio...)({quantity})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/7232747624688544395
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/7232747624688544395`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/machines/10/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/2747359453721605373
  * Node Name: `http://host.docker.internal:8080/api/machines/2747359453721605373`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code
  * Node Name: `http://host.docker.internal:8080/api/machines/code`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/machines/code (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/machines/code (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code/
  * Node Name: `http://host.docker.internal:8080/api/machines/code/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/machines/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products
  * Node Name: `http://host.docker.internal:8080/api/products`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products
  * Node Name: `http://host.docker.internal:8080/api/products ()(multipart:image)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/products (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3Fname=ZAP&description=Zaproxy%2520alias%2520impedit%2520expedita%2520quisquam%2520pariatur%2520exercitationem.%2520Nemo%2520rerum%2520eveniet%2520dolores%2520rem%2520quia%2520dignissimos.&price=1.2&sku=sku&class.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/products (class.module.classLoader.DefaultAssertio...,description,name,price,sku)(multipart:image)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3Fname=ZAP&description=Zaproxy%2520alias%2520impedit%2520expedita%2520quisquam%2520pariatur%2520exercitationem.%2520Nemo%2520rerum%2520eveniet%2520dolores%2520rem%2520quia%2520dignissimos.&price=1.2&sku=sku
  * Node Name: `http://host.docker.internal:8080/api/products (description,name,price,sku)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/products (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/
  * Node Name: `http://host.docker.internal:8080/api/products/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/.env
  * Node Name: `http://host.docker.internal:8080/api/products/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/products/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10
  * Node Name: `http://host.docker.internal:8080/api/products/10`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10
  * Node Name: `http://host.docker.internal:8080/api/products/10 ()({name,description,price,active})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/products/10 (class.module.classLoader.DefaultAssertio...)({name,description,price,active})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/products/10 (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/2353703956369538044
  * Node Name: `http://host.docker.internal:8080/api/products/2353703956369538044`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku
  * Node Name: `http://host.docker.internal:8080/api/products/sku`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/products/sku (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/products/sku (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku/
  * Node Name: `http://host.docker.internal:8080/api/products/sku/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/products/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales
  * Node Name: `http://host.docker.internal:8080/api/sales`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/sales (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/sales (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/
  * Node Name: `http://host.docker.internal:8080/api/sales/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/.env
  * Node Name: `http://host.docker.internal:8080/api/sales/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/sales/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/1824228719092253680
  * Node Name: `http://host.docker.internal:8080/api/sales/1824228719092253680`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine
  * Node Name: `http://host.docker.internal:8080/api/sales/machine`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/sales/machine (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/sales/machine (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/.env
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10 (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10 (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10/
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/806154749102721589
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/806154749102721589`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me
  * Node Name: `http://host.docker.internal:8080/api/sales/me`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/sales/me (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/sales/me (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me/
  * Node Name: `http://host.docker.internal:8080/api/sales/me/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase ()({productId,machineId,paymentToken,idempotencyKey})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase (class.module.classLoader.DefaultAssertio...)({productId,machineId,paymentToken,idempotencyKey})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/sales/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/telemetry (class.module.classLoader.DefaultAssertio...)({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/telemetry (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks
  * Node Name: `http://host.docker.internal:8080/api/webhooks`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/webhooks (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/webhooks (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/
  * Node Name: `http://host.docker.internal:8080/api/webhooks/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/.env
  * Node Name: `http://host.docker.internal:8080/api/webhooks/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/.htaccess
  * Node Name: `http://host.docker.internal:8080/api/webhooks/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/8768360951932161637
  * Node Name: `http://host.docker.internal:8080/api/webhooks/8768360951932161637`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe")`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment (class.module.classLoader.DefaultAssertio...)("John Doe")`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/trace.axd
  * Node Name: `http://host.docker.internal:8080/api/webhooks/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/app/etc/local.xml
  * Node Name: `http://host.docker.internal:8080/app/etc/local.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/composer.json
  * Node Name: `http://host.docker.internal:8080/composer.json`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/composer.lock
  * Node Name: `http://host.docker.internal:8080/composer.lock`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/config/database.yml
  * Node Name: `http://host.docker.internal:8080/config/database.yml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/config/databases.yml
  * Node Name: `http://host.docker.internal:8080/config/databases.yml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/core
  * Node Name: `http://host.docker.internal:8080/core`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/docs/
  * Node Name: `http://host.docker.internal:8080/docs/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/elmah.axd
  * Node Name: `http://host.docker.internal:8080/elmah.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/favicon.ico
  * Node Name: `http://host.docker.internal:8080/favicon.ico`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/filezilla.xml
  * Node Name: `http://host.docker.internal:8080/filezilla.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/i.php
  * Node Name: `http://host.docker.internal:8080/i.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/id_dsa
  * Node Name: `http://host.docker.internal:8080/id_dsa`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/id_rsa
  * Node Name: `http://host.docker.internal:8080/id_rsa`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/info.php
  * Node Name: `http://host.docker.internal:8080/info.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/key.pem
  * Node Name: `http://host.docker.internal:8080/key.pem`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/lfm.php
  * Node Name: `http://host.docker.internal:8080/lfm.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/myserver.key
  * Node Name: `http://host.docker.internal:8080/myserver.key`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/openapi.json
  * Node Name: `http://host.docker.internal:8080/openapi.json`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/openapi.yaml
  * Node Name: `http://host.docker.internal:8080/openapi.yaml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/phpinfo.php
  * Node Name: `http://host.docker.internal:8080/phpinfo.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/privatekey.key
  * Node Name: `http://host.docker.internal:8080/privatekey.key`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/server-info
  * Node Name: `http://host.docker.internal:8080/server-info`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/server-status
  * Node Name: `http://host.docker.internal:8080/server-status`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/server.key
  * Node Name: `http://host.docker.internal:8080/server.key`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/sftp-config.json
  * Node Name: `http://host.docker.internal:8080/sftp-config.json`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/sitemanager.xml
  * Node Name: `http://host.docker.internal:8080/sitemanager.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/sites/default/files/.ht.sqlite
  * Node Name: `http://host.docker.internal:8080/sites/default/files/.ht.sqlite`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/sites/default/private/files/backup_migrate/scheduled/test.txt
  * Node Name: `http://host.docker.internal:8080/sites/default/private/files/backup_migrate/scheduled/test.txt`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/swagger
  * Node Name: `http://host.docker.internal:8080/swagger`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/swagger.json
  * Node Name: `http://host.docker.internal:8080/swagger.json`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/swagger.yaml
  * Node Name: `http://host.docker.internal:8080/swagger.yaml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/swagger/ui/index.html
  * Node Name: `http://host.docker.internal:8080/swagger/ui/index.html`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/test.php
  * Node Name: `http://host.docker.internal:8080/test.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/trace.axd
  * Node Name: `http://host.docker.internal:8080/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v2/api-docs
  * Node Name: `http://host.docker.internal:8080/v2/api-docs`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3
  * Node Name: `http://host.docker.internal:8080/v3`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3%3Fclass.module.classLoader.DefaultAssertionStatus=nonsense
  * Node Name: `http://host.docker.internal:8080/v3 (class.module.classLoader.DefaultAssertio...)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3%3Fname=abc
  * Node Name: `http://host.docker.internal:8080/v3 (name)`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/
  * Node Name: `http://host.docker.internal:8080/v3/`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/.env
  * Node Name: `http://host.docker.internal:8080/v3/.env`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/.htaccess
  * Node Name: `http://host.docker.internal:8080/v3/.htaccess`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/3618685519382618388
  * Node Name: `http://host.docker.internal:8080/v3/3618685519382618388`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3/trace.axd
  * Node Name: `http://host.docker.internal:8080/v3/trace.axd`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/vb_test.php
  * Node Name: `http://host.docker.internal:8080/vb_test.php`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/vim_settings.xml
  * Node Name: `http://host.docker.internal:8080/vim_settings.xml`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/winscp.ini
  * Node Name: `http://host.docker.internal:8080/winscp.ini`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/ws_ftp.ini
  * Node Name: `http://host.docker.internal:8080/ws_ftp.ini`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/zap8777415140675354096
  * Node Name: `http://host.docker.internal:8080/zap8777415140675354096`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080
  * Node Name: `http://host.docker.internal:8080 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/ (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/ (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api
  * Node Name: `http://host.docker.internal:8080/api ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin
  * Node Name: `http://host.docker.internal:8080/api/admin ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/dashboard%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/dashboard (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations
  * Node Name: `http://host.docker.internal:8080/api/admin/operations ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup ()(multipart:1,0)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/backup/
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/backup/`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/operations/reports/sales/
  * Node Name: `http://host.docker.internal:8080/api/admin/operations/reports/sales/`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports
  * Node Name: `http://host.docker.internal:8080/api/admin/reports ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/reports (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/reports%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/reports (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users
  * Node Name: `http://host.docker.internal:8080/api/admin/users ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users
  * Node Name: `http://host.docker.internal:8080/api/admin/users ()({email,password,name,role})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/users (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/users (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/
  * Node Name: `http://host.docker.internal:8080/api/admin/users/ ()({email,password,name,role})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth
  * Node Name: `http://host.docker.internal:8080/api/auth ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims
  * Node Name: `http://host.docker.internal:8080/api/auth/claims ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/claims (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/claims%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/claims (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login
  * Node Name: `http://host.docker.internal:8080/api/auth/login ()({email,password})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `400`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login
  * Node Name: `http://host.docker.internal:8080/api/auth/login ()({email,password})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/login/
  * Node Name: `http://host.docker.internal:8080/api/auth/login/ ()({email,password})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me
  * Node Name: `http://host.docker.internal:8080/api/auth/me ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/me (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/me%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/me (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify ()({email,code})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `400`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify ()({email,code})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify/
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify/ ()({email,code})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register
  * Node Name: `http://host.docker.internal:8080/api/auth/register ()({email,password,name})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `400`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register/
  * Node Name: `http://host.docker.internal:8080/api/auth/register/ ()({email,password,name})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines
  * Node Name: `http://host.docker.internal:8080/api/machines ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines
  * Node Name: `http://host.docker.internal:8080/api/machines ()({code,location})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/
  * Node Name: `http://host.docker.internal:8080/api/machines/ ()({code,location})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10
  * Node Name: `http://host.docker.internal:8080/api/machines/10 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code
  * Node Name: `http://host.docker.internal:8080/api/machines/code ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/code (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/code%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/machines/code (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products
  * Node Name: `http://host.docker.internal:8080/api/products ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/products (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/products (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3Fname=ZAP&description=Zaproxy%2520alias%2520impedit%2520expedita%2520quisquam%2520pariatur%2520exercitationem.%2520Nemo%2520rerum%2520eveniet%2520dolores%2520rem%2520quia%2520dignissimos.&price=1.2&sku=sku
  * Node Name: `http://host.docker.internal:8080/api/products (description,name,price,sku)(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products%3Fname=ZAP&description=Zaproxy%2520alias%2520impedit%2520expedita%2520quisquam%2520pariatur%2520exercitationem.%2520Nemo%2520rerum%2520eveniet%2520dolores%2520rem%2520quia%2520dignissimos.&price=1.2&sku=sku
  * Node Name: `http://host.docker.internal:8080/api/products (description,name,price,sku)(multipart:image)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/
  * Node Name: `http://host.docker.internal:8080/api/products/ ()(multipart:image)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10
  * Node Name: `http://host.docker.internal:8080/api/products/10 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/products/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/products/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku
  * Node Name: `http://host.docker.internal:8080/api/products/sku ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/products/sku (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/sku%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/products/sku (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales
  * Node Name: `http://host.docker.internal:8080/api/sales ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine
  * Node Name: `http://host.docker.internal:8080/api/sales/machine ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/machine (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/machine (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/machine/10%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/machine/10 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me
  * Node Name: `http://host.docker.internal:8080/api/sales/me ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/me (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/me%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/me (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase ()({productId,machineId,paymentToken,idempotencyKey})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase/
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase/ ()({productId,machineId,paymentToken,idempotencyKey})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry/
  * Node Name: `http://host.docker.internal:8080/api/telemetry/ ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("#set($engine=\"\")\n#set($proc=$engine.getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"sleep 15\"))\n#set($null=$proc.waitFor())\n${null}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("#{%x(sleep 15)}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("#{global.process.mainModule.require('child_process').execSync('sleep 15').toString()}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("${@print(chr(122).chr(97).chr(112).chr(95).chr(116).chr(111).chr(107).chr(101).chr(110))}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("${@print(chr(122).chr(97).chr(112).chr(95).chr(116).chr(111).chr(107).chr(101).chr(110))}\\")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("${__import__(\"subprocess\").check_output(\"sleep 15\", shell=True)}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'(")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'; select \"java.lang.Thread.sleep\"(15000) from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = 'SYSTEM_COLUMNS' and COLUMN_NAME = 'TABLE_NAME' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("';print(chr(122).chr(97).chr(112).chr(95).chr(116).chr(111).chr(107).chr(101).chr(110));$var='")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'\"<img src=x onerror=prompt()>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'\"<scrIpt>alert(1);</scRipt>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'\"\u0000<scrIpt>alert(1);</scRipt>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("'case when cast(pg_sleep(15.0) as varchar) > '' then 0 else 1 end -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("() { :;}; /bin/sleep 15")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("() { :;}; echo 'x-powered-by: ShellShock-Vulnerable'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("(DBMS_SESSION.SLEEP(15))")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("); select \"java.lang.Thread.sleep\"(15000) from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = 'SYSTEM_COLUMNS' and COLUMN_NAME = 'TABLE_NAME' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("+response.write({0}*{1})+")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("../../../../../../../../../../../../../../../../")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("../../../../../../../../../../../../../../../../Windows/system.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("../../../../../../../../../../../../../../../../etc/passwd")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\Windows\\system.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("/")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("/WEB-INF/web.xml")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("/etc/passwd")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("/payment")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("0W45pz4p")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("5;URL='https://6361273244174470114.owasp.org'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("5;URL='https://6361273244174470114.owasp.org/?John Doe'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("6361273244174470114.owasp.org")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("; select \"java.lang.Thread.sleep\"(15000) from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = 'SYSTEM_COLUMNS' and COLUMN_NAME = 'TABLE_NAME' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()(";")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()(";print(chr(122).chr(97).chr(112).chr(95).chr(116).chr(111).chr(107).chr(101).chr(110));")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<!--")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<!--#EXEC cmd=\"dir \\\"-->")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<!--#EXEC cmd=\"ls /\"-->")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<#assign ex=\"freemarker.template.utility.Execute\"?new()> ${ ex(\"sleep 15\") }")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<%= global.process.mainModule.require('child_process').execSync('sleep 15').toString()%>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<%=%x(sleep 15)%>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<xsl:value-of select=\"document('http://host.docker.internal:22')\"/>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<xsl:value-of select=\"php:function('exec','erroneous_command 2>&amp;1')\"/>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<xsl:value-of select=\"system-property('xsl:vendor')\"/>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<xsl:value-of select=\"system-property('xsl:vendor')\"/><!--")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("<xsl:variable name=\"rtobject\" select=\"runtime:getRuntime()\"/>\n<xsl:variable name=\"process\" select=\"runtime:exec($rtobject,'erroneous_command')\"/>\n<xsl:variable name=\"waiting\" select=\"process:waitFor($process)\"/>\n<xsl:value-of select=\"$process\"/>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe / (DBMS_SESSION.SLEEP(15)) ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe / \"java.lang.Thread.sleep\"(15000) ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe / case when cast(pg_sleep(15.0) as varchar) > '' then 0 else 1 end ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe / sleep(15) ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe AND 1=1 -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe AND 1=2 -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe OR 1=1 -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe UNION ALL select NULL -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe and 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe and exists ( select \"java.lang.Thread.sleep\"(15000) from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = 'SYSTEM_COLUMNS' and COLUMN_NAME = 'TABLE_NAME') -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe and exists (DBMS_SESSION.SLEEP(15)) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe or 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe where 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe&cat /etc/passwd&")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe&sleep 15.0&")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe&timeout /T 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe&type %SYSTEMROOT%\\win.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' / (DBMS_SESSION.SLEEP(15)) / '")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' / \"java.lang.Thread.sleep\"(15000) / '")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' / sleep(15) / '")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' AND '1'='1' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' AND '1'='2' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' OR '1'='1' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' UNION ALL select NULL -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' and 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' and exists ( select \"java.lang.Thread.sleep\"(15000) from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = 'SYSTEM_COLUMNS' and COLUMN_NAME = 'TABLE_NAME') -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe' where 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'&cat /etc/passwd&'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'&sleep 15.0&'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'&timeout /T 15.0&'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'&type %SYSTEMROOT%\\win.ini&'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'(")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe') UNION ALL select NULL -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe';cat /etc/passwd;'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe';get-help")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe';sleep 15.0;'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe';start-sleep -s 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'|timeout /T 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe'|type %SYSTEMROOT%\\win.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe) ' WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe) UNION ALL select NULL -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe) WAITFOR DELAY '0:0:15' (")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe) WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe) \" WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe)) ' WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe)) WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe)) \" WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe0W45pz4p")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;cat /etc/passwd;")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;get-help #")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;get-help")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;sleep 15.0;")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;start-sleep -s 15.0 #")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe;start-sleep -s 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" / (DBMS_SESSION.SLEEP(15)) / \"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" / \"java.lang.Thread.sleep\"(15000) / \"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" / sleep(15) / \"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" UNION ALL select NULL -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" WAITFOR DELAY '0:0:15' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" and 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\" where 0 in (select sleep(15) ) -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"&cat /etc/passwd&\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"&sleep 15.0&\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"&timeout /T 15.0&\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"&type %SYSTEMROOT%\\win.ini&\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\";cat /etc/passwd;\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\";get-help")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\";sleep 15.0;\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\";start-sleep -s 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"|timeout /T 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe\"|type %SYSTEMROOT%\\win.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe|timeout /T 15.0")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe|type %SYSTEMROOT%\\win.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("Set-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("URL='http://6361273244174470114.owasp.org'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("WEB-INF/web.xml")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("WEB-INF\\web.xml")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("ZAP %1!s%2!s%3!s%4!s%5!s%6!s%7!s%8!s%9!s%10!s%11!s%12!s%13!s%14!s%15!s%16!s%17!s%18!s%19!s%20!s%21!n%22!n%23!n%24!n%25!n%26!n%27!n%28!n%29!n%30!n%31!n%32!n%33!n%34!n%35!n%36!n%37!n%38!n%39!n%40!n\n")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("ZAP")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("ZAP%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s%n%s\n")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"'")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"+response.write(614,339*514,114)+\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"/><xsl:value-of select=\"system-property('xsl:vendor')\"/><!--")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"; select \"java.lang.Thread.sleep\"(15000) from INFORMATION_SCHEMA.SYSTEM_COLUMNS where TABLE_NAME = 'SYSTEM_COLUMNS' and COLUMN_NAME = 'TABLE_NAME' -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\";print(chr(122).chr(97).chr(112).chr(95).chr(116).chr(111).chr(107).chr(101).chr(110));$var=\"")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"><!--#EXEC cmd=\"dir \\\"--><")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"><!--#EXEC cmd=\"ls /\"--><")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"case when cast(pg_sleep(15.0) as varchar) > '' then 0 else 1 end -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\"java.lang.Thread.sleep\"(15000)")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\\WEB-INF\\web.xml")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("\\payment")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("]]>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("any?\nSet-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("any?\r\nSet-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("any?\r\nSet-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8\r\n")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("any\nSet-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("any\r\nSet-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("any\r\nSet-cookie: Tamper=d8df88b7-f957-4d4c-a599-d65a4fb61fb8\r\n")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("c:/")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("c:/Windows/system.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("c:\\")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("c:\\Windows\\system.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("case when cast(pg_sleep(15.0) as varchar) > '' then 0 else 1 end -- ")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("case when cast(pg_sleep(15.0) as varchar) > '' then 0 else 1 end")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("cat /etc/passwd")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("get-help")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("http://\\6361273244174470114.owasp.org")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("http://www.google.com")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("http://www.google.com/")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("http://www.google.com/search?q=ZAP")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("http://www.google.com:80/")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("http://www.google.com:80/search?q=ZAP")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("https://6361273244174470114%2eowasp%2eorg")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("https://6361273244174470114.owasp.org")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("https://6361273244174470114.owasp.org/?John Doe")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("https://\\6361273244174470114.owasp.org")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("payment")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("response.write(614,339*514,114)")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("system-property('xsl:vendor')/>")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("thishouldnotexistandhopefullyitwillnot")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("type %SYSTEMROOT%\\win.ini")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("www.google.com")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("www.google.com/")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("www.google.com/search?q=ZAP")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("www.google.com:80/")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("www.google.com:80/search?q=ZAP")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("xZfyQZRTnywwtmTMwuqEBXOWajXmMLyWwbnyHgcuycexqBRBbDwrmieddUKHRbNELKcggtteQmkjOmnqgtwQgGKGRiRLeetIlJcUhVNGqysprUUeDKWxgwevhmVEwBbxINsMtfTdeaSFPZHhqOEmbmDbhFsteMUBKSDFMBVZaRJIrZsWfGddfCyhgSRKTHXtosnKiOkYPKYHeZTHdRXZHTwMrgZoFsPqyfaxqnHbTJtOvrpAMuxsqxtxMqJpKmjCbVgCvCTpbhGKGAbYmkBUmcQDtBSvjmmIEPIDdIEoVDvLfrsyjKNxPBikfFJjvVTxIxvcFfOHOUQZwfFOLrNdxLZRVpVwcYiMhoBuRqspJrsnoVDJVNBmbwwLjOIfHqABBtHUvWqLHDetMVBuRILDJRiuFbprlTwWquHicDAhrcqoKDWPuUKAAZHwNNKGSPdrJsEdlyyCYXUYlmUAPwxaKCcpGwjuPKFphZVRAXYFxvqQWxmvFheYlxwsPRZuFro)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zApPX12sS")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj 2887*4049 zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj#set($x=1640*4977)${x}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj#{3502*7412}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj${2392*7481}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj<%=1331*2704%>zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj<p th:text=\"${3208*1488}\"></p>zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{#3555*9601}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{6964*7006}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{@9752*3876}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{@math key=\"5560\" method=\"multiply\" operand=\"8580\"/}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{{2867*2375}}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{{47900|add:53300}}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{{=4880*4273}}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("zj{{print \"1284\" \"6302\"}}zj")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("{system(\"sleep 15\")}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("{{= global.process.mainModule.require('child_process').execSync('sleep 15').toString() }}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("{{\"\".__class__.__mro__[1].__subclasses__()[157].__repr__.__globals__.get(\"__builtins__\").get(\"__import__\")(\"subprocess\").check_output(\"sleep 15\")}}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("{{__import__(\"subprocess\").check_output(\"sleep 15\", shell=True)}}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("{{range.constructor(\"return eval(\\\"global.process.mainModule.require('child_process').execSync('sleep 15').toString()\\\")\")()}}")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/computeMetadata/v1/
  * Node Name: `http://host.docker.internal:8080/computeMetadata/v1/`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/latest/meta-data/
  * Node Name: `http://host.docker.internal:8080/latest/meta-data/`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/metadata/instance
  * Node Name: `http://host.docker.internal:8080/metadata/instance`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/metadata/v1
  * Node Name: `http://host.docker.internal:8080/metadata/v1`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/opc/v1/instance/
  * Node Name: `http://host.docker.internal:8080/opc/v1/instance/`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/opc/v2/instance/
  * Node Name: `http://host.docker.internal:8080/opc/v2/instance/`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/openstack/latest/meta_data.json
  * Node Name: `http://host.docker.internal:8080/openstack/latest/meta_data.json`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3
  * Node Name: `http://host.docker.internal:8080/v3 ()(class.module.classLoader.DefaultAssertio...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/v3 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('cmd.exe /C echo p5nvc2ecd7mq...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/v3%3F-d+allow_url_include%253d1+-d+auto_prepend_file%253dphp://input
  * Node Name: `http://host.docker.internal:8080/v3 (-d allow_url_include=1 -d auto_prepend_f...)(<?php exec('echo p5nvc2ecd7mqcr5xppd1',$...)`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10 ()({name,role,accountStatus})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/admin/users/10/
  * Node Name: `http://host.docker.internal:8080/api/admin/users/10/ ()({name,role,accountStatus})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10
  * Node Name: `http://host.docker.internal:8080/api/machines/10 ()({id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/
  * Node Name: `http://host.docker.internal:8080/api/machines/10/ ()({id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock ()({quantity})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock/
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock/ ()({quantity})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10
  * Node Name: `http://host.docker.internal:8080/api/products/10 ()({name,description,price,active})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10/
  * Node Name: `http://host.docker.internal:8080/api/products/10/ ()({name,description,price,active})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `401`
  * Other Info: ``


Instances: 614

### Solution



### Reference



#### CWE Id: [ 388 ](https://cwe.mitre.org/data/definitions/388.html)


#### WASC Id: 20

#### Source ID: 4

### [ Authentication Request Identified ](https://www.zaproxy.org/docs/alerts/10111/)



##### Informational (High)

### Description

The given request has been identified as an authentication request. The 'Other Info' field contains a set of key=value lines which identify any relevant fields. If the request is in a context which has an Authentication Method set to "Auto-Detect" then this rule will change the authentication to match the request identified.

* URL: http://host.docker.internal:8080/api/admin/users
  * Node Name: `http://host.docker.internal:8080/api/admin/users ()({email,password,name,role})`
  * Method: `POST`
  * Parameter: `email`
  * Attack: ``
  * Evidence: `password`
  * Other Info: `userParam=email
userValue=zaproxy@example.com
passwordParam=password`
* URL: http://host.docker.internal:8080/api/auth/login
  * Node Name: `http://host.docker.internal:8080/api/auth/login ()({email,password})`
  * Method: `POST`
  * Parameter: `email`
  * Attack: ``
  * Evidence: `password`
  * Other Info: `userParam=email
userValue=zaproxy@example.com
passwordParam=password`


Instances: 2

### Solution

This is an informational alert rather than a vulnerability and so there is nothing to fix.

### Reference


* [ https://www.zaproxy.org/docs/desktop/addons/authentication-helper/auth-req-id/ ](https://www.zaproxy.org/docs/desktop/addons/authentication-helper/auth-req-id/)



#### Source ID: 3

### [ Non-Storable Content ](https://www.zaproxy.org/docs/alerts/10049/)



##### Informational (Medium)

### Description

The response contents are not storable by caching components such as proxy servers. If the response does not contain sensitive, personal or user-specific information, it may benefit from being stored and cached, to improve performance.

* URL: http://host.docker.internal:8080/api/products
  * Node Name: `http://host.docker.internal:8080/api/products`
  * Method: `GET`
  * Parameter: ``
  * Attack: ``
  * Evidence: `no-store`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/sales/purchase
  * Node Name: `http://host.docker.internal:8080/api/sales/purchase ()({productId,machineId,paymentToken,idempotencyKey})`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `no-store`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/webhooks/payment
  * Node Name: `http://host.docker.internal:8080/api/webhooks/payment ()("John Doe")`
  * Method: `POST`
  * Parameter: ``
  * Attack: ``
  * Evidence: `no-store`
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/machines/10/slots/10/restock
  * Node Name: `http://host.docker.internal:8080/api/machines/10/slots/10/restock ()({quantity})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `PUT `
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/products/10
  * Node Name: `http://host.docker.internal:8080/api/products/10 ()({name,description,price,active})`
  * Method: `PUT`
  * Parameter: ``
  * Attack: ``
  * Evidence: `PUT `
  * Other Info: ``

Instances: Systemic


### Solution

The content may be marked as storable by ensuring that the following conditions are satisfied:
The request method must be understood by the cache and defined as being cacheable ("GET", "HEAD", and "POST" are currently defined as cacheable)
The response status code must be understood by the cache (one of the 1XX, 2XX, 3XX, 4XX, or 5XX response classes are generally understood)
The "no-store" cache directive must not appear in the request or response header fields
For caching by "shared" caches such as "proxy" caches, the "private" response directive must not appear in the response
For caching by "shared" caches such as "proxy" caches, the "Authorization" header field must not appear in the request, unless the response explicitly allows it (using one of the "must-revalidate", "public", or "s-maxage" Cache-Control response directives)
In addition to the conditions above, at least one of the following conditions must also be satisfied by the response:
It must contain an "Expires" header field
It must contain a "max-age" response directive
For "shared" caches such as "proxy" caches, it must contain a "s-maxage" response directive
It must contain a "Cache Control Extension" that allows it to be cached
It must have a status code that is defined as cacheable by default (200, 203, 204, 206, 300, 301, 404, 405, 410, 414, 501).

### Reference


* [ https://datatracker.ietf.org/doc/html/rfc7234 ](https://datatracker.ietf.org/doc/html/rfc7234)
* [ https://datatracker.ietf.org/doc/html/rfc7231 ](https://datatracker.ietf.org/doc/html/rfc7231)
* [ https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html ](https://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html)


#### CWE Id: [ 524 ](https://cwe.mitre.org/data/definitions/524.html)


#### WASC Id: 13

#### Source ID: 3

### [ User Agent Fuzzer ](https://www.zaproxy.org/docs/alerts/10104/)



##### Informational (Medium)

### Description

Check for differences in response based on fuzzed User Agent (eg. mobile sites, access as a Search Engine Crawler). Compares the response statuscode and the hashcode of the response body with the original response.

* URL: http://host.docker.internal:8080/api/auth/login
  * Node Name: `http://host.docker.internal:8080/api/auth/login ()({email,password})`
  * Method: `POST`
  * Parameter: `Header User-Agent`
  * Attack: `Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)`
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/mfa/verify
  * Node Name: `http://host.docker.internal:8080/api/auth/mfa/verify ()({email,code})`
  * Method: `POST`
  * Parameter: `Header User-Agent`
  * Attack: `Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)`
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/auth/register
  * Node Name: `http://host.docker.internal:8080/api/auth/register ()({email,password,name})`
  * Method: `POST`
  * Parameter: `Header User-Agent`
  * Attack: `Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)`
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `POST`
  * Parameter: `Header User-Agent`
  * Attack: `Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)`
  * Evidence: ``
  * Other Info: ``
* URL: http://host.docker.internal:8080/api/telemetry
  * Node Name: `http://host.docker.internal:8080/api/telemetry ()({id,machine:{id,code,location,active,status,lastTelemetryAt,createdAt,updatedAt},cpuUsage,memoryUsage,diskUsage,status,uptimeSeconds,totalSalesToday,temperatureCelsius,timestamp})`
  * Method: `POST`
  * Parameter: `Header User-Agent`
  * Attack: `Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)`
  * Evidence: ``
  * Other Info: ``

Instances: Systemic


### Solution



### Reference


* [ https://owasp.org/wstg ](https://owasp.org/wstg)



#### Source ID: 1


