package guru.springframework.sfgrestbrewery.services;

import guru.springframework.sfgrestbrewery.web.model.CustomerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by jt on 2019-04-21.
 */
@Slf4j
@Service
public class Customerserviceimpl implements CustomerService {



    @Override
    public CustomerDto getCustomerById(UUID customerId) {
        return CustomerDto.builder()
                .id(UUID.randomUUID())
                .name("Joe Buck")
                .build();
    }

    @Override
    public CustomerDto saveNewCustomer(CustomerDto customerDto) {
        return CustomerDto.builder()
                .id(UUID.randomUUID())
                .build();
    }

    @Override
    public void updateCustomer(UUID customerId, CustomerDto customerDto) {
        //todo impl
        log.debug("Updating....");
    }

    @Override
    public void deleteById(UUID customerId) {
        log.debug("Deleting.... ");
    }

    @Override
    public List<CustomerDto> getSeniorCustomer(int N) {
        //todo impl
        //Collections.sort(allCustomers, new ProductSortingComparator());
        //List<CustomerDto> seniorCustomers = allCustomers.subList(0,N);

        //Stream<CustomerDto> seniorCustomers = allCustomers.stream();

        List<Product> seniorCustomers = allCustomers
                .stream()
                .sorted(Comparator.comparing(CustomerDto::getCustomerByBirthday))
                .collect(Collectors.toList())
                .subList(0,N);

        //This function is to get the customer sorted by their age and only retrieve the first N
        log.debug("Retrieving....");
    }
}

public class ProductSortingComparator implements Comparator<Product> {

	@Override
	public int compare(Product prod1, Product prod2) {
		return Long.valueOf(prod1.getProdCreatedDate().getTime())
				.compareTo(prod2.getProdCreatedDate().getTime());
	}
}