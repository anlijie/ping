package com.example.ping.controller;

import com.example.ping.util.PingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   *实现ping的功能
 */
@Controller
public class JavaPing {
    static List<String> list=new ArrayList<>();

    @RequestMapping(value = "/jump")
    public String jump(){
        return "index";
    }

    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        try {   // 执行命令并获取输出
            Process p = r.exec(pingCommand);
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream(),Charset.forName("GBK")));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
            //br=new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            int connectedCount = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line);
                list.add(line);
            }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
            return connectedCount == pingTimes;
        } catch (Exception ex) {
            ex.printStackTrace();   // 出现异常则返回假
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line) {  // System.out.println("控制台输出的结果为:"+line);
        Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",    Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            return 1;
        }
        return 0;
    }

    @RequestMapping(value = "/ping")
    public String save(@RequestParam String ipAddress,@RequestParam int pingTimes, @RequestParam int timeOut, Model model){
        System.out.println(ping(ipAddress,pingTimes,timeOut)!=false);
        if (ping(ipAddress,pingTimes,timeOut)!=false) {
            model.addAttribute("list",list);
        }else {
            model.addAttribute("error","请求错误，请检查你输入的ip是否有错！");
        }
        return "index";
    }


}
