package com.dhcc.sysManager.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: WebControllerInterceptor
 * @Description: Controller层的拦截（这里用于登录的token验证）
 * @author xcl
 * @date 2020年4月29日
 */
//@Component
public class WebControllerInterceptor implements WebMvcConfigurer {
    @Bean
    public InterfaceAuthCheckInterceptor getInterfaceAuthCheckInterceptor() {
        return new InterfaceAuthCheckInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 多个拦截器组成一个拦截器链
         * addPathPatterns 用于添加拦截规则
         * excludePathPatterns 用于排除拦截
         */
        registry.addInterceptor(getInterfaceAuthCheckInterceptor()).addPathPatterns("/api/hdfs-rest/v1.0/**");
    }
    class InterfaceAuthCheckInterceptor implements HandlerInterceptor {
        /**
         * 预处理回调方法，实现处理器的预处理（如检查登陆），第三个参数为响应的处理器，自定义Controller
         * 返回值：true表示继续流程（如调用下一个拦截器或处理器）；false表示流程中断（如登录检查失败），
         * 不会继续调用其他的拦截器或处理器，此时我们需要通过response来产生响应；
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj)
                throws Exception {
            //从头或者参数中拿token
            String token = !StringUtils.isEmpty(request.getHeader("Authorization"))?
                    request.getHeader("Authorization"):request.getParameter("Authorization");
            System.out.println(request.getRequestURL());
            String failedReponseStr="";
            if(StringUtils.isEmpty(token)){
                failedReponseStr=TokenUtil.TaskResponse("-1", "未进行用户验证");
            }else{
                //这里需要去掉前台传过来的无用字符（这块java验证不需要）
                token=token.replace("Bearer ", "");
                Map<String, Object> params=new HashMap<>();
                //client_id,client_secret和在Authorization放base64的方式一样，这里统一走Authorization头
//				params.put("client_id", "xUF3ckILU4WcxsjoEhvRD7XbkP6oBZo6nF9o0DQFl3s");
//				params.put("client_secret", "GFGgcXAoL_ZdLgS_1KcVSDVjC4nXc25J3S8pWOe5Avfse71Nikv-iaer9HFbbJQX8tAKIf25RxNol9O3Kc_Q6w");
                params.put("token", token);
//				params.put("token", "RJ_yfCwGpQuUsy6_b-jXgV7O7Shl34bHy355sztr0vozmnch8hXGTgD4lSjm98_xqLOspJAsiaeyrC1nOMmp-g");
                params.put("token_type", "access_token");
                try {
//					JSONObject doPostByObj = httpClient.doPostByObj("https://114.116.38.58/oauth2/introspection", params);
                    //服务地址需要走配置文件
                    Object doPostByObj = TokenUtil.doTokenValidation("https://114.116.38.58/oauth2/introspection", params);
                    String result = doPostByObj.toString();
                    JSONObject resultObject = JSONObject.parseObject(result);
                    boolean active = (boolean) resultObject.get("active");
                    if(active){//"true".equals(active)
//						failedReponseStr=emrSwitchUtil.TaskResponse("0", "用户验证通过");
                        return true;
                    }else{
                        failedReponseStr=TokenUtil.TaskResponse("-1", "用户验证未通过");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failedReponseStr=TokenUtil.TaskResponse("-1", "用户验证服务出错");
                }

            }
            //
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.append(failedReponseStr);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            return false;
        }
        /**
         * 整个请求处理完毕回调方法，即在视图渲染完毕时回调，如性能监控中我们可以在此记录结束时间并输出消耗时间，
         * 还可以进行一些资源清理，类似于try-catch-finally中的finally，但仅调用处理器执行链中
         */
        @Override
        public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
                throws Exception {
        }
        /**
         * 后处理回调方法，实现处理器的后处理（但在渲染视图之前），此时我们可以通过modelAndView（模型和视图对象）
         * 对模型数据进行处理或对视图进行处理，modelAndView也可能为null。
         */
        @Override
        public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
                throws Exception {
        }

    }
}
