package demo.domain;

import org.springframework.data.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SupplyLocationRepository extends PagingAndSortingRepository<SupplyLocation, String> {

    SupplyLocation findFirstByLocationNear(@Param("location") Point location);
}
