package uas;

import java.util.*;

public class DominationGame {
    private Player player1, player2;
    private Scanner scanner;

    public DominationGame() {
        scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("DOMINATION: ORIGIN");
        System.out.println("");
        System.out.println("1. Start\n2. Quit");
        int choice = scanner.nextInt();

        if (choice == 1) {
            try {
                System.out.println("Mempersiapkan medan pertempuran...");
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                System.err.println("Terjadi gangguan saat menunggu!");
            }
            initializePlayers();
            gameLoop();
        } else {
            System.out.println("Keluar dari permainan...");
        }
    }

    private void initializePlayers() {
        System.out.print("Masukkan nama untuk Pemain 1 (Homo Sapiens): ");
        String name1 = scanner.next();
        player1 = new HomoSapiens(name1);
        player1.addItem(new Herb());
        player1.addItem(new Water());
        player1.addItem(new Spear());

        System.out.print("Masukkan nama untuk Pemain 2 (Neanderthal): ");
        String name2 = scanner.next();
        player2 = new Neanderthal(name2);
        player2.addItem(new Herb());
        player2.addItem(new Water());
        player2.addItem(new Spear());
    }

    private void gameLoop() {
        System.out.println("Pertempuran dimulai antara " + player1.getName() + " dan " + player2.getName() + "!");
        try {
            Thread.sleep(2000); // Jeda selama 2 detik sebelum memulai pertempuran
        } catch (InterruptedException e) {
            System.err.println("Terjadi gangguan saat menunggu!");
        }

        while (player1.getHp() > 0 && player2.getHp() > 0) {
            System.out.println(player1.getName() + " HP: " + player1.getHp());
            System.out.println(player2.getName() + " HP: " + player2.getHp());

            takeTurn(player1, player2);
            if (player2.getHp() <= 0) break;

            takeTurn(player2, player1);
        }

        String winner = player1.getHp() > 0 ? player1.getName() : player2.getName();
        System.out.println("Permainan selesai! Pemenangnya adalah " + winner + "!");
    }

    private void takeTurn(Player user, Player target) {
        if (user.isAsleep()) {
            System.out.println(user.getName() + " sedang tidur dan melewatkan giliran ini.");
            user.setAsleep(false);
            try {
                Thread.sleep(1500); // Jeda setelah tidur
            } catch (InterruptedException e) {
                System.err.println("Terjadi gangguan saat menunggu!");
            }
            return;
        }
    
        System.out.println("Giliran " + user.getName() + "!");
        try {
            Thread.sleep(1500); // Jeda sebelum opsi muncul
        } catch (InterruptedException e) {
            System.err.println("Terjadi gangguan saat menunggu!");
        }
    
        // Tampilkan menu utama
        System.out.println("1. Cari Item\n2. Serang\n3. Tidur\n4. Gunakan Item\n5. Menghindar\n6. Fokus");
        int action = scanner.nextInt();
    
        switch (action) {
            case 1:
                new Search().execute(user, target);
                break;
            case 2:
                new Attack().execute(user, target);
                break;
            case 3:
                new Sleep().execute(user, target);
                break;
            case 4:
                // Tampilkan daftar item sebelum meminta input
                if (user.inventory.isEmpty()) {
                    System.out.println("Tidak ada item di inventaris.");
                } else {
                    System.out.println("Item yang tersedia:");
                    for (Item item : user.inventory) {
                        String usageInfo = item.isReusable() ? "penggunaan tidak terbatas" : item.getUsageLimit() + " kali penggunaan tersisa";
                        System.out.println("- " + item.getName() + " (" + usageInfo + ")");
                    }
                    System.out.print("Masukkan nama item yang ingin digunakan: ");
                    String itemName = scanner.next();
    
                    // Cari item di inventaris berdasarkan input
                    boolean itemFound = false;
                    for (Item item : user.inventory) {
                        if (item.getName().equalsIgnoreCase(itemName)) {
                            itemFound = true;
    
                            // Gunakan item jika valid
                            if (item.getUsageLimit() > 0 || item.isReusable()) {
                                item.use(user, target);
                                if (!item.isReusable() && item.getUsageLimit() == 0) {
                                    user.inventory.remove(item); // Hapus item jika sudah habis
                                }
                            } else {
                                System.out.println(item.getName() + " tidak dapat digunakan karena tidak memiliki sisa penggunaan.");
                            }
                            break;
                        }
                    }
    
                    // Tampilkan pesan jika item tidak ditemukan
                    if (!itemFound) {
                        System.out.println("Item tidak ditemukan!");
                    }
                }
                break;
    
            case 5:
                new Dodge().execute(user, target);
                break;
            case 6:
                new Focus().execute(user, target);
                break;
            default:
                System.out.println("Pilihan tidak valid. Giliran dilewati.");
                try {
                    Thread.sleep(1500); // Jeda setelah pilihan tidak valid
                } catch (InterruptedException e) {
                    System.err.println("Terjadi gangguan saat menunggu!");
                }
        }
    
        try {
            Thread.sleep(1500); // Jeda setelah setiap giliran selesai
        } catch (InterruptedException e) {
            System.err.println("Terjadi gangguan saat menunggu!");
        }
    }
    

    public static void main(String[] args) {
        DominationGame game = new DominationGame();
        game.startGame();
    }
}
