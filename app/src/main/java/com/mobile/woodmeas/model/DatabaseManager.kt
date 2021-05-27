package com.mobile.woodmeas.model

import android.content.Context
import androidx.room.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

object DbConf {
    const val DB_NAME = "wood_meas"

    object TableNames {
        const val TREES                     = "trees"
        const val WOODEN_LOG_PACKAGES       = "wooden_log_packages"
        const val WOODEN_LOG_PACKAGES_FTS   = "wooden_log_packages_fts"
        const val WOODEN_LOG                = "wooden_log"
    }

    object TablesStruct {
        object Trees {
            const val ID    = "id"
            const val NAME  = "name"
            const val DIM_1 = "dim_1"
            const val DIM_2 = "dim_2"
            const val DIM_3 = "dim_3"
            const val DIM_4 = "dim_4"
        }

        object WoodenLogPackages {
            const val ID        = "id"
            const val NAME      = "name"
            const val ADD_DATE  = "add_date"
        }

        object WoodenLogPackagesFts {
            const val ID        = "id"
            const val NAME      = "name"
        }

        object WoodenLog {
            const val ID                = "id"
            const val WOOD_PACKAGES_ID  = "wood_packages_id"
            const val LOG_LENGTH_CM     = "log_length_cm"
            const val LOG_WIDTH_CM      = "log_width_cm"
            const val CUBIC_CM          = "cubic_cm"
            const val TREE_ID           = "tree_id"
            const val BARK_ON           = "bark_on"
            const val ADD_DATE          = "add_date"
        }
    }
}

object DatabaseManager {
    fun doesDatabaseExist(context: Context) : Boolean {
        val dbFile = context.getDatabasePath(DbConf.DB_NAME)
        return dbFile.exists()
    }

    fun copy(context: Context, pathDb: String) : Boolean {
        return try {
            val file = File(pathDb, "databases")
            file.mkdir()
            val dbFilePath = "$pathDb/databases/${DbConf.DB_NAME}"
            File(dbFilePath).createNewFile()
            val inputStream = context.assets.open(DbConf.DB_NAME)
            val fileOut = FileOutputStream(dbFilePath)
            val buffer = ByteArray(8192)
            var length: Int?
            while (true) {
                length = inputStream .read(buffer)
                if (length <= 0)
                    break
                fileOut.write(buffer, 0, length)
            }
            fileOut.flush()
            fileOut.close()
            inputStream .close()
            true
        } catch (ex: IOException) {
            false
        } catch (ex: NoSuchFileException) {
            false
        }
    }

}



@Entity(tableName = DbConf.TableNames.TREES)
    data class Trees(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = DbConf.TablesStruct.Trees.NAME) val name: String,
    @ColumnInfo(name = DbConf.TablesStruct.Trees.DIM_1) val dim1: Int,
    @ColumnInfo(name = DbConf.TablesStruct.Trees.DIM_2) val dim2: Int,
    @ColumnInfo(name = DbConf.TablesStruct.Trees.DIM_3) val dim3: Int,
    @ColumnInfo(name = DbConf.TablesStruct.Trees.DIM_4) val dim4: Int
    )

@Dao
    interface TreesDao {
        @Query("SELECT * FROM ${DbConf.TableNames.TREES} ORDER BY ${DbConf.TablesStruct.Trees.NAME} ASC")
        fun selectAll(): List<Trees>

        @Insert (onConflict = OnConflictStrategy.REPLACE)
        fun insert(trees: Trees)
    }

// WOODEN LOG PACKAGES _____________________________________________________________________________
@Entity (tableName = DbConf.TableNames.WOODEN_LOG_PACKAGES)
    data class WoodenLogPackages(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLogPackages.NAME) val name: String,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLogPackages.ADD_DATE) val addDate: Date?
    )

@Dao
    interface WoodenLogPackagesDao {
        @Query("SELECT * FROM ${DbConf.TableNames.WOODEN_LOG_PACKAGES} ORDER BY ${DbConf.TablesStruct.WoodenLogPackages.ID} DESC")
        fun selectAll(): List<WoodenLogPackages>

        @Query("SELECT * FROM ${DbConf.TableNames.WOODEN_LOG_PACKAGES} ORDER BY " +
                "${DbConf.TablesStruct.WoodenLogPackages.ID} DESC LIMIT 1")
        fun selectLast(): WoodenLogPackages

        @Query("SELECT * FROM ${DbConf.TableNames.WOODEN_LOG_PACKAGES} WHERE ${DbConf.TablesStruct.WoodenLogPackages.ID}= :id")
        fun selectWithId(id: Int): WoodenLogPackages

        @Insert
        fun insert(woodPackages: WoodenLogPackages)

        @Query("DELETE FROM ${DbConf.TableNames.WOODEN_LOG_PACKAGES} WHERE ${DbConf.TablesStruct.WoodenLogPackages.ID}= :id")
        fun deleteItem(id: Int)

        @Query("DELETE FROM ${DbConf.TableNames.WOODEN_LOG_PACKAGES}")
        fun deleteAll()

    }

