package com.teamtreehouse.instateam.dao;

import com.teamtreehouse.instateam.model.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Hibernate;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role> implements RoleDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Role findById(Long id) {
        Session session = sessionFactory.openSession();
        Role role = session.get(Role.class, id);
        Hibernate.initialize(role.getCollaborators());
        session.close();
        return role;
    }
}

