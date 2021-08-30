/*
 * ProductDAOJdbcImpl
 * Version 1.0
 * August 21, 2021 
 * Copyright 2021 Tecnologico de Monterrey
 */
package mx.tec.web.lab.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import mx.tec.web.lab.service.CommentsService;
import mx.tec.web.lab.vo.ProductVO;
import mx.tec.web.lab.vo.SkuVO;

/**
 * @author Enrique Sanchez
 *
 */
@Component("jdbc")
public class ProductDAOJdbcImpl implements ProductDAO {
	/** Id field **/
	public static final String ID = "id";
	
	/** Name field **/
	public static final String NAME = "name";
	
	/** Description field **/
	public static final String DESCRIPTION = "description";
	
	/** ChildSkus field **/
	public static final String CHILDSKUS = "childSkus";
	
	/** Color field **/
	public static final String COLOR = "color";
	/** Size field **/
	public static final String SIZE = "size";
	
	public static final String LISTPRICE = "listPrice";
	
	public static final String SALEPRICE = "salePrice";
	
	public static final String QUANTITYONHAND = "quantityOnHand";
	
	public static final String SMALLIMAGEURL = "smallImageUrl";
	
	public static final String MEDIUMIMAGEURL = "mediumImageUrl";
	
	public static final String LARGEIMAGEURL = "largeImageUrl";
	
	public static final String COMMENT = "comment";

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	CommentsService commentService;
	
	@Override
	public List<ProductVO> findAll() {
		String sql = "SELECT id, name, description FROM product";

		return jdbcTemplate.query(sql, (ResultSet rs) -> {
			List<ProductVO> list = new ArrayList<>();

			while(rs.next()){
				String querySkus = "SELECT id,color, size, listPrice, salePrice, quantityOnHand,smallImageUrl,mediumImageUrl, largeImageUrl from Sku WHERE parentProduct_id =?";
				List<SkuVO> childSkus = new ArrayList<>();
				
				jdbcTemplate.query(querySkus,new Object[]{rs.getLong(ID)}, new int[] {java.sql.Types.INTEGER}, (ResultSet skusrs) -> {
					while(skusrs.next()) {
						SkuVO sku = new SkuVO(
								skusrs.getLong(ID),
								skusrs.getString(COLOR), 
								skusrs.getString(SIZE), 
								skusrs.getDouble(LISTPRICE),
								skusrs.getDouble(SALEPRICE),
								skusrs.getLong(QUANTITYONHAND),
								skusrs.getString(SMALLIMAGEURL),
								skusrs.getString(MEDIUMIMAGEURL),
								skusrs.getString(LARGEIMAGEURL)						
								);
						childSkus.add(sku);
							}
				});
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					childSkus,
					commentService.getComments()
				);

				list.add(product);
			}
			
			return list;
		});
	}

	@Override
	public Optional<ProductVO> findById(long id) {
        String sql = "SELECT id, name, description FROM product WHERE id = ?";
        
		return jdbcTemplate.query(sql, new Object[]{id}, new int[]{java.sql.Types.INTEGER}, (ResultSet rs) -> {
			Optional<ProductVO> optionalProduct = Optional.empty();

			if(rs.next()){
				String querySkus = "SELECT id,color, size, listPrice, salePrice, quantityOnHand,smallImageUrl,mediumImageUrl, largeImageUrl from Sku WHERE parentProduct_id =?";
				List<SkuVO> childSkus = new ArrayList<>();
				
				jdbcTemplate.query(querySkus, new Object[]{id}, new int[] {java.sql.Types.INTEGER}, (ResultSet rsChSkus) -> {
						
				while(rsChSkus.next()) {
				SkuVO sku = new SkuVO(
						rsChSkus.getLong(ID),
						rsChSkus.getString(COLOR), 
						rsChSkus.getString(SIZE), 
						rsChSkus.getDouble(LISTPRICE),
						rsChSkus.getDouble(SALEPRICE),
						rsChSkus.getLong(QUANTITYONHAND),
						rsChSkus.getString(SMALLIMAGEURL),
						rsChSkus.getString(MEDIUMIMAGEURL),
						rsChSkus.getString(LARGEIMAGEURL)						
						);
				childSkus.add(sku);
					}
				});
				
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					// Challenge 2
					childSkus,
					commentService.getComments()
				);
				
				optionalProduct = Optional.of(product);
			}
			
			return optionalProduct;
		});
	}

	@Override
	public List<ProductVO> findByNameLike(String pattern) {
		String sql = "SELECT id, name, description FROM product WHERE name like ?";

		return jdbcTemplate.query(sql, new Object[]{"%" + pattern + "%"}, new int[]{java.sql.Types.VARCHAR}, (ResultSet rs) -> {
			List<ProductVO> list = new ArrayList<>();
			

			while(rs.next()){
				String querySkus = "SELECT id,color, size, listPrice, salePrice, quantityOnHand,smallImageUrl,mediumImageUrl, largeImageUrl from Sku WHERE parentProduct_id =?";
				List<SkuVO> childSkus = new ArrayList<>();
				
				jdbcTemplate.query(querySkus,new Object[]{rs.getLong(ID)}, new int[] {java.sql.Types.INTEGER}, (ResultSet skusrs) -> {
					while(skusrs.next()) {
						SkuVO sku = new SkuVO(
								skusrs.getLong(ID),
								skusrs.getString(COLOR), 
								skusrs.getString(SIZE), 
								skusrs.getDouble(LISTPRICE),
								skusrs.getDouble(SALEPRICE),
								skusrs.getLong(QUANTITYONHAND),
								skusrs.getString(SMALLIMAGEURL),
								skusrs.getString(MEDIUMIMAGEURL),
								skusrs.getString(LARGEIMAGEURL)						
								);
						childSkus.add(sku);
							}
				});
				ProductVO product = new ProductVO(
					rs.getLong(ID),
					rs.getString(NAME), 
					rs.getString(DESCRIPTION), 
					childSkus,
					commentService.getComments()
				);
				
				list.add(product);
			}
			
			return list;
		});
	}

	@Override
	public ProductVO insert(ProductVO newProduct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ProductVO existingProduct) {
		// Challenge 4
        String sql = "DELETE FROM product WHERE id = ?";
		jdbcTemplate.query(sql, new Object[]{existingProduct.getId()}, new int[]{java.sql.Types.INTEGER}, (ResultSet rs) -> {});
		
		String queryDeleteSkus = "DELETE FROM Sku WHERE parentProduct_id =?";
		jdbcTemplate.query(queryDeleteSkus, new Object[]{existingProduct.getId()}, new int[] {java.sql.Types.INTEGER}, (ResultSet rsChSkus) -> {});
		

	}

	@Override
	public void update(ProductVO existingProduct) {
		// TODO Auto-generated method stub

	}

}
