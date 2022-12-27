import request from "@/utils/request";

export default {
    //注册
    register(params){return request.post(`/cloudimpute/user/register`,params);},
    //登录
    login(params){return request.post(`/cloudimpute/user/login`,params);},
    //登出
    logout(params){return request.post(`/cloudimpute/user/logout`,params);},
    //获取用户信息
    querythisuser(params){return request.post(`/cloudimpute/user/querythisuser`,params);},
    //修改用户信息
    update(params){return request.post(`/cloudimpute/user/update`,params);},
    //注销
    unregister(params){return request.post(`/cloudimpute/user/unregister`,params);},
}