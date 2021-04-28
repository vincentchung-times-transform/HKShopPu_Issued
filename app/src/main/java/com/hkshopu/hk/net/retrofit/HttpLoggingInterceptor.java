package com.hkshopu.hk.net.retrofit;

import android.util.Log;

import com.hkshopu.hk.data.bean.BaseResponse;
import com.hkshopu.hk.net.GsonProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @Author: YangYang
 * @Date: 2017/6/1
 * @Version: 1.0.0
 * @Description:
 */
public final class HttpLoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //request
        Request request = chain.request();
        RequestBody requestBody = request.body();

        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = requestBody.contentType();
        if (contentType != null) {
            contentType.charset(charset);
        }
        Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE);
        Buffer buffer2 = source.buffer();
        MediaType contentType2 = responseBody.contentType();
        if (contentType2 != null) {
            charset = contentType2.charset(charset);
        }

        String url = request.url().toString();
        String params = buffer.readString(charset);
        Headers headers = request.headers();
        String responseS = buffer2.clone().readString(charset);

        BaseResponse<String> responseObj = null;
        String data = "";
        try {
            responseObj = GsonProvider.INSTANCE.getGson().fromJson(responseS, BaseResponse.class);
            data = responseObj.getRet_val();
        } catch (Exception e) {
        }

        StringBuilder builder = new StringBuilder();
        builder.append("url:\t");
        builder.append(url);
        builder.append("\n");
        if (!url.contains("uploads")) {
            builder.append("params:\t");
            builder.append(params);
            builder.append("\n");
            String originalParams = "";
            try {
                JSONObject jsonObject = new JSONObject(params);
//                originalParams = Base64Helper.decodeToString(jsonObject.getString("data"));
                originalParams = jsonObject.getString("data");
            } catch (JSONException e) {
            }
            builder.append("originalParams:\t");
            builder.append(originalParams);
            builder.append("\n");
        }
        builder.append("headers:\t");
        builder.append(headers.toString());
        if (headers.names().isEmpty()) {
            builder.append("\n");
        } else if (headers.names().contains("baseParam")) {
            builder.append("originalBaseParams:\t");
//            builder.append(Base64Helper.decodeToString(headers.values("baseParam").get(0)));
            builder.append(headers.values("baseParam").get(0));
            builder.append("\n");
        }
        builder.append("================================response=================================\n");
        if (responseObj != null) {
            builder.append("HttpCode:"+response.code());
            builder.append("\n");
            builder.append("\t code:");
            builder.append(responseObj.getStatus());
            builder.append("\n");
        }
        builder.append("response :\t");
        builder.append(responseS);
        builder.append("\n");
        if (responseObj != null) {
            builder.append("decodeData:\t");
            builder.append(data);
            builder.append("\n");
        }
        Log.d("HttpLoggingInterceptor", "HttpRequest:" + builder.toString());
        return response;
    }
}
