package com.spring.security.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author security
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private String phone;

    private String email;

    private Date created;

    private Date updated;


}
