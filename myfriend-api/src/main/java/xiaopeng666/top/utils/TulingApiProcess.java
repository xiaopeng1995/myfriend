package xiaopeng666.top.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import xiaopeng666.top.entity.Tuling;
import xiaopeng666.top.entity.TulingRoot;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * 调用图灵机器人api接口，获取智能回复内容
 *
 * @author pamchen-1
 */
public class TulingApiProcess {
    /**
     * 调用图灵机器人api接口，获取智能回复内容，解析获取自己所需结果
     *
     * @param content
     * @return
     */
    public static String getTulingResult(String content) {

        //此处为图灵api接口，参数key需要自己去注册申请，
        String apiUrl = "http://www.tuling123.com/openapi/api?key=4f80eea440844b0db0886ccc9d1836b3&info=";

        String param = "";

        try {
            param = apiUrl + URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } //将参数转为url编码

        //发送httpget请求
        HttpGet request = new HttpGet(param);
        String result = "";
        Map map = null;
        try {
            HttpResponse response = HttpClients.createDefault().execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(response.getEntity());
                map = JsonUtils.Mapper.readValue(result,Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //请求失败处理
        if (result == null) {
            return "对不起，你说的话真是太高深了……";
        }

        try {
            //以code=100000为例，参考图灵机器人api文档
            if (map != null) {
                if ("100000".equals(map.get("code").toString())) {
                    result = map.get("text").toString();
                }
                if ("200000".equals(map.get("code").toString())) {
                    result = map.get("text").toString() + "地址是:" + map.get("url").toString();
                }
                if ("302000".equals(map.get("code").toString())) {
                    TulingRoot tulingRoot = JsonUtils.Mapper.readValue(result,TulingRoot.class);
                    result = tulingRoot.getText();
                    List<Tuling> ttlist = tulingRoot.getList();
                    int i = 0;
                    for (Tuling tt : ttlist) {
                        i++;
                        result += "\n第:" + i + "条新闻:";
                        result += "\n标题:" + tt.getArticle();
                        result += "\n来源:" + tt.getSource();
                        result += "\n地址:" + tt.getDetailurl();
                    }
                }
            } else {
                result = map.get("text").toString();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}