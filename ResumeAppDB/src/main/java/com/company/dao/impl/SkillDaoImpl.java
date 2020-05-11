package com.company.dao.impl;

import com.company.dao.inter.AbstractDAO;
import com.company.dao.inter.SkillDaoInter;
import com.company.entity.Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SkillDaoImpl extends AbstractDAO implements SkillDaoInter {
    
    private Skill getSkill (ResultSet resultSet) throws Exception {
        int id = resultSet.getInt("id");
        String name=resultSet.getString("name");
        
        Skill skill = new Skill(id,name);
        
        return skill;
    }
    public List<Skill> getAllSkills () {
        List<Skill> result = new ArrayList<Skill>();
        
        try {
            Connection connection = connect();
            Statement statement = connection.createStatement();
            
            statement.execute("select * from skill");
    
            ResultSet resultSet = statement.getResultSet();
            
            while (resultSet.next()) {
                Skill skill = getSkill(resultSet);
                
                result.add(skill);
            }
            
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public Skill getById(int id) {
        Skill skill = null;
        try {
            Connection connection = connect();
        
            Statement statement =connection.createStatement();
            statement.execute("select * from skill where id="+id);
        
            ResultSet resultSet = statement.getResultSet();
        
            while (resultSet.next()) {
                skill = getSkill(resultSet);
            }
        
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return skill;
    }
    
    public int addCountry(Skill s) {
        int value= 0;
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into skill(name) values(?)",Statement.RETURN_GENERATED_KEYS);
        
            preparedStatement.setString(1,s.getName());
        
            value = preparedStatement.executeUpdate();
        
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                s.setId(generatedKeys.getInt(1));
            }
        
            connection.close();
        
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    
        return  value;
    }
    
    public boolean updateCountry(Skill s) {
       try {
           Connection connection = connect();
           PreparedStatement preparedStatement = connection.prepareStatement("update skill name=? where id=?");
           preparedStatement.setString(1,s.getName());
           preparedStatement.setInt(2,s.getId());
           
           boolean result = preparedStatement.execute();
           
           connection.close();
           
           return result;
       }
       catch (Exception e) {
           e.printStackTrace();
           return  false;
       }
    }
    
    public boolean removeCountry(int id) {
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("delete from skill where id=?");
            preparedStatement.setInt(1,id);
        
            boolean result = preparedStatement.execute();
        
            connection.close();
        
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }
}
