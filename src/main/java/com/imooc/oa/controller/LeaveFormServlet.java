package com.imooc.oa.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.oa.entity.LeaveForm;
import com.imooc.oa.entity.User;
import com.imooc.oa.service.LeaveFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LeaveFormServlet", urlPatterns = "/leave/*")
public class LeaveFormServlet extends HttpServlet {
    private LeaveFormService leaveFormService = new LeaveFormService();
    private Logger logger = LoggerFactory.getLogger(LeaveFormService.class);
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        String uri = req.getRequestURI();
        String methodName = uri.substring(uri.lastIndexOf("/") + 1);
        if (methodName.equals("create")) {
            this.create(req, resp);
        } else if (methodName.equals("list")) {
            this.getLeaveFormList(req, resp);
        } else if (methodName.equals("audit")) {
            this.audit(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * 创建请假单
     * @param req
     * @param resp
     * @throws IOException
     */
    private void create(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1、接收各项请假单数据
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("login_user"); // 登录信息
        String formType = req.getParameter("formType"); // 获取请假类型
        String strStartTime = req.getParameter("startTime"); // 获取请假开始时间
        String strEndTime = req.getParameter("endTime"); // 获取请假结束时间
        String reason = req.getParameter("reason"); // 获取请假原因

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map result = new HashMap();
        try {
            LeaveForm form = new LeaveForm();
            form.setEmployeeId(user.getEmployeeId());
            form.setStartTime(sdf.parse(strStartTime));
            form.setEndTime(sdf.parse(strEndTime));
            form.setFormType(Integer.parseInt(formType));
            form.setReason(reason);
            form.setCreateTime(new Date());
            // 2、调用业务逻辑方法
            leaveFormService.createLeaveForm(form);
            result.put("code", 0);
            result.put("message", "success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("请假申请异常");
            result.put("code", e.getClass().getSimpleName());
            result.put("message", e.getMessage());
        }
        // 3、组织响应数据
        String json = JSON.toJSONString(result);
        resp.getWriter().println(json);
    }

    /**
     * 查询需要审核的请假单列表
     * @param req
     * @param resp
     * @throws IOException
     */
    private void getLeaveFormList (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = (User) req.getSession().getAttribute("login_user");
        List<Map> formList = leaveFormService.getLeaveFormList("process", user.getEmployeeId());
        Map result = new HashMap();
        result.put("code", "0");
        result.put("msg", "");
        result.put("count", formList.size());
        result.put("data", formList);
        String json = JSON.toJSONString(result);
        resp.getWriter().println(json);
    }

    private void audit (HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String formId = req.getParameter("formId");
        String result = req.getParameter("result");
        String reason = req.getParameter("reason");
        User user = (User) req.getSession().getAttribute("login_user");
        Map mpResult = new HashMap();
        try {
            leaveFormService.audit(Long.parseLong(formId), user.getEmployeeId(), result, reason);
            mpResult.put("code", "0");
            mpResult.put("message", "success");
        } catch (Exception e) {
            logger.error("请假单审核失败", e);
            mpResult.put("code", e.getClass().getSimpleName());
            mpResult.put("message", e.getMessage());
        }
        String json = JSON.toJSONString(mpResult);
        resp.getWriter().println(json);
    }
}
