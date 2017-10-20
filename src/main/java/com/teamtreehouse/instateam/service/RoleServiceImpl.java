package com.teamtreehouse.instateam.service;

import com.teamtreehouse.instateam.dao.GenericDao;
import com.teamtreehouse.instateam.dao.RoleDao;
import com.teamtreehouse.instateam.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class RoleServiceImpl extends GenericServiceImpl<Role> implements RoleService{

    @Autowired
    private RoleDao roleDao;

    public RoleServiceImpl(){}

    @Autowired
    public RoleServiceImpl(@Qualifier("roleDaoImpl")GenericDao<Role> genericDAO) {
        super(genericDAO);
        this.roleDao = (RoleDao) genericDAO;
    }
}
