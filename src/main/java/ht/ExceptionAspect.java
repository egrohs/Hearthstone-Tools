package ht;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ExceptionAspect {
	// execution é mais abrangente, captura codigo jre ou outras libs.
	// @AfterThrowing(pointcut = "execution(* *.*(..))", throwing = "ex")
	// call somente as chamadas feitas dentro de seu codigo weaved.
	@AfterThrowing(pointcut = "call(* *.*(..))", throwing = "ex")
	public void doRecoveryActions(Exception ex) {
		System.out.println("possivel exececao: " + ex.getMessage() + " " + ex.getStackTrace()[0]);
	}
}