package carloschau.tokengenerator.configuration

import carloschau.tokengenerator.security.PreAuthenticationDetailsSource
import carloschau.tokengenerator.security.TokenAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity?) {
        http?.let {
            http.csrf().disable()
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            http.formLogin().disable()
            http.logout().disable()

            http.addFilter(getTokenAuthenticationFilter())
                    .authenticationProvider(getPreAuthenticatedAuthenticationProvider())
                    .authorizeRequests()
                    .antMatchers("/auth/**", "/token/**")
                    .permitAll()
                    .antMatchers("/**")
                    .authenticated()

        }
    }

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
            setAuthenticationDetailsSource(getPreAuthenticationDetailsSource())
            setAuthenticationFailureHandler(SimpleUrlAuthenticationFailureHandler())
        }
    }

    @Bean
    fun getPreAuthenticationDetailsSource(): PreAuthenticationDetailsSource{
        return PreAuthenticationDetailsSource()
    }
}

