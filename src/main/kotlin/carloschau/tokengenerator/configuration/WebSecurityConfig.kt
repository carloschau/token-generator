package carloschau.tokengenerator.configuration

import carloschau.tokengenerator.security.PreAuthenticatedUserDetailsService
import carloschau.tokengenerator.security.TokenAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.let {
            http.csrf().disable()
            //http.authorizeRequests().antMatchers("/api/hello").authenticated()

            http.antMatcher("/api/auth/**")
                    .anonymous()

            http.antMatcher("/api/**")
                    .addFilterAfter(getTokenAuthenticationFilter(), BasicAuthenticationFilter::class.java)
                    .authenticationProvider(getPreAuthenticatedAuthenticationProvider())
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()


        }
    }

//    override fun configure(auth: AuthenticationManagerBuilder?) {
//        auth?.let {
//            auth.authenticationProvider(getPreAuthenticatedAuthenticationProvider())
//        }
//    }

    @Bean
    fun getPreAuthenticatedAuthenticationProvider(): PreAuthenticatedAuthenticationProvider{
        return PreAuthenticatedAuthenticationProvider().apply {
            setPreAuthenticatedUserDetailsService(getPreAuthenticatedUserDetailsService())
        }
    }

    @Bean
    fun getPreAuthenticatedUserDetailsService(): PreAuthenticatedGrantedAuthoritiesUserDetailsService {
        return PreAuthenticatedGrantedAuthoritiesUserDetailsService().apply {

        }
    }

    @Bean
    fun getTokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter().apply {
            setAuthenticationManager(authenticationManager())


        }
    }
}

