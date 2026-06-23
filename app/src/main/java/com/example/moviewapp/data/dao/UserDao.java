package com.example.moviewapp.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.moviewapp.data.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    UserEntity login(String email, String password);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getUserById(int id);
}