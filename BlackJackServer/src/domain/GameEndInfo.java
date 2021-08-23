package domain;

import server.ConnectionHandler;

import java.util.Arrays;
import java.util.List;

public class GameEndInfo {
    private final List<String> winners;
    private final int points;
    private final int house;

    public GameEndInfo(List<String> winners, int points, int house) {
        this.winners = winners;
        this.points = points == -1 ? house : points;
        this.house = house;
    }

    public List<String> getWinners() {
        return winners;
    }

    public int getPoints() {
        return points;
    }

    public int getHouse() {
        return house;
    }

    public static NetworkTransferable<GameEndInfo> gameEndInfoNetworkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(GameEndInfo value) {
                String join = String.join(",", value.winners);
                return String.format("%s-%d-%d", join, value.points, value.house);
            }

            @Override
            public GameEndInfo fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                String[] values = transferString.split("-");
                return new GameEndInfo(Arrays.asList(values[0].split(",")), Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]));
            }
        };
    }
}
