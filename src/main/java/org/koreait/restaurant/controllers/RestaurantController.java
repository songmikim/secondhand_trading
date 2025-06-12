package org.koreait.restaurant.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.restaurant.entities.Restaurant;
import org.koreait.restaurant.repositories.RestaurantRepository;
import org.koreait.restaurant.services.RestaurantInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantRepository repository;
    private final RestaurantInfoService infoService;
    private final ObjectMapper om;
    private final Utils utils;

    @GetMapping({"", "/list"})
    public String list(@ModelAttribute RestaurantSearch search, Model model) {
        commonProcess("list", model);

        List<Restaurant> items = infoService.getNearest(search);
        model.addAttribute("items", items);
        try {
            model.addAttribute("json", om.writeValueAsString(items));
        } catch (JsonProcessingException e) {}

        return utils.tpl("restaurant/list");
    }

    @ResponseBody
    @GetMapping("/train")
    public List<Restaurant> train() {
        return repository.findAll();
    }

    /**
     * 공통 처리 부분
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "list";

        String pageTitle = "";
        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("list")) {
            pageTitle = utils.getMessage("오늘의_식당");
            addCss.add("restaurant/list");
            addScript.add("restaurant/list");
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
    }
}