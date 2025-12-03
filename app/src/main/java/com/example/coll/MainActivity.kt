package com.example.coll

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.coll.ui.ScheduleActivity
import com.example.coll.ui.SvgMapHelper
import com.example.coll.ui.SvgMapView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    
    private lateinit var svgMapView: SvgMapView
    private lateinit var debugButton: MaterialButton
    private lateinit var debugText: TextView
    private lateinit var instructionText: TextView
    private var debugMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        svgMapView = findViewById(R.id.svgMapView)
        debugButton = findViewById(R.id.debugButton)
        debugText = findViewById(R.id.debugText)
        instructionText = findViewById(R.id.instructionText)
        
        // Загружаем SVG карту
        // Используем post, чтобы убедиться, что View уже добавлен в иерархию
        svgMapView.post {
            try {
                svgMapView.loadSvgFromAssets("floor2.svg")
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Ошибка загрузки SVG: ${e.message}", e)
                e.printStackTrace()
            }
        }
        
        // Настраиваем интерактивные области кабинетов
        setupRoomAreas()
        
        // Обработчик клика на кабинет
        svgMapView.onRoomClickListener = { roomId ->
            if (!debugMode) {
                openSchedule(roomId)
            }
        }
        
        // Обработчик режима отладки
        svgMapView.onDebugClickListener = { svgX, svgY ->
            val message = "Координаты SVG: X=${svgX.toInt()}, Y=${svgY.toInt()}\n" +
                    "Используйте эти координаты в setupRoomAreas()\n\n" +
                    "Пример для прямоугольного кабинета:\n" +
                    "SvgMapHelper.createRectArea(\"room_XXX\", " +
                    "${(svgX - 50).toInt()}f, ${(svgY - 50).toInt()}f, " +
                    "${(svgX + 50).toInt()}f, ${(svgY + 50).toInt()}f)"
            debugText.text = message
            debugText.visibility = View.VISIBLE
            Log.d("SvgMap", "Click at SVG coordinates: X=$svgX, Y=$svgY")
        }
        
        // Кнопка переключения режима отладки
        debugButton.setOnClickListener {
            debugMode = !debugMode
            svgMapView.setDebugMode(debugMode)
            
            if (debugMode) {
                debugButton.text = "Выключить отладку"
                instructionText.text = "Кликайте на карте, чтобы увидеть координаты. Скопируйте их в setupRoomAreas()"
                debugText.visibility = View.VISIBLE
            } else {
                debugButton.text = "Режим отладки"
                instructionText.text = "Нажмите на кабинет для просмотра расписания"
                debugText.visibility = View.GONE
            }
        }
    }
    
    /**
     * Настраивает интерактивные области кабинетов на карте
     * Координаты основаны на анализе SVG файла floor2.svg
     * Размеры SVG: 1800x1000
     * 
     * ВАЖНО: Если координаты не совпадают, используйте режим отладки!
     * Включите режим отладки, кликните на кабинет и скопируйте координаты сюда.
     */
    private fun setupRoomAreas() {
        val areas = mutableListOf<com.example.coll.data.RoomArea>()
        
        // Координаты основаны на структуре SVG:
        // - Пол находится от y=116.5 до y=816.5
        // - Кабинеты в нижней части от y=585 до y=816.5
        // - Стены разделяют кабинеты на определенных x координатах
        
        // Кабинет 201 (текст на x=280, y=745.864)
        // Примерные границы: левая стена x=213-248, правая x=345-380, верх y=585, низ y=816.5
        areas.add(SvgMapHelper.createRectArea("room_201", 213f, 585f, 380f, 816.5f))
        
        // Кабинет 202 (текст на x=417, y=748.864)
        areas.add(SvgMapHelper.createRectArea("room_202", 380f, 585f, 446.5f, 816.5f))
        
        // Кабинет 203 (текст на x=466, y=752.864)
        areas.add(SvgMapHelper.createRectArea("room_203", 446.5f, 585f, 495.5f, 816.5f))
        
        // Кабинет 204 (текст на x=579, y=754.864)
        areas.add(SvgMapHelper.createRectArea("room_204", 495.5f, 585f, 605.5f, 816.5f))
        
        // Кабинет 205 (текст на x=745, y=754.864)
        areas.add(SvgMapHelper.createRectArea("room_205", 653f, 585f, 802.5f, 816.5f))
        
        // Кабинет 206 (текст на x=864, y=751.864)
        areas.add(SvgMapHelper.createRectArea("room_206", 802.5f, 585f, 933.5f, 816.5f))
        
        // Кабинет 207 (текст на x=1066, y=746.864)
        areas.add(SvgMapHelper.createRectArea("room_207", 933.5f, 585f, 1155f, 816.5f))
        
        // Кабинет 208 (текст на x=1225, y=752.864)
        areas.add(SvgMapHelper.createRectArea("room_208", 1155f, 585f, 1285f, 816.5f))
        
        // Кабинет 209 (текст на x=1433, y=748.864)
        areas.add(SvgMapHelper.createRectArea("room_209", 1309.5f, 585f, 1521.5f, 816.5f))
        
        // Sports room (текст на x=413, y=494.864) - находится в верхней части
        // Примерные границы на основе структуры SVG
        areas.add(SvgMapHelper.createRectArea("room_sports", 331f, 116.5f, 575.5f, 585f))
        
        svgMapView.setRoomAreas(areas)
    }
    
    /**
     * Открывает экран с расписанием кабинета
     */
    private fun openSchedule(roomId: String) {
        val intent = Intent(this, ScheduleActivity::class.java)
        intent.putExtra(ScheduleActivity.EXTRA_ROOM_ID, roomId)
        startActivity(intent)
    }
}
