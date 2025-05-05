package com.example.quanlybenhvien.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quanlybenhvien.Entity.TrieuChung;

@Repository
public interface TrieuChungDao extends JpaRepository<TrieuChung, Integer> {
    List<TrieuChung> findByTenTrieuChungContainingIgnoreCase(String tenTrieuChung);
}
