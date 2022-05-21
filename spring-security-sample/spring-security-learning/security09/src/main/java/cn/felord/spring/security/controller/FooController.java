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
     * 基于 SecurityExpressionOperations 接口的表达式
     * 注释的相关注解都是可用的.
     *
     * @return the string
     */
    @GetMapping("/test")
    @PreAuthorize("hasAnyRole('ADMIN')")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PreAuthorize("isAnonymous()")
    public String test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("current authentication: 【 {} 】", authentication);
        return "success";
    }

    /**
     * 基于 `UserDetails` 的表达式，此表达式用以对当前用户的一些额外的限定操作.
     *
     * @return the string
     */
    @GetMapping("/bar")
//    @PreAuthorize("principal.username.startsWith('elordcn')")
    @PreAuthorize("principal.username.startsWith('Felordcn')")
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
    @PreAuthorize("#id.equals(principal.username)")
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
//    @PreFilter(value = "filterObject.startsWith('F')",filterTarget = "ids")
    @PreFilter("hasRole('AD') or filterObject.startsWith('f')")
    public Collection<String> preFilter(@RequestBody Collection<String> ids){
        return ids;
    }


    /**
     * Postfilter collection.
     *
     * @return the collection
     */
    @GetMapping("/postfilter")
//    @PreFilter(value = "filterObject.startsWith('F')",filterTarget = "ids")
    @PostFilter("hasRole('AD') or filterObject.startsWith('f')")
    public Collection<String> postfilter(){
       List<String> list = new ArrayList<>();
       list.add("Felordcn");
       list.add("felord");
       list.add("jetty");
        return list;
    }


    /**
     * 测试 securedEnabled.
     *
     * @return the string
     */
    @GetMapping("/secure")
    @Secured({"ROLE_ADMIN1","ROLE_APP2","ROLE_POP"})
    public String  secure(){
        return "success";
    }

}
