package org.agoncal.application.petstore.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.*;

import org.agoncal.application.petstore.domain.Customer;
import org.agoncal.application.petstore.exception.ValidationException;
import org.agoncal.application.petstore.util.Loggable;

/**
 * @author Antonio Goncalves http://www.antoniogoncalves.org --
 */

@Stateless
@Loggable
public class CustomerService implements Serializable {

    // ======================================
    // = Attributes =
    // ======================================

    @Inject
    private EntityManager em;

    // ======================================
    // = Public Methods =
    // ======================================

    public Customer createCustomer(final Customer customer) {

        if (customer == null) {
            throw new ValidationException("Customer object is null");
        }

        em.persist(customer);

        return customer;
    }

    public boolean doesLoginAlreadyExist(@NotNull @Size(min = 4) final String login) {

        if (login == null) {
            throw new ValidationException("Login cannot be null");
        }

        // Login has to be unique
        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_BY_LOGIN, Customer.class);
        typedQuery.setParameter("login", login);
        try {
            typedQuery.getSingleResult();
            return true;
        }
        catch (NoResultException e) {
            return false;
        }
    }

    public List<Customer> findAllCustomers() {
        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return typedQuery.getResultList();
    }

    public Customer findCustomer(final String login) {

        if (login == null) {
            throw new ValidationException("Invalid login");
        }

        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_BY_LOGIN, Customer.class);
        typedQuery.setParameter("login", login);

        try {
            return typedQuery.getSingleResult();
        }
        catch (NoResultException e) {
            return null;
        }
    }

    public Customer findCustomer(
            @NotNull(message = "User name must be at least four characters long.") @Size(min = 4, message = "User name must be at least four characters long.") final String login,
            @NotNull(message = "User name must be at least eight characters long.") @Size(min = 4, message = "Password must be at least eight characters long.") final String password) {

        TypedQuery<Customer> typedQuery = em.createNamedQuery(Customer.FIND_BY_LOGIN_PASSWORD, Customer.class);
        typedQuery.setParameter("login", login);
        typedQuery.setParameter("password", password);
        List<Customer> results = typedQuery.getResultList();
        if (results.size() == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new NonUniqueResultException("Too many matching customers found.");
        }

        return results.get(0);
    }

    public void removeCustomer(final Customer customer) {
        if (customer == null) {
            throw new ValidationException("Customer object is null");
        }

        em.remove(em.merge(customer));
    }

    public Customer updateCustomer(final Customer customer) {

        // Make sure the object is valid
        if (customer == null) {
            throw new ValidationException("Customer object is null");
        }

        // Update the object in the database
        em.merge(customer);

        return customer;
    }
}
