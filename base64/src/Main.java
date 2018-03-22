import java.io.IOException;

public class Main {
    static boolean tests = false;
    public static void main(String[] args) {

        if (Main.tests){
            try {
                base64.decode("./decode.txt");

            } catch (IOException e) {
                System.out.print(e);
            }

            try {
                base64.code("./code.txt");
            } catch (IOException e) {
                System.out.print(e);
            }
            return;
        }

        if (args.length!=2){
            System.out.print("args error: -c path or -d path");
            return;
        }
        if (args[0].equalsIgnoreCase("-c")){
            try {
                base64.code(args[1]);
            } catch (IOException e) {
                System.out.print(e);
            }
            return;
        }
        if (args[0].equalsIgnoreCase("-d")){
            try {
                base64.decode(args[1]);
            } catch (IOException e) {
                System.out.print(e);
            }
            return;
        }
        System.out.print("args error: -c path or -d path");
    }
}
