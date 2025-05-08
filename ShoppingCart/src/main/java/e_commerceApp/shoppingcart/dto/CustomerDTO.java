package e_commerceApp.shoppingcart.dto;

public class CustomerDTO {
    private int id;
    private String name;
    private String email;

    public CustomerDTO() {}

    public CustomerDTO(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public static CustomerDTO fromEntity(e_commerceApp.shoppingcart.entity.Customer customer) {
        return new CustomerDTO(
            customer.getId(),
            customer.getName(),
            customer.getEmail()
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
} 