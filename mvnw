import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

/** Pengunduh sederhana untuk mvnw (dipakai saat curl dan wget tidak tersedia). */
public class Download {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(args[0])).build();
        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(Path.of(args[1])));
        if (response.statusCode() != 200) {
            System.err.println("Download gagal, HTTP " + response.statusCode());
            System.exit(1);
        }
    }
}