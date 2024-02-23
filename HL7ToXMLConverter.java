import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.parser.XMLParser;

public class HL7ToXMLConverter {

    public static void main(String[] args) throws Exception {
        // Messaggio HL7 di esempio
        String hl7Message = "MSH|^~\\&|HL7Soup|Instance1|HL7Soup|Instance2|20060307110114||ORM^O01|MSGID20060307110114|P|2.5.1|\n" +
                "PID||81243|12001||Jones^John^^^Mr.||19670824|M|||123 West St.^^Denver^CO^80020^USA|||||||\n" +
                "PV1||O|OP^PAREG^||||2342^Jones^Bob|||OP|||||||||2|||||||||||||||||||||||||20060307110111|\n" +
                "ORC|NW|20060307110114\n" +
                "OBR|1|20060307110114||003038^Urinalysis^L|||20060307110114";



        System.out.println(HL7Server.pipeToXML(hl7Message));

    }

    public static String convertHL7ToXML(String hl7Message) {
        try {
            // Inizializza il contesto HAPI
            HapiContext context = new DefaultHapiContext();
            context.setModelClassFactory(new CanonicalModelClassFactory("2.5.1")); // Usa un modello HL7 standard (2.5.1)

            // Inizializza il parser HL7
            PipeParser parser = context.getPipeParser();

            // Parsing del messaggio HL7
            Message message = parser.parse(hl7Message);

            //instantiate an XML parser
            XMLParser xmlParser = new DefaultXMLParser();

            //encode message in XML
            String ackMessageInXML = xmlParser.encode(message);
            return ackMessageInXML;
        } catch (Exception ex) {
            return ex.getMessage();
        }


    }
}