package me.camdenorrb.ontime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.camdenorrb.ontime.config.SqlConfig;
import me.camdenorrb.ontime.spigot.commands.OnTimeCmd;
import me.camdenorrb.ontime.spigot.commands.OnTimeTopCmd;
import me.camdenorrb.ontime.spigot.modules.NameModule;
import me.camdenorrb.ontime.spigot.modules.TimeModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public final class OnTimeSpigot extends JavaPlugin {

	private HikariDataSource hikariDataSource;


	private final TimeModule timeModule = new TimeModule(this);

	private final NameModule nameModule = new NameModule(this);

	private final Executor threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final Gson gson = new GsonBuilder().disableHtmlEscaping().enableComplexMapKeySerialization().setPrettyPrinting().create();


	@Override
	public void onLoad() {
		final File googleSheetsConfig = new File(getDataFolder(), "SheetsConfig.json");
		final SqlConfig mysqlConfig = SqlConfig.fromOrMake(new File(getDataFolder(), "mysqlConfig.json"), gson);

		final HikariConfig hikariConfig = new HikariConfig();

		System.out.println(mysqlConfig.getBase());

		//if(!googleSheetsConfig.exists()){
		//	boolean success = false;
		//	try {
		//		success = googleSheetsConfig.createNewFile();
		//	} catch (IOException e) { }
		//	if(!success){
		//		System.out.println("Failed to create SheetsConfig.json file.");
		//	}
		//}

		hikariConfig.setJdbcUrl("jdbc:mysql://" + mysqlConfig.getHost() + ':' + mysqlConfig.getPort() + '/' + mysqlConfig.getBase() + "?useSSL=false");
		hikariConfig.setUsername(mysqlConfig.getUser());
		hikariConfig.setPassword(mysqlConfig.getPass());
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		hikariDataSource = new HikariDataSource(hikariConfig);
	}

	@Override
	public void onEnable() {

		nameModule.enable();
		timeModule.enable();

		final OnTimeCmd onTimeCmd = new OnTimeCmd(this);
		final OnTimeTopCmd onTimeTopCmd = new OnTimeTopCmd(this);
		Objects.requireNonNull(getCommand("ontime")).setExecutor(onTimeCmd);
		Objects.requireNonNull(getCommand("ontime")).setTabCompleter(onTimeCmd);
		Objects.requireNonNull(getCommand("ontimetop")).setExecutor(onTimeTopCmd);
	}

	@Override
	public void onDisable() {

		nameModule.disable();
		timeModule.disable();

		hikariDataSource.close(); // Needs to close last
	}


	public NameModule getNameModule() {
		return nameModule;
	}

	public TimeModule getTimeModule() {
		return timeModule;
	}

	public Executor getThreadPool() {
		return threadPool;
	}

	public HikariDataSource getHikariDataSource() {
		return hikariDataSource;
	}

}
