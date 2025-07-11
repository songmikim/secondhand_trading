package org.koreait.restaurant.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyCommonController;
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
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantRepository repository;
    private final RestaurantInfoService infoService;
    private final ObjectMapper om;
    private final Utils utils;

    @GetMapping({"", "/list"})
    public String list(Model model) {
        commonProcess("list", model);

        return utils.tpl("restaurant/list");
    }

    @ResponseBody
    @GetMapping("/search")
    public List<Restaurant> search(@ModelAttribute RestaurantSearch search) {
//        List<Restaurant> items = infoService.getNearest(search);
//        return items;
        if (StringUtils.hasText(search.getSkey())) {
            return infoService.search(search);
        }

        return infoService.getNearest(search);
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
        List<String> addCommonScript = new ArrayList<>();

        if (mode.equals("list")) {
            pageTitle = utils.getMessage("오늘의_식당");
            addCss.add("restaurant/list");
            addScript.add("restaurant/list");
            addCommonScript.add("map");
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonScript", addCommonScript);
    }
}