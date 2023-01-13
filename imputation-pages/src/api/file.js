import request from "@/utils/request";

export default {
    //样本创建
    fileCreate(params){return request.post(`/cloudimpute/file/create`,params);},
    //样本检索
    fileSelect(params){return request.post(`/cloudimpute/file/select`,params);},
    //删除样本
    fileDelete(params){return request.post(`/cloudimpute/file/delete`,params);},
}