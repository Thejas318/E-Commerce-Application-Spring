package com.project.springbootecommerce.config;

import com.project.springbootecommerce.entity.Product;
import com.project.springbootecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class DataRestConfig implements RepositoryRestConfigurer {

    private EntityManager entityManager;

    public DataRestConfig(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);

        HttpMethod[] unSupportedActions = {HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT};

        //This is to disable the HTTP methods (PUT, POST, GET) for Product entity
        config.getExposureConfiguration()
                .forDomainType(Product.class)
                .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(unSupportedActions)))
                .withCollectionExposure(((metdata, httpMethods) -> httpMethods.disable(unSupportedActions)));

        //This is to disable the HTTP methods (PUT, POST, GET) for ProductCategory entity
        config.getExposureConfiguration()
                .forDomainType(ProductCategory.class)
                .withItemExposure(((metdata, httpMethods) -> httpMethods.disable(unSupportedActions)))
                .withCollectionExposure(((metdata, httpMethods) -> httpMethods.disable(unSupportedActions)));

        //call an internal helper method to expose entity ID's
        exposeIds(config);
    }

    private void exposeIds(RepositoryRestConfiguration config){

        //expose entity Id's

        // get list of all entity classes from the entity manager
        Set<EntityType<?>> entities = this.entityManager.getMetamodel().getEntities();

        // create an arraylist of entity types
        List<Class> entityClasses = new ArrayList<Class>();

        // get the entityTypes for the entities
        for(EntityType tempEntityType: entities){
            entityClasses.add(tempEntityType.getJavaType());
            log.info("Entity Type: ", tempEntityType.getJavaType());
        }

        // expose the entity ID's for the array of entity/domain types
        Class[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}
