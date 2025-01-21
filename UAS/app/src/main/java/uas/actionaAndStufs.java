
package uas;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

// Abstract class for Players
abstract class Player {
    protected String name;
    protected int hp, atk, def, spd;
    protected boolean isAsleep;
    protected boolean dodgedLastTurn;
    protected List<Item> inventory;

    public Player(String name, int hp, int atk, int def, int spd) {
        this.name = name;
        this.hp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.isAsleep = false;
        this.dodgedLastTurn = false;
        this.inventory = new ArrayList<>();
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getSpd() { return spd; }
    public boolean isAsleep() { return isAsleep; }
    public void setAsleep(boolean asleep) { this.isAsleep = asleep; }
    public boolean didDodgeLastTurn() { return dodgedLastTurn; }
    public void setDodgedLastTurn(boolean dodged) { this.dodgedLastTurn = dodged; }

    
    public void takeDamage(int damage) {
        this.hp -= Math.max(damage - (def / 2), 0);
    }
    
    public void heal(int amount) {
        this.hp = Math.min(this.hp + amount, 100);
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void useItem(String itemName, Player target) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                if (item.getUsageLimit() > 0 || item.isReusable()) {
                    item.use(this, target); // Use the item
                    if (item.getUsageLimit() == 0 && !item.isReusable()) {
                        // If the item is no longer usable and is non-reusable, remove it
                        inventory.remove(item);
                    }
                } else {
                    System.out.println(item.getName() + " cannot be used because it has no uses left.");
                }
                return;
            }
        }
        System.out.println("Item not found!");
    }
    



    public abstract void useAction();
}

class HomoSapiens extends Player {
    public HomoSapiens(String name) {
        super(name, 100, 20, 10, 15);
    }

    @Override
    public void useAction() {
        System.out.println(name + " is using an action!");
    }
}

class Neanderthal extends Player {
    public Neanderthal(String name) {
        super(name, 100, 20, 10, 15);
    }

    @Override
    public void useAction() {
        System.out.println(name + " is using an action!");
    }
}

// Abstract class for Items
abstract class Item {
    protected String name;
    protected boolean reusable;
    protected int usageLimit;

    public Item(String name, boolean reusable, int usageLimit) {
        this.name = name;
        this.reusable = reusable;
        this.usageLimit = usageLimit;
    }

    public String getName() { return name; }
    public boolean isReusable() { return reusable; }
    public int getUsageLimit() { return usageLimit; }
    public void decreaseUsage() { this.usageLimit--; }

    public abstract void use(Player user, Player target);
}


// Update Herb with a usage limit of 1
class Herb extends Item {
    public Herb() {
        super("Herba", false, 3); // Herb is non-reusable and has a limit of 1 use
    }

    @Override
    public void use(Player user, Player target) {
        if (usageLimit > 0) {
            user.heal(20);
            System.out.println(user.getName() + " menggunakan Herba dan memulihkan 20 HP!");
            decreaseUsage();
        } else {
            System.out.println(user.getName() + " tidak dapat menggunakan Herba karena tidak ada penggunaan yang tersisa.");
        }
    }
}


// Update Water with a reusable limit (e.g., unlimited usage)
class Water extends Item {
    public Water() {
        super("Air", true, Integer.MAX_VALUE); // Air is reusable
    }

    @Override
    public void use(Player user, Player target) {
        if (usageLimit > 0) {
            System.out.println(user.getName() + " menggunakan Air untuk menghilangkan semua efek status!");
            decreaseUsage(); // Decrease usage if it's limited
        } else {
            System.out.println(user.getName() + " tidak dapat menggunakan Air karena tidak ada penggunaan yang tersisa.");
        }
    }
}

class Spear extends Item {
    public Spear() {
        super("Tombak", false, 3); // Tombak is non-reusable and can be used 3 times
    }

    @Override
    public void use(Player user, Player target) {
        if (usageLimit > 0) {
            int damage = (int) (user.getSpd() * 1.75);
            target.takeDamage(damage);
            System.out.println(user.getName() + " menggunakan Tombak dan memberikan " + damage + " kerusakan kepada " + target.getName() + "!");
            decreaseUsage();
        } else {
            System.out.println(user.getName() + " tidak dapat menggunakan Tombak karena tidak ada penggunaan yang tersisa.");
        }
    }
}





// Abstract class for Actions
abstract class Action {
    protected String name;

    public Action(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public abstract void execute(Player user, Player target);
}

class Search extends Action {
    private static int successRate = 90;

    public Search() {
        super("Cari");
    }

    @Override
    public void execute(Player user, Player target) {
        Random random = new Random();
        if (random.nextInt(100) < successRate) {
            Item foundItem = random.nextInt(2) == 0 ? new Herb() : new Spear();
            user.addItem(foundItem);
            System.out.println(user.getName() + " menemukan " + foundItem.getName() + "!");
            successRate = Math.max(successRate - 10, 30);
        } else {
            System.out.println(user.getName() + " tidak menemukan apa-apa.");
            successRate = Math.min(successRate + 10, 70);
        }
    }
}



class Attack extends Action {
    public Attack() {
        super("Serang");
    }

    @Override
    public void execute(Player user, Player target) {
        Random random = new Random();
        if (random.nextInt(100) < 90) { // 90% chance to hit
            int damage = user.getAtk();
            target.takeDamage(damage);
            System.out.println(user.getName() + " menyerang " + target.getName() + " untuk " + damage + " kerusakan!");
        } else {
            System.out.println(user.getName() + " gagal menyerang dan meleset!");
        }
    }
}


class Sleep extends Action {
    public Sleep() {
        super("Tidur");
    }

    @Override
    public void execute(Player user, Player target) {
        System.out.println(user.getName() + " tidur untuk memulihkan HP.");
        user.heal(user.getHp() / 2);
        user.setAsleep(true);
    }
}


class Focus extends Action {
    public Focus() {
        super("Fokus");
    }

    @Override
    public void execute(Player user, Player target) {
        user.atk += 10;
        user.spd += 10;
        System.out.println(user.getName() + " fokus dan meningkatkan serangan dan kecepatan sebesar 10!");
    }
}



class Dodge extends Action {
    public Dodge() {
        super("Menghindar");
    }

    @Override
    public void execute(Player user, Player target) {
        Random random = new Random();
        if (user.didDodgeLastTurn()) {
            System.out.println(user.getName() + " mencoba menghindar lagi tetapi dengan peluang yang berkurang!");
            if (random.nextInt(100) < 50) { // 50% chance to dodge if dodged last turn
                System.out.println(user.getName() + " berhasil menghindar dari semua serangan pada giliran ini!");
                user.setDodgedLastTurn(true);
            } else {
                System.out.println(user.getName() + " gagal menghindar kali ini.");
                user.setDodgedLastTurn(false);
            }
        } else {
            if (random.nextInt(100) < 80) { // 80% chance to dodge if not dodged last turn
                System.out.println(user.getName() + " berhasil menghindar dari semua serangan pada giliran ini!");
                user.setDodgedLastTurn(true);
            } else {
                System.out.println(user.getName() + " gagal menghindar.");
                user.setDodgedLastTurn(false);
            }
        }
    }
}

