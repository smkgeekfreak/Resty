package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import play.Logger;
import play.data.validation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import play.db.*;
import play.libs.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("User")
public class User {
    @ApiModelProperty(required=false)
    public Integer id;
    @ApiModelProperty(required=true)
    public String name;

    public User(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
        Logger.info("User constructor");
    }

//    public void save() {
//
//        try {
//            Logger.info("Requesting save :" + this.name);
//            DataSource ds = DB.getDataSource();
//            Connection conn = DB.getConnection();
//            Logger.info("Preparing query:");
//            PreparedStatement s = conn.prepareStatement("SELECT * FROM create_account(\'"+ this.name + "\', 1)");
//            Logger.info("Prepared query:" + s.toString());
//            ResultSet rs = s.executeQuery();
//            Logger.info("Executed query:" + rs.getStatement().toString() + " returned (" + rs.getFetchSize() +")");
//
//            while(rs.next()) {
//                this.id = rs.getInt("ret_uid");
//                Logger.info("User saved = " + Json.toJson(this));
//               // return new User(this.id, this.name);
//            }
//            rs.close();
//            conn.close();
//        } catch (SQLException sqe) {
//            Logger.info("User model problem" + sqe.getMessage());
//        }
//        //return null;
//    }
//
//    public static User find (Integer id) {
//        try {
//        DataSource ds = DB.getDataSource();
//        Connection conn = DB.getConnection();
//            CallableStatement s = conn.prepareCall("Select * from account where uid ="+id);
//            ResultSet rs = s.executeQuery();
//            while (rs.next()) {
//                Integer uid = rs.getInt("uid");
//                String  email = rs.getString("email");
//                conn.close();
//                return new User(uid,email);
//            }
//            conn.close();
//        }catch (SQLException sqe){
//            Logger.info("User model problem" + sqe.getMessage());
//        }
//        return null;
//    }
//
//    public static List<User> findAll () {
//        List<User> users = new ArrayList<>();
//        try {
//            DataSource ds = DB.getDataSource();
//            Connection conn = DB.getConnection();
//            PreparedStatement s = conn.prepareStatement("Select * from account" );
//            ResultSet rs = s.executeQuery();
//            while (rs.next()) {
//                Integer uid = rs.getInt("uid");
//                String  email = rs.getString("email");
//                users.add(new User(uid, email));
//            }
//            rs.close();
//            conn.close();
//            return users;
//        }catch (SQLException sqe){
//            Logger.info("User model problem" + sqe.getMessage());
//        }
//        return null;
//    }
}

