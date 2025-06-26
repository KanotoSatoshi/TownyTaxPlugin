
package me.kanoto.townytax;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

public class TownyTaxPlugin extends JavaPlugin implements CommandExecutor {

    private Connection connection;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupDatabase();
        getCommand("cekpajak").setExecutor(this);
        getLogger().info("TownyTaxPlugin aktif!");
    }

    @Override
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Gagal menutup koneksi database.", e);
        }
    }

    private void setupDatabase() {
        try {
            String host = getConfig().getString("database.host");
            int port = getConfig().getInt("database.port");
            String database = getConfig().getString("database.name");
            String user = getConfig().getString("database.user");
            String password = getConfig().getString("database.password");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
            connection = DriverManager.getConnection(url, user, password);
            getLogger().info("Berhasil terhubung ke database!");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Gagal menghubungkan ke database.", e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Hanya pemain yang bisa menjalankan perintah ini.");
            return true;
        }

        Player player = (Player) sender;

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT name FROM towny_towns LIMIT 10");
            ResultSet rs = stmt.executeQuery();
            player.sendMessage("§7Top 10 kota dari database:");
            while (rs.next()) {
                player.sendMessage("- " + rs.getString("name"));
            }
        } catch (Exception e) {
            player.sendMessage("§cTerjadi kesalahan saat mengambil data kota.");
            getLogger().log(Level.SEVERE, "Kesalahan SQL:", e);
        }

        return true;
    }
}
