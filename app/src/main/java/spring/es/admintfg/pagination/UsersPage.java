package spring.es.admintfg.pagination;

import spring.es.admintfg.dto.UserDTO;

public class UsersPage extends Page<UserDTO> {

    @Override
    public Class<UserDTO> getClazz() {
        return UserDTO.class;
    }
}
