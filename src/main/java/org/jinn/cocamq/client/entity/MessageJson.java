package org.jinn.cocamq.client.entity;

import java.io.Serializable;

public class MessageJson implements Serializable
{
	
	private static final long serialVersionUID = 1L;

    private String action;

    private String time;

    private String redis_key_hash;

    private String source;

    private String mars_cid;

    private String session_id;

    private Info info=new Info();

    private String db_key_hash;

    public MessageJson() {
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRedis_key_hash() {
        return redis_key_hash;
    }

    public void setRedis_key_hash(String redis_key_hash) {
        this.redis_key_hash = redis_key_hash;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMars_cid() {
        return mars_cid;
    }

    public void setMars_cid(String mars_cid) {
        this.mars_cid = mars_cid;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @Override
    public String toString() {
        return "MessageJSON{" +
                "action='" + action + '\'' +
                ", time='" + time + '\'' +
                ", redis_key_hash='" + redis_key_hash + '\'' +
                ", source='" + source + '\'' +
                ", mars_cid='" + mars_cid + '\'' +
                ", session_id='" + session_id + '\'' +
                ", info=" + info +
                ", DB_key_hash='" + db_key_hash + '\'' +
                '}';
    }

    public String getDb_key_hash() {
        return db_key_hash;
    }

    public void setDb_key_hash(String db_key_hash) {
        this.db_key_hash = db_key_hash;
    }
}
