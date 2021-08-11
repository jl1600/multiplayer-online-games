import com.google.gson.Gson;
import shared.DTOs.sockets.MatchOutput;

import java.io.BufferedReader;
import java.io.IOException;

public class OutputPrinter extends Thread {
    private BufferedReader bf;
    private Gson gson;
    public OutputPrinter(BufferedReader bf) {
        this.bf = bf;
        gson = new Gson();
    }

    public void run() {
        while(true) {
            try {
                MatchOutput out = gson.fromJson(bf.readLine(), MatchOutput.class);
                System.out.println(out.textContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
