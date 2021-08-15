import com.google.gson.Gson;
import shared.DTOs.sockets.MatchInput;
import shared.DTOs.sockets.MatchOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MockClient {

    public static String getInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
    public static void main(String[] args) throws IOException {
        Socket cs = new Socket("localhost", 8888);
        PrintWriter writer = new PrintWriter(cs.getOutputStream());
        BufferedReader bf = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        OutputPrinter outPrinter = new OutputPrinter(bf);
        writer.println(2);
        writer.flush();
        MatchInput mi = new MatchInput();
        mi.sysCommand = "start";
        mi.gameMove = "";
        Gson gson = new Gson();
        writer.println(gson.toJson(mi));
        writer.flush();
        outPrinter.start();
        while(true) {
            MatchInput in = new MatchInput();
            in.gameMove = getInput();
            in.sysCommand = "";
            writer.println(gson.toJson(in));
            writer.flush();
        }
    }
}
