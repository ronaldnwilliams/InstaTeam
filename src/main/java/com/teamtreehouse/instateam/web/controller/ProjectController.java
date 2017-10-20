package com.teamtreehouse.instateam.web.controller;

import static com.teamtreehouse.instateam.model.Project.projectComparator;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.web.StatusCode;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.ProjectService;
import com.teamtreehouse.instateam.service.RoleService;
import com.teamtreehouse.instateam.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    // list all projects view index page
    @RequestMapping(value = {"", "/", "/index", "/projects"})
    @SuppressWarnings("unchecked")
    public String listProjects(Model model) {
        List<Project> projects = projectService.findAll();
        projects.sort(projectComparator);
        model.addAttribute("projects", projects);
        return "/projects/index";
    }

    // detail page for single project
    @RequestMapping("/projects/{projectId}")
    public String projectDetails(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId);
        Map<Role, Collaborator> rolesAssignment = getRoleCollaboratorMap(project);

        model.addAttribute("project", project);
        model.addAttribute("rolesAssignment", rolesAssignment);
        return "/projects/details";
    }

    // form for new projects
    @RequestMapping("/projects/add")
    public String formNewProject(Model model) {
        List<Role> roles = roleService.findAll();
        if(!model.containsAttribute("project")) {
            Project project = new Project();

            // Create a default role called unassigned to be used when editing collaborators
            Role defaultRole = new Role();
            defaultRole.setName("Unassigned");
            project.getRolesNeeded().add(defaultRole);

            model.addAttribute("project", project);
        }
        model.addAttribute("action","/projects");
        model.addAttribute("submit", "Save");
        model.addAttribute("statuses", StatusCode.values());
        model.addAttribute("roles", roleService.findAll());
        return "projects/form";
    }

    // form to edit project
    @RequestMapping("/projects/{projectId}/edit")
    public String formEditProject(@PathVariable Long projectId, Model model) {
        List<Role> roles = roleService.findAll();

        if (!model.containsAttribute("project")) {
            model.addAttribute("project", projectService.findById(projectId));
        }
        model.addAttribute("roles", roles);
        model.addAttribute("statuses", StatusCode.values());
        model.addAttribute("submit", "Update");
        model.addAttribute("action", String.format("/projects/%s", projectId));
        return "projects/form";
    }

    // add new project
    @RequestMapping(value = "/projects", method = RequestMethod.POST)
    public String addProject(Project project, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);
            redirectAttributes.addFlashAttribute("project",project);
            return "redirect:/projects/add";
        }
        projectService.save(project);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Project successfully added!", FlashMessage.Status.SUCCESS));
        return "redirect:/projects";
    }

    // edit collaborators for project
    @RequestMapping("/projects/{projectId}/collaborators")
    public String editCollaborators(Model model, @PathVariable Long projectId) {
        Project project = projectService.findById(projectId);
        model.addAttribute("project", project);
        return "projects/collaborators";
    }

    // assign collaborators for a project
    @RequestMapping(value = "/projects/{projectId}/collaborators/update", method = RequestMethod.POST)
    public String assignCollaborators(@PathVariable Long projectId, Project project, RedirectAttributes redirectAttributes){
        Project savedProject = projectService.findById(projectId);
        savedProject.setCollaborators(project.getCollaborators());
        projectService.save(savedProject);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Collaborators successfully assigned!", FlashMessage.Status.SUCCESS));
        return String.format("redirect:/projects/%s", projectId);
    }

    // update existing project
    @RequestMapping(value = "/projects/{projectId}", method = RequestMethod.POST)
    public String updateProject(@Valid Project project, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.project", result);
            redirectAttributes.addFlashAttribute("project", project);
            return String.format("redirect:/projects/%s/edit", project.getId());
        }
        projectService.save(project);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully updated!", FlashMessage.Status.SUCCESS));
        return "redirect:/projects";
    }

    // delete a project
    @RequestMapping(value = "/projects/{projectId}/delete", method = RequestMethod.POST)
    public String deleteProject(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        Project project = projectService.findById(projectId);
        List<Collaborator> collaborators = new ArrayList<>(project.getCollaborators());
        project.setRolesNeeded(null);
        collaborators.forEach(project::removeCollaborator);
        projectService.save(project);
        projectService.delete(project);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("Project successfully deleted!", FlashMessage.Status.SUCCESS));
        return "redirect:/projects";
    }

    private Map<Role, Collaborator> getRoleCollaboratorMap(Project project) {
        List<Role> rolesNeeded = project.getRolesNeeded();
        List<Collaborator> collaborators = project.getCollaborators();
        Map<Role, Collaborator> roleCollaborator = new LinkedHashMap<>();

        for (Role roleNeeded : rolesNeeded) {
            roleCollaborator.put(roleNeeded,
                    collaborators.stream()
                            .filter((col) -> col.getRole().getId().equals(roleNeeded.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                Collaborator unassigned = new Collaborator();
                                unassigned.setName("Unassigned");
                                return unassigned;
                            }));
        }
        return roleCollaborator;
    }
}
