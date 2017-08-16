

package xiaopeng666.top.utils.mongo;

import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import xiaopeng666.top.entity.User;
import xiaopeng666.top.entity.UserRole;
import xiaopeng666.top.entity.VerifyType;

import java.util.*;
import java.util.concurrent.TimeUnit;


import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

/**
 * MongoDB Storage
 */
public class MongoStorage {

    protected MongoClient client;
    protected MongoDatabase database;

    public void init(AbstractConfiguration config) {

        MongoClientOptions options = MongoClientOptions.builder().build();

        // MongoClient
        List<ServerAddress> addresses = parseAddresses(config.getString("mongo.address"));
        List<MongoCredential> credentials = parseCredentials(
                config.getString("mongo.userName"),
                "admin",
                config.getString("mongo.password"));
        if (addresses.size() == 1) {
            this.client = new MongoClient(addresses.get(0), credentials);
        } else {
            this.client = new MongoClient(addresses, credentials);
        }
        this.database = this.client.getDatabase(config.getString("mongo.database"));

        // indexes
        try {
            this.database.getCollection("users").dropIndex(ascending("name"));
        } catch (MongoCommandException e) {
            System.out.println("不存在的索引");
        }
        this.database.getCollection("users").createIndex(ascending("name"), new IndexOptions().unique(true));
        this.database.getCollection("sms_verifies").createIndex(ascending("updated_at"), new IndexOptions().expireAfter(1440L, TimeUnit.MINUTES));
//        this.database.getCollection("mail_verifies").createIndex(ascending("mail"));
//        this.database.getCollection("mail_verifies").createIndex(ascending("updated_at"), new IndexOptions().expireAfter(5L, TimeUnit.MINUTES));
//
//        this.database.getCollection("products").createIndex(ascending("user_id"));
//        this.database.getCollection("products").createIndex(ascending("settings.fields.device_type", "settings.fields.key"));
//
//        this.database.getCollection("agents").createIndex(ascending("product_id"));
//        this.database.getCollection("agents").createIndex(ascending("permissions.user_id", "permissions.level"));
//        this.database.getCollection("devices").createIndex(ascending("agent_id", "sn"), new IndexOptions().unique(true));
//        this.database.getCollection("geetest_verifies").createIndex(ascending("datetime"), new IndexOptions().expireAfter(10L, TimeUnit.MINUTES));
//        this.database.getCollection("event_logs").createIndex(ascending("action_date"), new IndexOptions().expireAfter(15L, TimeUnit.DAYS));
//        this.database.getCollection("event_logs").createIndex(ascending("user_id", "product_id", "agent_id"));
//        // this.database.getCollection("emulator_datas").createIndex(ascending("test"), new IndexOptions().expireAfter(1L, TimeUnit.MINUTES));
        //drop index(这里暂时没用，预留，当我们需要改变与时间相关的检索字段时，需要先删除再新建，删除的前提是检索字段已经存在，不存在会报错，慎改)
        //this.database.getCollection("emulator_datas").dropIndex(ascending("test"));
        //this.database.getCollection("mail_verifies").dropIndex(ascending("mail"));
        //this.database.getCollection("users").dropIndex(ascending("email"));
        //this.database.getCollection("sms_verifies").dropIndex(ascending("updated_at"));
        //this.database.getCollection("sms_verifies").dropIndex(ascending("mobile"));
    }

    public void destroy() {
        if (this.client != null) this.client.close();
    }

    private ServerAddress parseAddress(String address) {
        int idx = address.indexOf(':');
        return (idx == -1) ?
                new ServerAddress(address) :
                new ServerAddress(address.substring(0, idx), Integer.parseInt(address.substring(idx + 1)));
    }

    private List<ServerAddress> parseAddresses(String addresses) {
        List<ServerAddress> result = new ArrayList<>();
        String[] addrs = addresses.split(" *, *");
        for (String addr : addrs) {
            result.add(parseAddress(addr));
        }
        return result;
    }

