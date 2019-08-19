package com.hunghd.musicplayer;

public class SongInfo {
    private String Baihat;
    private String Casi;
    private String Url;

    public SongInfo(String baihat, String casi, String url) {
        Baihat = baihat;
        Casi = casi;
        Url = url;
    }

    public String getBaihat() {
        return Baihat;
    }

    public void setBaihat(String baihat) {
        Baihat = baihat;
    }

    public String getCasi() {
        return Casi;
    }

    public void setCasi(String casi) {
        Casi = casi;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}


