package com.model2.mvc.service.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;

public class ProductDAO {
	
	public ProductDAO(){
	}
	
	public Product getProduct(int prodNo) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "select * from PRODUCT where PROD_NO=?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, prodNo);

		ResultSet rs = stmt.executeQuery();

		Product product = null;
		while (rs.next()) {
			product = new Product();
			product.setProdNo(rs.getInt("PROD_NO"));
			product.setProdName(rs.getString("PROD_NAME"));
			product.setProdDetail(rs.getString("PROD_DETAIL"));
			product.setManuDate(rs.getString("MANUFACTURE_DAY"));
			product.setPrice(rs.getInt("PRICE"));
			product.setFileName(rs.getString("IMAGE_FILE"));
			product.setRegDate(rs.getDate("REG_DATE"));
		}
		
		con.close();

		return product;
	}

//	public HashMap<String,Object> getProductList(Search search) throws Exception {
//		
//		Connection con = DBUtil.getConnection();
//		
//		String sql = "select * from PRODUCT ";
//		if (search.getSearchCondition() != null) {
//			if (search.getSearchCondition().equals("0")) {
//				sql += " where PROD_NO='" + search.getSearchKeyword()
//						+ "'";
//			} else if (search.getSearchCondition().equals("1")) {
//				sql += " where PROD_NAME='" + search.getSearchKeyword()
//						+ "'";
//			} else if (search.getSearchCondition().equals("2")) {
//				sql += " where PRICE='" + search.getSearchKeyword()
//				+ "'";
//	}
//		}
//		sql += " order by PROD_NAME";
//
//		PreparedStatement stmt = 
//			con.prepareStatement(	sql,
//														ResultSet.TYPE_SCROLL_INSENSITIVE,
//														ResultSet.CONCUR_UPDATABLE);
//		ResultSet rs = stmt.executeQuery();
//
//		rs.last();
//		int total = rs.getRow();
//		System.out.println("로우의 수:" + total);
//
//		HashMap<String,Object> map = new HashMap<String,Object>();
//		map.put("count", new Integer(total));
//
//		rs.absolute(search.getCurrentPage() * search.getPageUnit() - search.getPageUnit()+1);
//		System.out.println("search.getPage():" + search.getCurrentPage());
//		System.out.println("search.getPageUnit():" + search.getPageUnit());
//
//		ArrayList<Product> list = new ArrayList<Product>();
//		if (total > 0) {
//			for (int i = 0; i < search.getPageUnit(); i++) {
//				Product vo = new Product();
//				vo.setProdNo(rs.getInt("PROD_No"));
//				vo.setProdName(rs.getString("PROD_NAME"));
//				vo.setProdDetail(rs.getString("PROD_DETAIL"));
//				vo.setManuDate(rs.getString("MANUFACTURE_DAY"));
//				vo.setPrice(rs.getInt("PRICE"));
//				vo.setFileName(rs.getString("IMAGE_FILE"));
//				vo.setRegDate(rs.getDate("REG_DATE"));
//
//				list.add(vo);
//				if (!rs.next())
//					break;
//			}
//		}
//		System.out.println("list.size() : "+ list.size());
//		map.put("list", list);
//		System.out.println("map().size() : "+ map.size());
//
//		con.close();
//			
//		return map;
//	}
//	
	public Map<String , Object> getProductList(Search search) throws Exception {
		
		Map<String , Object>  map = new HashMap<String, Object>();
		
		Connection con = DBUtil.getConnection();
		
		System.out.println("search.getSearchCondition() :"+search.getSearchCondition());
		
		// Original Query 구성
		String sql = "SELECT prod_no, prod_name, price, manufacture_day FROM  product ";
		
		if (search.getSearchCondition() != null) {
			if ( search.getSearchCondition().equals("0") && !search.getSearchKeyword().equals("") ) {
				sql += " WHERE prod_no like '%" + search.getSearchKeyword()+"%'";
			} else if ( search.getSearchCondition().equals("1") && !search.getSearchKeyword().equals("")) {
				sql += " WHERE prod_name like '%" + search.getSearchKeyword()+"%'";
			} else if ( search.getSearchCondition().equals("2") && !search.getSearchKeyword().equals("")) {
				sql += " WHERE price ='" + search.getSearchKeyword()+"'";	
			}
		}
		sql += " ORDER BY prod_name";
		
		System.out.println("ProductDAO::Original SQL :: " + sql);
		
		//==> TotalCount GET
		int totalCount = this.getTotalCount(sql);
		System.out.println("ProductDAO :: totalCount  :: " + totalCount);
		
		//==> CurrentPage 게시물만 받도록 Query 다시구성
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
	
		System.out.println(search);

		List<Product> list = new ArrayList<Product>();
		
		while(rs.next()){
			Product product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setPrice(rs.getInt("price"));
			product.setManuDate(rs.getString("manufacture_day"));
			list.add(product);
		}
		
		//==> totalCount 정보 저장
		map.put("totalCount", new Integer(totalCount));
		//==> currentPage 의 게시물 정보 갖는 List 저장
		map.put("list", list);

		rs.close();
		pStmt.close();
		con.close();

		return map;
	}
	
	// 게시판 Page 처리를 위한 전체 Row(totalCount)  return
		private int getTotalCount(String sql) throws Exception {
			
			sql = "SELECT COUNT(*) "+
			          "FROM ( " +sql+ ") countTable";
			
			Connection con = DBUtil.getConnection();
			PreparedStatement pStmt = con.prepareStatement(sql);
			ResultSet rs = pStmt.executeQuery();
			
			int totalCount = 0;
			if( rs.next() ){
				totalCount = rs.getInt(1);
			}
			
			pStmt.close();
			con.close();
			rs.close();
			
			return totalCount;
		}
		
		// 게시판 currentPage Row 만  return 
		private String makeCurrentPageSql(String sql , Search search){
			sql = 	"SELECT * "+ 
						"FROM (		SELECT inner_table. * ,  ROWNUM AS row_seq " +
										" 	FROM (	"+sql+" ) inner_table "+
										"	WHERE ROWNUM <="+search.getCurrentPage()*search.getPageSize()+" ) " +
						"WHERE row_seq BETWEEN "+((search.getCurrentPage()-1)*search.getPageSize()+1) +" AND "+search.getCurrentPage()*search.getPageSize();
			
			System.out.println("ProductDAO :: make SQL :: "+ sql);	
			
			return sql;
		}

	
	public void insertProduct(Product product) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql ="insert  into  product values(seq_product_prod_no.NEXTVAL,?,?,?,?,?,to_date(sysdate,'YYYY-MM-DD HH24:MI:SS'))"; 
				//"insert into PRODUCT values (?,?,?,?,?,?,?,sysdate)";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		
		stmt.setString(1, product.getProdName());
		stmt.setString(2, product.getProdDetail());
		stmt.setString(3, product.getManuDate().replace("-", ""));
		stmt.setInt(4, product.getPrice());
		stmt.setString(5, product.getFileName());
		
		
		int i = stmt.executeUpdate();
		System.out.println("1번 insert 유뮤 : "+i+" 개 행이 만들어졌습니다.");
		
		con.close();
	}

	

	public void updateProduct(Product product) throws Exception {
		
	    Connection con = DBUtil.getConnection();

	      String sql = "update product set prod_name=?, prod_detail=?, manufacture_day=?, price=?, image_file=? where prod_no=?";
	      
	      PreparedStatement stmt = con.prepareStatement(sql);
	      stmt.setString(1,product.getProdName());
	      stmt.setString(2,product.getProdDetail());
	      stmt.setString(3, product.getManuDate());
	      stmt.setInt(4, product.getPrice());
	      stmt.setString(5, product.getFileName());
	      stmt.setInt(6, product.getProdNo());
	   
	      int i = stmt.executeUpdate();
	      System.out.println("1번 insert 유뮤 : "+i+" 개 행이 수정되었습니다.");
	      
	      con.close();
	}
}