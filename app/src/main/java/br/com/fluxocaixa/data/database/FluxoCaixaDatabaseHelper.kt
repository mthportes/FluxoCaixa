package br.com.fluxocaixa.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.fluxocaixa.data.model.Lancamento
import br.com.fluxocaixa.data.model.TipoLancamento

class FluxoCaixaDatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_LANCAMENTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_VALOR REAL NOT NULL,
                $COLUMN_DESCRICAO TEXT NOT NULL,
                $COLUMN_DATA INTEGER NOT NULL,
                $COLUMN_TIPO TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LANCAMENTOS")
        onCreate(db)
    }

    fun inserir(lancamento: Lancamento): Long {
        val values = ContentValues().apply {
            put(COLUMN_VALOR, lancamento.valor)
            put(COLUMN_DESCRICAO, lancamento.descricao)
            put(COLUMN_DATA, lancamento.dataMillis)
            put(COLUMN_TIPO, lancamento.tipo.name)
        }
        return writableDatabase.insert(TABLE_LANCAMENTOS, null, values)
    }

    fun listarTodos(): List<Lancamento> {
        val lista = mutableListOf<Lancamento>()
        val cursor = readableDatabase.query(
            TABLE_LANCAMENTOS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_DATA DESC, $COLUMN_ID DESC"
        )

        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(COLUMN_ID)
            val valorIndex = it.getColumnIndexOrThrow(COLUMN_VALOR)
            val descricaoIndex = it.getColumnIndexOrThrow(COLUMN_DESCRICAO)
            val dataIndex = it.getColumnIndexOrThrow(COLUMN_DATA)
            val tipoIndex = it.getColumnIndexOrThrow(COLUMN_TIPO)

            while (it.moveToNext()) {
                lista.add(
                    Lancamento(
                        id = it.getLong(idIndex),
                        valor = it.getDouble(valorIndex),
                        descricao = it.getString(descricaoIndex),
                        dataMillis = it.getLong(dataIndex),
                        tipo = TipoLancamento.valueOf(it.getString(tipoIndex))
                    )
                )
            }
        }
        return lista
    }

    companion object {
        private const val DATABASE_NAME = "fluxo_caixa.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_LANCAMENTOS = "lancamentos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_VALOR = "valor"
        private const val COLUMN_DESCRICAO = "descricao"
        private const val COLUMN_DATA = "data_lancamento"
        private const val COLUMN_TIPO = "tipo"

        @Volatile
        private var instance: FluxoCaixaDatabaseHelper? = null

        fun getInstance(context: Context): FluxoCaixaDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: FluxoCaixaDatabaseHelper(context).also { instance = it }
            }
        }
    }
}
