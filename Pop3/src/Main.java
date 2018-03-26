import java.io.IOException;
import java.util.Map;

public class Main {
    static String configPath = "./App.config";
    public static void main(String[] args) {
        Map<String,String> config;
        try {
            config=CrunchifyGetPropertyValues.getPropValues(configPath);
        } catch (IOException e) {
            System.out.println("Unable to find config file");
            return;
        }
        catch (Exception e) {
            System.out.println("Unable to read " + e.getMessage() + " from config file.");
            return;
        }
        Pop pop = new Pop(config.get("server") , config.get("port"));
        pop.connect();
        pop.login(config.get("username"), config.get("password"));

        // nawiązać połączenie z serwerem poprzez gniazdo i się uwierzytelnić
        // co X sekund program powinien łączyć się z serwerem i sprawdzać, czy pojawiła się nowa wiadomość
        // w przypadku pojawienia się nowej wiadomości program powinien to zasygnalizować - pokazać jej tytuł, albo przynajmniej poinformować o nowej wiadomości
        // program powinien pozwolić na zakończenie połączenia (np. poprzez wpisanie znaku 'q').
        // Program powinien wówczas zamknąć gniazda i poinformować, ile maili łącznie zostało odebranych od czasu uruchomienia.
        // program powinien mieć zaimplementowany mechanizm UIDL (Unique Id Listing) w celu uniknięcia fałszywego wykrywania każdorazowo tych samych wiadomości.
    }
}
