package org.agoncal.application.petstore.web;

import java.io.Serializable;
import java.util.*;

import javax.enterprise.context.*;
import javax.enterprise.inject.Instance;
import javax.inject.*;

import org.agoncal.application.petstore.domain.*;
import org.agoncal.application.petstore.service.*;
import org.agoncal.application.petstore.util.Loggable;

/**
 * @author Antonio Goncalves http://www.antoniogoncalves.org --
 */

@Named
@ConversationScoped
@Loggable
@CatchException
public class ShoppingCartController extends Controller implements Serializable {

    // ======================================
    // = Attributes =
    // ======================================

    private List<CartItem> cartItems;
    @Inject
    private CatalogService catalogBean;
    @Inject
    private Conversation conversation;
    private CreditCard creditCard = new CreditCard();

    @Inject
    @LoggedIn
    private Instance<Customer> loggedInCustomer;
    private Order order;
    @Inject
    private OrderService orderBean;

    // ======================================
    // = Public Methods =
    // ======================================

    public String addItemToCart(String itemID) {
        final Long id = Long.valueOf(itemID);
        Item item = catalogBean.findItem(id);

        // Start conversation
        if (conversation.isTransient()) {
            cartItems = new ArrayList<CartItem>();
            conversation.begin();
        }

        boolean itemFound = false;
        for (CartItem cartItem : cartItems) {
            // If item already exists in the shopping cart we just change the quantity
            if (cartItem.getItem().equals(item)) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                itemFound = true;
            }
        }
        if (!itemFound) {
            // Otherwise it's added to the shopping cart
            cartItems.add(new CartItem(item, 1));
        }

        return "showcart.faces";
    }

    public String checkout() {
        return "confirmorder.faces";
    }

    public String confirmOrder() {
        order = orderBean.createOrder(getCustomer(), creditCard, getCartItems());
        cartItems.clear();

        // Stop conversation
        if (!conversation.isTransient()) {
            conversation.end();
        }

        return "orderconfirmed.faces";
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public CreditCardType[] getCreditCardTypes() {
        return CreditCardType.values();
    }

    public Customer getCustomer() {
        return loggedInCustomer.get();
    }

    // ======================================
    // = Getters & setters =
    // ======================================

    public Order getOrder() {
        return order;
    }

    public Float getTotal() {

        if ((cartItems == null) || cartItems.isEmpty()) {
            return 0f;
        }

        Float total = 0f;

        // Sum up the quantities
        for (CartItem cartItem : cartItems) {
            total += (cartItem.getSubTotal());
        }
        return total;
    }

    public String removeItemFromCart(String itemId) {
        Item item = catalogBean.findItem(Long.valueOf(itemId));

        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().equals(item)) {
                cartItems.remove(cartItem);
                return null;
            }
        }

        return null;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean shoppingCartIsEmpty() {
        return (getCartItems() == null) || (getCartItems().size() == 0);
    }

    public String updateQuantity() {
        return null;
    }
}