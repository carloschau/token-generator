package carloschau.tokengenerator.security.expression

import org.aopalliance.intercept.MethodInvocation
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.core.Authentication

class CustomMethodSecurityExpressionHandler: DefaultMethodSecurityExpressionHandler(){

    override fun createSecurityExpressionRoot(authentication: Authentication, invocation: MethodInvocation?): MethodSecurityExpressionOperations {
        val root = CustomMethodSecurityExpressionRoot(authentication)
        root.setThis(invocation!!.getThis())
        root.setPermissionEvaluator(permissionEvaluator)
        root.setTrustResolver(trustResolver)
        root.setRoleHierarchy(roleHierarchy)
        root.setDefaultRolePrefix(defaultRolePrefix)

        return root
    }
}