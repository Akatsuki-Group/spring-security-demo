package cn.felord.spring.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * FooController
 *
 * @author Felordcn
 * @since 9 :36 2019/10/12
 */
@RestController
@RequestMapping("/foo")
@Slf4j
public class FooController {


    /**
     *
     * @return the string
     */
    @GetMapping("/test")
    public String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("current authentication: 【 {} 】", authentication);
        return "success";
    }

    /**
     *
     *
     * @return the string
     */
    @GetMapping("/bar")

    public String self() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("current authentication: 【 {} 】", authentication);
        return "bar";
    }

    /**
     * Param string.
     *
     * @param id the id
     * @return the string
     */
    @GetMapping("/param/{id}")
    public String param(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("current authentication: 【 {} 】", authentication);

        return id;
    }


    /**
     * Pre filter collection.
     *
     * @param ids the ids
     * @return the collection
     */
    @PostMapping("/prefilter")
    public Collection<String> preFilter(@RequestBody Collection<String> ids){
        return ids;
    }


    /**
     * Postfilter collection.
     *
     * @return the collection
     */
    @GetMapping("/postfilter")
    public Collection<String> postfilter(){
       List<String> list = new ArrayList<>();
       list.add("Felordcn");
       list.add("felord");
       list.add("jetty");
        return list;
    }


    /**
     *
     *
     * @return the string
     */
    @GetMapping("/secure")
    public String  secure(){
        return "success";
    }

}
