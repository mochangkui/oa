package com.imooc.oa.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.oa.entity.Notice;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.NoticeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NoticeServlet", urlPatterns = "/notice/list")
public class NoticeServlet extends HttpServlet {
    private NoticeService noticeService = new NoticeService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("login_user");
        List<Notice> noticeList = noticeService.getNoticeList(user.getEmployeeId());
        Map result = new HashMap<>();
        result.put("code", "0");
        result.put("msg", "");
        result.put("count", noticeList.size());
        result.put("data", noticeList);
        String json = JSON.toJSONString(result);
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().println(json);
    }
}
