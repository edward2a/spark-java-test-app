# spark-java-test-app

This is a simple java app to test connectivity to URL endpoints.

## Usage
Make sure the claspath in the jar has priority over local libraries.
For spark, the following properties need to be set:
```
spark.executor.userClassPathFirst = true

```

```
java -cp simple-project-1.0-jar-with-dependencies.jar \
    -Dconfig.location="gs://<bucket>/<config.json>" \
    SimpleApp
```

## Example
```
user@cluster-c6bd-m:~$ java -cp simple-project-1.0-jar-with-dependencies.jar -Dconfig.location="gs://dataproc-84c76178-e6d6-47b1-b64a-52c7c6e6b791-europe-west2/test-fail.json" SimpleApp; echo $?
URL                             RESULT    REASON
https://dl.google.com           FAIL      Endpoint reachable, HTTP/200
https://www.googleapis.com      PASS      Endpoint reachable, HTTP/404
https://drive.google.com        FAIL      Endpoint reachable, HTTP/200
https://google.com              FAIL      Endpoint reachable, HTTP/200
https://yahoo.com               FAIL      Endpoint reachable, HTTP/200
https://amazon.com              FAIL      Endpoint reachable, HTTP/503
https://x.realreadme.com        PASS      UHX: Host name not resolved
https://127.0.0.100:8443        PASS      IOX: Connection refused (Connection refused)

        ==== TEST RESULT: FAIL ====

1

user@cluster-c6bd-m:~$ java -cp simple-project-1.0-jar-with-dependencies.jar -Dconfig.location="gs://dataproc-84c76178-e6d6-47b1-b64a-52c7c6e6b791-europe-west2/test-pass.json" SimpleApp; echo $?
URL                             RESULT    REASON
https://dl.google.com           PASS      Endpoint reachable, HTTP/200
https://www.googleapis.com      PASS      Endpoint reachable, HTTP/404
https://drive.google.com        PASS      Endpoint reachable, HTTP/200
https://google.com              PASS      Endpoint reachable, HTTP/200
https://yahoo.com               PASS      Endpoint reachable, HTTP/200
https://amazon.com              PASS      Endpoint reachable, HTTP/503
https://x.realreadme.com        PASS      UHX: Host name not resolved
https://127.0.0.100:8443        PASS      IOX: Connection refused (Connection refused)

        ==== TEST RESULT: PASS ====

0

```

## Build
```
mvn package
```
