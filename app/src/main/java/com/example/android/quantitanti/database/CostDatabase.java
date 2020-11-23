package com.example.android.quantitanti.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CostEntry.class, TagEntry.class, Expenses_tags_join.class, PicsEntry.class}, version = 4, exportSchema = true)
public abstract class CostDatabase extends RoomDatabase {

    private static final String LOG_TAG = CostDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "costlist";
    private static CostDatabase sInstance;


    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP VIEW IF EXISTS `DailyExpensesView`");
            database.execSQL("ALTER TABLE `expenses` ADD COLUMN `currency` TEXT DEFAULT null");
            database.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`tag_id` INTEGER NOT NULL, " +
                    "`tag_name` TEXT, PRIMARY KEY(`tag_id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `expenses_tags_join` (`expense_id` INTEGER NOT NULL, " +
                    "`tag_id` INTEGER NOT NULL, PRIMARY KEY(`expense_id`, `tag_id`), " +
                    "FOREIGN KEY (`expense_id`) REFERENCES `expenses`(`id`) ON DELETE CASCADE, " +
                    "FOREIGN KEY (`tag_id`) REFERENCES `tags`(`tag_id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `pics` (`pics_id` INTEGER NOT NULL, " +
                    "`pic_uri` TEXT, `pic_name` TEXT DEFAULT (DATETIME('now','localtime')), `expense_id` INTEGER NOT NULL, PRIMARY KEY(`pics_id`), " +
                    "FOREIGN KEY(`expense_id`) REFERENCES `expenses`(`id`) ON DELETE CASCADE)");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX IF NOT EXISTS `cost_id` ON `expenses`(`id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `category` ON `expenses`(`category`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `date` ON `expenses`(`date`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `tag_id` ON `tags`(`tag_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `tag_name` ON `tags`(`tag_name`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `expense_id` ON `pics`(`expense_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `expense_id_join` ON `expenses_tags_join`(`expense_id`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `tag_id_join` ON `expenses_tags_join`(`tag_id`)");
        }
    };

    public static CostDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CostDatabase.class, CostDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                        //                  .allowMainThreadQueries()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract CostDao costDao();

    public abstract TagsDao tagsDao();

    public abstract Expenses_tags_join_dao expenses_tags_join_dao();

    public abstract PicsDao picsDao();

    public abstract DailyExpensesDao dailyExpensesDao();
}
