package org.example.moodvine_backend.model.VO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {  //ResponseData：响应用户请求打包返回的信息

    private int code;
    private String message;
    private Object data;

    public static ResponseData ok() {  //告知响应成功
        return new ResponseData(200,"ok",null);
    }

    public static ResponseData success(Object data) {  //返回响应参数
        return new ResponseData(200,"success",data);
    }

    public static ResponseData result(Object data) {  //返回多个结果
        return new ResponseData(200,"result",data);
    }

    public static ResponseData failure(int code, String msg) {  //告知操作失败，并返回原因
        return new ResponseData(code, msg,null);
    }

    public static ResponseData error(int code, Object data) {  //告知出现错误,并返回错误
        return new ResponseData(code,"error",data);
    }

    public static ResponseData noContent() {  //接收了请求，但没找到返回值
        return new ResponseData(204,"No Content",null);
    }

    public static ResponseData notFound() {  //告知资源未找到错误
        return new ResponseData(404, "Not Found",null);
    }
}