    private List<MongoCredential> parseCredentials(String userName, String database, String password) {
        List<MongoCredential> result = new ArrayList<>();
        //MongoCredential类的createCredential方法可以指定认证的用户名，密码，以及使用的数据库，并返回一个MongoCredential对象
       // result.add(MongoCredential.createCredential(userName, database, password.toCharArray()));
        return result;
    }

    /*===============================================User Operations===========================================*/

    /**
     * 判断 用户名 是否存在
     *
     * @param name 用户名
     * @return True 如果存在
     */
    public boolean isUserNameExist(String name) {
        Document d = this.database.getCollection("users")
                .find(eq("name", name))
                .projection(include("_id"))
                .first();
        return d != null;
    }
    /**
     * 更新 用户密码
     *
     * @param user 用户的信息
     * @return 用户（含Id）
     */
    public Boolean updateUserPwd(User user) {

        Document d = new Document();

        if (user.getPassword() != null) {
            d.append("password", user.getPassword());
        }
        d.append("updated_at", new Date());
        return this.database.getCollection("users").updateOne(and(eq("role", user.getRole().value()), ne("is_delete", true), eq("email", user.getEmail())), new Document().append("$set", d)).getModifiedCount() > 0;

    }

    /**
     * 判断 用户邮箱 是否存在
     *
     * @param email 用户邮箱
     * @return True 如果存在
     */
    public boolean isUserEmailExist(String email) {
        Document d = this.database.getCollection("users")
                .find(eq("email", email))
                .projection(include("_id"))
                .first();
        return d != null;
    }

    /**
     * 判断 验证码 是否有效
     *
     * @param email     手机号码
     * @param type       验证类型
     * @param verifyCode 验证码
     * @return True 有效
     */
    public boolean isSmsVerifyCodeValid(String email, VerifyType type, String verifyCode) {
        return this.database.getCollection("sms_verifies")
                .find(and(eq("email", email), eq("type", type.value()), eq("verify_code", verifyCode)))
                .projection(include("_id"))
                .first() != null;
    }

    /**
     * 判断 用户邮箱 是否存在
     *
     * @param email 用户邮箱
     * @param role  用户角色
     * @return True 如果存在
     */
    public boolean isUserEmailExist(String email, UserRole role) {
        Document d = this.database.getCollection("users")
                .find(and(eq("email", email), eq("role", role.value())))
                .projection(include("_id"))
                .first();
        return d != null;
    }

    /**
     * 判断 用户密码 是否正确
     *
     * @return True 密码正确、存在
     */
    public boolean isUserPasswordCorrect(ObjectId id, String password) {
        Document d = this.database.getCollection("users")
                .find(and(eq("_id", id), eq("password", password)))
                .projection(include("_id"))
                .first();
        return d != null;
    }


    /**
     * 判断 用户手机 是否存在
     *
     * @param mobile 用户手机
     * @return True 如果存在
     */
    public boolean isUserMobileExist(String mobile) {
        Document d = this.database.getCollection("users")
                .find(eq("mobile", mobile))
                .projection(include("_id"))
                .first();
        return d != null;
    }

    /**
     * 判断 用户手机 是否存在
     *
     * @param mobile 用户手机
     * @param role   用户角色
     * @return True 如果存在
     */
    public boolean isUserMobileExist(String mobile, UserRole role) {
        Document d = this.database.getCollection("users")
                .find(and(eq("mobile", mobile), eq("role", role.value())))
                .projection(include("_id"))
                .first();
        return d != null;
    }


    /**
     * 新增 用户
     * 如果 用户名 邮箱 手机 已存在，会抛出异常
     * 调用方法应捕捉异常
     *
     * @param user 用户的信息
     * @return 用户（含Id）
     */
    public User insertUser(User user) {
        Document d = new Document();
        d.append("name", user.getName());
        d.append("password", user.getPassword());
        if (user.getEmail() != null) {
            d.append("email", user.getEmail());
        }
        if (user.getMobile() != null) {
            d.append("mobile", user.getMobile());
        }

        d.append("role", user.getRole().value());
        d.append("updated_at", new Date()); // 当前版本 MongoDB insert 不支持试用 $currentDate
        this.database.getCollection("users").insertOne(d);
        user.setId(d.getObjectId("_id"));
        user.setUpdatedAt(d.getDate("updated_at"));
        return user;
    }

