import request from "@/utils/request";

export default {
    //样本创建
    fileCreate(params){return request.post(`/cloudimpute/file/create`,params);},
}