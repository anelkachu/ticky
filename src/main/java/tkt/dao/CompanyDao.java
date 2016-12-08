package tkt.dao;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import tkt.model.Company;

@CacheConfig(cacheNames = "companies")
public interface CompanyDao extends CrudRepository<Company, Long> {

	@Cacheable
	Iterable<Company> findAll();

}
