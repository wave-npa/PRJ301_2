/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.DBUtils;

/**
 *
 * @author Admin
 */
public class ProductDAO {
    private static final String LOWER = "update Product set stock = ? where ProID = ?";

    private static final String SELECT = "select * from Product where ProBrand like ? and ProType like ? and Price <= ? and Stock > ? and Sale >= ? and Size like ? and Color like ? and ProName like ?";
    private static final String CREATE = "insert into Product values(?, ?, ?, ?, ?, ?, ? ,? ,?, ?)";
    private static final String READ = "select * from Product where ProID = ? and Stock > 0";
    private static final String DELETE = "update Product set stock = 0 where ProId = ?";
    private static final String UPDATE = "update Product set ProBrand = ?, ProType = ?, price = ?, sale = ?, stock = ?, ageGroup = ?, size = ?, color = ?, ProName = ? where ProID = ?";
    
    public List<ProductDTO> select (ProductDTO selector, String max, String min) throws SQLException {
        List<ProductDTO> list;
        Connection con = DBUtils.getConnection();
        PreparedStatement stm = con.prepareStatement(SELECT + " and AgeGroup in" + selector.toData(max, min));
        stm.setString(1, "%"+selector.getProBrand()+"%");
        stm.setString(2, "%"+selector.getProType()+"%");
        stm.setFloat(3, (float)selector.getPrice());
        stm.setInt(4, selector.getStock());
        stm.setFloat(5, (float)selector.getSale());
        stm.setString(6, "%"+ selector.getSize() +"%");
        stm.setString(7, "%"+ selector.getColor() +"%");
        stm.setString(8, "%"+ selector.getName()+"%");
        ResultSet rs = stm.executeQuery();
        
        list = new ArrayList<>();
        while (rs.next()) {

            ProductDTO product = new ProductDTO();
            product.setName(rs.getString("ProName"));
            product.setProID(rs.getString("ProId"));
            product.setProBrand(rs.getString("ProBrand"));
            product.setProType(rs.getString("ProType"));
            product.setPrice(rs.getFloat("price"));
            product.setSale(rs.getFloat("sale"));
            product.setStock((int) rs.getFloat("stock"));
            product.setAgeGroup(rs.getString("ageGroup"));
            product.setSize(rs.getString("size"));
            product.setColor(rs.getString("color"));
            list.add(product);
        }

        rs.close();
        stm.close();
        con.close();
        return list;
    }

    public int create(ProductDTO product) throws SQLException {
        Connection con = DBUtils.getConnection();
        PreparedStatement stm = con.prepareStatement(CREATE);
        
        stm.setString(1, product.getProID());
        stm.setString(2, product.getProBrand());
        stm.setString(3, product.getProType());
        stm.setFloat(4, (float) product.getPrice());
        stm.setFloat(5, (float) product.getSale());
        stm.setFloat(6, product.getStock());
        stm.setString(7, product.getAgeGroup());
        stm.setString(8, product.getSize());
        stm.setString(9, product.getColor());
        stm.setString(10, product.getName());
        
        int count = stm.executeUpdate();

        stm.close();
        con.close();
        return count;
    }

    public ProductDTO read(String id) throws SQLException {
        ProductDTO product = null;
        Connection con = DBUtils.getConnection();
        PreparedStatement stm = con.prepareStatement(READ);
        stm.setString(1, id);
        ResultSet rs = stm.executeQuery();
        
        if (rs.next()) {
            product = new ProductDTO();
            product.setProID(rs.getString("ProId"));
            product.setProBrand(rs.getString("ProBrand"));
            product.setProType(rs.getString("ProType"));
            product.setPrice(rs.getDouble("price"));
            product.setSale(rs.getInt("sale"));
            product.setStock(rs.getInt("stock"));
            product.setAgeGroup(rs.getString("ageGroup"));
            product.setSize(rs.getString("size"));
            product.setColor(rs.getString("color"));
            product.setName(rs.getString("ProName"));
        }
        rs.close();
        stm.close();
        con.close();

        return product;
    }

    public int update(ProductDTO product) throws SQLException {
        //Tạo connection để kết nối vào DBMS
        Connection con = DBUtils.getConnection();
        //Tạo đối tượng PreparedStatement
        PreparedStatement stm = con.prepareStatement(UPDATE);
        stm.setString(1, product.getProBrand());
        stm.setString(2, product.getProType());
        stm.setFloat(3, (float) product.getPrice());
        stm.setFloat(4, (float) product.getSale());
        stm.setFloat(5, product.getStock());
        stm.setString(6, product.getAgeGroup());
        stm.setString(7, product.getSize());
        stm.setString(8, product.getColor());
        stm.setString(9, product.getName());
        stm.setString(10, product.getProID());
        //Thực thi lệnh sql
        int count = stm.executeUpdate();
        //Đóng kết nối
        stm.close();
        con.close();
        
        return count;
    }

    public int delete(String id) throws SQLException {
        //Tạo connection để kết nối vào DBMS
        Connection con = DBUtils.getConnection();
        //Tạo đối tượng PreparedStatement
        PreparedStatement stm = con.prepareStatement(DELETE);
        stm.setString(1, id);
        //Thực thi lệnh sql
        int count = stm.executeUpdate();
        stm.close();
        con.close();
        return count;
    }

    public int lowerStock(HashMap<ProductDTO, Integer> cart) throws SQLException {
        Connection con = DBUtils.getConnection();
        PreparedStatement stm = con.prepareStatement(LOWER);
        int count = 0;
        for (ProductDTO i : cart.keySet()) {
            stm.setFloat(1, (i.getStock() - cart.get(i)));
            stm.setString(2, i.getProID());
            count += stm.executeUpdate();
        }
        stm.close();
        con.close();
        return count;
    }
}
