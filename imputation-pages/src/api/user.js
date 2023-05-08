import request from "@/utils/request";
let baseURL = process.env.VUE_APP_BASE_API
export default {
    //注册
    register(params) {
        return request.post(baseURL+`/cloudimpute/user/register`, params);
    },
    //登录
    login(params) {
        return request.post(baseURL+`/cloudimpute/user/login`, params);
    },
    //登出
    logout(params) {
        return request.post(baseURL+`/cloudimpute/user/logout`, params);
    },
    //获取用户信息
    querythisuser(params) {
        return request.post(baseURL+`/cloudimpute/user/querythisuser`, params);
    },
    //修改用户信息
    update(params) {
        return request.post(baseURL+`/cloudimpute/user/update`, params);
    },
    //注销
    unregister(params) {
        return request.post(baseURL+`/cloudimpute/user/unregister`, params);
    },
}