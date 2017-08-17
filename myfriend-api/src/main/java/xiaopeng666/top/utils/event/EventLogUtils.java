package xiaopeng666.top.utils.event;

import org.bson.types.ObjectId;
import xiaopeng666.top.entity.Event_Log;
import xiaopeng666.top.entity.Event_Type;
import xiaopeng666.top.utils.mongo.MongoStorage;

import java.util.Date;

/**
 * Created by WY on 2017/7/4.
 */
public class EventLogUtils {


    /**
     * 写入用户操作日志
     *
     * @param mongo
     * @param userId      主账户ID
     * @param event_type  类型
     * @param description 操作内容
     */
    public static void insertInfoEventLog(MongoStorage mongo, ObjectId userId, Event_Type event_type, String description) {
        Event_Log log = new Event_Log();
        log.setUser_id(userId);
        log.setAction_type(event_type);
        log.setAction_description(description);
        log.setAction_date(new Date());
        log.setChecked(false); //不需要check,默认checked
        mongo.insertEvent(log);
    }


}
