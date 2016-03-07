package netgloo.models;


import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface AccountDao extends CrudRepository<Account, Long> {

    /**
     * This method will find an User instance in the database by its email.
     * Note that this method is not implemented and its working code will be
     * automagically generated from its signature by Spring Data JPA.
     */
    public Account findBy_id(String _id);

    public Account findByusername(String username);

}
