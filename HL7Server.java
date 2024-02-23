
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HL7Server {
    public static void main(String[] args) throws Exception {
        int portNumber = 7080;

        //  convertHL7ToXML("MSH|^~\\&|HL7Soup|Instance1|HL7Soup|Instance2|201407271408||ADT^A04|1817457|D|2.5.1|123456||AL");

        try {
            // Crea un ServerSocket sulla porta specificata
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server in ascolto sulla porta " + portNumber + "...");

            while (true) {
                // Accetta una nuova connessione
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connessione accettata da " + clientSocket.getInetAddress());

                // Crea un BufferedReader per leggere i dati in arrivo dalla connessione
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Crea un PrintWriter per inviare dati alla connessione
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                StringBuilder stringBuilder = new StringBuilder();

                int riga = 0;

                String inputLine;
                while ((inputLine = in.readLine()) != null) {

                    System.out.println(inputLine);

                    if (inputLine.length() == 1 && inputLine.contains("\u001C")) {
                        break;
                    }

                    if (riga > 1 && inputLine.length() > 1 && inputLine.contains("\u001C")) {
                        break;
                    }

                    stringBuilder.append(inputLine).append("\n");
                    riga++;
                }

                System.out.println(stringBuilder);

                System.out.println("");

                // String cleanedHL7Message = cleanHL7Message(stringBuilder.toString().replace("\n", ""));


                //  var xml = convertHL7ToXML(cleanedHL7Message);

                var xml = pipeToXML(stringBuilder.toString());

                System.out.println("");

                System.out.println(xml);


                String response = "ACK|1";
                out.println(response);

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String pipeToXML(String hl7Message) {
        StringBuilder xmlBuilder = new StringBuilder();

        String[] lines = hl7Message.split("\n");

        int count = 0;

        for (String line : lines) {
            String cleanedLine = cleanHL7Message(line);
            List<String> fields = new ArrayList<>(Arrays.asList(cleanedLine.split("\\|")));

            String tag = fields.get(0);
            String tagBegin = "<" + tag + ">";
            String tagEnd = "</" + tag + ">";


            if (count == 0) {
                fields.add(1, "|");
            }

            xmlBuilder.append(tagBegin).append("\n");

            for (int j = 1; j < fields.size(); j++) {
                String value = fields.get(j);

                String[] subValues = value.split("\\^");

                if (subValues.length > 1) {
                    for (int k = 0; k < subValues.length; k++) {
                        String subValue = subValues[k];
                        if (!subValue.isEmpty()) {
                            xmlBuilder.append(formatXMLTag(tag, j, k + 1, subValue)).append("\n");
                        }
                    }
                } else if (!value.isEmpty()) {
                    xmlBuilder.append(formatXMLTag(tag, j, 0, value)).append("\n");
                }
            }

            xmlBuilder.append(tagEnd).append("\n");
            count++;
        }

        return xmlBuilder.toString();
    }

    private static String formatXMLTag(String tagName, int fieldIndex, int subIndex, String value) {
        String index = subIndex > 0 ? "." + subIndex : "";
        return " <" + tagName + "-" + fieldIndex + index + ">" + value + "</" + tagName + "-" + fieldIndex + index + ">";
    }



    public static String cleanHL7Message(String hl7Message) {
        // Rimuovi tutti i caratteri di controllo non visualizzabili dal messaggio HL7
        return hl7Message.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
    }

}
