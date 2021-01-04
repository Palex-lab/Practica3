import java.util.concurrent.ConcurrentHashMap;

public class Servidor implements Runnable {

    public static ConcurrentHashMap<String, MySocket> llistausuaris = new ConcurrentHashMap<>();

    public static boolean continuar = false;
    public boolean desconnectat = false;
    MySocket mysocket;
    String nick;

    public Servidor(String nick, MySocket mysocket) {
        this.nick = nick;
        this.mysocket = mysocket;
    }

    public static void main(String[] args) throws Exception {
        MyServerSocket myserversocket = new MyServerSocket(8001);
        System.out.println("Server ready");

        while (true) {
            MySocket ms = myserversocket.accept();
            while (continuar == false) {
                // ms.println("Introdueix el teu nom d'usuari: ");
                String possible_nick = ms.readLine();
                if (llistausuaris.containsKey(possible_nick)) {
                    // ms.println("El nom d'usuari " + possible_nick + " ja existeix.");
                    ms.println("DNC");
                } else {
                    ms.println("DC");
                    System.out.println("S'ha connectat " + possible_nick);
                    llistausuaris.put(possible_nick, ms);
                    new Thread(new Servidor(possible_nick, ms)).start();
                    continuar = true;
                }
            }
            continuar = false;

        }
    }

    public void run() {
        while (desconnectat == false) { //connectat
            llistaUsuaris();
            String linia = llistausuaris.get(nick).readLine();

            desconnectat = desconnexio(nick, linia); //boolean

            if (desconnectat == false) {
                for (MySocket s : llistausuaris.values()) {
                    if (s != mysocket) {
                        s.println(nick + ": " + linia);
                        System.out.println(nick + " diu: " + linia);
                    }

                }
            } //else

        }
    }

    public boolean desconnexio(String nick, String linia) {
        boolean desconnexio = false;
        if (linia.equals("Adeu")) {
            llistausuaris.get(nick).println("Adeu");
            llistausuaris.get(nick).close();
            llistausuaris.remove(nick);
            llistaUsuaris();

            for (MySocket s : llistausuaris.values()) {
                if (s != mysocket) {
                    s.println(nick + ": Adeu");
                    s.println(nick + ": ha abandonat el xat");
                    System.out.println(nick + " diu: Adeu");
                    System.out.println(nick + " s'ha desconnectat");
                    llistausuaris.get(nick).println("Adeu");
                    llistausuaris.get(nick).close();
                    s.close();
                }

            }
            desconnexio = true;
            //connectat = false;
        }
        return desconnexio;
    }

    public void llistaUsuaris() {
        String llista = "";

        for (ConcurrentHashMap.Entry<String, MySocket> entry : llistausuaris.entrySet()) {
            llista = llista + entry.getKey() + " ";
        }

        for (ConcurrentHashMap.Entry<String, MySocket> entry : llistausuaris.entrySet()) {
            entry.getValue().println(".updateList");
            entry.getValue().println(llista);
        }
    }

}