    /**
     * 更新 用户信息
     *
     * @param user 用户的信息
     * @return 用户（含Id）
     */
    public Boolean updateUserProfile(User user) {

        Document d = new Document();
        if (user.getName() != null) {
            d.append("name", user.getName());
        }
        if (user.getPassword() != null) {
            d.append("password", user.getPassword());
        }
        if (user.getCompany() != null) {
            d.append("company", user.getCompany());
        }
        if (user.getEmail() != null) {
            d.append("email", user.getEmail());
        }
        if (user.getMobile() != null) {
            d.append("mobile", user.getMobile());
        }
        d.append("updated_at", new Date());
        return this.database.getCollection("users").updateOne(eq("_id", user.getId()), new Document().append("$set", d)).getModifiedCount() > 0;
    }

    /**
     * 更新 用户密码
     *
     * @param user 用户的信息
     * @return 用户 是否验证成功
     */
    public Boolean updateUserPassword(User user) {
        Document d = new Document();
        d.append("password", user.getPassword());
        d.append("updated_at", new Date());
        return this.database.getCollection("users").updateOne(eq("_id", user.getId()), new Document().append("$set", d)).getModifiedCount() > 0;
    }

    /**
     * 找回密码
     *
     * @param user 用户的信息
     * @return 是否重置成功
     */
    public Boolean retrievePassword(User user) {
        Document d = new Document();
        d.append("mobile", user.getMobile());
        d.append("password", user.getPassword());
        d.append("updated_at", new Date());
        return this.database.getCollection("users").updateOne(eq("mobile", user.getMobile()), new Document().append("$set", d)).getModifiedCount() > 0;
    }

    /**
     * 获取 ID，根据 用户名 密码
     * 密码需要加密
     *
     * @param name     用户名
     * @param password 密码
     * @return _id or Null
     */
    public ObjectId getUserIdByNamePassword(String name, String password) {
        Document d = this.database.getCollection("users")
                .find(and(eq("name", name), eq("password", password)))
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }

    /**
     * 获取 _id，根据 用户名 密码
     * 密码需要加密
     *
     * @param name     用户名
     * @param password 密码
     * @return _id or Null
     */
    public ObjectId getUserIdByNamePasswordRole(String name, String password, UserRole role) {
        BasicDBObject query = new BasicDBObject();
        query.put("name", name);
        query.put("password", password);
        query.put("role", role.value());
        Document d = this.database.getCollection("users")
                .find(query)
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }


    /**
     * 获取 _id，根据 邮箱 密码
     * 密码需要加密
     *
     * @param email    邮箱
     * @param password 密码
     * @return _id or Null
     */
    public ObjectId getUserIdByEmailPassword(String email, String password) {
        Document d = this.database.getCollection("users")
                .find(and(eq("email", email), eq("password", password)))
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }

    /**
     * 获取 Id，根据 邮箱 密码
     * 密码需要加密
     *
     * @param email    邮箱
     * @param password 密码
     * @param role     用户角色
     * @return _id or Null
     */
    public ObjectId getUserIdByEmailPasswordRole(String email, String password, UserRole role) {
        BasicDBObject query = new BasicDBObject();
        query.put("email", email);
        query.put("password", password);
        query.put("role", role.value());
        Document d = this.database.getCollection("users")
                .find(query)
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }

    /**
     * 获取 Id，根据 手机
     *
     * @param mobile 手机
     * @return Id or Null
     */
    public ObjectId getUserIdByMobile(String mobile) {
        Document d = this.database.getCollection("users")
                .find(eq("mobile", mobile))
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }

