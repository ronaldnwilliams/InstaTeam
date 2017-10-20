package com.teamtreehouse.instateam.service;

import com.teamtreehouse.instateam.dao.CollaboratorDao;
import com.teamtreehouse.instateam.dao.GenericDao;
import com.teamtreehouse.instateam.model.Collaborator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class CollaboratorServiceImpl extends GenericServiceImpl<Collaborator> implements CollaboratorService {
    @Autowired
    private CollaboratorDao collaboratorDao;

    public CollaboratorServiceImpl(){}

    @Autowired
    public CollaboratorServiceImpl(@Qualifier("collaboratorDaoImpl")GenericDao<Collaborator> genericDAO) {
        super(genericDAO);
        this.collaboratorDao = (CollaboratorDao) genericDAO;
    }
}
