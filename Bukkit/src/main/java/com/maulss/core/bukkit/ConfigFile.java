package com.maulss.core.bukkit;

import com.google.common.collect.Lists;
import com.maulss.core.collect.EnhancedList;
import com.maulss.core.text.StringUtil;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public abstract class ConfigFile {

    private final File config;
    private final FileConfiguration fileConfig;

    protected ConfigFile() {
        this(true);
    }

    protected ConfigFile(final boolean instaLoad) {
        Validate.notNull(getPluginInstance());
        Validate.notNull(getConfigName());
        Validate.notEmpty(getConfigName());

        config = new File(getPluginInstance().getDataFolder(), getConfigName());

        /*
         * Checks whether the configuration file is present.
         * If it's not, then generate it.
         */
        check(config, getPluginInstance().getResource(getConfigName()));
        fileConfig = new YamlConfiguration();

        if (instaLoad) load();
    }

    /**
     * Loads the configuration file and loads all the values
     * according to their paths specified in the parameters
     * of the inherited methods.
     */
    public final void load() {
        List<Object> values = Lists.newArrayList();

        try {
            fileConfig.load(config);

            String[] paths = getPaths();
            for (String path : paths) {

                // obtain individual value from path, default is null
                // if null is the case then it will not be accessed
                Object value = fileConfig.get(path, null);
                if (value == null) {
                    Core.get().debug("Path '%s' resulted in a null value in config %s", path, config.getPath());
                } else {
                    values.add(value);
                }
            }

            EnhancedList<Field> usableFields = new EnhancedList<>();

            Field[] fields = getClass().getFields();
            for (Field field : fields) {

                // make sure the fields are not final, not static and not transient
                int mod = field.getModifiers();
                if (!Modifier.isFinal(mod)
                        && !Modifier.isStatic(mod)
                        && !Modifier.isTransient(mod)) {
                    usableFields.add(field);
                }
            }

            int usableFieldsLen = usableFields.size();
            int pathsLen = paths.length;
            int valuesLen = values.size();

            // output an error and stop the task if the amount of usable
            // fields doesn't equal to the amount of non-null paths
            if (usableFieldsLen != pathsLen) {
                Core.get().log(
                        "File '%s' was not loaded for plugin '%s' because the " +
                                "amount of fields (%s) does not equal to the amount of paths (%s). " +
                                "Total fields: %s",
                        getConfigName(),
                        getPluginInstance().getDescription().getName(),
                        usableFieldsLen,
                        pathsLen,
                        fields.length
                );
                return;
            }

            for (int x = 0; x < valuesLen; x++) {
                Field field = usableFields.get(x);
                Object value = values.get(x);

                try {
                    field.set(this, value);
                } catch (Exception e) {
                    Core.get().log(
                            "An error occurred while attempting to set the value " +
                                    "'%s' to field '%s' from file '%s' for plugin '%s': %s",
                            value,
                            field.getName(),
                            getConfigName(),
                            getPluginInstance().getDescription().getName(),
                            e.getMessage()
                    );
                }
            }

            Core.get().log(
                    "Loaded &e%s %s&r from file '&e%s&r'",
                    valuesLen,
                    StringUtil.checkPlural("value", "values", valuesLen),
                    config.getPath()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getConfig() {
        return config;
    }

    public FileConfiguration getFileConfig() {
        return fileConfig;
    }

    protected abstract JavaPlugin getPluginInstance();

    protected abstract String getConfigName();

    protected abstract String[] getPaths();

    /**
     * Checks if File exists. If not, regenerates configuration
     * file.
     *
     * <p>In case the file needs to be generated, all parent
     * directories are also made.</p>
     *
     * @param f The file object to check.
     * @param i The resource file.
     */
    public static void check(final File f,
                             final InputStream i) {
        Validate.notNull(f);
        Validate.notNull(i);

        try {
            if (!f.exists()) {
                f.getParentFile().mkdirs();

                OutputStream out = new FileOutputStream(f);
                byte[] buf = new byte[1024];
                int len;
                while ((len = i.read(buf)) > 0) out.write(buf, 0, len);
                out.close();
                i.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}