import java.util.ArrayList;

public class TestArray 
{

   public static void main(String[] args) {
      ArrayList<String> t1 = new ArrayList<String>();
      ArrayList<String> t2 = new ArrayList<String>();
      
      t1.add("191.168.0.101");
      t1.add("191.168.0.102");
      t1.add("191.168.0.103");
      
      t2.add("191.168.0.101");
      t2.add("191.168.0.102");
      t2.add("191.168.0.103");
      
      System.out.println("t1 "+t1.hashCode());
      System.out.println("t2 "+t2.hashCode());
      
      t2.add("191.168.0.104");
      System.out.println("t1 "+t1.hashCode());
      System.out.println("t2 "+t2.hashCode());
      
      System.exit(0);
   }
}
