package com.timeco.application.Configuration;

import com.timeco.application.Service.userservice.UserService;
import com.timeco.application.exception.BlockedUserAuthenticationFailureHandler;
import com.timeco.application.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private BlockedUserAuthenticationFailureHandler  blockedUserAuthenticationFailureHandler;


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/registration/**",
                        "/static/assets/productImages/**",
                        "/js/**",
                        "/css/**",
                        "/img/**"
                        ,"/"
                        ,"/user/products"
                        ,"/user//productDetails/{productId}"
                        ,"/video/**"
                        , "/registration/**"
                        , "/verif"
                        , "/otpSignUp"
                        ,"/main/**"
                        ,"/assets/**"
                        ,"/admin/**"
                        ,"/main/ResetOtpVerification"
                ).permitAll()
//                .antMatchers("/").hasAnyRole("USER","ADMIN")
                .antMatchers("/accessdenied").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole(("USER"))
                .antMatchers("/pro/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/main/login")
                .failureUrl("/main/login?error")
                .failureHandler(blockedUserAuthenticationFailureHandler)
                .permitAll()
                .successHandler((request, response, authentication) -> {

                    // Customize the redirection based on the user's role
                    if (authentication.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/admin/adminHome");
                    } else {
                        response.sendRedirect("/");
                    }
                })
//                .failureHandler((request, response, exception) -> {
//                    // Customize the behavior for 401 Unauthorized
//                    response.sendRedirect("/error/401"); // Redirect to your custom 401 error page
//                })
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/main/login?logout")
                .permitAll()
                .and()
                .rememberMe()
                .userDetailsService(userService) // Replace with the name of your UserDetailsService bean
                .key("rememberMe") // Set a secret key for the cookie
                .tokenValiditySeconds(60 * 60 * 24 * 7);
//                .exceptionHandling().accessDeniedPage("/main/access-denied");
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception{
        return (web) -> web.ignoring().antMatchers("/static/**","/templates/**");
    }
}
