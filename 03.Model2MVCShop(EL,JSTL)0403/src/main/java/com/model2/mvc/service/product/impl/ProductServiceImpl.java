package com.model2.mvc.service.product.impl;

import java.util.HashMap;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.dao.ProductDAO;


public class ProductServiceImpl implements ProductService{
	
	private ProductDAO productDAO;
	
	public ProductServiceImpl() {
		productDAO=new ProductDAO();
	}

	public void addProduct(Product product) throws Exception {
		productDAO.insertProduct(product);
	}
	
	public Product getProduct(int prodNo) throws Exception {
		return productDAO.getProduct(prodNo);
	}
	
	public HashMap<String,Object> getProductList(Search search) throws Exception {
		return (HashMap<String, Object>) productDAO.getProductList(search);
	}
	
	public void updateProduct(Product product) throws Exception {
		productDAO.updateProduct(product);
	}

	

	
}