package com.model2.mvc.view.product;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;



public class UpdateProductAction extends Action{

	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int prodNo = Integer.parseInt(request.getParameter("prodNo"));
		
		System.out.println("°¡³ª´Ù");
		System.out.println(prodNo);
		Product product=new Product();
		product.setProdNo(prodNo );
		product.setProdName(request.getParameter("prodName"));
		product.setProdDetail(request.getParameter("prodDetail"));
		product.setManuDate(request.getParameter("manuDate"));
		product.setPrice(Integer.parseInt(request.getParameter("price")) );
		product.setFileName(request.getParameter("fileName"));
		
		ProductService service=new ProductServiceImpl();
		service.updateProduct(product);
//		
//		HttpSession session=request.getSession();
//		int sessionNO=((Product)session.getAttribute("product")).getProdNo();
//	
//		if(sessionNO==prodNo){
//			session.setAttribute("product", product);
//		}
		
		return "redirect:/getProduct.do?prodNo="+prodNo;
	}

}
