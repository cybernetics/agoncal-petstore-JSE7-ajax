package org.agoncal.application.petstore.web;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.*;

import org.agoncal.application.petstore.util.Loggable;

/**
 * @author Stephan Rauh http://www.beyondjava.net
 */

@Named
@SessionScoped
@Loggable
@CatchException
public class NavigationController extends Controller implements Serializable {

    private static final long serialVersionUID = -2228012616376885656L;

    @Inject
    private CatalogController catalog;

    public void showItem(String category, String productID, String itemID) {
        catalog.setCategoryName(category);
        catalog.setProductId(Long.valueOf(productID));
        catalog.setItemId(Long.valueOf(itemID));
        catalog.doFindItem();
    }

    public void showItems(String category, String productID) {
        catalog.setCategoryName(category);
        catalog.setProductId(Long.valueOf(productID));
        catalog.doFindItems();
    }

    public void showProductCategory(String category) {
        catalog.setCategoryName(category);
        catalog.doFindProducts();
    }

}
