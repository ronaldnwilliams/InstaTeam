package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;
import com.teamtreehouse.instateam.model.Project;
import com.teamtreehouse.instateam.model.Role;
import com.teamtreehouse.instateam.service.CollaboratorService;
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
import java.util.List;

@Controller
public class CollaboratorController {
    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    // list collaborators
    @RequestMapping("/collaborators")
    @SuppressWarnings("unchecked")
    public String listCollaborators(Model model) {
        List<Collaborator> collaborators = collaboratorService.findAll();
        List<Role> roles = roleService.findAll();
        if (!model.containsAttribute("collaborator")) {
            model.addAttribute("collaborator", new Collaborator());
        }
        model.addAttribute("roles", roles);
        model.addAttribute("collaborators", collaborators);
        return "/collaborator/index";
     }

    // add collaborator
    @RequestMapping(value = "/collaborators", method = RequestMethod.POST)
    public String addCollaborator(@Valid Collaborator collaborator, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.collaborator", result);
            redirectAttributes.addFlashAttribute("collaborator", collaborator);
            return "redirect:/collaborators";
        }
        collaboratorService.save(collaborator);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Collaborator successfully added!", FlashMessage.Status.SUCCESS));
        return "redirect:/collaborators";
    }

    // edit collaborator
    @RequestMapping(value = "/collaborators/{id}")
    public String editCollaborator(Model model, @PathVariable Long id) {
        Collaborator collaborator = collaboratorService.findById(id);
        List<Role> roles = roleService.findAll();
        if (!model.containsAttribute("collaborator")){
            model.addAttribute("collaborator", collaborator);
        }
        model.addAttribute("roles", roles);

        return "/collaborator/edit";
    }

    // update collaborator
    @RequestMapping(value = "/collaborators/update", method = RequestMethod.POST)
    public String updateCollaborator(@Valid Collaborator collaborator, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.collaborator", result);
            redirectAttributes.addFlashAttribute("collaborator", collaborator);
            return String.format("redirect:/collaborators/%s", collaborator.getId());
        }
        collaboratorService.save(collaborator);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Collaborator successfully added!", FlashMessage.Status.SUCCESS));
        return "redirect:/collaborators";
    }

    // delete collaborator
    @RequestMapping(value = "collaborators/{id}/delete", method = RequestMethod.POST)
    public String deleteCollaborator(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Collaborator collaborator = collaboratorService.findById(id);
        List<Project> allProjects = projectService.findAll();

        for (Project project : allProjects) {
            project.removeCollaborator(collaborator);
            projectService.save(project);
        }
        collaborator.setRole(null);
        collaboratorService.save(collaborator);
        collaboratorService.delete(collaborator);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Collaborator successfully deleted!", FlashMessage.Status.SUCCESS));
        return "redirect:/collaborators";
    }
}
