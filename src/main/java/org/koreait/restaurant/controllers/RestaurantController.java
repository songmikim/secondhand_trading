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

/**
 * 오늘의 식당 조회 및 검색 기능을 제공하는 컨트롤러
 */
@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantRepository repository;
    private final RestaurantInfoService infoService;
    private final ObjectMapper om;
    private final Utils utils;

    /**
     * 식당 목록 페이지 표시
     * @param model 뷰에 전달할 모델
     * @return restaurant/list 템플릿 경로
     */
    @GetMapping({"", "/list"})
    public String list(Model model) {
        commonProcess("list", model);

        return utils.tpl("restaurant/list");
    }

    /**
     * 식당 검색 또는 위치 기반 조회
     * @param search 검색 조건(키워드 또는 좌표 등)
     * @return 조회된 식당 리스트를 JSON으로 반환
     */
    @ResponseBody
    @GetMapping("/search")
    public List<Restaurant> search(@ModelAttribute RestaurantSearch search) {
        // 검색어가 있으면 키워드 기반 검색 실행
        if (StringUtils.hasText(search.getSkey())) {
            return infoService.search(search);
        }
        // 검색어 없으면 가까운 위치 기반 조회 실행
        return infoService.getNearest(search);
    }

    /**
     * 전체 식당 데이터 반환 (모델 학습 또는 테스트용)
     * @return 모든 식당 엔티티 리스트
     */
    @ResponseBody
    @GetMapping("/train")
    public List<Restaurant> train() {
        return repository.findAll();
    }

    /**
     * 공통 속성(페이지 제목, CSS/JS 스크립트 파일 목록) 설정
     * @param mode 모드 구분 (예: "list")
     * @param model 뷰에 전달할 모델
     */
    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "list";

        String pageTitle = "";
        List<String> addCss = new ArrayList<>();            // 추가할 CSS 파일 리스트
        List<String> addScript = new ArrayList<>();         // 추가할 JS 파일 리스트
        List<String> addCommonScript = new ArrayList<>();   // 공통 스크립트(지도 등)

        if (mode.equals("list")) {
            // 목록 페이지 설정
            pageTitle = utils.getMessage("오늘의_식당"); // 페이지 제목 설정
            addCss.add("restaurant/list");                  // 리스트 전용 CSS
            addScript.add("restaurant/list");               // 리스트 전용 JS
            addCommonScript.add("map");                     // 공통 지도 스크립트
        }

        // 모델에 공통 속성 추가
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonScript", addCommonScript);
    }
}