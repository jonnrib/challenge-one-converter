import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CurrencyConverter {
    private static final Logger logger = Logger.getLogger(CurrencyConverter.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String apiKey = "0b841bdd1629f8bc445b9136";
        Gson gson = new Gson();

        while (true) {
            printMenu();
            String input = scanner.nextLine();
            int opcao;

            try {
                opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Opção não reconhecida: " + input);
                System.out.println("Opção não reconhecida. Por favor, tente novamente.");
                continue;
            }

            if (opcao == 7) {
                System.out.println("Obrigado por usar o conversor. Até logo!");
                break;
            } else if (opcao < 1 || opcao > 6) {
                logger.log(Level.WARNING, "Opção não reconhecida: " + opcao);
                System.out.println("Opção não reconhecida. Por favor, tente novamente.");
                continue;
            }

            System.out.print("Digite o valor que deseja converter: ");
            double valor = Double.parseDouble(scanner.nextLine());

            String fromCurrency = "";
            String toCurrency = "";

            switch (opcao) {
                case 1:
                    fromCurrency = "USD";
                    toCurrency = "ARS";
                    break;
                case 2:
                    fromCurrency = "ARS";
                    toCurrency = "USD";
                    break;
                case 3:
                    fromCurrency = "USD";
                    toCurrency = "BRL";
                    break;
                case 4:
                    fromCurrency = "BRL";
                    toCurrency = "USD";
                    break;
                case 5:
                    fromCurrency = "USD";
                    toCurrency = "COP";
                    break;
                case 6:
                    fromCurrency = "COP";
                    toCurrency = "USD";
                    break;
            }

            double convertedValue = convertCurrency(apiKey, gson, fromCurrency, toCurrency, valor);
            if (convertedValue >= 0) {
                System.out.printf("Valor %.2f [%s] corresponde ao valor final de >>> %.2f [%s]%n",
                        valor, fromCurrency, convertedValue, toCurrency);
            } else {
                logger.log(Level.SEVERE, "Falha na conversão da moeda.");
            }
        }

        scanner.close();
    }

    private static double convertCurrency(String apiKey, Gson gson, String fromCurrency, String toCurrency, double amount) {
        String url_str = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", apiKey, fromCurrency);

        try {
            URL url = new URL(url_str);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonObject jsonobj = gson.fromJson(new InputStreamReader(request.getInputStream()), JsonObject.class);

            String result = jsonobj.get("result").getAsString();
            if (!"success".equals(result)) {
                logger.log(Level.WARNING, "Failed to retrieve exchange rate.");
                return -1;
            }

            JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");
            JsonElement rateElement = rates.get(toCurrency);
            if (rateElement == null) {
                logger.log(Level.WARNING, "Target currency not found.");
                return -1;
            }

            double exchangeRate = rateElement.getAsDouble();
            return amount * exchangeRate;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred during currency conversion.", e);
            return -1;
        }
    }

    private static void printMenu() {
        System.out.println("******************************************");
        System.out.println("Bem-vindo ao Conversor de Moeda!");
        System.out.println(" ");
        System.out.println("1) Dólar => Peso argentino");
        System.out.println("2) Peso argentino => Dólar");
        System.out.println("3) Dólar => Real brasileiro");
        System.out.println("4) Real brasileiro => Dólar");
        System.out.println("5) Dólar => Peso colombiano");
        System.out.println("6) Peso colombiano => Dólar");
        System.out.println("7) Sair");
        System.out.println("******************************************");
        System.out.println(" ");
        System.out.print("Escolha uma opção válida: ");
    }
}