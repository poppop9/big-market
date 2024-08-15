package app.xlog.ggbond.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/award")
public class AwardController {

    /**
     * 查询奖品列表
     **/
    @GetMapping("/queryAwardList")
    public String queryAwardList(@RequestParam Map<String, String> allParams) {
        // todo

        Set<Map.Entry<String, String>> entries = allParams.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        return "queryAwardList";
    }

}
