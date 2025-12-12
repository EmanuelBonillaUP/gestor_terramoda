/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.terramoda.gestor_terramoda;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

class PaginationResult<T> {

  public List<T> items;
  public int items_count;
  public int total_items;
  public int current_page;
  public int per_page;
}

class Customer {

  public int id;
  public String cc;
  public String name;
  public String email;
  public String phone;
  public String direction;
}

class CustomerToCreate {

  public String cc;
  public String name;
  public String email;
  public String phone;
  public String direction;
}

class CustomerCreated {

  public int customer_id;
}

class CustomerToEdit {

  public String name;
  public String email;
  public String phone;
}

class Product {

  public int id;
  public String sku;
  public String name;
  public float price;
  public int stock;
  public String description;

  public List<String> flags;
  public String img_url;
}

class ProductToEdit {

  public String name;
  public String description;
  public float price;
  public int stock;

  public String img_url;
  public List<String> flags;
}

class ProductToCreate {
  public String sku;
  public String name;
  public float price;
  public int stock;
  public String description;

  public List<String> flags;

}

class ProductCreated {

  public int product_id;
}

class ProductSku {

  public String sku;
  public int quantity;
}

class SaleToCreate {

  public String customer_cc;
  public List<ProductSku> product_skus_quantity;
}

class SaleCreated {

  public String sale_id;
  public float total_amount;
}

class ProductQuantity {

  public Product product;
  public int quantity;
}

class Sale {

  public int id;
  public Customer customer;
  public List<ProductQuantity> products;
  public float total_amount;
  public String generated_at;
}

class Login {

  public String user;
  public String pass;
}

class LoginResponse {

  public String key;
}

/**
 *
 * @author Emanuel Bonilla
 */
public class GestorVentasClient {

  String uri;
  HttpClient client;
  ObjectMapper mapper;
  String key;

  public GestorVentasClient(String uri) {
    this.uri = uri;
    this.client = HttpClient.newHttpClient();
    this.mapper = new ObjectMapper();
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  static String authPath() {
    return "/login";
  }

  public <I> HttpRequest requestBuilder(String path, String method, Optional<I> data) throws Exception {
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(URI.create(this.uri + path));

    if (!path.equals(authPath())) {
      requestBuilder.header("API_KEY", key);
    }
    // Serializar data si está presente
    if (data.isPresent()) {
      String json = mapper.writeValueAsString(data.get());
      requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(json))
          .header("Content-Type", "application/json");
    } else {
      requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
    }

    return requestBuilder.build();
  }

  public <I> HttpResponse<String> requestCommon(String path, String method, Optional<I> data) throws Exception {
    HttpResponse<String> response = client.send(requestBuilder(path, method, data),
        HttpResponse.BodyHandlers.ofString());
    return response;
  }

  public <O, I> O request(String path, String method, Optional<I> data, TypeReference<O> typeRef) throws Exception {

    var response = this.requestCommon(path, method, data);
    if (response.statusCode() >= 200 && response.statusCode() < 300) {
      // Deserializamos la respuesta a O
      return mapper.readValue(response.body(), typeRef);
    } else {
      throw new RuntimeException("Error en la petición: " + response.statusCode() + " - " + response.body());
    }
  }

  // CUSTOMERS
  public CustomerCreated createCustomer(CustomerToCreate toCreate) throws Exception {
    return this.request(
        "/customers",
        "POST",
        Optional.of(toCreate),
        new TypeReference<CustomerCreated>() {
        });
  }

  public void editCustomer(int id, CustomerToEdit toEdit) throws Exception {
    this.requestCommon(
        "/customers/" + id,
        "PUT",
        Optional.of(toEdit));
  }

  public PaginationResult<Customer> getPaginatedCustomers(int page, int perPage) throws Exception {
    return this.request(
        "/customers?page=" + page + "&per_page=" + perPage,
        "GET",
        Optional.empty(),
        new TypeReference<PaginationResult<Customer>>() {
        });
  }

  public Customer getCustomerByCC(String cc) throws Exception {
    return this.request(
        "/customers/?cc=" + cc,
        "GET",
        Optional.empty(),
        new TypeReference<Customer>() {
        });
  }

  // PRODUCTS
  public ProductCreated createProduct(ProductToCreate toCreate) throws Exception {
    toCreate.flags = toCreate.flags == null ? List.of() : toCreate.flags;
    return this.request(
        "/products",
        "POST",
        Optional.of(toCreate),
        new TypeReference<ProductCreated>() {
        });
  }

  public void editProduct(int id, ProductToEdit toEdit) throws Exception {
    toEdit.flags = toEdit.flags == null ? List.of() : toEdit.flags;
    this.requestCommon(
        "/products/" + id,
        "PUT",
        Optional.of(toEdit));
  }

  public PaginationResult<Product> getPaginatedProducts(int page, int perPage) throws Exception {
    return this.request(
        "/products?page=" + page + "&per_page=" + perPage,
        "GET",
        Optional.empty(),
        new TypeReference<PaginationResult<Product>>() {
        });
  }

  public Product getProductBySKU(String sku) throws Exception {
    return this.request(
        "/products/?sku=" + sku,
        "GET",
        Optional.empty(),
        new TypeReference<Product>() {
        });
  }

  // SALES
  public SaleCreated createSale(SaleToCreate toCreate) throws Exception {
    return this.request(
        "/sales",
        "POST",
        Optional.of(toCreate),
        new TypeReference<SaleCreated>() {
        });
  }

  public PaginationResult<Sale> getPaginatedSales(int page, int perPage) throws Exception {
    return this.request(
        "/sales?page=" + page + "&per_page=" + perPage,
        "GET",
        Optional.empty(),
        new TypeReference<PaginationResult<Sale>>() {
        });
  }

  public Sale getSaleById(int id) throws Exception {
    return this.request(
        "/sales/" + id,
        "GET",
        Optional.empty(),
        new TypeReference<Sale>() {
        });
  }

  public void login(Login data) throws Exception {
    this.key = this.request(
        authPath(),
        "POST",
        Optional.of(data),
        new TypeReference<LoginResponse>() {
        }).key;
  }

  public void genearteReportCsv(String path) throws Exception {
    var request = this.requestBuilder("/reports/csv", "GET", Optional.empty());
    var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    Files.write(Path.of(path), response.body());
  }
}
