package org.example.moodvine_backend.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okio.ByteString;
import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.utils.ByteArrayMultipartFile;
import org.example.moodvine_backend.utils.R2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class TtsService {

    @Value("${xfyun.tts.host-url}")
    String hostUrl;

    @Value("${xfyun.tts.app-id}")
    String appId;

    @Value("${xfyun.tts.api-key}")
    String apiKey;

    @Value("${xfyun.tts.api-secret}")
    String apiSecret;

    @Value("${xfyun.tts.default-vcn}")
    String defaultVcn;

    @Value("${xfyun.tts.default-tte}")
    String defaultTte;

    @Autowired
    R2Utils r2Utils;

    private static final Gson json = new Gson();

    public ResponseData speech(String text) throws Exception {

        String targetFileName = "audio_" + System.currentTimeMillis() + "_" +
                UUID.randomUUID().toString().substring(0, 8) + ".mp3";

        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        //将url中的 schema http://和https://分别替换为ws:// 和 wss://
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        System.out.println("request url: " + url);
        Request request = new Request.Builder().url(url).build();

        // 创建字节数组输出流来收集音频数据
        ByteArrayOutputStream audioData = new ByteArrayOutputStream();
        final String[] resultUrl = new String[1]; // 用于存储最终URL

        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //发送数据
                JsonObject frame = new JsonObject();
                JsonObject business = new JsonObject();
                JsonObject common = new JsonObject();
                JsonObject data = new JsonObject();
                // 填充common
                common.addProperty("app_id", appId);
                //填充business
                business.addProperty("aue", "lame");
                business.addProperty("tte", defaultTte);
                business.addProperty("ent", "intp65");
                business.addProperty("vcn", defaultVcn);
                business.addProperty("auf", "rate");
                business.addProperty("sfl", 1);
                business.addProperty("pitch", 50);
                business.addProperty("speed", 50);
                //填充data
                data.addProperty("status", 2);
                try {
                    data.addProperty("text", Base64.getEncoder().encodeToString(text.getBytes("utf8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //填充frame
                frame.add("common", common);
                frame.add("business", business);
                frame.add("data", data);
                webSocket.send(frame.toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                //处理返回数据
                System.out.println("receive=>" + text);

                try {
                    // 使用JsonParser解析响应
                    JsonObject resp = JsonParser.parseString(text).getAsJsonObject();

                    // 检查错误码
                    if (resp.has("code") && resp.get("code").getAsInt() != 0) {
                        System.out.println("error=>" + resp.get("message").getAsString() +
                                " sid=" + resp.get("sid").getAsString());
                        return;
                    }

                    // 处理音频数据
                    if (resp.has("data")) {
                        JsonObject data = resp.getAsJsonObject("data");
                        String audioBase64 = data.get("audio").getAsString();
                        byte[] audio = Base64.getDecoder().decode(audioBase64);
                        audioData.write(audio);

                        // 检查是否结束
                        if (data.has("status") && data.get("status").getAsInt() == 2) {
                            System.out.println("session end");
                            webSocket.close(1000, "");

                            // 上传音频数据到R2
                            try {
                                MultipartFile multipartFile = new ByteArrayMultipartFile(
                                        targetFileName,
                                        targetFileName,
                                        "audio/mpeg",
                                        audioData.toByteArray()
                                );

                                String fileUrl = r2Utils.uploadFile(multipartFile);
                                System.out.println("合成的音频文件URL: " + fileUrl);

                                audioData.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println("socket closing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println("socket closed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("connection failed");
            }
        });

        String finalUrl = "https://img.rainnn.top/" + targetFileName;
        return new ResponseData(200, "ok", finalUrl);
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                append("date: ").append(date).append("\n").//
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("hmac username=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        return httpUrl.toString();
    }
}
