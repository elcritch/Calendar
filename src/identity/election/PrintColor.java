public class PrintColor
{
   public static final String ANSI_RESET = "\u001B[0m";

   public static final String ANSI_HIGH_INTENSITY = "\u001B[1m";
   public static final String ANSI_LOW_INTESITY = "\u001B[2m";

   public static final String ANSI_ITALIC = "\u001B[3m";
   public static final String ANSI_UNDERLINE = "\u001B[4m";
   public static final String ANSI_BLINK = "\u001B[5m";
   public static final String ANSI_RAPID_BLINK = "\u001B[6m";
   public static final String ANSI_REVERSE_VIDEO = "\u001B[7m";
   public static final String ANSI_INVISIBLE_TEXT = "\u001B[8m";

   public static final String ANSI_BLACK = "\u001B[30m";
   public static final String ANSI_RED = "\u001B[31m";
   public static final String ANSI_GREEN = "\u001B[32m";
   public static final String ANSI_YELLOW = "\u001B[33m";
   public static final String ANSI_BLUE = "\u001B[34m";
   public static final String ANSI_MAGENTA = "\u001B[35m";
   public static final String ANSI_CYAN = "\u001B[36m";
   public static final String ANSI_WHITE = "\u001B[37m";

   public static final String ANSI_BACKGROUND_BLACK = "\u001B[40m";
   public static final String ANSI_BACKGROUND_RED = "\u001B[41m";
   public static final String ANSI_BACKGROUND_GREEN = "\u001B[42m";
   public static final String ANSI_BACKGROUND_YELLOW = "\u001B[43m";
   public static final String ANSI_BACKGROUND_BLUE = "\u001B[44m";
   public static final String ANSI_BACKGROUND_MAGENTA = "\u001B[45m";
   public static final String ANSI_BACKGROUND_CYAN = "\u001B[46m";
   public static final String ANSI_BACKGROUND_WHITE = "\u001B[47m";
   
   public static boolean ansi = false;
   /**
    * red
    * Colors the string red
    * @param output 
    */
   public static void red(String output) {
      if (ansi)
         System.out.println(ANSI_RED+output+ANSI_RESET);
      else
         System.out.println("\\<font color='red'\\>"+output+"\\</font\\>");
   }
   
   /**
    * green
    * Colors the string green
    * @param output 
    */
   public static void green(String output) {
      if (ansi)
         System.out.println(ANSI_GREEN+output+ANSI_RESET);
      else
         System.out.println("\\<font color='green'\\>"+output+"\\</font\\>");
   }
   /**
    * blue
    * Colors the string blue
    * @param output 
    */
   public static void blue(String output) {
      if (ansi)
         System.out.println(ANSI_BLUE+output+ANSI_RESET);
      else
         System.out.println("\\<font color='blue'\\>"+output+"\\</font\\>");
   }
   /**
    * orange
    * Colors the string orange
    * @param output 
    */
   public static void yellow(String output) {
      if (ansi)
         System.out.println(ANSI_YELLOW+output+ANSI_RESET);
      else
      System.out.println("\\<font color='orange'\\>"+output+"\\</font\\>");
   }

   public static void red(Object output) {
      green(output.toString());
   }
   public static void green(Object output) {
      green(output.toString());
   }
   public static void blue(Object output) {
      green(output.toString());
   }
   public static void yellow(Object output) {
      green(output.toString());
   }   
   
   public static void main(String[] args) {
      String example = "Hi There!";
      PrintColor.ansi = false;
      PrintColor.red(example);
      PrintColor.green(example);
      PrintColor.blue(example);
      PrintColor.yellow(example);
      PrintColor.ansi = true;
      PrintColor.red(example);
      PrintColor.green(example);
      PrintColor.blue(example);
      PrintColor.yellow(example);
      System.out.println("Print Normal");
      System.exit(0);
   }
}
