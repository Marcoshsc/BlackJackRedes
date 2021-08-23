package domain;

import java.util.Arrays;
import java.util.List;

public class GameEndInfo {
    private final List<String> winners;
    private final int points;
    private final int house;

    public GameEndInfo(List<String> winners, int points, int house) {
        this.winners = winners;
        this.points = points;
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
                return String.format("%s-%d", String.join(",", value.winners), value.points);
            }

            @Override
            public GameEndInfo fromTransferString(String transferString) {
                String[] values = transferString.split("-");
                return new GameEndInfo(Arrays.asList(values[0].split(",")), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
            }
        };
    }
}
