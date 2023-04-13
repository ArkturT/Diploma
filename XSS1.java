import org.owasp.encoder.Encode;

public class SulpakExample {
    public static void main(String[] args) {
        // Original untrusted input
        String untrustedInput = "<script>alert('XSS attack!')</script>";

        // Encode the input for output on the web page
        String encodedOutput = Encode.forHtml(untrustedInput);

        // Output the encoded output to the web page
        System.out.println(encodedOutput);
    }
}
