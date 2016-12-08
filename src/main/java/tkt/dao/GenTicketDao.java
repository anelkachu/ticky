package tkt.dao;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import tkt.model.Company;
import tkt.model.GenTicket;

@CacheConfig(cacheNames = "genTickets")
public interface GenTicketDao extends CrudRepository<GenTicket, Long> {

	@Cacheable
	Iterable<GenTicket> findAll();

	GenTicket findById(String ticketId);

}
