package com.mgl.demo.popularpurchases.dawps.client;

import com.mgl.demo.popularpurchases.dawps.client.DawPurchasesClient;
import com.mgl.demo.popularpurchases.dawps.client.ProductNotFoundException;
import com.mgl.demo.popularpurchases.dawps.client.UserNotFoundException;

import static com.mgl.demo.popularpurchases.dawps.client.DawPurchasesClient.USE_HTTP;

import java.util.List;

import com.mgl.demo.popularpurchases.dawps.client.model.Product;
import com.mgl.demo.popularpurchases.dawps.client.model.Purchase;
import com.mgl.demo.popularpurchases.dawps.client.model.User;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DawPurchasesClientIT {

    private static DawPurchasesClient client;

    @BeforeClass
    public static void setUpClass() {
        client = new DawPurchasesClient(USE_HTTP, "74.50.59.155", 6000);
    }

    @AfterClass
    public static void tearDownClass() {
        client.dispose();
        client = null;
    }

    @Test
    public void testGetUsers() {
        List<User> users = client.getUsers(10);
        Assert.assertFalse(users.isEmpty());
        Assert.assertTrue(users.size() <= 10);
        users.forEach(System.out::println);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetInexistingUser() throws UserNotFoundException {
        client.getUser("hopethereisnosuchusername"); // cross fingers :)
    }

    @Test
    public void testGetProducts() {
        List<Product> products = client.getProducts(10);
        Assert.assertFalse(products.isEmpty());
        Assert.assertTrue(products.size() <= 10);
        products.forEach(System.out::println);
    }

    @Test
    public void testGetProduct() {
        List<Product> products = client.getProducts(2);
        Assert.assertFalse(products.isEmpty());
        Assert.assertTrue(products.size() <= 2);
        products.forEach(p -> {
            try {
                Product product = client.getProduct(p.getId());
                System.out.println(product);
            } catch (ProductNotFoundException ex) {
                Assert.fail(String.valueOf(ex));
            }
        });
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetInexistingProduct() throws ProductNotFoundException {
        client.getProduct(123456714); // cross fingers :)
    }

    @Test
    public void testGetPurchasesByUser() {
        List<User> users = client.getUsers(2);
        Assert.assertFalse(users.isEmpty());
        Assert.assertTrue(users.size() <= 2);
        users.forEach(user -> {
            List<Purchase> purchases = client.getPurchasesByUser(user.getUsername(), 4);
            Assert.assertFalse(purchases.isEmpty());
            purchases.forEach(System.out::println);
        });
    }

    @Test
    public void testGetPurchasesByProduct() {
        List<Product> products = client.getProducts(2);
        Assert.assertFalse(products.isEmpty());
        Assert.assertTrue(products.size() <= 2);
        products.forEach(product -> {
            List<Purchase> purchases = client.getPurchasesByProduct(product.getId(), 4);
            Assert.assertFalse(purchases.isEmpty());
            purchases.forEach(System.out::println);
        });
    }

}
