package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.bussinessObjects.User;
import com.example.demo.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/addUser")
	@PreAuthorize("hasRole('ADMIN')")
	public String showAddUserForm() {
		return "addUser";
	}

	@PostMapping("/addUser")
	@PreAuthorize("hasRole('ADMIN')")
	public String addUser(@RequestParam String username, @RequestParam String password,
			RedirectAttributes redirectAttr) {

		boolean isAdded = userService.addUser(username, password);
		if (isAdded) {
			redirectAttr.addFlashAttribute("successMessage", "User Added Successfully!.");
		} else {
			redirectAttr.addFlashAttribute("errorMessage", "User Already Present Or Failed to add user :(");
		}
		return "redirect:/addUser";
	}

	@GetMapping(path = "/getUsers", produces = MediaType.APPLICATION_JSON_VALUE)
	// @PreAuthorize("hasRole('ADMIN')")
	@ResponseBody
	public List<String> listUsersApi() {
		return userService.getAllUsernames();
	}

	@GetMapping("/getAllUsers")
	@PreAuthorize("hasRole('ADMIN')")
	public String getAllUserData(Model model) {
		List<User> users = userService.getAllUserData();
		model.addAttribute("users", users);
		return "usersList";
	}

	@PostMapping("/deleteUser")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteUser(@RequestParam String username, RedirectAttributes redirectAttributes) {
		boolean deleted = userService.deleteUser(username);
		if (deleted) {
			redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "User could not be deleted.");
		}
		return "redirect:/getAllUsers";
	}

	@GetMapping("/editUser")
	@PreAuthorize("hasRole('ADMIN')")
	public String showEditForm(@RequestParam String username, Model model) {
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);
		return "editUser";
	}

	@PostMapping("/editUser")
	@PreAuthorize("hasRole('ADMIN')")
	public String editUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
		boolean edited = userService.editUser(user);
		if (edited) {
			redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "User update failed.");
		}
		return "redirect:/getAllUsers";

	}
}