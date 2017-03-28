package com.htq.baidu.coolnote.entity;


/**
 *
 * 服务器回复的回调接口
 *
 */
public interface OnResponseListener {

    /**
     * 成功
     * @param response 回复的结果
     */
    void onResponse(Response response);

}
