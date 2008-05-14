package identity.util;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jaremy
 */
public class Stacker {
    public static void stack() {

       StackTraceElement cst = Thread.currentThread().getStackTrace()[1];
       for (int i = 3; i<Thread.currentThread().getStackTrace().length; ++i)
          System.out.print("-");
       
       PrintColor.red("> "+cst.getMethodName() + "@"+
               cst.getClassName()+
               "::"+
               cst.getLineNumber());
        
    }
}
