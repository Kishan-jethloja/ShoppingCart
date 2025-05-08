package e_commerceApp.shoppingcart.dto;

import java.math.BigDecimal;

public class ResponseOrderDTO {

	  private BigDecimal amount;
	    private int invoiceNumber;
	    private String date;
	    private String orderDescription;
	    private int orderId;

	    public BigDecimal getAmount() {
	        return amount;
	    }

	    public void setAmount(BigDecimal amount) {
	        this.amount = amount;
	    }

	    public int getInvoiceNumber() {
	        return invoiceNumber;
	    }

	    public void setInvoiceNumber(int invoiceNumber) {
	        this.invoiceNumber = invoiceNumber;
	    }

	    public String getDate() {
	        return date;
	    }

	    public void setDate(String date) {
	        this.date = date;
	    }

	    public String getOrderDescription() {
	        return orderDescription;
	    }

	    public void setOrderDescription(String orderDescription) {
	        this.orderDescription = orderDescription;
	    }

	    public int getOrderId() {
	        return orderId;
	    }

	    public void setOrderId(int orderId) {
	        this.orderId = orderId;
	    }
}
