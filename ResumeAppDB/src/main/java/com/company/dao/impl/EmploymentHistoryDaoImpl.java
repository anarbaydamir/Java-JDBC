package com.company.dao.impl;

import com.company.dao.inter.AbstractDAO;
import com.company.dao.inter.EmploymentHistoryDaoInter;
import com.company.entity.EmploymentHistory;
import com.company.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmploymentHistoryDaoImpl extends AbstractDAO implements EmploymentHistoryDaoInter {
    
    public EmploymentHistory getEmploymentHistory (ResultSet resultSet) throws  Exception {
        String header = resultSet.getString("header");
        String jobDescription = resultSet.getString("job_description");
        Date beginDate= resultSet.getDate("begin_date");
        Date endDate=resultSet.getDate("end_date");
        int userId=resultSet.getInt("user_id");
        
        EmploymentHistory employmentHistory = new EmploymentHistory(null,header,beginDate,endDate,jobDescription,new User(userId));
        
        return employmentHistory;
    }
    
    public List<EmploymentHistory> getAllEmploymentHistoryByUserId (int userId) {
        List<EmploymentHistory> result = new ArrayList<EmploymentHistory>();
        
        try {
            Connection connection = connect();
    
            PreparedStatement preparedStatement = connection.prepareStatement("select * from employment_history where user_id=?");
            preparedStatement.setInt(1,userId);
            preparedStatement.execute();
    
            ResultSet resultSet = preparedStatement.getResultSet();
            
            while(resultSet.next()) {
                EmploymentHistory employmentHistory = getEmploymentHistory(resultSet);
                
                result.add(employmentHistory);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }
    
    public int addEmploymentHistory(EmploymentHistory eh) {
        int value = 0;
        try {
            Connection connection = connect();
            
            PreparedStatement preparedStatement = connection.prepareStatement("insert into employment_history (header,begin_date,end_date,job_description,user_id) values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,eh.getHeader());
            preparedStatement.setDate(2,eh.getBeginDate());
            preparedStatement.setDate(3,eh.getEndDate());
            preparedStatement.setString(4,eh.getJobDescription());
            preparedStatement.setInt(5,eh.getUser().getId());
    
    
            value = preparedStatement.executeUpdate();
    
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                eh.setId(generatedKeys.getInt(1));
            }
    
            connection.close();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    
    public boolean updateEmploymentHistory(EmploymentHistory eh) {
        try {
            Connection connection = connect();
            
            PreparedStatement preparedStatement = connection.prepareStatement("update employment_history header=?,begin_date=?,end_date=?,job_description=?,user_id=? where id=?");
            preparedStatement.setString(1,eh.getHeader());
            preparedStatement.setDate(2,eh.getBeginDate());
            preparedStatement.setDate(3,eh.getEndDate());
            preparedStatement.setString(4,eh.getJobDescription());
            preparedStatement.setInt(5,eh.getUser().getId());
            preparedStatement.setInt(6,eh.getId());
            
            boolean result = preparedStatement.execute();
            
            connection.close();
            
            return result;
            
            
        }
        catch (Exception e ){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeEmploymentHistory(int id) {
        try {
            Connection connection = connect();
        
            PreparedStatement preparedStatement = connection.prepareStatement("delete from employment_history where id=?");
            preparedStatement.setInt(1,id);
        
            boolean result = preparedStatement.execute();
        
            connection.close();
        
            return result;
        
        
        }
        catch (Exception e ){
            e.printStackTrace();
            return false;
        }
    }
}
