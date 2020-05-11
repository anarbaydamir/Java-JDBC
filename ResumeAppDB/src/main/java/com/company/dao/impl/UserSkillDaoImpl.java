package com.company.dao.impl;

import com.company.dao.inter.AbstractDAO;
import com.company.dao.inter.UserSkillDaoInter;
import com.company.entity.Skill;
import com.company.entity.User;
import com.company.entity.UserSkill;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserSkillDaoImpl extends AbstractDAO implements UserSkillDaoInter {
    public UserSkill getUserSkill (ResultSet resultSet) throws Exception {
        int userId = resultSet.getInt("id");
        int skillId=resultSet.getInt("skill_id");
        String skillName=resultSet.getString("skill_name");
        int power=resultSet.getInt("power");
        
        return new UserSkill(userId,new User(userId),new Skill(skillId,skillName),power);
    }
    
    public List<UserSkill> getAllSkillByUserId (int userId) {
        List<UserSkill> result =new ArrayList<UserSkill>();
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("select u.*,us.skill_id,s.name as skill_name,us.power from user_skill us left join users u on us.user_id=u.id left join skill s on us.skill_id=s.id where us.user_id=?");
            preparedStatement.setInt(1,userId);
            preparedStatement.execute();
            
            ResultSet resultSet = preparedStatement.getResultSet();
            
            while (resultSet.next()) {
                UserSkill userSkill = getUserSkill(resultSet);
                
                result.add(userSkill);
            }
            
            connection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    public int addUserSkill(UserSkill u) {
        int value = 0;
        
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user_skill (user_id,skill_id,power) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,u.getUser().getId());
            preparedStatement.setInt(2,u.getSkill().getId());
            preparedStatement.setInt(3,u.getPower());
    
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
    
    public boolean updateUserSkill(UserSkill u) {
        try {
            Connection connection =connect();
            PreparedStatement preparedStatement = connection.prepareStatement("update user_skill set user_id=?,skill_id=?,power=? where id=?");
            preparedStatement.setInt(1,u.getUser().getId());
            preparedStatement.setInt(2,u.getSkill().getId());
            preparedStatement.setInt(3,u.getPower());
            preparedStatement.setInt(4,u.getId());
            
            boolean result = preparedStatement.execute();
            
            connection.close();
            
            return  result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeUserSkill(int id) {
        try {
            Connection connection =connect();
            PreparedStatement preparedStatement = connection.prepareStatement("delete from user_skill where id=?");
            preparedStatement.setInt(1,id);
        
            boolean result = preparedStatement.execute();
        
            connection.close();
        
            return  result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
