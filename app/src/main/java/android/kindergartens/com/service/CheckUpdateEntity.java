package android.kindergartens.com.service;

import android.kindergartens.com.base.BaseEntity;

/**
 * Created by zhangruiyu on 2017/12/2.
 */

public class CheckUpdateEntity implements BaseEntity {

    /**
     * code : 200
     * msg :
     * data : {"checkState":0,"message":null,"downloadUrl":null}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean  implements BaseEntity {
        /**
         * checkState : 0
         * message : null
         * downloadUrl : null
         */

        private int checkState;
        private String message;
        private String downloadUrl;

        public int getCheckState() {
            return checkState;
        }

        public void setCheckState(int checkState) {
            this.checkState = checkState;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}
