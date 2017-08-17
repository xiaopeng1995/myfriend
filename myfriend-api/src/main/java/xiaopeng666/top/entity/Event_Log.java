package xiaopeng666.top.entity;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Event Log Data Structure
 */
public class Event_Log {

    //固件升级使用字段
    String controller, from, to, by;
    private ObjectId id;
    private ObjectId user_id;
    private String email;
    private Event_Type action_type;
    private Date action_date;
    private String action_description;
    private String action_data;
    private boolean checked;            // 是否处理过？
    private Integer role; //操作用户的角色
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUser_id() {
        return user_id;
    }

    public void setUser_id(ObjectId user_id) {
        this.user_id = user_id;
    }


    public Event_Type getAction_type() {
        return action_type;
    }

    public void setAction_type(Event_Type action_type) {
        this.action_type = action_type;
    }

    public Date getAction_date() {
        return action_date;
    }

    public void setAction_date(Date action_date) {
        this.action_date = action_date;
    }

    public String getAction_description() {
        return action_description;
    }

    public void setAction_description(String action_description) {
        this.action_description = action_description;
    }

    public String getAction_data() {
        return action_data;
    }

    public void setAction_data(String action_data) {
        this.action_data = action_data;
    }




    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
