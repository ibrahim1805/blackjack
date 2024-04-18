import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackjackGUI extends Application {
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private Label playerLabel;
    private Label dealerLabel;
    private Button hitButton;
    private Button standButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        deck = new Deck();
        deck.shuffle();
        player = new Player();
        dealer = new Dealer();

        playerLabel = new Label("Player Hand: ");
        dealerLabel = new Label("Dealer Hand: ");

        hitButton = new Button("Hit");
        hitButton.setOnAction(event -> {
            player.addCard(deck.dealCard());
            playerLabel.setText("Player Hand: " + player.getHand() + " Total: " + player.getTotal());
            if (player.getTotal() > 21) {
                playerLabel.setText("Player Hand: " + player.getHand() + " Total: " + player.getTotal() + " - BUST!");
                endGame();
            }
        });

        standButton = new Button("Stand");
        standButton.setOnAction(event -> {
            dealer.play(deck);
            dealerLabel.setText("Dealer Hand: " + dealer.getHand() + " Total: " + dealer.getTotal());
            endGame();
        });

        HBox buttonBox = new HBox(10, hitButton, standButton);
        buttonBox.setPadding(new Insets(10));

        VBox root = new VBox(10, playerLabel, dealerLabel, buttonBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Blackjack");
        primaryStage.show();

        initialDeal();
    }

    private void initialDeal() {
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());

        playerLabel.setText("Player Hand: " + player.getHand() + " Total: " + player.getTotal());
        dealerLabel.setText("Dealer Hand: " + dealer.getHand() + " Total: " + dealer.getTotal());

        if (player.getTotal() == 21) {
            playerLabel.setText("Player Hand: " + player.getHand() + " Total: " + player.getTotal() + " - Blackjack!");
            endGame();
        }
    }

    private void endGame() {
        hitButton.setDisable(true);
        standButton.setDisable(true);

        int playerTotal = player.getTotal();
        int dealerTotal = dealer.getTotal();

        if ((dealerTotal > 21) || (playerTotal <= 21 && playerTotal > dealerTotal)) {
            playerLabel.setText(playerLabel.getText() + " - You win!");
        } else if (playerTotal == dealerTotal) {
            playerLabel.setText(playerLabel.getText() + " - It's a tie.");
        } else {
            playerLabel.setText(playerLabel.getText() + " - Dealer wins.");
        }
    }
}

class Card {
    private String rank;
    private String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getValue() {
        if (rank.equals("Ace")) {
            return 11;
        } else if (rank.equals("King") || rank.equals("Queen") || rank.equals("Jack")) {
            return 10;
        } else {
            return Integer.parseInt(rank);
        }
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        return cards.remove(0);
    }
}

class Hand {
    protected List<Card> cards;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getTotal() {
        int total = 0;
        int aceCount = 0;
        for (Card card : cards) {
            total += card.getValue();
            if (card.getValue() == 11) {
                aceCount++;
            }
        }
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getHand() {
        StringBuilder handString = new StringBuilder();
        for (Card card : cards) {
            handString.append(card.toString()).append(", ");
        }
        return handString.toString();
    }
}

class Player extends Hand {
}

class Dealer extends Hand {
    public void play(Deck deck) {
        while (getTotal() < 17) {
            addCard(deck.dealCard());
        }
    }

    @Override
    public String getHand() {
        return cards.get(0).toString() + ", [Hidden]";
    }
}
