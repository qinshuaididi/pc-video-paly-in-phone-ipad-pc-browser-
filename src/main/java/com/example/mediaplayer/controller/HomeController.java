package com.example.mediaplayer.controller;

import com.example.mediaplayer.Handle.VideoHttpRequestHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    String url =  "D:/";

    @Autowired
    private VideoHttpRequestHandler videoHttpRequestHandler;




    @RequestMapping("/")
    public String index(Model model,  String path) {
        File file;
        if(path==null)
        file = new File(url);
        else file = new File(path);
        List<Map<String,String>> list = new ArrayList<>();
        getFileNames(file, list);
        model.addAttribute("list",list);
        if(path != null)
            model.addAttribute("path",path);
        else model.addAttribute("path",url);
        return "index";
    }

    @RequestMapping(path = "/media")
    public String media(String path,Model model){
        String s = path.substring(path.lastIndexOf("."));
        s=s.toLowerCase();
        model.addAttribute("p",path);
        if(s.equals(".mp4") || s.equals(".avi")||s.equals(".ts"))
         return "player";
         else return "image";
   }

    @RequestMapping("/image")
    public void photo(String path,HttpServletResponse response) throws IOException {
        InputStream in = new FileInputStream(new File(path));
        String s = path.substring(path.lastIndexOf("."));
        s = s.toLowerCase();
        if(s.equals("jpg") || s.equals("jpeg")) s = "image/jpeg";
        else if(s.equals("png")) s = "image/png";
       response.setContentType(s);
        OutputStream out = response.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

       in.close();
        out.flush();
        out.close();

    }



    @RequestMapping(path = "/video",method = RequestMethod.GET)
    public void  getPlayResource(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{

        Path path1 = Paths.get(path);
        if (Files.exists(path1)) {
            String mimeType = Files.probeContentType(path1);
            if (!StringUtils.isEmpty(mimeType)) {
                response.setContentType(mimeType);
            }
            request.setAttribute(VideoHttpRequestHandler.ATTR_FILE, path1);
            videoHttpRequestHandler.handleRequest(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        }
    }

    public void getFileNames(File file, List fileNames) {
        File[] files = file.listFiles();
        for (File f : files) {
            Map<String,String> map = new HashMap<>();
            map.put("name",f.getName());
            if(f.isFile()) map.put("type","file");
            if(f.isDirectory()) map.put("type","directory");
            fileNames.add(map);
        }
    }
}


