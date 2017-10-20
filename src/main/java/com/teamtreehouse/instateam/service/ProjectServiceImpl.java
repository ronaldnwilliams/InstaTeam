package com.teamtreehouse.instateam.service;

import com.teamtreehouse.instateam.dao.GenericDao;
import com.teamtreehouse.instateam.dao.ProjectDao;
import com.teamtreehouse.instateam.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Qualifier;


@Service
public class ProjectServiceImpl extends GenericServiceImpl<Project> implements ProjectService {
    @Autowired
    private ProjectDao projectDao;

    public ProjectServiceImpl(){}

    @Autowired
    public ProjectServiceImpl(@Qualifier("projectDaoImpl")GenericDao<Project> genericDAO) {
        super(genericDAO);
        this.projectDao = (ProjectDao) genericDAO;
    }
}
