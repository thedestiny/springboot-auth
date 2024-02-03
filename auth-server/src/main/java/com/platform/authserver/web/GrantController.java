package com.platform.authserver.web;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author kdyzm
 */
@Controller
@SessionAttributes("authorizationRequest")
public class GrantController {

    /**
     * @throws Exception
     */
    @RequestMapping("/custom/confirm_access")
    public String getAccessConfirmation(Map<String, Object> params, HttpServletRequest request, Model model) throws Exception {
        AuthorizationRequest author =
                (AuthorizationRequest) params.get("authorizationRequest");
        model.addAttribute("clientId", author.getClientId());
        model.addAttribute("scopes", author.getScope());
        return "grant";
    }

}
