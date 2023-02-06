package guru.springframework.sfgrestbrewery.services;

import guru.springframework.sfgrestbrewery.domain.Beer;
import guru.springframework.sfgrestbrewery.repositories.BeerRepository;
import guru.springframework.sfgrestbrewery.web.controller.NotFoundException;
import guru.springframework.sfgrestbrewery.web.mappers.BeerMapper;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import guru.springframework.sfgrestbrewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by jt on 2019-04-20.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Beerserviceimpl implements BeerService {
    
    @Autowired
    private BeerRepository beerRepository;
    
    @Autowired
    private BeerMapper beerMapper;

    @Override
    public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {
        BeerPagedList beerPagedList = new BeerPagedList();
        Page<Beer> beerPage = new Page<>();
        List<BeerDto> beerDtos = new ArrayList<>();

        if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            //search both
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
        } else {

            beerPage = this.getBeersIfNameOrStyleIsEmpty(beerPage);

            // if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
            //     //search beer_service name
            //     beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
            // } else {
            //     if (StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            //         //search beer_service style
            //         beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
            //     } else {
            //         beerPage = beerRepository.findAll(pageRequest);
            //     }
            // }
        }

        for (Beer beer : beerPage.getContent()) {
            if (showInventoryOnHand) {
                beerDtos.add(beerMapper.beerToBeerDtoWithInventory(beer));
                beerPagedList.setContent(beerDtos);
                beerPagedList.setPageable(PageRequest.of(beerPage.getPageable().getPageNumber(), beerPage.getPageable().getPageSize()));
                beerPagedList.setTotalElements(beerPage.getTotalElements());
            } else {
                beerDtos.add(beerMapper.beerToBeerDto(beer));
                beerPagedList.setContent(beerDtos);
                beerPagedList.setPageable(PageRequest.of(beerPage.getPageable().getPageNumber(), beerPage.getPageable().getPageSize()));
                beerPagedList.setTotalElements(beerPage.getTotalElements());
            }
        }

        return beerPagedList;
    }

    private Page<Beer>  getBeersIfNameOrStyleIsEmpty(Page<Beer> beer){
        
        // if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
        //         //search beer_service name
        //         beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
        // } else {
        //     if (StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
        //         //search beer_service style
        //         beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
        //     } else {
        //         beerPage = beerRepository.findAll(pageRequest);
        //     }
        // }

        if(!StringUtils.isEmpty(beerName)){
            if(StringUtils.isEmpty(beerStyle)){
                beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
            }
        }else{
            if(!StringUtils.isEmpty(beerStyle)){
                beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
            }else{
                beerPage = beerRepository.findAll(pageRequest);
            }
        }
    }


    @Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false ")
    @Override
    public BeerDto getById(UUID beerId, Boolean showInventoryOnHand) {
        Beer beer = beerRepository.findById(beerId).orElse(null);
        if (showInventoryOnHand) {
            return beerMapper.beerToBeerDtoWithInventory(beer);
        } else {
            return beerMapper.beerToBeerDto(beer);
        }
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
    }

    @Override
    public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
        Beer beer = beerRepository.findById(beerId).orElseThrow(NotFoundException::new);

        beer.setBeerName(beerDto.getBeerName());
        beer.setBeerStyle(BeerStyleEnum.PILSNER.valueOf(beerDto.getBeerStyle()));
        beer.setPrice(beerDto.getPrice());
        beer.setUpc(beerDto.getUpc());

        Beer save = beerRepository.save(beer);
        
        return save;
    }

    @Cacheable(cacheNames = "beerUpcCache")
    @Override
    public BeerDto getByUpc(String upc) {
        return beerMapper.beerToBeerDto(beerRepository.findByUpc(upc));
    }

    @Override
    public void deleteBeerById(UUID beerId) {
        beerRepository.deleteById(beerId);
    }

    @Override
    public void popularBeerPrice() {

        //This method is to search for most popular beer type and get the average price of that beer type
        String popularBeerType;
        int totalQuantity;
        double averagePrice;

        //todo impl

        System.out.println("The most popular beer type is " + popularBeerType + " with a total quantity of " + totalQuantity + " and an average price of $" + averagePrice);

    }
}
