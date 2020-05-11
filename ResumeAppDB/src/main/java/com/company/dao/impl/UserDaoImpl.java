package com.company.dao.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.company.entity.Country;
import com.company.entity.Skill;
import com.company.entity.User;
import com.company.entity.UserSkill;
import com.company.dao.inter.AbstractDAO;
import com.company.dao.inter.UserDaoInter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl extends AbstractDAO implements UserDaoInter {
    
    private User getUser(ResultSet resultSet) throws Exception{
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String surname = resultSet.getString("surname");
        String mail = resultSet.getString("email");
        String phone = resultSet.getString("phone_number");
        String profileDescription = resultSet.getString("profile_description");
        String address = resultSet.getString("address");
        int nationalityID=resultSet.getInt("nationality_id");
        int birthplaceID=resultSet.getInt("birthplace_id");
        String nationalityStr = resultSet.getString("nationality");
        String birthplaceStr = resultSet.getString("birthplace");
        Date birthdate = resultSet.getDate("birthdate");

        Country nationality = new Country(nationalityID,null,nationalityStr);
        Country birthplace = new Country(birthplaceID,birthplaceStr,null);

        User u = new User(id,name,surname,phone,mail,profileDescription,address,birthdate,nationality,birthplace);
        u.setPassword(resultSet.getString("password"));
        return u;
    }
    public List<User> getAll(String name,String surname,Integer nationalityId) {
        
        List<User> result = new ArrayList<User>();
        try {
            Connection connection = connect();

            String sql = "select u.*,c.name as birthplace, n.nationality as nationality from USERS as u left join country as n on u.nationality_id=n.id left join country as c on u.birthplace_id=c.id where 1=1";

            if (name!=null && !name.trim().isEmpty()) {
                sql+=" and u.name=?";
            }
            if (surname!=null && !surname.trim().isEmpty()) {
                sql+=" and u.surname=?";
            }
            if (nationalityId!=null) {
                sql+=" and u.nationality_id=?";
            }
            PreparedStatement statement = connection.prepareStatement(sql);

            int i = 1;

            if (name!=null && !name.trim().isEmpty()){
                statement.setString(i,name);
                i++;
            }
            if (surname!=null && !surname.trim().isEmpty()){
                statement.setString(i,surname);
                i++;
            }
            if (nationalityId!=null) {
                statement.setInt(i,nationalityId);
            }
            statement.execute();
    
            ResultSet resultSet = statement.getResultSet();
    
            while (resultSet.next()) {
                User u = getUser(resultSet);
        
               result.add(u);
            }
            
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public User getById(int userID) {
        User u = null;
        try {
            Connection connection = connect();
            Statement statement = connection.createStatement();
            statement.execute("select u.*,c.name as birthplace, n.nationality as nationality from USERS as u left join country as n on u.nationality_id=n.id left join country as c on u.birthplace_id=c.id WHERE u.id="+userID);

            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                u = getUser(resultSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return u;
    }

    private BCrypt.Hasher crypt = BCrypt.withDefaults();

    public int addUser(User u) {
        int value=0;
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into users (name,surname,email,phone_number,profile_description,address,birthdate,birthplace_id,nationality_id,password) values (?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,u.getName());
            preparedStatement.setString(2,u.getSurname());
            preparedStatement.setString(3,u.getEmail());
            preparedStatement.setString(4,u.getPhone());
            preparedStatement.setString(5,u.getProfileDescription());
            preparedStatement.setString(6,u.getAddress());
            preparedStatement.setDate(7,u.getBirthDate());
            preparedStatement.setInt(8,u.getBirthPlace().getId());
            preparedStatement.setInt(9,u.getNationality().getId());
            preparedStatement.setString(10,crypt.hashToString(4,u.getPassword().toCharArray()));
    
            value = preparedStatement.executeUpdate();
    
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                u.setId(generatedKeys.getInt(1));
            }
    
            connection.close();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    
    public boolean updateUser(User u) {
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE USERS SET name=?,surname=?,phone_number=?,email=?,profile_description=?,address=?,birthdate=?,birthplace_id=?,nationality_id=? where id=?");
            
            preparedStatement.setString(1,u.getName());
            preparedStatement.setString(2,u.getSurname());
            preparedStatement.setString(3,u.getPhone());
            preparedStatement.setString(4,u.getEmail());
            preparedStatement.setString(5,u.getProfileDescription());
            preparedStatement.setString(6,u.getAddress());
            preparedStatement.setDate(7,u.getBirthDate());
            preparedStatement.setInt(8,u.getBirthPlace().getId());
            preparedStatement.setInt(9,u.getNationality().getId());
            preparedStatement.setInt(10,u.getId());
            
            boolean result =  preparedStatement.execute();
            
            connection.close();
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean removeUser(int id) {
        try {
            Connection connection = connect();
            Statement statement = connection.createStatement();
        
            boolean result =  statement.execute("DELETE FROM USERS WHERE ID=" + id);
            
            connection.close();
            
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserByEmailAndPassword(String email, String password) {
        User u = null;
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("select u.*,c.name as birthplace, n.nationality as nationality from USERS as u left join country as n on u.nationality_id=n.id left join country as c on u.birthplace_id=c.id where email=? and password=?");
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);

            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                u = getUser(resultSet);
            }

            connection.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return  u;
    }

    @Override
    public User getUserByEmail(String email) {
        User u = null;
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("select u.*,c.name as birthplace, n.nationality as nationality from USERS as u left join country as n on u.nationality_id=n.id left join country as c on u.birthplace_id=c.id where email=?");
            preparedStatement.setString(1,email);

            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.getResultSet();

            while (resultSet.next()) {
                u = getUser(resultSet);
            }

            connection.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return  u;
    }
}
