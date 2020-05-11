package com.company.dao.impl;

import com.company.dao.inter.AbstractDAO;
import com.company.dao.inter.CountryDaoInter;
import com.company.entity.Country;
import com.company.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CountryDaoImpl extends AbstractDAO implements CountryDaoInter {
    
    private Country getCountry(ResultSet resultSet) throws Exception {
        int id = resultSet.getInt("id");
        String name=resultSet.getString("name");
        String nationality = resultSet.getString("nationality");
        
        return new Country(id,name,nationality);
    }
    public List<Country> getAllCountries () {
        List<Country> result = new ArrayList<Country>();
        
        try {
            Connection connection = connect();
    
            Statement statement = connection.createStatement();
            statement.execute("select * from country");
    
            ResultSet resultSet = statement.getResultSet();
            
            while (resultSet.next()) {
                Country country = getCountry(resultSet);
                
                result.add(country);
            }
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public Country getById(int id) {
        Country country = null;
        try {
            Connection connection = connect();
            
            Statement statement =connection.createStatement();
            statement.execute("select * from country where id="+id);
            
            ResultSet resultSet = statement.getResultSet();
            
            while (resultSet.next()) {
                country = getCountry(resultSet);
            }
            
            connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return country;
    }
    
    public int addCountry(Country c) {
        int value= 0;
        try {
            Connection connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into country(name,nationality) values(?,?)",Statement.RETURN_GENERATED_KEYS);
            
            preparedStatement.setString(1,c.getName());
            preparedStatement.setString(2,c.getNationality());
            
            value = preparedStatement.executeUpdate();
    
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                c.setId(generatedKeys.getInt(1));
            }
            
            connection.close();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return  value;
    }
    
    public boolean updateCountry(Country c) {
        try {
            Connection connection = connect();
            
            PreparedStatement preparedStatement = connection.prepareStatement("update country set name=?,nationality=? where id=?");
            preparedStatement.setString(1,c.getName());
            preparedStatement.setString(2,c.getNationality());
            preparedStatement.setInt(3,c.getId());
            
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
    
           PreparedStatement preparedStatement = connection.prepareStatement("delete from country where id=?");
           preparedStatement.setInt(1,id);
           
           boolean result = preparedStatement.execute();
           
           connection.close();
           return result;
       }
       catch (Exception e) {
           e.printStackTrace();
           return false;
       }
    }
}
