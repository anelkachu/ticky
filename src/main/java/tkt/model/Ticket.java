package tkt.model;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by inre on 15/09/2016.
 */
public class Ticket {

	private final String batchTicketId;
	private final long date;
	private final Double totalAmount;
	private final String currency;
	private final int companyId;
	private final String shopId;

	public Ticket(String batchTicketId, int companyId, long date, Double totalAmount, String currency, String shopId) {
		this.batchTicketId = batchTicketId;
		this.companyId = companyId;
		this.date = date;
		this.totalAmount = totalAmount;
		this.currency = currency;
		this.shopId = shopId;
	}

	public long getDate() {
		return date;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public String getBatchTicketId() {
		return batchTicketId;
	}

	public int getCompanyId() {
		return companyId;
	}

	public String getShopId() {
		return shopId;
	}

	public static Ticket generateTicketFromMap(Map<String, Object> map) {
		String totalText = (String) map.get("Facturae.FileHeader.Batch.TotalExecutableAmount.TotalAmount");
		Double totalAmount = Double.parseDouble(totalText);
		String currency = (String) map.get("Facturae.FileHeader.Batch.InvoiceCurrencyCode");
		String stringDate = map.get("Facturae.Invoices.Invoice.AdditionalData.TicketTimestamp").toString();
		Long date = Long.parseLong(stringDate);
		String batchTicketId = (String) map.get("Facturae.FileHeader.Batch.BatchIdentifier");
		BigDecimal companyId = (BigDecimal) map.get("Facturae.Invoices.Invoice.AdditionalData.CompanyId");
		String shopId = (String) map.get("Facturae.Invoices.Invoice.AdditionalData.ShopId");
		return new Ticket(batchTicketId, companyId.intValue(), date, totalAmount, currency, shopId);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
