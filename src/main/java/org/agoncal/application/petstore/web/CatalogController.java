package org.agoncal.application.petstore.web;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;

import org.agoncal.application.petstore.domain.*;
import org.agoncal.application.petstore.service.CatalogService;
import org.agoncal.application.petstore.util.Loggable;

/**
 * @author Antonio Goncalves http://www.antoniogoncalves.org --
 */

@Named
// @RequestScoped TODO should be request scoped
@SessionScoped
@Loggable
@CatchException
public class CatalogController extends Controller implements Serializable {

    // ======================================
    // = Attributes =
    // ======================================

    @Inject
    private CatalogService catalogService;

    private String categoryName;
    private Item item;
    private Long itemId;

    private List<Item> items;
    private String keyword;
    private Product product;
    private Long productId;
    private List<Product> products;

    // ======================================
    // = Public Methods =
    // ======================================

    public String doFindItem() {
        if (null != itemId) {
            item = catalogService.findItem(itemId);
        }
        return null;
    }

    public String doFindItems() {
        if (null == productId) {
            // AJAX request without leaving the page
            return null;
        }
        product = catalogService.findProduct(productId);
        items = catalogService.findItems(productId);
        return null;
    }

    public void doFindProducts() {
        if (null == categoryName) {
            // AJAX request without leaving the page
            return;
        }
        products = catalogService.findProducts(categoryName);
    }

    public String doSearch(String keyword) {
        this.keyword = keyword;
        items = catalogService.searchItems(keyword);
        return "searchresult.faces";
    }

    // ======================================
    // = Getters & setters =
    // ======================================

    public String getCategoryName() {
        return categoryName;
    }

    public Item getItem() {
        return item;
    }

    public Long getItemId() {
        return itemId;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getKeyword() {
        return keyword;
    }

    public Product getProduct() {
        return product;
    }

    public Long getProductId() {
        return productId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}