    /**
     * 获取 Id，根据 手机 用户角色
     *
     * @param mobile 手机
     * @param role   用户角色
     * @return Id or Null
     */
    public ObjectId getUserIdByMobile(String mobile, UserRole role) {
        Document d = this.database.getCollection("users")
                .find(and(eq("mobile", mobile), eq("role", role.value())))
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }

    /**
     * 获取 Id，根据 邮箱
     *
     * @param mail 邮箱
     * @return Id or Null
     */
    public ObjectId getUserIdByMail(String mail) {
        Document d = this.database.getCollection("users")
                .find(eq("mail", mail))
                .projection(include("_id"))
                .first();
        if (d != null) {
            ObjectId Id = d.getObjectId("_id");
            return Id;
        } else return null;
    }

    /**
     * 获取 Id，根据 邮箱 用户角色
     *
     * @param mail 邮箱
     * @param role 用户角色
     * @return Id or Null
     */
    public ObjectId getUserIdByMail(String mail, UserRole role) {
        Document d = this.database.getCollection("users")
                .find(and(eq("mail", mail), eq("role", role.value())))
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }


    /**
     * 获取 Id，根据 手机 密码
     * 密码需要加密
     *
     * @param mobile   手机
     * @param password 密码
     * @return Id or Null
     */
    public ObjectId getUserIdByMobilePassword(String mobile, String password) {
        Document d = this.database.getCollection("users")
                .find(and(eq("mobile", mobile), eq("password", password)))
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }

    /**
     * 获取 Id，根据 手机 密码
     * 密码需要加密
     *
     * @param mobile   手机
     * @param password 密码
     * @return Id or Null
     */
    public ObjectId getUserIdByMobilePasswordRole(String mobile, String password, UserRole role) {
        BasicDBObject query = new BasicDBObject();
        query.put("mobile", mobile);
        query.put("password", password);
        query.put("role", role.value());
        Document d = this.database.getCollection("users")
                .find(query)
                .projection(include("_id"))
                .first();
        if (d != null) return d.getObjectId("_id");
        else return null;
    }


    /**
     * 获取 用户信息，根据用户Id
     *
     * @param id 用户Id
     * @return 用户信息 or Null
     */
    public User getUserById(ObjectId id) {
        Document d = this.database.getCollection("agents")
                .find(eq("_id", id))
                .projection(include("product_id"))
                .first();
        ObjectId pid = d.getObjectId("product_id");

        Document products = this.database.getCollection("products")
                .find(eq("_id", pid))
                .projection(include("user_id"))
                .first();
        ObjectId uid = (ObjectId) products.get("user_id");

        Document user = this.database.getCollection("users")
                .find(eq("_id", uid))
                .projection(exclude("password"))
                .first();
        if (user == null) return null;
        return parseUserDocument(user);
    }

    /**
     * 获取 用户信息，根据 Id
     *
     * @param Id Id
     * @return 用户信息 or Null
     */
    public User getUserById(String Id) {
        Document d = this.database.getCollection("users")
                .find(eq("_id", Id))
                .projection(exclude("password"))
                .first();
        if (d == null) return null;
        return parseUserDocument(d);
    }

    private User parseUserDocument(Document d) {
        User u = new User();
        u.setId(d.getObjectId("_id"));
        u.setName(d.getString("name"));
        u.setId(d.getObjectId("_id"));
        u.setEmail(d.getString("email"));
        u.setMobile(d.getString("mobile"));
        u.setCompany(d.getString("company"));
        u.setRole(UserRole.valueOf(d.getInteger("role")));
        u.setUpdatedAt(d.getDate("updated_at"));
        return u;
    }



    /**
     * 判断 用户注册码 是否过期 根据手机号
     *
     * @param mobile 用户手机
     * @return True 如果存在
     */
    public boolean verifyCodeExistByMobile(String mobile) {
        Document d = this.database.getCollection("sms_verifies")
                .find(eq("mobile", mobile))
                .projection(include("_id"))
                .first();
        return d != null;
    }

}
