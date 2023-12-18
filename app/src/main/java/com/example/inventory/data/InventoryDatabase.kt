package com.example.inventory.data

import android.content.ContentValues.TAG
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        private const val DATABASE_NAME = "item_database"
        private const val KEY_ALIAS = "inventory_cipher_key"

        @Volatile
        private var instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): InventoryDatabase {
            val cipherKey = getCipherKey(context) ?: initCipherKey(context)
            return Room.databaseBuilder(
                context.applicationContext,
                InventoryDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(SupportFactory(cipherKey))
                .fallbackToDestructiveMigration()
                .build()
        }

        private fun getCipherKey(context: Context): ByteArray? {
            val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
            val entry: KeyStore.Entry = ks.getEntry(KEY_ALIAS, null)
            if (entry !is KeyStore.PrivateKeyEntry) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry")
                return null
            }

            return entry.privateKey.encoded
        }

        private fun initCipherKey(context: Context): ByteArray? {
            generateCipherKey(context)

            val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
            val entry: KeyStore.Entry = ks.getEntry(KEY_ALIAS, null)
            if (entry is KeyStore.PrivateKeyEntry) {
                return entry.privateKey.encoded
            }

            return null
        }
        private fun generateCipherKey(context: Context) {
            val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                "AndroidKeyStore"
            )
            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).run {
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                build()
            }

            kpg.initialize(parameterSpec)
            kpg.generateKeyPair()
        }
    }
}