@Fts4(contentEntity = WoodenLogPackages::class)
@Entity(tableName = DbConf.TableNames.WOODEN_LOG_PACKAGES_FTS)
    class WoodenLogPackagesFts(val id: String, val name: String)

@Dao
    interface WoodenLogPackagesDaoFts {
        @Query("SELECT * FROM ${DbConf.TableNames.WOODEN_LOG_PACKAGES} JOIN ${DbConf.TableNames.WOODEN_LOG_PACKAGES_FTS} ON " +
            "${DbConf.TableNames.WOODEN_LOG_PACKAGES}.${DbConf.TablesStruct.WoodenLogPackages.ID} == " +
                "${DbConf.TableNames.WOODEN_LOG_PACKAGES_FTS}.${DbConf.TablesStruct.WoodenLogPackagesFts.ID} WHERE " +
                "${DbConf.TableNames.WOODEN_LOG_PACKAGES_FTS}.${DbConf.TablesStruct.WoodenLogPackagesFts.NAME} MATCH :text GROUP BY " +
                "${DbConf.TableNames.WOODEN_LOG_PACKAGES}.${DbConf.TablesStruct.WoodenLogPackages.ID}")
        fun selectFromSearchText(text: String): List<WoodenLogPackages>

}

//__________________________________________________________________________________________________

@Entity(tableName = DbConf.TableNames.WOODEN_LOG)
data class WoodenLog(
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.WOOD_PACKAGES_ID) val woodenPackagesId: Int,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.LOG_LENGTH_CM) val logLengthCm: Int,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.LOG_WIDTH_CM) val logWidthCm: Int,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.CUBIC_CM) val cubicCm: Int,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.TREE_ID) val treeId: Int,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.BARK_ON) val barkOn: Int,
    @ColumnInfo(name = DbConf.TablesStruct.WoodenLog.ADD_DATE) val addDate: Date?
)

@Dao interface WoodenLogDao {
    @Query("SELECT * FROM ${DbConf.TableNames.WOODEN_LOG} " +
            "WHERE ${DbConf.TablesStruct.WoodenLog.WOOD_PACKAGES_ID} " +
            "= :woodPackageId ORDER BY ${DbConf.TablesStruct.WoodenLog.ID} DESC")
    fun selectWithWoodPackageId(woodPackageId: Int): List<WoodenLog>

    @Query("SELECT * FROM ${DbConf.TableNames.WOODEN_LOG}")
    fun selectAll(): List<WoodenLog>

    @Insert
    fun insert(woodenLog: WoodenLog)

    @Query("DELETE FROM ${DbConf.TableNames.WOODEN_LOG} WHERE ${DbConf.TablesStruct.WoodenLog.ID} = :id")
    fun deleteItem(id: Int)

    @Query("SELECT count(*) FROM ${DbConf.TableNames.WOODEN_LOG} WHERE ${DbConf.TablesStruct.WoodenLog.WOOD_PACKAGES_ID} = :id")
    fun count(id: Int): Int

    @Query("DELETE FROM ${DbConf.TableNames.WOODEN_LOG} WHERE ${DbConf.TablesStruct.WoodenLog.WOOD_PACKAGES_ID} = :id")
    fun deleteItemWithPackages(id: Int)

    @Query("DELETE FROM ${DbConf.TableNames.WOODEN_LOG}")
    fun deleteAll()
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}


@Database(entities = [Trees::class, WoodenLogPackages::class, WoodenLogPackagesFts::class, WoodenLog::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseManagerDao : RoomDatabase() {
    abstract fun treesDao(): TreesDao
    abstract fun woodenLogPackagesDao(): WoodenLogPackagesDao
    abstract fun woodenLogPackagesDaoFts(): WoodenLogPackagesDaoFts
    abstract fun woodenLogDao(): WoodenLogDao
    companion object {
        private var INSTANCE: DatabaseManagerDao? = null
        fun getDataBase(context: Context): DatabaseManagerDao? {
            if (INSTANCE === null) {
                synchronized(DatabaseManagerDao::class) {
                    INSTANCE = Room
                        .databaseBuilder(context.applicationContext,
                            DatabaseManagerDao::class.java,
                            DbConf.DB_NAME)
                        .build()
                }
            }
            return INSTANCE
        }
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}

